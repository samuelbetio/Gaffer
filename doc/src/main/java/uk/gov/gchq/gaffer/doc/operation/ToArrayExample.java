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

import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToArray;

public class ToArrayExample extends OperationExample {
    public ToArrayExample() {
        super(ToArray.class, "Note - conversion into an array is done in memory, so it is not advised for a large number of results.");
    }

    public static void main(final String[] args) throws OperationException {
        new ToArrayExample().run();
    }

    @Override
    public void runExamples() {
        toArrayExample();
    }

    public Element[] toArrayExample() {
        // ---------------------------------------------------------
        final OperationChain<? extends Element[]> opChain = new OperationChain.Builder()
                .first(new GetElements.Builder()
                        .input(new EntitySeed(1), new EntitySeed(2))
                        .build())
                .then(new ToArray<>())
                .build();
        // ---------------------------------------------------------

        return runExample(opChain, null);
    }
}
