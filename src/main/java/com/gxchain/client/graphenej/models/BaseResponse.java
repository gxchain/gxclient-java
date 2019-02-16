package com.gxchain.client.graphenej.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nelson on 11/12/16.
 */
@Data public class BaseResponse {
    public long id;
    public Error error;


    @Data @NoArgsConstructor public static class Error {
        public ErrorData data;
        public int code;
        public String message;

        public Error(String message) {
            this.message = message;
        }
    }


    @Data @NoArgsConstructor public static class ErrorData {
        public int code;
        public String name;
        public String message;

        public ErrorData(String message) {
            this.message = message;
        }
    }
}
