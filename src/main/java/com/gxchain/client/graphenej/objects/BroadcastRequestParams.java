package com.gxchain.client.graphenej.objects;

import com.google.common.primitives.Bytes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.graphenej.Util;
import com.gxchain.client.graphenej.Varint;
import com.gxchain.client.graphenej.interfaces.ByteSerializable;
import com.gxchain.client.graphenej.interfaces.JsonSerializable;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @Description
 * @Author Hanawa
 * @Date 2018/3/15
 * @Version 1.0
 */
@Data @Builder public class BroadcastRequestParams implements ByteSerializable, JsonSerializable {
    private static final long serialVersionUID = 6867501721046700516L;
    private UserAccount from;
    private UserAccount to;
    private UserAccount proxyAccount;
    private AssetAmount amount;
    private int percentage;
    private String memo;
    private long expiration;
    private List<String> signatures;

    @Override public byte[] toBytes() {
        byte[] fromBytes = from.toBytes();
        byte[] toBytes = to.toBytes();
        byte[] proxyAccountBytes = proxyAccount.toBytes();
        byte[] amountBytes = amount.toBytes();
        byte[] percentageBytes = Varint.writeUnsignedSize(percentage);
        byte[] memoPrefix = new byte[] {(byte) memo.length()};
        byte[] memoBytes = memo.getBytes();
        byte[] expirationBytes = Varint.writeUnsignedSize(expiration);
        byte[] signaturesSizeBytes = new byte[] {0};
        if (null != signatures && signatures.size() != 0) {
            signaturesSizeBytes = new byte[] {(byte) signatures.size()};
            StringBuilder s = new StringBuilder();
            signatures.forEach(s::append);
            byte[] signatureBytes = Util.hexToBytes(s.toString());
            return Bytes.concat(fromBytes, toBytes, proxyAccountBytes, amountBytes, percentageBytes, memoPrefix, memoBytes, expirationBytes, signaturesSizeBytes, signatureBytes);
        }
        return Bytes.concat(fromBytes, toBytes, proxyAccountBytes, amountBytes, percentageBytes, memoPrefix, memoBytes, expirationBytes, signaturesSizeBytes);
    }

    @Override public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override public JsonElement toJsonObject() {
        DateTime expirationTime = new DateTime(expiration * 1000);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from", from.getObjectId());
        jsonObject.addProperty("to", to.getObjectId());
        jsonObject.addProperty("proxy_account", proxyAccount.getObjectId());
        jsonObject.add("amount", amount.toJsonObject());
        jsonObject.addProperty("percentage", percentage);
        jsonObject.addProperty("memo", memo);
        jsonObject.addProperty("expiration", expirationTime.plusHours(-8).toString().substring(0, 19));
        JsonArray array = new JsonArray();
        signatures.forEach(array::add);
        jsonObject.add("signatures", array);
        return jsonObject;
    }
}
