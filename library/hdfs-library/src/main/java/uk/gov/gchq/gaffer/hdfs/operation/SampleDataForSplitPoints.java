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
package uk.gov.gchq.gaffer.hdfs.operation;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Partitioner;
import uk.gov.gchq.gaffer.commonutil.FieldUtil;
import uk.gov.gchq.gaffer.commonutil.Required;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.JobInitialiser;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.MapperGenerator;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.Options;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.tuple.n.Tuple3;
import java.util.List;
import java.util.Map;


/**
 * The <code>SampleDataForSplitPoints</code> operation is for creating a splits file, either for use in a {@link uk.gov.gchq.gaffer.operation.impl.SplitStore} operation or an
 * {@link uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs} operation.
 * This operation requires an input and output path as well as a path to a file to use as the resultingSplitsFile.
 * It order to be generic and deal with any type of input file you also need to provide a
 * {@link MapperGenerator} class name and a
 * {@link uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.JobInitialiser}.
 * <b>NOTE</b> - currently this job has to be run as a hadoop job.
 *
 * @see SampleDataForSplitPoints.Builder
 */
public class SampleDataForSplitPoints implements
        Operation,
        MapReduce,
        Options {

    @Required
    private String splitsFilePath;

    private Integer numSplits;
    private boolean useProvidedSplits;

    private boolean validate = true;
    private float proportionToSample = 0.01f;

    /**
     * Used to generate elements from the Hdfs files.
     * For Avro data see {@link uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.AvroMapperGenerator}.
     * For Text data see {@link uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.TextMapperGenerator}.
     */
    @Required
    private String mapperGeneratorClassName;
    @Required
    private List<String> inputPaths;
    @Required
    private String outputPath;
    @Required
    private JobInitialiser jobInitialiser;

    private Integer numMapTasks;
    private Integer minMapTasks;
    private Integer maxMapTasks;

    private Map<String, String> options;
    private Class<? extends CompressionCodec> compressionCodec = GzipCodec.class;

    @Override
    public ValidationResult validate() {
        final ValidationResult result = Operation.super.validate();
        result.add(FieldUtil.validateRequiredFields(
                new Tuple3<>("proportionToSample must be greater than 0", proportionToSample, new IsMoreThan(0f))
        ));

        return result;
    }

    public SampleDataForSplitPoints() {
        setNumReduceTasks(1);
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(final boolean validate) {
        this.validate = validate;
    }

    public String getMapperGeneratorClassName() {
        return mapperGeneratorClassName;
    }

    @JsonSetter(value = "mapperGeneratorClassName")
    public void setMapperGeneratorClassName(final String mapperGeneratorClassName) {
        this.mapperGeneratorClassName = mapperGeneratorClassName;
    }

    public void setMapperGeneratorClassName(final Class<? extends MapperGenerator> mapperGeneratorClass) {
        this.mapperGeneratorClassName = mapperGeneratorClass.getName();
    }

    public String getSplitsFilePath() {
        return splitsFilePath;
    }

    public void setSplitsFilePath(final String splitsFilePath) {
        this.splitsFilePath = splitsFilePath;
    }

    public Integer getNumSplits() {
        return numSplits;
    }

    public void setNumSplits(final Integer numSplits) {
        this.numSplits = numSplits;
    }

    public float getProportionToSample() {
        return proportionToSample;
    }

    public void setProportionToSample(final float proportionToSample) {
        this.proportionToSample = proportionToSample;
    }

    @Override
    public List<String> getInputPaths() {
        return inputPaths;
    }

    @Override
    public void setInputPaths(final List<String> inputPaths) {
        this.inputPaths = inputPaths;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    @Override
    public void setOutputPath(final String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public JobInitialiser getJobInitialiser() {
        return jobInitialiser;
    }

    @Override
    public void setJobInitialiser(final JobInitialiser jobInitialiser) {
        this.jobInitialiser = jobInitialiser;
    }

    @Override
    public Integer getNumMapTasks() {
        return numMapTasks;
    }

    @Override
    public void setNumMapTasks(final Integer numMapTasks) {
        this.numMapTasks = numMapTasks;
    }

    @Override
    public Integer getMinMapTasks() {
        return minMapTasks;
    }

    @Override
    public void setMinMapTasks(final Integer minMapTasks) {
        this.minMapTasks = minMapTasks;
    }

    @Override
    public Integer getMaxMapTasks() {
        return maxMapTasks;
    }

    @Override
    public void setMaxMapTasks(final Integer maxMapTasks) {
        this.maxMapTasks = maxMapTasks;
    }

    @Override
    public Integer getNumReduceTasks() {
        return 1;
    }

    @Override
    public void setNumReduceTasks(final Integer numReduceTasks) {
        if (null != numReduceTasks && 1 != numReduceTasks) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " requires the number of reducers to be 1");
        }
    }

    @Override
    public Integer getMinReduceTasks() {
        return 1;
    }

    @Override
    public void setMinReduceTasks(final Integer minReduceTasks) {
        if (null != minReduceTasks && 1 != minReduceTasks) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " requires the number of reducers to be 1");
        }
    }

    @Override
    public Integer getMaxReduceTasks() {
        return 1;
    }

    @Override
    public void setMaxReduceTasks(final Integer maxReduceTasks) {
        if (null != maxReduceTasks && 1 != maxReduceTasks) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " requires the number of reducers to be 1");
        }
    }

    @Override
    public boolean isUseProvidedSplits() {
        return useProvidedSplits;
    }

    @Override
    public void setUseProvidedSplits(final boolean useProvidedSplits) {
        this.useProvidedSplits = useProvidedSplits;
    }

    @Override
    public Class<? extends Partitioner> getPartitioner() {
        return null;
    }

    @Override
    public void setPartitioner(final Class<? extends Partitioner> partitioner) {
        throw new IllegalArgumentException(getClass().getSimpleName() + " is not able to set its own partitioner");
    }

    public Class<? extends CompressionCodec> getCompressionCodec() {
        return compressionCodec;
    }

    public void setCompressionCodec(final Class<? extends CompressionCodec> compressionCodec) {
        this.compressionCodec = compressionCodec;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public void setOptions(final Map<String, String> options) {
        this.options = options;
    }


    public static class Builder extends Operation.BaseBuilder<SampleDataForSplitPoints, Builder>
            implements MapReduce.Builder<SampleDataForSplitPoints, Builder>,
            Options.Builder<SampleDataForSplitPoints, Builder> {
        public Builder() {
            super(new SampleDataForSplitPoints());
        }

        public Builder validate(final boolean validate) {
            _getOp().setValidate(validate);
            return _self();
        }

        public Builder mapperGenerator(final Class<? extends MapperGenerator> mapperGeneratorClass) {
            _getOp().setMapperGeneratorClassName(mapperGeneratorClass);
            return _self();
        }

        public Builder proportionToSample(final float proportionToSample) {
            _getOp().setProportionToSample(proportionToSample);
            return _self();
        }

        public Builder compressionCodec(final Class<? extends CompressionCodec> compressionCodec) {
            _getOp().setCompressionCodec(compressionCodec);
            return _self();
        }

        public Builder numSplits(final Integer numSplits) {
            _getOp().setNumSplits(numSplits);
            return _self();
        }
    }
}
