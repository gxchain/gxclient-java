package com.gxchain.client.rpc.api;

import com.google.gson.JsonElement;
import com.gxchain.client.graphenej.models.ApiCall;
import com.gxchain.client.graphenej.models.WitnessResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:33
 */
public interface GXChainApiService {
    @POST("/rpc")
    Call<WitnessResponse<JsonElement>> call(@Body ApiCall apiCall);
}
