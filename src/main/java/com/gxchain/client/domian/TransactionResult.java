package com.gxchain.client.domian;

import com.google.gson.JsonElement;
import com.gxchain.client.graphenej.objects.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liruobin
 * @since 2019/2/16 3:04 PM
 */
@Getter
@AllArgsConstructor
public class TransactionResult {
    private Transaction transaction;

    private JsonElement result;
}
