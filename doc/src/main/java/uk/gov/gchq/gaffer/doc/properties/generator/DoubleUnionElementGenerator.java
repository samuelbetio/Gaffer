/*
 * Copyright 2017 Crown Copyright
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
package uk.gov.gchq.gaffer.doc.properties.generator;

import com.yahoo.sketches.quantiles.DoublesUnion;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.generator.OneToManyElementGenerator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DoubleUnionElementGenerator implements OneToManyElementGenerator<String> {
    // Fix the seed so that the results are consistent
    private static final Random RANDOM = new Random(123456789L);

    @Override
    public Iterable<Element> _apply(final String line) {
        final Set<Element> elements = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final DoublesUnion doublesUnion = DoublesUnion.builder().build();
            doublesUnion.update(RANDOM.nextGaussian());
            final Edge edge = new Edge.Builder()
                    .group("red")
                    .source("A")
                    .dest("B")
                    .property("doublesUnion", doublesUnion)
                    .build();
            elements.add(edge);
        }
        return elements;
    }
}
