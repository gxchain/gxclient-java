package com.gxchain.client.graphenej.models.contract;

import lombok.Data;

/**
 * @author liruobin
 * @since 2019/2/15 7:17 PM
 */
@Data
public class Action {

    private String name;

    private String type;

    private Boolean payable;
}
