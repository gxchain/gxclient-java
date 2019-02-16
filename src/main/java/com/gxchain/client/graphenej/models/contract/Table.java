package com.gxchain.client.graphenej.models.contract;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author liruobin
 * @since 2019/2/15 7:17 PM
 */
@Data
public class Table {

    private String name;
    @SerializedName("index_type")
    private String indexType;

    @SerializedName("key_names")
    private List<String> keyNames;
    @SerializedName("key_types")
    private List<String> keyTypes;

    private String type;
}
