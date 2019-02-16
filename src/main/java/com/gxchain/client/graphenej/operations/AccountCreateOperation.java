package com.gxchain.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLong;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.graphenej.Varint;
import com.gxchain.client.graphenej.enums.OperationType;
import com.gxchain.client.graphenej.objects.*;
import lombok.Data;

/**
 * Class used to encapsulate operations related to the ACCOUNT_CREATE_OPERATION.
 */
@Data
public class AccountCreateOperation extends BaseOperation {
    public static final String KEY_REGISTRAR = "registrar";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_FEE = "fee";
    public static final String KEY_OPTIONS = "options";
    public static final String KEY_EXTENSIONS = "extensions";
    public static final String KEY_REFERRER = "referrer";
    public static final String KEY_REFERRER_PERCENT = "referrer_percent";
    public static final String KEY_NAME = "name";

    private AssetAmount fee;
    private Authority owner;
    private Authority active;
    private AccountOptions options;

    private UserAccount registrar;

    private UserAccount referrer;

    private int referrerPercent;

    private String name;

    /**
     * Account create operation constructor.
     *
     * @param registrar User account to update. Can't be null.
     * @param owner     Owner authority to set. Can be null.
     * @param active    Active authority to set. Can be null.
     * @param options   Active authority to set. Can be null.
     * @param fee       The fee to pay. Can be null.
     */
    public AccountCreateOperation(UserAccount registrar, UserAccount referrer, Authority owner, Authority active, AccountOptions options, AssetAmount fee) {
        super(OperationType.ACCOUNT_CREATE_OPERATION);
        this.fee = fee;
        this.registrar = registrar;
        this.referrer = referrer;
        this.owner = owner;
        this.active = active;
        this.options = options;
        extensions = new Extensions();
    }

    public AccountCreateOperation(UserAccount registrar, UserAccount referrer, Authority owner, Authority active, AccountOptions options) {
        this(registrar, referrer, owner, active, options, new AssetAmount(UnsignedLong.valueOf(0), new Asset("1.3.0")));
    }

    @Override
    public void setFee(AssetAmount fee) {
        this.fee = fee;
    }

    public void setOwner(Authority owner) {
        this.owner = owner;
    }

    public void setActive(Authority active) {
        this.active = active;
    }

    public void setAccountOptions(AccountOptions options) {
        this.options = options;
    }

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());

        JsonObject accountCreate = new JsonObject();
        accountCreate.add(KEY_FEE, fee.toJsonObject());
        accountCreate.addProperty(KEY_REGISTRAR, registrar.getObjectId());
        accountCreate.addProperty(KEY_REFERRER, referrer.getObjectId());
        accountCreate.addProperty(KEY_REFERRER_PERCENT, referrerPercent);
        accountCreate.addProperty(KEY_NAME, name);
        accountCreate.add(KEY_OWNER, owner.toJsonObject());
        accountCreate.add(KEY_ACTIVE, active.toJsonObject());
        accountCreate.add(KEY_OPTIONS, options.toJsonObject());
        accountCreate.add(KEY_EXTENSIONS, extensions.toJsonObject());
        array.add(accountCreate);
        return array;
    }

    @Override
    public byte[] toBytes() {
        byte[] feeBytes = fee.toBytes();
        byte[] registrarBytes = registrar.toBytes();
        byte[] referrerBytes = referrer.toBytes();
        byte[] referrerPercentBytes = Varint.writeUnsignedSize(referrerPercent);
        byte[] nameBytes = name.getBytes();
        byte[] ownerBytes = owner.toBytes();
        byte[] activeBytes = active.toBytes();
        byte[] optionsBytes = options.toBytes();
        byte[] extensionBytes = extensions.toBytes();
        return Bytes.concat(feeBytes, registrarBytes, referrerBytes, referrerPercentBytes, new byte[]{(byte) this.name.length()}, nameBytes, ownerBytes, activeBytes, optionsBytes, extensionBytes);
    }
}
