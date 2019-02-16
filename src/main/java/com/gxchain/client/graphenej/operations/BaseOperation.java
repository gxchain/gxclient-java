package com.gxchain.client.graphenej.operations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.gxchain.client.graphenej.enums.OperationType;
import com.gxchain.client.graphenej.interfaces.ByteSerializable;
import com.gxchain.client.graphenej.interfaces.JsonSerializable;
import com.gxchain.client.graphenej.objects.AssetAmount;
import com.gxchain.client.graphenej.objects.Extensions;

/**
 * Created by nelson on 11/5/16.
 */
public abstract class BaseOperation implements ByteSerializable, JsonSerializable {

    public static final String KEY_FEE = "fee";
    public static final String KEY_EXTENSIONS = "extensions";

    protected OperationType type;
    protected Extensions extensions;

    public BaseOperation(OperationType type){
        this.type = type;
        this.extensions = new Extensions();
    }

    public byte getId() {
        return (byte) this.type.getCode();
    }

    public abstract void setFee(AssetAmount assetAmount);

    public JsonElement toJsonObject(){
        JsonArray array = new JsonArray();
        array.add(this.getId());
        return array;
    }
}
