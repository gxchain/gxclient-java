package com.gxchain.client.exception;

import com.gxchain.client.graphenej.models.BaseResponse;
import lombok.Getter;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:20
 */
public class GXChainApiException extends RuntimeException {
    @Getter
    private BaseResponse.Error error;

    public GXChainApiException() {
        super();
    }


    public GXChainApiException(String message) {
        super(message);
    }

    public GXChainApiException(BaseResponse.Error error) {
        this.error = error;
    }

    public GXChainApiException(Throwable cause) {
        super(cause);
    }


    public GXChainApiException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        if (error != null) {
            return error.getMessage();
        }
        return super.getMessage();
    }
}
