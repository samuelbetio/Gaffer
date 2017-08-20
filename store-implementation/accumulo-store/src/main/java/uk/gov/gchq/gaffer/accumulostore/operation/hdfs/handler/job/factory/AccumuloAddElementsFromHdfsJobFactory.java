/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.operation.hdfs.handler.job.factory;

import org.apache.accumulo.core.client.mapreduce.AccumuloFileOutputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.handler.job.partitioner.GafferKeyRangePartitioner;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.mapper.AddElementsFromHdfsMapper;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.reducer.AccumuloKeyValueReducer;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloStoreConstants;
import uk.gov.gchq.gaffer.accumulostore.utils.IngestUtils;
import uk.gov.gchq.gaffer.accumulostore.utils.TableUtils;
import uk.gov.gchq.gaffer.commonutil.CommonConstants;
import uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.factory.AddElementsFromHdfsJobFactory;
import uk.gov.gchq.gaffer.hdfs.operation.partitioner.NoPartitioner;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreException;
import java.io.IOException;

public class AccumuloAddElementsFromHdfsJobFactory implements AddElementsFromHdfsJobFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloAddElementsFromHdfsJobFactory.class);

    /**
     * Creates a job with the store specific job initialisation and then applies the operation specific
     * {@link uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.JobInitialiser}.
     *
     * @param operation the add elements from hdfs operation
     * @param store     the store executing the operation
     * @return the created job
     * @throws IOException for IO issues
     */
    @Override
    public Job createJob(final AddElementsFromHdfs operation, final Store store) throws IOException {
        final JobConf jobConf = createJobConf(operation, store);
        final Job job = Job.getInstance(jobConf);
        setupJob(job, operation, store);

        // Apply Operation Specific Job Configuration
        if (null != operation.getJobInitialiser()) {
            operation.getJobInitialiser().initialiseJob(job, operation, store);
        }

        return job;
    }

    @Override
    public void prepareStore(final Store store) throws StoreException {
        TableUtils.ensureTableExists(((AccumuloStore) store));
    }

    protected JobConf createJobConf(final AddElementsFromHdfs operation, final Store store) throws IOException {
        final JobConf jobConf = new JobConf(new Configuration());

        LOGGER.info("Setting up job conf");
        jobConf.set(SCHEMA, new String(store.getSchema().toCompactJson(), CommonConstants.UTF_8));
        LOGGER.info("Added {} {} to job conf", SCHEMA, new String(store.getSchema().toCompactJson(), CommonConstants.UTF_8));
        jobConf.set(MAPPER_GENERATOR, operation.getMapperGeneratorClassName());
        LOGGER.info("Added {} of {} to job conf", MAPPER_GENERATOR, operation.getMapperGeneratorClassName());
        jobConf.set(VALIDATE, String.valueOf(operation.isValidate()));
        LOGGER.info("Added {} option of {} to job conf", VALIDATE, operation.isValidate());
        Integer numTasks = operation.getNumMapTasks();
        if (null != numTasks) {
            jobConf.setNumMapTasks(numTasks);
            LOGGER.info("Set number of map tasks to {} on job conf", numTasks);
        }
        numTasks = operation.getNumReduceTasks();
        if (null != numTasks) {
            jobConf.setNumReduceTasks(numTasks);
            LOGGER.info("Set number of reduce tasks to {} on job conf", numTasks);
        }
        jobConf.set(AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS,
                ((AccumuloStore) store).getKeyPackage().getKeyConverter().getClass().getName());

        return jobConf;
    }

    protected String getJobName(final String mapperGenerator, final String outputPath) {
        return "Ingest HDFS data: Generator=" + mapperGenerator + ", output=" + outputPath;
    }

    protected void setupJob(final Job job, final AddElementsFromHdfs operation, final Store store) throws IOException {
        job.setJarByClass(getClass());
        job.setJobName(getJobName(operation.getMapperGeneratorClassName(), operation.getOutputPath()));

        setupMapper(job);
        setupCombiner(job);
        setupReducer(job);
        setupOutput(job, operation);

        if (!NoPartitioner.class.equals(operation.getPartitioner())) {
            if (null != operation.getPartitioner()) {
                operation.setPartitioner(GafferKeyRangePartitioner.class);
                LOGGER.warn("Partitioner class " + operation.getPartitioner().getName() + " will be replaced with " + GafferKeyRangePartitioner.class.getName());
            }
            setupPartitioner(job, operation, (AccumuloStore) store);
        }
    }

    protected void setupMapper(final Job job) throws IOException {
        job.setMapperClass(AddElementsFromHdfsMapper.class);
        job.setMapOutputKeyClass(Key.class);
        job.setMapOutputValueClass(Value.class);
    }

    protected void setupCombiner(final Job job) throws IOException {
        job.setCombinerClass(AccumuloKeyValueReducer.class);
    }

    protected void setupReducer(final Job job) throws IOException {
        job.setReducerClass(AccumuloKeyValueReducer.class);
        job.setOutputKeyClass(Key.class);
        job.setOutputValueClass(Value.class);
    }

    protected void setupOutput(final Job job, final AddElementsFromHdfs operation) throws IOException {
        job.setOutputFormatClass(AccumuloFileOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(operation.getOutputPath()));
    }

    protected void setupPartitioner(final Job job, final AddElementsFromHdfs operation, final AccumuloStore store)
            throws IOException {
        if (operation.getSplitsFilePath() == null) {
            // Provide a default path if the splits file path is missing
            operation.setSplitsFilePath("");
            LOGGER.warn("HDFS splits file path not set - using the current directory as the default path.");
        }

        if (operation.isUseProvidedSplits()) {
            // Use provided splits file
            setUpPartitionerFromUserProvidedSplitsFile(job, operation);
        } else {
            // User didn't provide a splits file
            setUpPartitionerGenerateSplitsFile(job, operation, store);
        }
    }

    protected void setUpPartitionerGenerateSplitsFile(final Job job, final AddElementsFromHdfs operation,
                                                      final AccumuloStore store) throws IOException {
        final String splitsFilePath = operation.getSplitsFilePath();
        LOGGER.info("Creating splits file in location {} from table {}", splitsFilePath, store.getTableName());
        final int maxReducers = validateValue(operation.getMaxReduceTasks());
        final int minReducers = validateValue(operation.getMinReduceTasks());
        if ((maxReducers != -1 && minReducers != -1)
                && (minReducers > maxReducers)) {
            LOGGER.error("Minimum number of reducers must be less than the maximum number of reducers: minimum was {} "
                    + "maximum was {}", minReducers, maxReducers);
            throw new IOException("Minimum number of reducers must be less than the maximum number of reducers");
        }
        int numSplits;
        try {
            if (maxReducers == -1) {
                numSplits = IngestUtils.createSplitsFile(store.getConnection(), store.getTableName(),
                        FileSystem.get(job.getConfiguration()), new Path(splitsFilePath));
            } else {
                numSplits = IngestUtils.createSplitsFile(store.getConnection(), store.getTableName(),
                        FileSystem.get(job.getConfiguration()), new Path(splitsFilePath), maxReducers - 1);
            }
        } catch (final StoreException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        int numReducers = numSplits + 1;
        LOGGER.info("Number of splits is {}; number of reducers is {}", numSplits, numReducers);
        // If neither min or max are specified then nothing to do; if max specified and min not then already taken care of.
        // If min is specified and the number of reducers is not greater than that then set the appropriate number of
        // subbins.
        if ((minReducers != -1)
                && (numReducers < minReducers)) {
            LOGGER.info("Number of reducers is {} which is less than the specified minimum number of {}", numReducers,
                    minReducers);
            int factor = (minReducers / numReducers) + 1;
            LOGGER.info("Setting number of subbins on GafferKeyRangePartitioner to {}", factor);
            GafferKeyRangePartitioner.setNumSubBins(job, factor);
            numReducers = numReducers * factor;
            LOGGER.info("Number of reducers is {}", numReducers);
        }
        job.setNumReduceTasks(numReducers);
        job.setPartitionerClass(GafferKeyRangePartitioner.class);
        GafferKeyRangePartitioner.setSplitFile(job, splitsFilePath);
    }

    protected void setUpPartitionerFromUserProvidedSplitsFile(final Job job, final AddElementsFromHdfs operation)
            throws IOException {
        final String splitsFilePath = operation.getSplitsFilePath();
        if (validateValue(operation.getMaxReduceTasks()) != -1
                || validateValue(operation.getMinReduceTasks()) != -1) {
            LOGGER.info("Using splits file provided by user {}, ignoring minReduceTasks and maxReduceTasks", splitsFilePath);
        } else {
            LOGGER.info("Using splits file provided by user {}", splitsFilePath);
        }
        final int numSplits = IngestUtils.getNumSplits(FileSystem.get(job.getConfiguration()), new Path(splitsFilePath));
        job.setNumReduceTasks(numSplits + 1);
        job.setPartitionerClass(GafferKeyRangePartitioner.class);
        GafferKeyRangePartitioner.setSplitFile(job, splitsFilePath);
    }

    protected static int validateValue(final Integer value) throws IOException {
        int result = -1;
        if (null != value) {
            result = value;
            if (result < 1) {
                LOGGER.error("Invalid field - must be >=1, got {}", result);
                throw new IOException("Invalid field - must be >=1, got " + result);
            }
        }
        return result;
    }
}
