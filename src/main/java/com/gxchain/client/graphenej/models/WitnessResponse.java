package com.gxchain.client.graphenej.models;

import lombok.Data;

/**
 * Generic witness response
 */
@Data
public class WitnessResponse<T> extends BaseResponse {
    public static final String KEY_ID = "id";
    public static final String KEY_RESULT = "result";

    public T result;
}
