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

package uk.gov.gchq.gaffer.rest;

import uk.gov.gchq.gaffer.commonutil.DebugUtil;
import uk.gov.gchq.gaffer.graph.hook.OperationChainLimiter;
import uk.gov.gchq.gaffer.rest.factory.DefaultGraphFactory;
import uk.gov.gchq.gaffer.rest.factory.UnknownUserFactory;

/**
 * System property keys and default values.
 */
public abstract class SystemProperty {
    // KEYS
    public static final String GRAPH_LIBRARY_CLASS = "gaffer.graph.library.class";
    public static final String GRAPH_LIBRARY_CONFIG = "gaffer.graph.library.config";
    public static final String GRAPH_ID = "gaffer.graph.id";
    public static final String SCHEMA_PATHS = "gaffer.schemas";
    public static final String STORE_PROPERTIES_PATH = "gaffer.storeProperties";
    public static final String BASE_URL = "gaffer.rest-api.basePath";
    public static final String VERSION = "gaffer.rest-api.version";
    public static final String GRAPH_FACTORY_CLASS = "gaffer.graph.factory.class";
    public static final String USER_FACTORY_CLASS = "gaffer.user.factory.class";
    public static final String SERVICES_PACKAGE_PREFIX = "gaffer.rest-api.resourcePackage";
    public static final String PACKAGE_PREFIXES = "gaffer.package.prefixes";
    public static final String OP_AUTHS_PATH = "gaffer.operation.auths.path";
    public static final String ADD_OPERATIONS_TO_CHAIN_PATH = "gaffer.graph.hook.add.operations.path";
    public static final String OPERATION_SCORES_FILE = OperationChainLimiter.OPERATION_SCORES_FILE_KEY;
    public static final String AUTH_SCORES_FILE = OperationChainLimiter.AUTH_SCORES_FILE_KEY;
    public static final String ENABLE_CHAIN_LIMITER = "gaffer.chain.limiter.enable";
    public static final String REST_DEBUG = DebugUtil.DEBUG;

    // DEFAULTS
    /**
     * Comma separated list of package prefixes to search for Functions and {@link uk.gov.gchq.gaffer.operation.Operation}s.
     */
    public static final String PACKAGE_PREFIXES_DEFAULT = "uk.gov.gchq";
    public static final String SERVICES_PACKAGE_PREFIX_DEFAULT = "uk.gov.gchq.gaffer.rest";
    public static final String BASE_URL_DEFAULT = "rest/v1";
    public static final String CORE_VERSION = "1.0.0";
    public static final String GRAPH_FACTORY_CLASS_DEFAULT = DefaultGraphFactory.class.getName();
    public static final String USER_FACTORY_CLASS_DEFAULT = UnknownUserFactory.class.getName();
    public static final String REST_DEBUG_DEFAULT = DebugUtil.DEBUG_DEFAULT;

    private SystemProperty() {
        // Private constructor to prevent instantiation.
    }
}
