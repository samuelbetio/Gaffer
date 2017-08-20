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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.ElementTuple;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicateComposite;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ElementFilter extends TupleAdaptedPredicateComposite<String> {
    private final ElementTuple elementTuple = new ElementTuple();
    private boolean readOnly;

    public boolean test(final Element element) {
        elementTuple.setElement(element);
        return test(elementTuple);
    }

    @Override
    public List<TupleAdaptedPredicate<String, ?>> getComponents() {
        if (readOnly) {
            return Collections.unmodifiableList(super.getComponents());
        }

        return super.getComponents();
    }

    public void lock() {
        readOnly = true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ElementFilter that = (ElementFilter) obj;

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(elementTuple, that.elementTuple)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(elementTuple)
                .toHashCode();
    }

    public static class Builder {
        private final ElementFilter filter;

        public Builder() {
            this(new ElementFilter());
        }

        private Builder(final ElementFilter filter) {
            this.filter = filter;
        }

        public SelectedBuilder select(final String... selection) {
            final TupleAdaptedPredicate<String, Object> current = new TupleAdaptedPredicate<>();
            current.setSelection(selection);
            return new SelectedBuilder(filter, current);
        }

        public ElementFilter build() {
            return filter;
        }
    }

    public static final class SelectedBuilder {
        private final ElementFilter filter;
        private final TupleAdaptedPredicate<String, Object> current;

        private SelectedBuilder(final ElementFilter filter, final TupleAdaptedPredicate<String, Object> current) {
            this.filter = filter;
            this.current = current;
        }

        public Builder execute(final Predicate function) {
            current.setPredicate(function);
            filter.getComponents().add(current);
            return new Builder(filter);
        }
    }
}
