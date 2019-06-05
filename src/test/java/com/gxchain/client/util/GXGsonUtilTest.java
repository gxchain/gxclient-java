package com.gxchain.client.util;

import com.gxchain.client.graphenej.objects.Transaction;
import org.junit.Test;

/**
 * @author liruobin
 * @since 2019/6/4 9:31 PM
 */
public class GXGsonUtilTest {
    @Test
    public void transferDeserializerTest() {
        String hex = "{\"ref_block_num\": 33185,\n" +
                "      \"ref_block_prefix\": 1487534235,\n" +
                "      \"expiration\": \"2019-05-23T11:34:15\",\n" +
                "      \"operations\": [\n" +
                "        [\n" +
                "          0,\n" +
                "          {\n" +
                "            \"fee\": {\n" +
                "              \"amount\": 1000,\n" +
                "              \"asset_id\": \"1.3.1\"\n" +
                "            },\n" +
                "            \"from\": \"1.2.521\",\n" +
                "            \"to\": \"1.2.2136\",\n" +
                "            \"amount\": {\n" +
                "              \"amount\": 110000,\n" +
                "              \"asset_id\": \"1.3.1\"\n" +
                "            },\n" +
                "            \"extensions\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      ],\n" +
                "      \"extensions\": [],\n" +
                "      \"signatures\": [\n" +
                "        \"1f56095fd3ed01eea62d609c01d8f00160b42e5d12de7d0b9538bce63b80b02a53504cd67b152cca1348f8678afdbaeee32461d6946d1bb4ef83d2cf4a03e1b539\"\n" +
                "      ]}";
        Transaction transaction = GXGsonUtil.fromJson(hex, Transaction.class);
        transaction.setChainId("4f7d07969c446f8342033acb3ab2ae5044cbe0fde93db02de75bd17fa8fd84b8");

        System.out.println(transaction.toJsonObjectNoSign());
    }

    @Test
    public void createAccountDeserializerTest() {
        String hex = "{\"ref_block_num\": 25100,\n" +
                "      \"ref_block_prefix\": 2370734044,\n" +
                "      \"expiration\": \"2019-06-05T02:55:12\",\n" +
                "      \"operations\": [\n" +
                "        [\n" +
                "          5,\n" +
                "          {\n" +
                "            \"fee\": {\n" +
                "              \"amount\": 102,\n" +
                "              \"asset_id\": \"1.3.1\"\n" +
                "            },\n" +
                "            \"registrar\": \"1.2.26\",\n" +
                "            \"referrer\": \"1.2.26\",\n" +
                "            \"referrer_percent\": 0,\n" +
                "            \"name\": \"w13920644712\",\n" +
                "            \"owner\": {\n" +
                "              \"weight_threshold\": 1,\n" +
                "              \"account_auths\": [],\n" +
                "              \"key_auths\": [\n" +
                "                [\n" +
                "                  \"GXC6uQFDBDUYvR4mx3K4qTNSNJLckqozkXFKLAnYjJmhprJaC3PHL\",\n" +
                "                  1\n" +
                "                ]\n" +
                "              ],\n" +
                "              \"address_auths\": []\n" +
                "            },\n" +
                "            \"active\": {\n" +
                "              \"weight_threshold\": 1,\n" +
                "              \"account_auths\": [],\n" +
                "              \"key_auths\": [\n" +
                "                [\n" +
                "                  \"GXC6uQFDBDUYvR4mx3K4qTNSNJLckqozkXFKLAnYjJmhprJaC3PHL\",\n" +
                "                  1\n" +
                "                ]\n" +
                "              ],\n" +
                "              \"address_auths\": []\n" +
                "            },\n" +
                "            \"options\": {\n" +
                "              \"memo_key\": \"GXC6uQFDBDUYvR4mx3K4qTNSNJLckqozkXFKLAnYjJmhprJaC3PHL\",\n" +
                "              \"voting_account\": \"1.2.5\",\n" +
                "              \"num_witness\": 0,\n" +
                "              \"num_committee\": 0,\n" +
                "              \"votes\": [],\n" +
                "              \"extensions\": []\n" +
                "            },\n" +
                "            \"extensions\": {}\n" +
                "          }\n" +
                "        ]\n" +
                "      ],\n" +
                "      \"extensions\": [],\n" +
                "      \"signatures\": [\n" +
                "        \"2029cab05cc3d032ee402092c4b6ccc7b20f6380aab2dca4763448d50e69e3581b7494ed1f92a9da8e7a0053766e7cb112f8aff324d7e6e86f9be78d374319c377\"\n" +
                "      ]}";
        Transaction transaction = GXGsonUtil.fromJson(hex, Transaction.class);
        transaction.setChainId("4f7d07969c446f8342033acb3ab2ae5044cbe0fde93db02de75bd17fa8fd84b8");
        transaction.setPrivateKey("5J8CYVMcMz2f7vDc9fYcySbVUx7f3JnCJJVBeE6eWJmwZToTSqz");
        System.out.println(transaction.toJsonObject());
    }
}