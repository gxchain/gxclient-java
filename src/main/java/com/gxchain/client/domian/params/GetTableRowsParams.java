package com.gxchain.client.domian.params;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liruobin
 * @since 2019/2/16 5:49 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTableRowsParams {
    /**
     * 查询时指定的key最小值, 默认为0
     */
    @SerializedName("lower_bound")
    private Number lowerBound;
    /**
     * 查询时指定的key最大值，默认为-1，即最大的无符号整形
     */
    @SerializedName("upper_bound")
    private Number upperBound;
    /**
     * 查询时指定的index，默认为1，即第1个索引
     */
    @SerializedName("index_position")
    private Number indexPosition;
    /**
     * 查询时指定返回limit条，默认返回10条
     */
    private Number limit;
    /**
     * 查询结果按key的倒序输出，默认为0，即按key从小到大输出
     */
    private Boolean reverse;
}
