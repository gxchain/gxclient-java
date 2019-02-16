package com.gxchain.client.exception;

import com.gxchain.client.graphenej.models.BaseResponse;
import lombok.Getter;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:20
 */
public class GxchainApiException extends RuntimeException {
    @Getter
    private BaseResponse.Error error;

    public GxchainApiException() {
        super();
    }


    public GxchainApiException(String message) {
        super(message);
    }

    public GxchainApiException(BaseResponse.Error error) {
        this.error = error;
    }

    public GxchainApiException(Throwable cause) {
        super(cause);
    }


    public GxchainApiException(String message, Throwable cause) {
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
