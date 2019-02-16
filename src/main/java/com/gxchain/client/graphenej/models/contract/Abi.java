package com.gxchain.client.graphenej.models.contract;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author liruobin
 * @since 2019/2/15 7:14 PM
 */
@Data
public class Abi {
    private String version;

    private JsonArray types;

    private List<Struct> structs;

    private List<Action> actions;

    private List<Table> tables;

    @SerializedName("error_messages")
    private JsonArray errorMessages;
    @SerializedName("abi_extensions")
    private JsonArray abiExtensions;
}
