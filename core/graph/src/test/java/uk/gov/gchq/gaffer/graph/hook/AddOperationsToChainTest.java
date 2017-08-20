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

package uk.gov.gchq.gaffer.graph.hook;

import org.junit.Test;
import sun.misc.IOUtils;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.SplitStore;
import uk.gov.gchq.gaffer.operation.impl.Validate;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddOperationsToChainTest {
    private static final String ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH = "addOperationsToChain.json";

    @Test
    public void shouldAddAllOperationsWithNoAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain addOperationsToChain = new AddOperationsToChain(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStore();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit<>();

        final List expectedOperations = new ArrayList<Operation>();
        expectedOperations.add(discardOutput);
        expectedOperations.add(splitStore);
        expectedOperations.add(validate);
        expectedOperations.add(getAdjacentIds);
        expectedOperations.add(count);
        expectedOperations.add(discardOutput);
        expectedOperations.add(countGroups);
        expectedOperations.add(getElements);
        expectedOperations.add(getAllElements);
        expectedOperations.add(limit);
        expectedOperations.add(validate);
        expectedOperations.add(count);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        addOperationsToChain.preExecute(opChain, new User());

        // Then
        for (int i = 0; i < opChain.getOperations().size(); i++) {
            assertTrue(expectedOperations.get(i).getClass().getName().contains(opChain.getOperations().get(i).getClass().getSimpleName()));
        }
    }

    @Test
    public void shouldAddAllOperationsWithFirstAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain addOperationsToChain = new AddOperationsToChain(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        User user = new User.Builder().opAuths("auth1", "auth2").build();

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStore();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();

        final List expectedOperations = new ArrayList<Operation>();
        expectedOperations.add(discardOutput);
        expectedOperations.add(getAdjacentIds);
        expectedOperations.add(getElements);
        expectedOperations.add(getAllElements);
        expectedOperations.add(splitStore);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        addOperationsToChain.preExecute(opChain, user);

        // Then
        for (int i = 0; i < opChain.getOperations().size(); i++) {
            assertTrue(expectedOperations.get(i).getClass().getName().contains(opChain.getOperations().get(i).getClass().getSimpleName()));
        }
    }

    @Test
    public void shouldAddAllOperationsWithSecondAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain addOperationsToChain = new AddOperationsToChain(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        User user = new User.Builder().opAuths("auth2").build();

        Operation splitStore = new SplitStore();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();

        final List expectedOperations = new ArrayList<Operation>();
        expectedOperations.add(validate);
        expectedOperations.add(getAdjacentIds);
        expectedOperations.add(countGroups);
        expectedOperations.add(getElements);
        expectedOperations.add(getAllElements);
        expectedOperations.add(splitStore);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        addOperationsToChain.preExecute(opChain, user);

        // Then
        for (int i = 0; i < opChain.getOperations().size(); i++) {
            assertTrue(expectedOperations.get(i).getClass().getName().contains(opChain.getOperations().get(i).getClass().getSimpleName()));
        }
    }

    @Test
    public void shouldAddAllOperationsGivenJson() throws IOException {
        // Given
        final byte[] bytes;
        try (final InputStream inputStream = StreamUtil.openStream(getClass(), ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH)) {
            bytes = IOUtils.readFully(inputStream, inputStream.available(), true);
        }
        final AddOperationsToChain addOperationsToChain = new AddOperationsToChain(bytes);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStore();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit<>();

        final List expectedOperations = new ArrayList<Operation>();
        expectedOperations.add(discardOutput);
        expectedOperations.add(splitStore);
        expectedOperations.add(validate);
        expectedOperations.add(getAdjacentIds);
        expectedOperations.add(count);
        expectedOperations.add(discardOutput);
        expectedOperations.add(countGroups);
        expectedOperations.add(getElements);
        expectedOperations.add(getAllElements);
        expectedOperations.add(limit);
        expectedOperations.add(validate);
        expectedOperations.add(count);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        addOperationsToChain.preExecute(opChain, new User());

        // Then
        for (int i = 0; i < opChain.getOperations().size(); i++) {
            assertTrue(expectedOperations.get(i).getClass().getName().contains(opChain.getOperations().get(i).getClass().getSimpleName()));
        }
    }

    @Test
    public void shouldThrowExceptionWhenFileDoesntExist() throws IOException {
        // Given
        final String falseAddOperationsPath = "/this/path/doesnt/exist";

        // When / Then
        try {
            new AddOperationsToChain(falseAddOperationsPath);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullExtraOperation() throws IOException {
        // Given
        final String nullTestJson = "{\"start\":[{\"class\": null}]}";

        //When / Then
        try {
            new AddOperationsToChain(nullTestJson.getBytes());
            fail("Exception expected");
        } catch (SchemaException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingEmptyExtraOperation() throws IOException {
        // Given
        final String emptyTestJson = "{\"start\":[{\"class\": \"\"}]}";

        //When / Then
        try {
            new AddOperationsToChain(emptyTestJson.getBytes());
            fail("Exception expected");
        } catch (SchemaException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingFalseExtraOperation() throws IOException {
        // Given
        final String falseOperationTestJson = "{\"start\":[{\"class\": \"this.Operation.Doesnt.Exist\"}]}";

        //When / Then
        try {
            new AddOperationsToChain(falseOperationTestJson.getBytes());
            fail("Exception expected");
        } catch (SchemaException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldClearListWhenAddingOperations() throws IOException {
        //Given
        final AddOperationsToChain addOperationsToChain = new AddOperationsToChain(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        addOperationsToChain.setBefore(null);
        addOperationsToChain.setAfter(null);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStore();
        Operation count = new Count<>();
        Operation getElements = new GetElements();

        final List expectedOperations = new ArrayList<Operation>();
        expectedOperations.add(discardOutput);
        expectedOperations.add(splitStore);
        expectedOperations.add(getElements);
        expectedOperations.add(count);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getElements)
                .build();

        // When
        addOperationsToChain.preExecute(opChain, new User());

        // Then
        for (int i = 0; i < opChain.getOperations().size(); i++) {
            assertTrue(expectedOperations.get(i).getClass().getName().contains(opChain.getOperations().get(i).getClass().getSimpleName()));
        }

    }

    @Test
    public void shouldReturnClonedOperations() throws IOException {
        // Given
        final AddOperationsToChain addOperationsToChain = new AddOperationsToChain(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        // When / Then
        assertClonedOperations(addOperationsToChain.getStart(), addOperationsToChain.getStart());
        assertClonedOperations(addOperationsToChain.getBefore(), addOperationsToChain.getBefore());
        assertClonedOperations(addOperationsToChain.getAfter(), addOperationsToChain.getAfter());
        assertClonedOperations(addOperationsToChain.getEnd(), addOperationsToChain.getEnd());
    }

    public void assertClonedOperations(final Map<String, List<Operation>> after1, final Map<String, List<Operation>> after2) {
        for (Map.Entry<String, List<Operation>> entry1 : after1.entrySet()) {
            final List<Operation> ops1 = entry1.getValue();
            final List<Operation> ops2 = after2.get(entry1.getKey());
            assertClonedOperations(ops1, ops2);
        }
    }

    public void assertClonedOperations(final List<Operation> ops1, final List<Operation> ops2) {
        assertEquals(ops1.size(), ops2.size());
        for (int i = 0; i < ops1.size(); i++) {
            assertEquals(ops1.get(i).getClass(), ops2.get(i).getClass());
            assertNotSame(ops1.get(i), ops2.get(i));
        }
    }
}
