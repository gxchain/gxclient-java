package com.gxchain.client.graphenej.models;

import com.gxchain.client.graphenej.objects.Transaction;

/**
 * @author liruobin
 * @since 2018/7/5 下午4:36
 */
public class Block {
    public String previous;
    public String timestamp;
    public String witness;
    public String transaction_merkle_root;
    public Object[] extension;
    public String witness_signature;
    public String block_id;
    public String signing_key;
    public String[] transaction_ids;
    public Transaction[] transactions;

}
