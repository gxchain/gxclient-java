package com.gxchain.client.util;

import com.gxchain.client.graphenej.enums.OperationType;
import com.gxchain.client.graphenej.objects.Transaction;
import org.junit.Assert;
import org.junit.Test;

public class GXGsonUtilTest {


    @Test
    public void operationTypeTest() {
        Assert.assertEquals(5, OperationType.ACCOUNT_CREATE_OPERATION.getCode());
    }


    @Test
    public void transactionDeserializerTest() {
        String hex = "{\"ref_block_num\":7971,\"ref_block_prefix\":2672178873,\"expiration\":\"2019-04-01T13:16:24\"," +
                "\"operations\":[" +
                "[5,{\"fee\":{\"amount\":1000,\"asset_id\":\"1.3.1\"},\"extensions\":[],\"registrar\":\"1.2.1131733\",\"referrer\":\"1.2.1131733\",\"referrer_percent\":10000," +
                "\"name\":\"cherryreg22\",\"owner\":{\"weight_threshold\":1,\"account_auths\":[],\"key_auths\":[[\"GXC5u2vbL1ME9PBnJTRtBHRr8JCM7hDEiY4fJS5mY3HjyhvRj8tAP\",1]],\"address_auths\":[]},\"active\":{\"weight_threshold\":1,\"account_auths\":[],\"key_auths\":[[\"GXC5u2vbL1ME9PBnJTRtBHRr8JCM7hDEiY4fJS5mY3HjyhvRj8tAP\",1]],\"address_auths\":[]},\"options\":{\"memo_key\":\"GXC5u2vbL1ME9PBnJTRtBHRr8JCM7hDEiY4fJS5mY3HjyhvRj8tAP\",\"num_committee\":0,\"num_witness\":0,\"voting_account\":\"1.2.5\",\"votes\":[],\"extensions\":[]}}]],\"signatures\":[\"206b20fccb39d914c4af2682c2552f948a7e12039d08d70fad788e9d62a2724b511608ae8c754067e08eb83ba6131910c655f36de26c568c97cca18e54d3c271fe\"],\"extensions\":[]}";
        Transaction transaction = GXGsonUtil.fromJson(hex, Transaction.class);
        transaction.setChainId("4f7d07969c446f8342033acb3ab2ae5044cbe0fde93db02de75bd17fa8fd84b8");

        System.out.println(transaction.toJsonString());
    }


}
