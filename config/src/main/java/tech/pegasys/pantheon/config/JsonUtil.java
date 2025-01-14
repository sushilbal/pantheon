/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.config;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {

  /**
   * Get the string representation of the value at {@code key}. For example, a numeric value like 5
   * will be returned as "5".
   *
   * @param node The {@code ObjectNode} from which the value will be extracted.
   * @param key The key corresponding to the value to extract.
   * @return The value at the given key as a string if it exists.
   */
  public static Optional<String> getValueAsString(final ObjectNode node, final String key) {
    return getValue(node, key).map(JsonNode::asText);
  }

  /**
   * Get the string representation of the value at {@code key}. For example, a numeric value like 5
   * will be returned as "5".
   *
   * @param node The {@code ObjectNode} from which the value will be extracted.
   * @param key The key corresponding to the value to extract.
   * @param defaultValue The value to return if no value is found at {@code key}.
   * @return The value at the given key as a string if it exists, otherwise {@code defaultValue}
   */
  public static String getValueAsString(
      final ObjectNode node, final String key, final String defaultValue) {
    return getValueAsString(node, key).orElse(defaultValue);
  }

  /**
   * Returns textual (string) value at {@code key}. See {@link #getValueAsString} for retrieving
   * non-textual values in string form.
   *
   * @param node The {@code ObjectNode} from which the value will be extracted.
   * @param key The key corresponding to the value to extract.
   * @return The textual value at {@code key} if it exists.
   */
  public static Optional<String> getString(final ObjectNode node, final String key) {
    return getValue(node, key)
        .filter(jsonNode -> validateType(jsonNode, JsonNodeType.STRING))
        .map(JsonNode::asText);
  }

  /**
   * Returns textual (string) value at {@code key}. See {@link #getValueAsString} for retrieving
   * non-textual values in string form.
   *
   * @param node The {@code ObjectNode} from which the value will be extracted.
   * @param key The key corresponding to the value to extract.
   * @param defaultValue The value to return if no value is found at {@code key}.
   * @return The textual value at {@code key} if it exists, otherwise {@code defaultValue}
   */
  public static String getString(
      final ObjectNode node, final String key, final String defaultValue) {
    return getString(node, key).orElse(defaultValue);
  }

  public static OptionalInt getInt(final ObjectNode node, final String key) {
    return getValue(node, key)
        .filter(jsonNode -> validateType(jsonNode, JsonNodeType.NUMBER))
        .filter(JsonUtil::validateInt)
        .map(JsonNode::asInt)
        .map(OptionalInt::of)
        .orElse(OptionalInt.empty());
  }

  public static int getInt(final ObjectNode node, final String key, final int defaultValue) {
    return getInt(node, key).orElse(defaultValue);
  }

  public static OptionalLong getLong(final ObjectNode json, final String key) {
    return getValue(json, key)
        .filter(jsonNode -> validateType(jsonNode, JsonNodeType.NUMBER))
        .filter(JsonUtil::validateLong)
        .map(JsonNode::asLong)
        .map(OptionalLong::of)
        .orElse(OptionalLong.empty());
  }

  public static long getLong(final ObjectNode json, final String key, final long defaultValue) {
    return getLong(json, key).orElse(defaultValue);
  }

  public static Optional<Boolean> getBoolean(final ObjectNode node, final String key) {
    return getValue(node, key)
        .filter(jsonNode -> validateType(jsonNode, JsonNodeType.BOOLEAN))
        .map(JsonNode::asBoolean);
  }

  public static boolean getBoolean(
      final ObjectNode node, final String key, final boolean defaultValue) {
    return getBoolean(node, key).orElse(defaultValue);
  }

  public static ObjectNode createEmptyObjectNode() {
    ObjectMapper mapper = getObjectMapper();
    return mapper.createObjectNode();
  }

  public static ObjectNode objectNodeFromMap(final Map<String, Object> map) {
    return (ObjectNode) getObjectMapper().valueToTree(map);
  }

  public static ObjectNode objectNodeFromString(final String jsonData) {
    return objectNodeFromString(jsonData, false);
  }

  public static ObjectNode objectNodeFromString(
      final String jsonData, final boolean allowComments) {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(Feature.ALLOW_COMMENTS, allowComments);
    try {
      final JsonNode jsonNode = objectMapper.readTree(jsonData);
      validateType(jsonNode, JsonNodeType.OBJECT);
      return (ObjectNode) jsonNode;
    } catch (IOException e) {
      // Reading directly from a string should not raise an IOException, just catch and rethrow
      throw new RuntimeException(e);
    }
  }

  public static String getJson(final Object objectNode) throws JsonProcessingException {
    return getJson(objectNode, true);
  }

  public static String getJson(final Object objectNode, final boolean prettyPrint)
      throws JsonProcessingException {
    ObjectMapper mapper = getObjectMapper();
    if (prettyPrint) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
    } else {
      return mapper.writeValueAsString(objectNode);
    }
  }

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }

  public static Optional<ObjectNode> getObjectNode(final ObjectNode json, final String fieldKey) {
    return getObjectNode(json, fieldKey, true);
  }

  public static Optional<ObjectNode> getObjectNode(
      final ObjectNode json, final String fieldKey, final boolean strict) {
    final JsonNode obj = json.get(fieldKey);
    if (obj == null || obj.isNull()) {
      return Optional.empty();
    }

    if (!obj.isObject()) {
      if (strict) {
        validateType(obj, JsonNodeType.OBJECT);
      } else {
        return Optional.empty();
      }
    }

    return Optional.of((ObjectNode) obj);
  }

  public static Optional<ArrayNode> getArrayNode(final ObjectNode json, final String fieldKey) {
    return getArrayNode(json, fieldKey, true);
  }

  public static Optional<ArrayNode> getArrayNode(
      final ObjectNode json, final String fieldKey, final boolean strict) {
    final JsonNode obj = json.get(fieldKey);
    if (obj == null || obj.isNull()) {
      return Optional.empty();
    }

    if (!obj.isArray()) {
      if (strict) {
        validateType(obj, JsonNodeType.ARRAY);
      } else {
        return Optional.empty();
      }
    }

    return Optional.of((ArrayNode) obj);
  }

  private static Optional<JsonNode> getValue(final ObjectNode node, final String key) {
    JsonNode jsonNode = node.get(key);
    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    }
    return Optional.of(jsonNode);
  }

  private static boolean validateType(final JsonNode node, final JsonNodeType expectedType) {
    if (node.getNodeType() != expectedType) {
      final String errorMessage =
          String.format(
              "Expected %s value but got %s",
              expectedType.toString().toLowerCase(), node.getNodeType().toString().toLowerCase());
      throw new IllegalArgumentException(errorMessage);
    }
    return true;
  }

  private static boolean validateLong(final JsonNode node) {
    if (!node.canConvertToLong()) {
      throw new IllegalArgumentException("Cannot convert value to long: " + node.toString());
    }
    return true;
  }

  private static boolean validateInt(final JsonNode node) {
    if (!node.canConvertToInt()) {
      throw new IllegalArgumentException("Cannot convert value to integer: " + node.toString());
    }
    return true;
  }
}
