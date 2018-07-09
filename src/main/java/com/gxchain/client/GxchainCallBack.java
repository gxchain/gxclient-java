package com.gxchain.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author liruobin
 * @since 2018/7/5 下午4:49
 */
@FunctionalInterface
public interface GxchainCallBack {
    void response(long blockHeight, String txid, JsonArray operation);
}
