package com.gxchain.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.graphenej.enums.OperationType;
import com.gxchain.client.graphenej.objects.AssetAmount;
import com.gxchain.client.graphenej.objects.BroadcastRequestParams;
import com.gxchain.client.graphenej.objects.Extensions;
import lombok.Data;

/**
 * @Description
 * @Author Hanawa
 * @Date 2018/3/6
 * @Version 1.0
 */
@Data public class BroadcastOperation extends BaseOperation {
    private static final long serialVersionUID = 4544825479406548507L;
    private String proxyMemo;
    private AssetAmount fee;
    private BroadcastRequestParams requestParams;
    private Extensions extensions;

    public BroadcastOperation() {
        super(OperationType.BROADCAST_STORE_DATA);
    }

    @Override public void setFee(AssetAmount assetAmount) {
        this.fee = assetAmount;
    }

    @Override public byte[] toBytes() {
        byte[] proxyMemoPrefix = new byte[] {(byte) proxyMemo.length()};
        byte[] proxyMemoBytes = proxyMemo.getBytes();
        byte[] feeBytes = fee.toBytes();
        byte[] requestParamsBytes = requestParams.toBytes();
        byte[] extensionsBytes = extensions.toBytes();

        return Bytes.concat(proxyMemoPrefix, proxyMemoBytes, feeBytes, requestParamsBytes, extensionsBytes);
    }

    @Override public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("proxy_memo", proxyMemo);
        jsonObject.add("fee", fee.toJsonObject());
        jsonObject.add("request_params",requestParams.toJsonObject());
        jsonObject.add("extensions", new JsonArray());
        array.add(jsonObject);
        return array;
    }
}

