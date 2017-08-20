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

package uk.gov.gchq.gaffer.commonutil;

import java.util.Collection;
import java.util.TreeSet;

public final class CollectionUtil {

    private CollectionUtil() {
        // this class should not be instantiated - it contains only util methods and constants.
    }


    public static <T> TreeSet<T> treeSet(final T item) {
        final TreeSet<T> treeSet = new TreeSet<>();
        if (null != item) {
            treeSet.add(item);
        }

        return treeSet;
    }

    public static <T> TreeSet<T> treeSet(final T[] items) {
        final TreeSet<T> treeSet = new TreeSet<>();
        if (null != items) {
            for (final T item : items) {
                if (null != item) {
                    treeSet.add(item);
                }
            }
        }

        return treeSet;
    }

    public static boolean containsAny(final Collection collection, final Object[] objects) {
        boolean result = false;
        if (null != collection && null != objects) {
            for (final Object object : objects) {
                if (collection.contains(object)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public static boolean anyMissing(final Collection collection, final Object[] objects) {
        boolean result = false;
        if (null == collection || collection.isEmpty()) {
            if (null != objects && objects.length > 0) {
                result = true;
            }
        } else if (null != objects) {
            for (final Object object : objects) {
                if (!collection.contains(object)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}
