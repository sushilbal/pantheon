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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

public class JsonUtilTest {
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void getLong_nonExistentKey() {
    final ObjectNode node = mapper.createObjectNode();
    final OptionalLong result = JsonUtil.getLong(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getLong_nullValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final OptionalLong result = JsonUtil.getLong(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getLong_validValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", Long.MAX_VALUE);
    final OptionalLong result = JsonUtil.getLong(node, "test");
    assertThat(result).hasValue(Long.MAX_VALUE);
  }

  @Test
  public void getLong_overflowingValue() {
    final String overflowingValue = Long.toString(Long.MAX_VALUE, 10) + "100";
    final String jsonStr = "{\"test\": " + overflowingValue + " }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getLong(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot convert value to long: " + overflowingValue);
  }

  @Test
  public void getLong_wrongType() {
    final String jsonStr = "{\"test\": \"bla\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getLong(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected number value but got string");
  }

  @Test
  public void getLong_nullValue_withDefault() {
    final long defaultValue = 11;
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final long result = JsonUtil.getLong(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getLong_nonExistentKey_withDefault() {
    final long defaultValue = 11;
    final ObjectNode node = mapper.createObjectNode();
    final long result = JsonUtil.getLong(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getLong_validValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", Long.MAX_VALUE);
    final long result = JsonUtil.getLong(node, "test", 11);
    assertThat(result).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  public void getLong_overflowingValue_withDefault() {
    final String overflowingValue = Long.toString(Long.MAX_VALUE, 10) + "100";
    final String jsonStr = "{\"test\": " + overflowingValue + " }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getLong(rootNode, "test", 11))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot convert value to long: " + overflowingValue);
  }

  @Test
  public void getLong_wrongType_withDefault() {
    final String jsonStr = "{\"test\": \"bla\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getLong(rootNode, "test", 11))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected number value but got string");
  }

  @Test
  public void getInt_nonExistentKey() {
    final ObjectNode node = mapper.createObjectNode();
    final OptionalInt result = JsonUtil.getInt(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getInt_nullValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final OptionalInt result = JsonUtil.getInt(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getInt_validValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", Integer.MAX_VALUE);
    final OptionalInt result = JsonUtil.getInt(node, "test");
    assertThat(result).hasValue(Integer.MAX_VALUE);
  }

  @Test
  public void getInt_overflowingValue() {
    final String overflowingValue = Integer.toString(Integer.MAX_VALUE, 10) + "100";
    final String jsonStr = "{\"test\": " + overflowingValue + " }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getInt(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot convert value to integer: " + overflowingValue);
  }

  @Test
  public void getInt_wrongType() {
    final String jsonStr = "{\"test\": \"bla\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getInt(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected number value but got string");
  }

  @Test
  public void getInt_nullValue_withDefault() {
    final int defaultValue = 11;
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final int result = JsonUtil.getInt(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getInt_nonExistentKey_withDefault() {
    final int defaultValue = 11;
    final ObjectNode node = mapper.createObjectNode();
    final int result = JsonUtil.getInt(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getInt_validValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", Integer.MAX_VALUE);
    final int result = JsonUtil.getInt(node, "test", 11);
    assertThat(result).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void getInt_overflowingValue_withDefault() {
    final String overflowingValue = Integer.toString(Integer.MAX_VALUE, 10) + "100";
    final String jsonStr = "{\"test\": " + overflowingValue + " }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getInt(rootNode, "test", 11))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot convert value to integer: " + overflowingValue);
  }

  @Test
  public void getInt_wrongType_withDefault() {
    final String jsonStr = "{\"test\": \"bla\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getInt(rootNode, "test", 11))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected number value but got string");
  }

  @Test
  public void getString_nonExistentKey() {
    final ObjectNode node = mapper.createObjectNode();
    final Optional<String> result = JsonUtil.getString(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getString_nullValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final Optional<String> result = JsonUtil.getString(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getString_validValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", "bla");
    final Optional<String> result = JsonUtil.getString(node, "test");
    assertThat(result).hasValue("bla");
  }

  @Test
  public void getString_wrongType() {
    final String jsonStr = "{\"test\": 123 }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getString(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected string value but got number");
  }

  @Test
  public void getString_nullValue_withDefault() {
    final String defaultValue = "bla";
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final String result = JsonUtil.getString(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getString_nonExistentKey_withDefault() {
    final String defaultValue = "bla";
    final ObjectNode node = mapper.createObjectNode();
    final String result = JsonUtil.getString(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getString_validValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", "bla");
    final String result = JsonUtil.getString(node, "test", "11");
    assertThat(result).isEqualTo("bla");
  }

  @Test
  public void getValueAsString_nonExistentKey() {
    final ObjectNode node = mapper.createObjectNode();
    final Optional<String> result = JsonUtil.getValueAsString(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getValueAsString_nullValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final Optional<String> result = JsonUtil.getValueAsString(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getValueAsString_stringValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", "bla");
    final Optional<String> result = JsonUtil.getValueAsString(node, "test");
    assertThat(result).hasValue("bla");
  }

  @Test
  public void getValueAsString_nonStringValue() {
    final String jsonStr = "{\"test\": 123 }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<String> result = JsonUtil.getValueAsString(rootNode, "test");
    assertThat(result).hasValue("123");
  }

  @Test
  public void getValueAsString_nullValue_withDefault() {
    final String defaultValue = "bla";
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final String result = JsonUtil.getValueAsString(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getValueAsString_nonExistentKey_withDefault() {
    final String defaultValue = "bla";
    final ObjectNode node = mapper.createObjectNode();
    final String result = JsonUtil.getValueAsString(node, "test", defaultValue);
    assertThat(result).isEqualTo(defaultValue);
  }

  @Test
  public void getValueAsString_stringValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", "bla");
    final String result = JsonUtil.getValueAsString(node, "test", "11");
    assertThat(result).isEqualTo("bla");
  }

  @Test
  public void getValueAsString_nonStringValue_withDefault() {
    final String jsonStr = "{\"test\": 123 }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final String result = JsonUtil.getValueAsString(rootNode, "test", "11");
    assertThat(result).isEqualTo("123");
  }

  // Boolean
  @Test
  public void getBoolean_nonExistentKey() {
    final ObjectNode node = mapper.createObjectNode();
    final Optional<Boolean> result = JsonUtil.getBoolean(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getBoolean_nullValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final Optional<Boolean> result = JsonUtil.getBoolean(node, "test");
    assertThat(result).isEmpty();
  }

  @Test
  public void getBoolean_validValue() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", true);
    final Optional<Boolean> result = JsonUtil.getBoolean(node, "test");
    assertThat(result).hasValue(true);
  }

  @Test
  public void getBoolean_wrongType() {
    final String jsonStr = "{\"test\": 123 }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getBoolean(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected boolean value but got number");
  }

  @Test
  public void getBoolean_nullValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.set("test", null);
    final Boolean result = JsonUtil.getBoolean(node, "test", false);
    assertThat(result).isEqualTo(false);
  }

  @Test
  public void getBoolean_nonExistentKey_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    final Boolean result = JsonUtil.getBoolean(node, "test", true);
    assertThat(result).isEqualTo(true);
  }

  @Test
  public void getBoolean_validValue_withDefault() {
    final ObjectNode node = mapper.createObjectNode();
    node.put("test", false);
    final Boolean result = JsonUtil.getBoolean(node, "test", true);
    assertThat(result).isEqualTo(false);
  }

  @Test
  public void getBoolean_wrongType_withDefault() {
    final String jsonStr = "{\"test\": 123 }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getBoolean(rootNode, "test", true))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected boolean value but got number");
  }

  @Test
  public void objectNodeFromMap() {
    final Map<String, Object> map = new TreeMap<>();
    map.put("a", 1);
    map.put("b", 2);

    final Map<String, Object> subMap = new TreeMap<>();
    subMap.put("c", "bla");
    subMap.put("d", 2L);
    map.put("subtree", subMap);

    ObjectNode node = JsonUtil.objectNodeFromMap(map);
    assertThat(node.get("a").asInt()).isEqualTo(1);
    assertThat(node.get("b").asInt()).isEqualTo(2);
    assertThat(node.get("subtree").get("c").asText()).isEqualTo("bla");
    assertThat(node.get("subtree").get("d").asLong()).isEqualTo(2L);
  }

  @Test
  public void objectNodeFromString() {
    final String jsonStr = "{\"a\":1, \"b\":2}";

    final ObjectNode result = JsonUtil.objectNodeFromString(jsonStr);
    assertThat(result.get("a").asInt()).isEqualTo(1);
    assertThat(result.get("b").asInt()).isEqualTo(2);
  }

  @Test
  public void objectNodeFromString_withComments_commentsDisabled() {
    final String jsonStr = "// Comment\n{\"a\":1, \"b\":2}";

    assertThatThrownBy(() -> JsonUtil.objectNodeFromString(jsonStr, false))
        .hasCauseInstanceOf(JsonParseException.class)
        .hasMessageContaining("Unexpected character ('/'");
  }

  @Test
  public void objectNodeFromString_withComments_commentsEnabled() {
    final String jsonStr = "// Comment\n{\"a\":1, \"b\":2}";

    final ObjectNode result = JsonUtil.objectNodeFromString(jsonStr, true);
    assertThat(result.get("a").asInt()).isEqualTo(1);
    assertThat(result.get("b").asInt()).isEqualTo(2);
  }

  @Test
  public void getJson() throws JsonProcessingException {
    final String jsonStr = "{\"a\":1, \"b\":2}";
    final ObjectNode objectNode = JsonUtil.objectNodeFromString(jsonStr);

    final String resultUgly = JsonUtil.getJson(objectNode, false);
    final String resultPretty = JsonUtil.getJson(objectNode, true);

    assertThat(resultUgly).isEqualToIgnoringWhitespace(jsonStr);
    assertThat(resultPretty).isEqualToIgnoringWhitespace(jsonStr);
    // Pretty printed value should have more whitespace and contain returns
    assertThat(resultPretty.length()).isGreaterThan(resultUgly.length());
    assertThat(resultPretty).contains("\n");
    assertThat(resultUgly).doesNotContain("\n");
  }

  @Test
  public void getObjectNode_validValue() {
    final String jsonStr = "{\"test\": {\"a\":1, \"b\":2} }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ObjectNode> maybeTestNode = JsonUtil.getObjectNode(rootNode, "test");
    assertThat(maybeTestNode).isNotEmpty();
    final ObjectNode testNode = maybeTestNode.get();
    assertThat(testNode.get("a").asInt()).isEqualTo(1);
    assertThat(testNode.get("b").asInt()).isEqualTo(2);
  }

  @Test
  public void getObjectNode_nullValue() {
    final String jsonStr = "{\"test\": null }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ObjectNode> maybeTestNode = JsonUtil.getObjectNode(rootNode, "test");
    assertThat(maybeTestNode).isEmpty();
  }

  @Test
  public void getObjectNode_nonExistentKey() {
    final String jsonStr = "{}";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ObjectNode> maybeTestNode = JsonUtil.getObjectNode(rootNode, "test");
    assertThat(maybeTestNode).isEmpty();
  }

  @Test
  public void getObjectNode_wrongNodeType() {
    final String jsonStr = "{\"test\": \"abc\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getObjectNode(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected object value but got string");
  }

  @Test
  public void getArrayNode_validValue() {
    final String jsonStr = "{\"test\": [\"a\", \"b\"] }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ArrayNode> maybeTestNode = JsonUtil.getArrayNode(rootNode, "test");
    assertThat(maybeTestNode).isNotEmpty();
    final ArrayNode testNode = maybeTestNode.get();
    assertThat(testNode.get(0).asText()).isEqualTo("a");
    assertThat(testNode.get(1).asText()).isEqualTo("b");
  }

  @Test
  public void getArrayNode_nullValue() {
    final String jsonStr = "{\"test\": null }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ArrayNode> maybeTestNode = JsonUtil.getArrayNode(rootNode, "test");
    assertThat(maybeTestNode).isEmpty();
  }

  @Test
  public void getArrayNode_nonExistentKey() {
    final String jsonStr = "{}";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    final Optional<ArrayNode> maybeTestNode = JsonUtil.getArrayNode(rootNode, "test");
    assertThat(maybeTestNode).isEmpty();
  }

  @Test
  public void getArrayNode_wrongNodeType() {
    final String jsonStr = "{\"test\": \"abc\" }";
    final ObjectNode rootNode = JsonUtil.objectNodeFromString(jsonStr);

    assertThatThrownBy(() -> JsonUtil.getArrayNode(rootNode, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Expected array value but got string");
  }
}
