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
package org.jboss.pnc.common.pnc;

import org.jboss.pnc.common.Numbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts ids from long to base32 strings and vice versa, while keeping backward compatibility with decimal ids up to
 * {@value OLD_ID_BOUNDARY}.
 */
public class LongBase32IdConverter {

    private static final Logger logger = LoggerFactory.getLogger(LongBase32IdConverter.class);
    /**
     * For backward compatibility to support old urls. GUID are always bigger than this number. We need also to avoid
     * confusing "number-like" base32 encodings with true numbers, the lowest "number-like" base32 encoded long is
     * "2222222222222" and the highest "number-like" base32 encoded long is "9999999999999".
     */
    public static final long OLD_ID_BOUNDARY = 2222222222222L;
    public static final long HIGHEST_BASE32_NUMBER_LIKE = 9999999999999L; // more than 13 chars, it must be a long value
                                                                          // already

    public static Long toLong(String id) {
        if (id == null) {
            return null;
        }
        if (id.matches("[0-9]+")) {
            try {
                Long possibleId = Long.parseLong(id);
                if (possibleId < OLD_ID_BOUNDARY) {
                    return possibleId;
                } else {
                    if (possibleId > HIGHEST_BASE32_NUMBER_LIKE) {
                        return possibleId;
                    }
                }
            } catch (NumberFormatException e) {
                // not a long number
                logger.warn("Id is not a long.", e);
            }
        }
        return Numbers.base32ToDecimal(id);
    }

    public static String toString(Long id) {
        if (id == null) {
            return null;
        }
        if (id < OLD_ID_BOUNDARY) {
            return id.toString();
        } else {
            return Numbers.decimalToBase32(id);
        }
    }
}
