package com.gxchain.client;

import com.alibaba.fastjson.JSON;
import com.gxchain.client.domian.KeyPair;
import com.gxchain.common.signature.MsgCryptUtil;
import com.gxchain.common.ws.client.graphenej.objects.Memo;
import com.gxchain.common.ws.client.graphenej.objects.Transaction;
import com.gxchain.common.ws.client.graphenej.operations.TransferOperation;
import com.gxchain.common.ws.client.util.WsGsonUtil;
import com.gxchain.client.domian.GxcAssetAmount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;


/**
 * @author liruobin
 * @since 2018/7/5 下午10:19
 */
@Slf4j
public class GxchainClientTest {
    String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
    String accountId = "1.2.323";
    String memoPrivate = privateKey;
    int assetPrecicion = 5;
    GxchainClient client = new GxchainClient(privateKey, accountId, "ws://192.168.1.118:28090");

    @Test
    public void generateKey() throws Exception {
        System.out.println(JSON.toJSONString(GxchainClient.generateKey()));
    }

    @Test
    public void register() throws Exception {
        KeyPair keyPair = GxchainClient.generateKey();
        System.out.println(client.register("lirb-test001", keyPair.getPublicKey(), "", "", "http://47.96.164.78:8888"));
    }

    @Test
    public void detectTransaction() throws Exception {
        client.latestIrreversibleBlockTask();
        client.detectTransaction(11042137, (blockHeight, txid, operation) -> {
            //deal with transfer operation
            if (operation.get(0).getAsInt() == 0) {
                TransferOperation op = WsGsonUtil.fromJson(operation.get(1).toString(), TransferOperation.class);
                if (op.getTo().getObjectId().equalsIgnoreCase(accountId)) {
                    Memo memo = op.getMemo();
                    if (memo != null) {
                        String decryptedMsg = MsgCryptUtil.decrypt(memoPrivate, memo.getSource().toString(), memo.getNonce().longValue(), memo.getByteMessage());
                        log.info("decryptedMsg:{}", decryptedMsg);
                    } else {
                        log.info("no memo,txid:{}", txid);
                    }
                }
                if (op.getFrom().getObjectId().equalsIgnoreCase(accountId)) {
                    log.info("{} should be confirmed", txid);
                }
            }
        });
    }

    @Test
    public void transfer() throws Exception {
        Transaction transaction = client.transfer("gxb456", null,
                GxcAssetAmount.builder().amount(new BigDecimal(0.01)).assetId("1.3.1").precision(assetPrecicion).build(), true);
        log.info(transaction.toJsonString());
        transaction = client.transfer("gxb456", null,
                GxcAssetAmount.builder().amount(new BigDecimal(0.015)).assetId("1.3.1").precision(assetPrecicion).build(), true);
        log.info(transaction.toJsonString());
    }
}