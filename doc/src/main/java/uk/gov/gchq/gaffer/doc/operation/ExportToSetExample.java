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
package uk.gov.gchq.gaffer.doc.operation;

import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.export.GetExports;
import uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import java.util.Map;

public class ExportToSetExample extends OperationExample {
    public static void main(final String[] args) throws OperationException {
        new ExportToSetExample().run();
    }

    public ExportToSetExample() {
        super(ExportToSet.class);
    }

    @Override
    public void runExamples() {
        simpleExportAndGet();
        simpleExportAndGetWithPagination();
        exportMultipleResultsToSetAndGetAllResults();
    }

    public Iterable<?> simpleExportAndGet() {
        // ---------------------------------------------------------
        final OperationChain<Iterable<?>> opChain = new OperationChain.Builder()
                .first(new GetAllElements())
                .then(new ExportToSet<>())
                .then(new DiscardOutput())
                .then(new GetSetExport())
                .build();
        // ---------------------------------------------------------

        return runExample(opChain, null);
    }

    public Iterable<?> simpleExportAndGetWithPagination() {
        // ---------------------------------------------------------
        final OperationChain<Iterable<?>> opChain = new OperationChain.Builder()
                .first(new GetAllElements())
                .then(new ExportToSet<>())
                .then(new DiscardOutput())
                .then(new GetSetExport.Builder()
                        .start(2)
                        .end(4)
                        .build())
                .build();
        // ---------------------------------------------------------

        return runExample(opChain, null);
    }


    public Map<String, CloseableIterable<?>> exportMultipleResultsToSetAndGetAllResults() {
        // ---------------------------------------------------------
        final OperationChain<Map<String, CloseableIterable<?>>> opChain = new OperationChain.Builder()
                .first(new GetAllElements())
                .then(new ExportToSet.Builder<>()
                        .key("edges")
                        .build())
                .then(new DiscardOutput())
                .then(new GetAllElements())
                .then(new ExportToSet.Builder<>()
                        .key("entities")
                        .build())
                .then(new DiscardOutput())
                .then(new GetExports.Builder()
                        .exports(new GetSetExport.Builder()
                                        .key("edges")
                                        .build(),
                                new GetSetExport.Builder()
                                        .key("entities")
                                        .build())
                        .build())
                .build();
        // ---------------------------------------------------------

        return runExample(opChain, null);
    }
}
