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
package uk.gov.gchq.gaffer.flink.operation.handler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GafferQueue<T> implements Iterable<T> {
    private final ConcurrentLinkedQueue<T> queue;
    private boolean iteratorAvailable = true;

    public GafferQueue(final ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        if (!iteratorAvailable) {
            throw new IllegalArgumentException("This iterable can only be iterated over once.");
        }

        iteratorAvailable = false;
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                return queue.poll();
            }
        };
    }

    /**
     * Warning - this will convert the entire queue to an array to check if the
     * items are equal so use it with with caution.
     *
     * @param obj the object to compare
     * @return true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final GafferQueue gafferQueue = (GafferQueue) obj;

        return new EqualsBuilder()
                .append(queue.toArray(), gafferQueue.queue.toArray())
                .append(iteratorAvailable, gafferQueue.iteratorAvailable)
                .isEquals();
    }

    /**
     * Warning - this will convert the entire queue to an array to get a hashcode
     * so use it with caution.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37)
                .append(queue.toArray())
                .append(iteratorAvailable)
                .toHashCode();
    }
}
