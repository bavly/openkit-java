/**
 * Copyright 2018-2019 Dynatrace LLC
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

package com.dynatrace.openkit.providers;

import com.dynatrace.openkit.api.Logger;
import com.dynatrace.openkit.core.configuration.HTTPClientConfiguration;
import com.dynatrace.openkit.protocol.HTTPClient;

/**
 * Implementation of an HTTPClientProvider which creates a HTTP client for executing status check and beacon send requests.
 */
public class DefaultHTTPClientProvider implements HTTPClientProvider {

    private final Logger logger;

    public DefaultHTTPClientProvider(Logger logger) {
        this.logger = logger;
    }

    @Override
    public HTTPClient createClient(HTTPClientConfiguration configuration) {
        return new HTTPClient(logger, configuration);
    }

}
