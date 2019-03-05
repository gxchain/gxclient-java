package com.gxchain.client.rpc.api;


import com.gxchain.client.exception.HttpAccessFailException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class GxbApiFactory {
    private static final Logger logger = LoggerFactory.getLogger(GxbApiFactory.class);
    private ConcurrentHashMap<Class<?>, Object> typeCache = new ConcurrentHashMap<>();

    private Retrofit retrofit;

    private GxbApiFactory(final String baseUrl, final Long timeout) {
        OkHttpClient httpClient = new OkHttpClient().newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).addInterceptor(new Interceptor() {

            /*
             * 记录访问日志，统一接口异常处理
             *
             * @see okhttp3.Interceptor#intercept(okhttp3.Interceptor.Chain)
             */
            @Override
            public Response intercept(Chain chain) throws IOException {
                logger.info("gxb api request:" + chain.request().toString());
                Response response = chain.proceed(chain.request());
                logger.info("gxb response:" + response.toString());
                if (!response.isSuccessful()) {
                    throw new HttpAccessFailException(response.body().string());
                } else {
                    return response;
                }
            }

        }).writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
        httpClient.dispatcher().setMaxRequestsPerHost(100);
        httpClient.dispatcher().setMaxRequests(150);

        retrofit = new Retrofit.Builder().baseUrl(HttpUrl.parse(baseUrl)).addConverterFactory(GxbGsonConverterFactory.create()).callFactory(httpClient).build();
    }


    public <T> T newApi(Class<T> clz) {
        Object object = typeCache.get(clz);
        if (object != null) {
            return (T) object;
        } else {
            if (clz.isInterface()) {
                T result = retrofit.create(clz);
                typeCache.putIfAbsent(clz, result);
                return result;
            } else {
                throw new IllegalArgumentException("interface class required");
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl;
        private Long timeout = 15000L; // default 15s;

        public Builder baseUrl(String url) {
            this.baseUrl = url;
            return this;
        }

        public Builder timeout(Long ms) {
            this.timeout = ms;
            return this;
        }

        public GxbApiFactory build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("baseUrl invalid");
            }
            return new GxbApiFactory(this.baseUrl, timeout);
        }
    }
}
