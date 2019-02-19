package com.gxchain.client;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.domian.KeyPair;
import com.gxchain.client.domian.TransactionResult;
import com.gxchain.client.domian.params.GetTableRowsParams;
import com.gxchain.client.graphenej.Util;
import com.gxchain.client.graphenej.models.AccountProperties;
import com.gxchain.client.graphenej.models.Block;
import com.gxchain.client.graphenej.models.contract.Abi;
import com.gxchain.client.graphenej.models.contract.Table;
import com.gxchain.client.graphenej.objects.*;
import com.gxchain.client.graphenej.operations.TransferOperation;
import com.gxchain.client.util.GXGsonUtil;
import com.gxchain.common.signature.MsgCryptUtil;
import com.gxchain.common.signature.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * @author liruobin
 * @since 2018/7/5 下午10:19
 */
@Slf4j
public class GXChainClientTest {
//    String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
//    String accountId = "1.2.323";
//    String accountName = "des-test";
//    String node = "ws://192.168.1.118:28090";

    String privateKey = "5J8CYVMcMz2f7vDc9fYcySbVUx7f3JnCJJVBeE6eWJmwZToTSqz";
    String accountId = "1.2.521";
    String accountName = "liruobin1";
    String node = "wss://testnet.gxchain.org";

    String memoPrivate = privateKey;
    int assetPrecicion = 5;
    GXChainClient client = new GXChainClient(privateKey, accountId, node);

    @Test
    public void generateKey() throws Exception {
        System.out.println(JSON.toJSONString(GXChainClient.generateKey()));
        /**
         *{
         "brainKey": "plass niche banian hurter spadone ligular fancify hayseed theres proxysm slub chess talisay orillon steam curtail",
         "privateKey": "5JTHkfd8gH6ebsSjRRJbVhEa7u5vh2YZTJH3qC2osjUM9XxKvKR",
         "publicKey": "GXC5bgYX7xNDt1YG7DjD178nK6x9phHAHjZJA7Ug3dkeefLsATiCQ"
         }
         */
    }

    @Test
    public void privateToPublic() {
        System.out.println(GXChainClient.privateToPublic("5JTHkfd8gH6ebsSjRRJbVhEa7u5vh2YZTJH3qC2osjUM9XxKvKR"));
    }

    @Test
    public void isValidPublic() {
        System.out.println(GXChainClient.isValidPublic("GXC5bgYX7xNDt1YG7DjD178nK6x9phHAHjZJA7Ug3dkeefLsATiCQ"));
    }

    @Test
    public void isValidPrivate() {
        System.out.println(GXChainClient.isValidPrivate("5JTHkfd8gH6ebsSjRRJbVhEa7u5vh2YZTJH3qC2osjUM9XxKvKR"));
    }

    @Test
    public void register() throws Exception {
        KeyPair keyPair = GXChainClient.generateKey();
        System.out.println(client.register("liruobin2", keyPair.getPublicKey(), "", "", "https://testnet.faucet.gxchain.org"));
        /**
         * {"ref_block_num":18490,"ref_block_prefix":827801284,"expiration":"2018-07-10T08:18:18","operations":[[5,{"fee":{"amount":114746,"asset_id":"1.3.0"},"registrar":"1.2.6","referrer":"1.2.6","referrer_percent":0,"name":"lirb-test002","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"active":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"options":{"memo_key":"GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"extensions":{}}]],"extensions":[],"signatures":["1f3a0c4cbeda10d5387296b1d6ecff8e2e47250427daad4efd30c2ff975ff43ce311d667f4e833c218f97dfc591b5370a0ca13d20e50a1cca84cb00d9cc2bdf1c3"]}
         */
    }

    @Test
    public void query() {
        JsonArray param = new JsonArray();
        JsonArray symbols = new JsonArray();
        symbols.add("GXC");
        symbols.add("PPS");
        param.add(symbols);
        //{"id":0,"method":"lookup_asset_symbols","params":[["GXC","PPS"]],"jsonrpc":"2.0"}
        JsonElement result = client.query("lookup_asset_symbols", param);
        log.info(result.toString());
    }

    @Test
    public void getObject() {
        JsonElement result = client.getObject("1.2.323");
        log.info(result.toString());
    }

    @Test
    public void getObjects() {
        JsonElement result = client.getObjects(Arrays.asList("1.2.1", "2.1.0", "1.3.1"));
        log.info(result.toString());
    }

    @Test
    public void getAccount() {
        AccountProperties accountProperties = client.getAccount(accountName);
        log.info(accountProperties.getId());
    }

    @Test
    public void getAccountByPublicKey() {
        log.info(JSON.toJSONString(client.getAccountByPublicKey(GXChainClient.privateToPublic(privateKey))));
    }

    @Test
    public void getAccountBalances() {
        List<AssetAmount> assetAmounts = client.getAccountBalances(accountName);
        log.info(assetAmounts.get(0).toJsonString());
    }

    @Test
    public void getAsset() {
        Asset asset = client.getAsset("GXC");
        log.info(GXGsonUtil.toJson(asset));
    }

    @Test
    public void getAssets() {
        List<Asset> assets = client.getAssets(Arrays.asList("GXC", "PPS"));
        log.info(GXGsonUtil.toJson(assets));
    }

    @Test
    public void getBlock() {
        Block block = client.getBlock(8973904);
        log.info(block.block_id);
    }

    @Test
    public void getChainId() {
        log.info(client.getChainID());
    }


    @Test
    public void transfer() throws Exception {
        TransactionResult transactionResult = client.transfer("liruobin2", "", "1 GXC", "GXC", true);
        log.info(transactionResult.getTransaction().toJsonString());
        log.info("txid:{},fee:{}", transactionResult.getTransaction().calculateTxid(), ((TransferOperation) transactionResult.getTransaction().getOperations().get(0)).getFee().getAmount().longValue() / Math.pow(10, 5));
        // > txid:2f9532ebc9ba12c285a0240f7fcc2ec24d4aa6d2,fee:0.0118
    }

    @Test
    public void vote() {
        TransactionResult transactionResult = client.vote(Arrays.asList("zhuliting"), "GXC", true);
        log.info(transactionResult.getTransaction().toJsonString());
    }

    @Test
    public void getContractABI() {
        Abi abi = client.getContractABI("hello22");
        log.info(GXGsonUtil.toJson(abi));
    }

    @Test
    public void getContractTable() {
        List<Table> tables = client.getContractTable("hello22");
        log.info(GXGsonUtil.toJson(tables));
    }

    @Test
    public void getTableRows() {
        JsonElement result = client.getTableRows("bank", "account", 0, -1);
        log.info(result.toString());
    }

    @Test
    public void getTableRowsEx() {
        GetTableRowsParams params = GetTableRowsParams.builder().lowerBound(0).upperBound(400).limit(10).build();
        JsonElement result = client.getTableRowsEx("bank", "account", params);
        log.info(result.toString());
    }

    @Test
    public void callContract() {
        JsonObject param = new JsonObject();
        param.addProperty("user", "robin");
        TransactionResult transactionResult = client.callContract("hello22", "hi", param, null, true);
        log.info(transactionResult.getTransaction().toJsonString());
//        JsonObject params = new JsonObject();
//        params.addProperty("to_account", "des-test");
//
//        JsonObject amount = new JsonObject();
//        amount.addProperty("asset_id", 1);
//        amount.addProperty("amount", 1000000);
//        params.add("amount", amount);
//        client.callContract("bank", "withdraw", params, null, false);
    }

    ///////////////////////////////////////////////


    @Test
    public void detectTransaction() throws Exception {
        client.latestIrreversibleBlockTask();
        client.detectTransaction(8973904, (blockHeight, txid, operation) -> {

            //deal with transfer operation
            if (operation.get(0).getAsInt() == 0) {
                TransferOperation op = GXGsonUtil.fromJson(operation.get(1).toString(), TransferOperation.class);
                log.info("{},{},{}", blockHeight, txid, op.toJsonString());
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
    public void proxyTransfer() throws Exception {
        BroadcastRequestParams requestParams =
                BroadcastRequestParams.builder().
                        from(new UserAccount(accountId)).
                        to(new UserAccount("1.2.2136")).
                        proxyAccount(new UserAccount(accountId)).
                        amount(new AssetAmount(10L, "1.3.1")).percentage(1000)
                        .memo("test").
                        expiration(DateTime.now().plusMinutes(30).getMillis() / 1000).
                        build();

        String sig = Util.bytesToHex(SignatureUtil.signature(requestParams.toBytes(), privateKey));//发起方私钥签名
        requestParams.setSignatures(Arrays.asList(sig));
        TransactionResult transactionResult = client.proxyTransfer("proxy test", "1.3.1", requestParams, true);
        log.info(transactionResult.getTransaction().toJsonString());
    }

    @Test
    public void diyOperation() {
        String data = "Hello GXChain!";
        log.info(client.diyOperation(data, "1.3.1", true).getTransaction().toJsonString());
    }
}
