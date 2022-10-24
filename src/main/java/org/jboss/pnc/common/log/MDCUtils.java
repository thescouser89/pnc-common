/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014-2020 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.common.log;

import org.jboss.pnc.api.constants.MDCHeaderKeys;
import org.jboss.pnc.common.Strings;
import org.jboss.pnc.common.concurrent.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class MDCUtils {
    public static final Map<String, String> HEADER_KEY_MAPPING = new HashMap<>();
    static {
        for (MDCHeaderKeys headerKey : MDCHeaderKeys.values()) {
            HEADER_KEY_MAPPING.put(headerKey.getMdcKey(), headerKey.getHeaderName());
        }
    }

    /**
     * Will replace MDC content with content from headers in the request context. If MDCHeaderKeys.REQUEST_CONTEXT is
     * not provided in headers, it will generate new one.
     */
    public static void setMDCFromRequestContext(ContainerRequestContext requestContext) {
        setMDCFromHeaders(requestContext::getHeaderString);
    }

    /**
     * Will replace MDC content with content from headers. If MDCHeaderKeys.REQUEST_CONTEXT is not provided in headers,
     * it will generate new one.
     */
    public static void setMDCFromHeaders(Function<String, String> headers) {
        Map<String, String> mdcContext = new HashMap<>();

        for (MDCHeaderKeys key : MDCHeaderKeys.values()) {
            copyToMap(mdcContext, key, headers);
        }
        copyToMap(mdcContext, MDCHeaderKeys.REQUEST_CONTEXT, headers, () -> Sequence.nextId().toString());

        MDC.setContextMap(mdcContext);
    }

    public static Map<String, String> getHeadersFromMDC() {
        Map<String, String> headers = new HashMap<>();

        for (MDCHeaderKeys key : MDCHeaderKeys.values()) {
            copyToMap(headers, key, MDC::get);
        }
        return headers;
    }

    private static void copyToMap(
            Map<String, String> map,
            MDCHeaderKeys headerKeys,
            Function<String, String> valueGetter) {
        String value = valueGetter.apply(headerKeys.getHeaderName());
        if (!Strings.isEmpty(value)) {
            map.put(headerKeys.getMdcKey(), value);
        }
    }

    private static void copyToMap(
            Map<String, String> map,
            MDCHeaderKeys headerKeys,
            Function<String, String> valueGetter,
            Supplier<String> defaultSupplier) {
        String value = valueGetter.apply(headerKeys.getHeaderName());
        if (Strings.isEmpty(value)) {
            map.put(headerKeys.getMdcKey(), defaultSupplier.get());
        } else {
            map.put(headerKeys.getMdcKey(), value);
        }
    }
}
