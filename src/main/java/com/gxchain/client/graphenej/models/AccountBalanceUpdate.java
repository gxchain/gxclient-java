package com.gxchain.client.graphenej.models;

import com.gxchain.client.graphenej.objects.GrapheneObject;

import java.io.Serializable;

/**
 * Created by nelson on 1/12/17.
 */

public class AccountBalanceUpdate extends GrapheneObject implements Serializable {
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ASSET_TYPE = "asset_type";
    public static final String KEY_BALANCE = "balance";

    public String owner;
    public String asset_type;
    public long balance;

    public AccountBalanceUpdate(String id) {
        super(id);
    }
}
