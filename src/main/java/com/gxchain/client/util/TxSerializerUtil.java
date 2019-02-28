package com.gxchain.client.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.exception.GXChainApiException;
import com.gxchain.common.signature.utils.Util;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;

/**
 * @author liruobin
 * @since 2019/2/14 5:27 PM
 */
@Slf4j
public class TxSerializerUtil {
    private static ScriptEngine engine;

    static {
        log.info("init script engine");
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        try {
            InputStream inputStream = TxSerializerUtil.class.getClassLoader().getResourceAsStream("js/tx_serializer.min.js");
            StringBuilder script = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = br.readLine()) != null) {
                script.append(line);
            }
            engine.eval(script.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GXChainApiException(e.getMessage());
        }
        log.info("init script engine finish");
    }

    public static byte[] serializeTransaction(JsonObject transaction) {
        try {
            log.debug("start serialize");
            String result = (String) engine.eval("serializer.serializeTransaction(" + transaction.toString() + ").toString('hex')");
            log.debug("serialize finished:{}", result);
            return Util.hexToBytes(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GXChainApiException(e.getMessage());
        }
    }

    public static String serializeCallData(String methodName, JsonElement param, JsonElement abi) {
        try {
            String script = String.format("serializer.serializeCallData(\"%s\",%s,%s).toString('hex')", methodName, param == null ? new JsonObject().toString() : param.toString(), abi.toString());
            return (String) engine.eval(script);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GXChainApiException(e.getMessage());
        }
    }
}
