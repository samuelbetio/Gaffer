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
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters.IncludeIncomingOutgoingType;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;

public class GetAdjacentIdsExample extends OperationExample {
    public static void main(final String[] args) {
        new GetAdjacentIdsExample().run();
    }

    public GetAdjacentIdsExample() {
        super(GetAdjacentIds.class);
    }

    @Override
    public void runExamples() {
        getAdjacentIdsFromVertex2();
        getAdjacentIdsAlongOutboundEdgesFromVertex2();
        getAdjacentIdsAlongOutboundEdgesFromVertex2WithCountGreaterThan1();
    }

    public CloseableIterable<? extends EntityId> getAdjacentIdsFromVertex2() {
        // ---------------------------------------------------------
        final GetAdjacentIds operation = new GetAdjacentIds.Builder()
                .input(new EntitySeed(2))
                .build();
        // ---------------------------------------------------------

        return runExample(operation, null);
    }

    public CloseableIterable<? extends EntityId> getAdjacentIdsAlongOutboundEdgesFromVertex2() {
        // ---------------------------------------------------------
        final GetAdjacentIds operation = new GetAdjacentIds.Builder()
                .input(new EntitySeed(2))
                .inOutType(IncludeIncomingOutgoingType.OUTGOING)
                .build();
        // ---------------------------------------------------------

        return runExample(operation, null);
    }

    public CloseableIterable<? extends EntityId> getAdjacentIdsAlongOutboundEdgesFromVertex2WithCountGreaterThan1() {
        // ---------------------------------------------------------
        final GetAdjacentIds operation = new GetAdjacentIds.Builder()
                .input(new EntitySeed(2))
                .inOutType(IncludeIncomingOutgoingType.OUTGOING)
                .view(new View.Builder()
                        .entity("entity", new ViewElementDefinition.Builder()
                                .preAggregationFilter(new ElementFilter.Builder()
                                        .select("count")
                                        .execute(new IsMoreThan(1))
                                        .build())
                                .build())
                        .edge("edge", new ViewElementDefinition.Builder()
                                .preAggregationFilter(new ElementFilter.Builder()
                                        .select("count")
                                        .execute(new IsMoreThan(1))
                                        .build())
                                .build())
                        .build())
                .build();
        // ---------------------------------------------------------

        return runExample(operation, null);
    }
}
