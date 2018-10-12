package tech.pegasys.pantheon.ethereum.core;

import tech.pegasys.pantheon.ethereum.mainnet.MainnetBlockHashFunction;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.uint.UInt256;

import java.util.Map;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class HeaderDecodingHelpers {

  public static class LoadedBlockHeader {
    private final BlockHeader header;
    private final Hash parsedBlockHash;

    public LoadedBlockHeader(final BlockHeader header, final Hash parsedBlockHash) {
      this.header = header;
      this.parsedBlockHash = parsedBlockHash;
    }

    public BlockHeader getHeader() {
      return header;
    }

    public Hash getParsedBlockHash() {
      return parsedBlockHash;
    }
  }

  /**
   * @param blockHeaderStr A block header string as generated by BlockHeader.toString()
   * @return a data object representing the string passed in.
   */
  public static LoadedBlockHeader fromString(final String blockHeaderStr) {
    final Map<String, String> kv = Maps.newHashMap();
    final Iterable<String> items = Splitter.on(", ").split(blockHeaderStr);
    for (final String item : items) {
      kv.put(item.replaceAll("=.*", ""), item.replaceAll(".*=", ""));
    }
    final BlockHeaderBuilder builder = new BlockHeaderBuilder();
    builder
        .parentHash(Hash.fromHexString(kv.get("parentHash")))
        .ommersHash(Hash.fromHexString(kv.get("ommersHash")))
        .coinbase(Address.fromHexString(kv.get("coinbase")))
        .stateRoot(Hash.fromHexString(kv.get("stateRoot")))
        .transactionsRoot(Hash.fromHexString(kv.get("transactionsRoot")))
        .receiptsRoot(Hash.fromHexString(kv.get("receiptsRoot")))
        .logsBloom(LogsBloomFilter.fromHexString(kv.get("logsBloom")))
        .difficulty(UInt256.of(Long.parseLong(kv.get("difficulty"))))
        .number(Long.parseLong(kv.get("number")))
        .gasLimit(Long.parseLong(kv.get("gasLimit")))
        .gasUsed(Long.parseLong(kv.get("gasUsed")))
        .timestamp(Long.parseLong(kv.get("timestamp")))
        .extraData(BytesValue.fromHexString(kv.get("extraData")))
        .mixHash(Hash.fromHexString(kv.get("mixHash")))
        .nonce(Long.parseLong(kv.get("nonce")))
        .blockHashFunction(MainnetBlockHashFunction::createHash);

    final Hash parsedHash = Hash.fromHexString(kv.get("hash"));

    builder.blockHashFunction(MainnetBlockHashFunction::createHash);
    return new LoadedBlockHeader(builder.buildBlockHeader(), parsedHash);
  }

  public static LoadedBlockHeader fromGethConsoleBlockDump(final String ethBlockDump) {
    Json.mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    final JsonObject jsonObj = new JsonObject(ethBlockDump);

    final BlockHeaderBuilder builder = new BlockHeaderBuilder();
    builder.difficulty(UInt256.of(jsonObj.getLong("difficulty")));
    builder.extraData(BytesValue.fromHexString(jsonObj.getString("extraData")));
    builder.gasLimit(jsonObj.getLong("gasLimit"));
    builder.gasUsed(jsonObj.getLong("gasUsed"));
    // Do not do Hash.
    builder.logsBloom(LogsBloomFilter.fromHexString(jsonObj.getString("logsBloom")));
    builder.coinbase(Address.fromHexString(jsonObj.getString("miner")));
    builder.mixHash(Hash.fromHexString(jsonObj.getString("mixHash")));
    builder.nonce(Long.decode(jsonObj.getString("nonce")));
    builder.number(jsonObj.getLong("number"));
    builder.parentHash(Hash.fromHexString(jsonObj.getString("parentHash")));
    builder.receiptsRoot(Hash.fromHexString(jsonObj.getString("receiptsRoot")));
    builder.ommersHash(Hash.fromHexString(jsonObj.getString("sha3Uncles")));
    builder.stateRoot(Hash.fromHexString(jsonObj.getString("stateRoot")));
    builder.timestamp(jsonObj.getLong("timestamp"));
    builder.transactionsRoot(Hash.fromHexString(jsonObj.getString("transactionsRoot")));

    final Hash parsedHash = Hash.fromHexString(jsonObj.getString("hash"));

    builder.blockHashFunction(MainnetBlockHashFunction::createHash);
    return new LoadedBlockHeader(builder.buildBlockHeader(), parsedHash);
  }
}