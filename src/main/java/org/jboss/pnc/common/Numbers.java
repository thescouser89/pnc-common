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
package org.jboss.pnc.common;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Base64;

import org.apache.commons.codec.binary.Base32;

public class Numbers {

    private static Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static Base64.Decoder decoder = Base64.getUrlDecoder();
    private static Base32 base32 = new Base32();

    public static byte[] longToBytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(l);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        if (bytes.length > Long.BYTES) {
            throw new BufferOverflowException();
        }
        byte[] longBytes = new byte[Long.BYTES];
        // copy at the end of array
        System.arraycopy(bytes, 0, longBytes, longBytes.length - bytes.length, bytes.length);
        return ByteBuffer.wrap(longBytes).getLong();
    }

    public static String decimalToBase64(long l) {
        return encoder.encodeToString(longToBytes(l));
    }

    /**
     * Convert long to base32
     */
    public static String decimalToBase32(long l) {
        return base32.encodeToString(longToBytes(l)).substring(0,13);
    }

    /**
     * Convert numeral base64 to long
     */
    public static Long base64ToDecimal(String numeralBase64) {
        if (Strings.isEmpty(numeralBase64)) {
            return null;
        }
        return bytesToLong(decoder.decode(numeralBase64));
    }

    /**
     * Convert numeral base32 to long
     */
    public static Long base32ToDecimal(String numeralBase32) {
        if (Strings.isEmpty(numeralBase32)) {
            return null;
        }
        return bytesToLong(base32.decode(numeralBase32));
    }

}
