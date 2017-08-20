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

package uk.gov.gchq.gaffer.data.element.function;

import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.impl.predicate.Not;
import uk.gov.gchq.koryphe.impl.predicate.Or;
import uk.gov.gchq.koryphe.tuple.predicate.KoryphePredicate2;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;
import java.util.List;
import java.util.function.Predicate;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ElementFilterTest {

    @Test
    public void shouldSerialiseAndDeserialiseIdentifiers() throws SerialisationException {
        // Given
        final ElementFilter filter = new ElementFilter();

        final JSONSerialiser serialiser = new JSONSerialiser();

        // When
        final byte[] serialisedElement = serialiser.serialise(filter);
        final ElementFilter deserialisedElement = serialiser.deserialise(serialisedElement, filter.getClass());

        // Then
        assertEquals(filter, deserialisedElement);
    }

    public static class MockPredicate implements Predicate<Element> {

        @Override
        public boolean test(final Element element) {
            return true;
        }
    }

    @Test
    public void shouldTestElementOnPredicate2() {
        // Given
        final ElementFilter filter = new ElementFilter.Builder()
                .select("prop1", "prop2")
                .execute(new KoryphePredicate2<String, String>() {
                    @Override
                    public boolean test(final String o, final String o2) {
                        return "value".equals(o) && "value2".equals(o2);
                    }
                })
                .build();

        final Entity element1 = new Entity.Builder()
                .property("prop1", "value")
                .property("prop2", "value2")
                .build();

        final Entity element2 = new Entity.Builder()
                .property("prop1", "unknown")
                .property("prop2", "value2")
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);

        // Then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnInlinePredicate() {
        // Given
        final ElementFilter filter = new ElementFilter.Builder()
                .select("prop1")
                .execute("value"::equals)
                .build();

        final Entity element1 = new Entity.Builder()
                .property("prop1", "value")
                .build();

        final Entity element2 = new Entity.Builder()
                .property("prop1", "unknown")
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);

        // Then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnLambdaPredicate() {
        // Given
        final Predicate<Object> predicate = p -> null == p || String.class.isAssignableFrom(p.getClass());
        final ElementFilter filter = new ElementFilter.Builder()
                .select("prop1")
                .execute(predicate)
                .build();

        final Entity element1 = new Entity.Builder()
                .property("prop1", "value")
                .build();

        final Entity element2 = new Entity.Builder()
                .property("prop1", 1)
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);

        // Then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnComplexLambdaPredicate() {
        // Given
        final Predicate<Object> predicate1 = p -> Integer.class.isAssignableFrom(p.getClass());
        final Predicate<Object> predicate2 = "value"::equals;
        final ElementFilter filter = new ElementFilter.Builder()
                .select("prop1")
                .execute(predicate1.negate().and(predicate2))
                .build();

        final Entity element1 = new Entity.Builder()
                .property("prop1", "value")
                .build();

        final Entity element2 = new Entity.Builder()
                .property("prop1", 1)
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);

        // Then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void shouldBuildFilter() {
        // Given
        final String property1 = "property 1";
        final String property2a = "property 2a";
        final String property2b = "property 2b";
        final String property3 = "property 3";

        final Predicate func1 = mock(Predicate.class);
        final Predicate func2 = mock(Predicate.class);
        final Predicate func3 = mock(Predicate.class);

        // When - check you can build the selection/function in any order,
        // although normally it will be done - select then execute.
        final ElementFilter filter = new ElementFilter.Builder()
                .select(property1)
                .execute(func1)
                .select(property2a, property2b)
                .execute(func2)
                .select(property3)
                .execute(func3)
                .build();

        // Then
        int i = 0;
        TupleAdaptedPredicate<String, ?> adaptedFunction = filter.getComponents().get(i++);
        assertEquals(1, adaptedFunction.getSelection().length);
        assertEquals(property1, adaptedFunction.getSelection()[0]);
        assertSame(func1, adaptedFunction.getPredicate());

        adaptedFunction = filter.getComponents().get(i++);
        assertEquals(2, adaptedFunction.getSelection().length);
        assertEquals(property2a, adaptedFunction.getSelection()[0]);
        assertEquals(property2b, adaptedFunction.getSelection()[1]);
        assertSame(func2, adaptedFunction.getPredicate());

        adaptedFunction = filter.getComponents().get(i++);
        assertSame(func3, adaptedFunction.getPredicate());
        assertEquals(1, adaptedFunction.getSelection().length);
        assertEquals(property3, adaptedFunction.getSelection()[0]);

        assertEquals(i, filter.getComponents().size());
    }

    @Test
    public void shouldExecuteOrPredicates() {
        final ElementFilter filter = new ElementFilter.Builder()
                .select(TestPropertyNames.PROP_1, TestPropertyNames.PROP_2)
                .execute(new Or.Builder<>()
                        .select(0)
                        .execute(new IsMoreThan(2))
                        .select(1)
                        .execute(new IsEqual("some value"))
                        .build())
                .build();

        final Entity element1 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 3)
                .property(TestPropertyNames.PROP_2, "some value")
                .build();

        final Entity element2 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 1)
                .property(TestPropertyNames.PROP_2, "some value")
                .build();

        final Entity element3 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 3)
                .property(TestPropertyNames.PROP_2, "some invalid value")
                .build();

        final Entity element4 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 1)
                .property(TestPropertyNames.PROP_2, "some invalid value")
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        final boolean result3 = filter.test(element3);
        final boolean result4 = filter.test(element4);

        // Then
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        assertFalse(result4);
    }

    @Test
    public void shouldExecuteNotPredicates() {
        final ElementFilter filter = new ElementFilter.Builder()
                .select(TestPropertyNames.PROP_1, TestPropertyNames.PROP_2)
                .execute(new Not<>(new Or.Builder<>()
                        .select(0)
                        .execute(new IsMoreThan(2))
                        .select(1)
                        .execute(new IsEqual("some value"))
                        .build()))
                .build();

        final Entity element1 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 3)
                .property(TestPropertyNames.PROP_2, "some value")
                .build();

        final Entity element2 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 1)
                .property(TestPropertyNames.PROP_2, "some value")
                .build();

        final Entity element3 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 3)
                .property(TestPropertyNames.PROP_2, "some invalid value")
                .build();

        final Entity element4 = new Entity.Builder()
                .property(TestPropertyNames.PROP_1, 1)
                .property(TestPropertyNames.PROP_2, "some invalid value")
                .build();

        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        final boolean result3 = filter.test(element3);
        final boolean result4 = filter.test(element4);

        // Then
        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
        assertTrue(result4);
    }

    @Test
    public void shouldReturnUnmodifiableComponentsWhenLocked() {
        // Given
        final ElementFilter filter = new ElementFilter();

        // When
        filter.lock();
        final List<TupleAdaptedPredicate<String, ?>> components = filter.getComponents();

        // Then
        try {
            components.add(null);
            fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void shouldReturnModifiableComponentsWhenNotLocked() {
        // Given
        final ElementFilter filter = new ElementFilter();

        // When
        final List<TupleAdaptedPredicate<String, ?>> components = filter.getComponents();

        // Then - no exceptions
        components.add(null);
    }
}
