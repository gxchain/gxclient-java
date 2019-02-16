package com.gxchain.client.graphenej.models;

import com.google.gson.*;
import com.gxchain.client.graphenej.interfaces.JsonSerializable;
import com.gxchain.client.util.GXGsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Class used to build a Graphene websocket API call.
 *
 * @see <a href="http://docs.bitshares.org/api/websocket.html">Websocket Calls & Notifications</a>
 */
public class ApiCall implements JsonSerializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCall.class);
    public static final String KEY_SEQUENCE_ID = "id";
    public static final String KEY_METHOD = "method";
    public static final String KEY_PARAMS = "params";
    public static final String KEY_JSON_RPC = "jsonrpc";

    public String method;
    public String methodToCall;
    public String jsonrpc;
    public JsonArray params;
    public Integer apiId;
    public Integer sequenceId;

    public ApiCall(Integer apiId, String methodToCall, JsonArray params, String jsonrpc, Integer sequenceId) {
        this.apiId = apiId;
        this.method = "call";
        this.methodToCall = methodToCall;
        this.jsonrpc = jsonrpc;
        this.params = params;
        this.sequenceId = sequenceId;
    }

    public ApiCall(String method, JsonArray params, String jsonrpc, Integer sequenceId) {
        this.method = method;
        this.jsonrpc = jsonrpc;
        this.params = params;
        this.sequenceId = sequenceId;
    }

    @Override
    public String toJsonString() {
        return GXGsonUtil.toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty(KEY_SEQUENCE_ID, this.sequenceId);
        obj.addProperty(KEY_METHOD, this.method);
        JsonArray paramsArray = new JsonArray();
        if (this.apiId != null && StringUtils.isNotBlank(this.methodToCall)) {
            paramsArray.add(this.apiId);
            paramsArray.add(this.methodToCall);
            paramsArray.add(this.params);
        } else {
            paramsArray = this.params;
        }
        obj.add(KEY_PARAMS, paramsArray);
        obj.addProperty(KEY_JSON_RPC, this.jsonrpc);
        return obj;
    }

    public static class ApiCallSerializer implements JsonSerializer<ApiCall> {

        @Override
        public JsonElement serialize(ApiCall apiCall, Type type, JsonSerializationContext jsonSerializationContext) {
            return apiCall.toJsonObject();
        }
    }
}
