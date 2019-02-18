package com.gxchain.client.rpc;


import com.gxchain.client.rpc.impl.GXChainApiRestClientImpl;

/**
 * gxchain client 工厂类
 *
 * @author liruobin
 * @since 2018/7/3 上午10:15
 */
public class GXChainClientFactory {
    private static GXChainClientFactory clientFactory = new GXChainClientFactory();

    public static GXChainClientFactory getInstance() {
        return clientFactory;
    }
    /**
     * 创建http client
     * @param url
     * @return
     */
    public GXChainApiRestClient newRestCLient(String url) {
        return new GXChainApiRestClientImpl(url);
    }
}
