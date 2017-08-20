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
package uk.gov.gchq.gaffer.doc.predicate;

import uk.gov.gchq.koryphe.impl.predicate.IsIn;

public class IsInExample extends PredicateExample {
    public static void main(final String[] args) {
        new IsInExample().run();
    }

    public IsInExample() {
        super(IsIn.class);
    }

    @Override
    public void runExamples() {
        isInSet();
    }

    public void isInSet() {
        // ---------------------------------------------------------
        final IsIn function = new IsIn(5, 5L, "5", '5');
        // ---------------------------------------------------------

        runExample(function,
                null,
                5, 5L, "5", '5', 1, 1L, "1", '1');
    }
}
