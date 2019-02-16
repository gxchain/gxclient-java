package com.gxchain.client.graphenej.operations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.graphenej.enums.OperationType;
import com.gxchain.client.graphenej.objects.AssetAmount;
import com.gxchain.client.graphenej.objects.Optional;
import com.gxchain.client.graphenej.objects.UserAccount;
import lombok.Data;

/**
 * @author liruobin
 * @since 2019/2/15 8:59 PM
 */
@Data
public class CallContractOperation extends BaseOperation {

    private AssetAmount fee;

    private UserAccount account;

    private UserAccount contractId;

    private String methodName;

    private Optional<AssetAmount> amount;

    private String data;

    public CallContractOperation(AssetAmount fee, UserAccount account, UserAccount contractId, String methodName, AssetAmount amount, String data) {
        super(OperationType.CALL_CONTRACT);
        this.fee = fee;
        this.account = account;
        this.contractId = contractId;
        this.methodName = methodName;
        this.amount = new Optional<>(amount);
        this.data = data;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public String toJsonString() {
        return toJsonObject().toString();
    }

    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null)
            jsonObject.add("fee", fee.toJsonObject());
        jsonObject.addProperty("account", account.getObjectId());
        jsonObject.addProperty("method_name", methodName);
        jsonObject.addProperty("contract_id", contractId.getObjectId());
        jsonObject.addProperty("data", data);
        if (amount.isSet() && amount.value().getAmount().longValue() != 0) {
            jsonObject.add("amount", amount.toJsonObject());
        }
        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        array.add(jsonObject);
        return array;
    }
}
