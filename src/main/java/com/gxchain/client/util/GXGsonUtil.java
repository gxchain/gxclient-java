package com.gxchain.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gxchain.client.graphenej.models.ApiCall;
import com.gxchain.client.graphenej.models.DynamicGlobalProperties;
import com.gxchain.client.graphenej.objects.*;
import com.gxchain.client.graphenej.operations.AccountCreateOperation;
import com.gxchain.client.graphenej.operations.TransferOperation;

import java.lang.reflect.Type;

/**
 * @author liruobin
 * @since 2018/7/3 下午4:48
 */
public class GXGsonUtil {
    private static GsonBuilder builder = new GsonBuilder();

    static {
        builder.registerTypeAdapter(Transaction.class, new Transaction.TransactionDeserializer());
        builder.registerTypeAdapter(TransferOperation.class, new TransferOperation.TransferDeserializer());
        builder.registerTypeAdapter(AssetAmount.class, new AssetAmount.AssetAmountDeserializer());
        builder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountSimpleDeserializer());
        builder.registerTypeAdapter(DynamicGlobalProperties.class, new DynamicGlobalProperties.DynamicGlobalPropertiesDeserializer());
        builder.registerTypeAdapter(Memo.class, new Memo.MemoDeserializer());
        builder.registerTypeAdapter(Authority.class, new Authority.AuthorityDeserializer());
        builder.registerTypeAdapter(Asset.class, new Asset.AssetDeserializer());
        builder.registerTypeAdapter(AccountOptions.class, new AccountOptions.AccountOptionsDeserializer());
        builder.registerTypeAdapter(ApiCall.class, new ApiCall.ApiCallSerializer());
        builder.registerTypeAdapter(AccountCreateOperation.class, new AccountCreateOperation.AccountCreateDeserializer());
    }

    public static Gson getGson() {
        return builder.create();
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getGson().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> className) {
        return getGson().fromJson(json, className);
    }

    public static String toJson(Object src) {
        return getGson().toJson(src);
    }

    public static <T> T mapJson(Object src, Class<T> className) {
        return fromJson(toJson(src), className);
    }

    public static <T> T mapJson(Object src, Type typeOfT) {
        return fromJson(toJson(src), typeOfT);
    }


}
