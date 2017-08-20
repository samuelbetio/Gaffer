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

import uk.gov.gchq.koryphe.impl.predicate.IsA;

public class IsAExample extends PredicateExample {
    public static void main(final String[] args) {
        new IsAExample().run();
    }

    public IsAExample() {
        super(IsA.class);
    }

    @Override
    public void runExamples() {
        isAString();
        isANumber();
    }

    public void isAString() {
        // ---------------------------------------------------------
        final IsA function = new IsA(String.class);
        // ---------------------------------------------------------

        runExample(function,
                null,
                1, 2.5, "abc");
    }

    public void isANumber() {
        // ---------------------------------------------------------
        final IsA function = new IsA(Number.class);
        // ---------------------------------------------------------

        runExample(function,
                null,
                1, 2.5, "abc");
    }
}
