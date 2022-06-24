package org.jboss.pnc.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Json {

    public static <T extends Number> Optional<T> getNumber(JsonNode node, String path) {
        JsonNode jsonNode = node.at(path);
        if (jsonNode.isMissingNode()) {
            return Optional.empty();
        } else {
            if (jsonNode.isNumber()) {
                return Optional.of((T) jsonNode.numberValue());
            } else {
                // number is probably quoted
                String value = jsonNode.textValue();
                return Optional.of((T) Long.valueOf(value));
            }
        }
    }

    public static Optional<String> getText(JsonNode rootNode, String path) {
        JsonNode jsonNode = rootNode.at(path);
        if (jsonNode.isMissingNode()) {
            return Optional.empty();
        } else {
            return Optional.of(jsonNode.asText());
        }
    }
}
