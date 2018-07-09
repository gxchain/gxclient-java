package com.gxchain.client.domian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liruobin
 * @since 2018/7/5 下午3:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyPair {
    /**
     * 脑key
     */
    private String brainKey;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
}
