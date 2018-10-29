package com.gxchain.client;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonElement;
import com.gxchain.client.domian.GxcAssetAmount;
import com.gxchain.client.domian.KeyPair;
import com.gxchain.common.signature.MsgCryptUtil;
import com.gxchain.common.signature.SignatureUtil;
import com.gxchain.common.ws.client.constant.WSConstants;
import com.gxchain.common.ws.client.graphenej.objects.*;
import com.gxchain.common.ws.client.graphenej.operations.TransferOperation;
import com.gxchain.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;


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
        /**
         *{
             "brainKey": "plass niche banian hurter spadone ligular fancify hayseed theres proxysm slub chess talisay orillon steam curtail",
             "privateKey": "5JTHkfd8gH6ebsSjRRJbVhEa7u5vh2YZTJH3qC2osjUM9XxKvKR",
             "publicKey": "GXC5bgYX7xNDt1YG7DjD178nK6x9phHAHjZJA7Ug3dkeefLsATiCQ"
         }
         */
    }

    @Test
    public void register() throws Exception {
        KeyPair keyPair = GxchainClient.generateKey();
        System.out.println(client.register("lirb-test002", keyPair.getPublicKey(), "", "", "http://47.96.164.78:8888"));
        /**
         * {"ref_block_num":18490,"ref_block_prefix":827801284,"expiration":"2018-07-10T08:18:18","operations":[[5,{"fee":{"amount":114746,"asset_id":"1.3.0"},"registrar":"1.2.6","referrer":"1.2.6","referrer_percent":0,"name":"lirb-test002","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"active":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"options":{"memo_key":"GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"extensions":{}}]],"extensions":[],"signatures":["1f3a0c4cbeda10d5387296b1d6ecff8e2e47250427daad4efd30c2ff975ff43ce311d667f4e833c218f97dfc591b5370a0ca13d20e50a1cca84cb00d9cc2bdf1c3"]}
         */
    }

    @Test
    public void detectTransaction() throws Exception {
        client.latestIrreversibleBlockTask();
        client.detectTransaction(8973904, (blockHeight, txid, operation) -> {

            //deal with transfer operation
            if (operation.get(0).getAsInt() == 0) {
                TransferOperation op = WsGsonUtil.fromJson(operation.get(1).toString(), TransferOperation.class);
                log.info("{},{},{}",blockHeight,txid,op.toJsonString());
                /**
                 * eg.
                 * 8973904,fa7d92765dc845e90fd686eb90de4f888f742127,
                 * [ 0,{"fee": {"amount": 1000,"asset_id": "1.3.1"},
                 *      "from": "1.2.323",
                 *      "to": "1.2.21",
                 *      "amount": {"amount": 1000,"asset_id": "1.3.1"},
                 *      "extensions": []
                 * }]
                 */
                if (op.getTo().getObjectId().equalsIgnoreCase(accountId)) {
                    Memo memo = op.getMemo();
                    // decrypt memo if assigned
                    if (memo != null) {
                        String decryptedMsg = MsgCryptUtil.decrypt(memoPrivate, memo.getSource().toString(), memo.getNonce().longValue(), memo.getByteMessage());
                        log.info("decryptedMsg:{}", decryptedMsg);
                        // TODO: Persistent blockHeight, txid and operation to the database,
                        // it's recommended to use blockHeight and txid as the primary key
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
        Transaction transaction = client.transfer("gxb456", "GXChain NB",
                GxcAssetAmount.builder().amount(new BigDecimal(0.01)).assetId("1.3.1").precision(assetPrecicion).build(), false);
        log.info(transaction.toJsonString());
        log.info("txid:{},fee:{}",transaction.calculateTxid(),((TransferOperation)transaction.getOperations().get(0)).getFee().getAmount().longValue()/ Math.pow(10, assetPrecicion));
        // > txid:2f9532ebc9ba12c285a0240f7fcc2ec24d4aa6d2,fee:0.0118
        // Since gxchain implemented dpos consensus, the transaction will be confirmed until the block becomes irreversible
        // You can find the logic when a transfer operation was confirmed in the example of detectTransaction
        transaction = client.transfer("gxb456", null,
                GxcAssetAmount.builder().amount(new BigDecimal(0.015)).assetId("1.3.1").precision(assetPrecicion).build(), false);
        log.info(transaction.toJsonString());
    }

    @Test
    public void proxyTransfer()throws Exception {
        BroadcastRequestParams requestParams =
                BroadcastRequestParams.builder().
                        from(new UserAccount(accountId)).
                        to(new UserAccount("1.2.21")).
                        proxyAccount(new UserAccount(accountId)).
                        amount(new AssetAmount(10L, WSConstants.GXS_ASSET_ID)).percentage(1000)
                        .memo("test").
                        expiration(DateTime.now().plusMinutes(30).getMillis()/1000).
                        build();

        String sig = SignatureUtil.signature(requestParams.toBytes(),privateKey);//发起方私钥签名
        requestParams.setSignatures(Arrays.asList(sig));
        Transaction transaction = client.proxyTransfer("proxy test",WSConstants.GXS_ASSET_ID,requestParams,false);
        log.info(transaction.toJsonString());
    }

    @Test
    public void diyOperation(){
        String data = "Hello GXChain!";
        log.info(client.diyOperation(data,WSConstants.GXS_ASSET_ID,true).toJsonString());
    }

    @Test
    public void getObjects(){
        JsonElement result = client.getObjects(Arrays.asList("1.2.1","2.1.0","1.3.1"));
        log.info(result.toString());
    }
}
