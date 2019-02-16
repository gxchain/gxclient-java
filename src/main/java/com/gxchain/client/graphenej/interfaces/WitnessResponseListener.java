package com.gxchain.client.graphenej.interfaces;

import com.gxchain.client.graphenej.models.BaseResponse;
import com.gxchain.client.graphenej.models.WitnessResponse;

/**
 * Class used to represent any listener to network requests.
 */
public interface WitnessResponseListener {

    void onSuccess(WitnessResponse response);

    void onError(BaseResponse.Error error);
}
