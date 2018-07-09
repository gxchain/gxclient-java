package com.gxchain.client.domian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author liruobin
 * @since 2018/7/5 下午8:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GxcAssetAmount {

    private BigDecimal amount;

    private String assetId = "1.3.1";//GXS

    private int precision = 5;
}
