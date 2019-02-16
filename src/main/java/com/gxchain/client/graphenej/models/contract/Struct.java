package com.gxchain.client.graphenej.models.contract;

import lombok.Data;

import java.util.List;

/**
 * @author liruobin
 * @since 2019/2/15 7:15 PM
 */
@Data
public class Struct {

    private String name;

    private String base;

    private List<Field> fields;
}
