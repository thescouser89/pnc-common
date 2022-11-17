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
package org.jboss.pnc.common.otel;

import static com.redhat.resilience.otel.internal.OtelContextUtil.extractTraceState;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;

import org.jboss.pnc.api.constants.MDCHeaderKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.resilience.otel.internal.OtelContextUtil;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.TraceStateBuilder;

public class OtelUtils {

    private static final Logger logger = LoggerFactory.getLogger(OtelUtils.class);

    private static final String TRACEPARENT_VERSION_00 = "00";
    private static final Pattern MEMBER_LIST_PATTERN = Pattern.compile("\\s*,\\s*");

    public static SpanContext extractSpanContextFromHeaders(ContainerRequestContext requestContext) {

        SpanContext extractedSpanContext = null;

        // Extract the "traceparent" header
        String traceparent = requestContext.getHeaderString(MDCHeaderKeys.TRACEPARENT.getHeaderName());
        if (traceparent != null) {
            extractedSpanContext = OtelContextUtil.extractContextFromTraceParent(traceparent);
        }

        if (extractedSpanContext == null) {
            // The "traceparent" header was null, fall back to "trace-id" and "span-id" headers
            String traceId = requestContext.getHeaderString(MDCHeaderKeys.TRACE_ID.getHeaderName());
            String parentSpanId = requestContext.getHeaderString(MDCHeaderKeys.SPAN_ID.getHeaderName());
            if (parentSpanId == null) {
                // Some vendors use parent-id instead (https://www.w3.org/TR/trace-context/#parent-id)
                parentSpanId = requestContext.getHeaderString(MDCHeaderKeys.PARENT_ID.getHeaderName());
            }
            if (traceId != null && !traceId.isEmpty() && parentSpanId != null && !parentSpanId.isEmpty()) {
                extractedSpanContext = SpanContext.createFromRemoteParent(
                        traceId,
                        parentSpanId,
                        TraceFlags.getDefault(),
                        TraceState.getDefault());
            }
        }

        if (extractedSpanContext == null) {
            return SpanContext.getInvalid();
        } else if (!extractedSpanContext.isValid()) {
            return extractedSpanContext;
        }

        String traceStateValue = requestContext.getHeaderString(MDCHeaderKeys.TRACESTATE.getHeaderName());
        if (traceStateValue == null || traceStateValue.isEmpty()) {
            return extractedSpanContext;
        }

        try {
            TraceState traceState = extractTraceState(traceStateValue);
            return SpanContext.createFromRemoteParent(
                    extractedSpanContext.getTraceId(),
                    extractedSpanContext.getSpanId(),
                    extractedSpanContext.getTraceFlags(),
                    traceState);
        } catch (IllegalArgumentException e) {
            logger.debug("Unparseable tracestate header. Returning span context without state.");
            return extractedSpanContext;
        }
    }

    public static Map<String, String> createTraceParentHeaderFromSpan(SpanContext spanContext) {
        return Collections.singletonMap(
                MDCHeaderKeys.TRACEPARENT.getHeaderName(),
                String.format(
                        "%s-%s-%s-%s",
                        TRACEPARENT_VERSION_00,
                        spanContext.getTraceId(),
                        spanContext.getSpanId(),
                        spanContext.getTraceFlags().asHex()));
    }

    public static Map<String, String> createTraceStateHeaderFromSpan(SpanContext spanContext) {
        return Collections.singletonMap(
                MDCHeaderKeys.TRACESTATE.getHeaderName(),
                spanContext.getTraceState()
                        .asMap()
                        .entrySet()
                        .stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining(",")));

    }

    public static TraceState createTraceStateFromMembersMap(Map<String, String> members) {
        TraceStateBuilder builder = TraceState.builder();
        members.forEach((key, value) -> {
            builder.put(key, value);
        });
        return builder.build();
    }

    public static TraceState createTraceStateFromMembersString(String members) {
        Map<String, String> membersMap = MEMBER_LIST_PATTERN.splitAsStream(members.trim())
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : "", (a1, a2) -> a1));
        TraceStateBuilder builder = TraceState.builder();
        membersMap.forEach((key, value) -> {
            builder.put(key, value);
        });
        return builder.build();
    }

}
