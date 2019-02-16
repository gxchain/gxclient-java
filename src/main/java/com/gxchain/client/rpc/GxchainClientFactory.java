package com.gxchain.client.rpc;


import com.gxchain.client.rpc.impl.GxchainApiRestClientImpl;

/**
 * gxchain client 工厂类
 *
 * @author liruobin
 * @since 2018/7/3 上午10:15
 */
public class GxchainClientFactory {
    private static GxchainClientFactory clientFactory = new GxchainClientFactory();

    public static GxchainClientFactory getInstance() {
        return clientFactory;
    }
    /**
     * 创建http client
     * @param url
     * @return
     */
    public GxchainApiRestClient newRestCLient(String url) {
        return new GxchainApiRestClientImpl(url);
    }
}
