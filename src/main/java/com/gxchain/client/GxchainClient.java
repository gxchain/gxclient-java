package com.gxchain.client;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.gxchain.client.domian.GxcAssetAmount;
import com.gxchain.client.domian.KeyPair;
import com.gxchain.common.signature.KeyUtil;
import com.gxchain.common.signature.MsgCryptUtil;
import com.gxchain.common.signature.utils.PrivateKey;
import com.gxchain.common.signature.utils.PublicKey;
import com.gxchain.common.signature.utils.Util;
import com.gxchain.common.signature.utils.Wif;
import com.gxchain.common.ws.client.GxchainApiRestClient;
import com.gxchain.common.ws.client.GxchainClientFactory;
import com.gxchain.common.ws.client.GxchainWebSocketClient;
import com.gxchain.common.ws.client.exception.GxchainApiException;
import com.gxchain.common.ws.client.graphenej.Address;
import com.gxchain.common.ws.client.graphenej.errors.MalformedAddressException;
import com.gxchain.common.ws.client.graphenej.models.AccountProperties;
import com.gxchain.common.ws.client.graphenej.models.Block;
import com.gxchain.common.ws.client.graphenej.models.DynamicGlobalProperties;
import com.gxchain.common.ws.client.graphenej.models.WitnessResponse;
import com.gxchain.common.ws.client.graphenej.objects.*;
import com.gxchain.common.ws.client.graphenej.operations.BaseOperation;
import com.gxchain.common.ws.client.graphenej.operations.TransferOperation;
import com.gxchain.common.ws.client.graphenej.operations.TransferOperationBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * gxchain client
 *
 * @author liruobin
 * @since 2018/7/5 下午2:55
 */
@Slf4j
public class GxchainClient {
    /**
     * active private key
     */
    private String activePrivateKey;
    /**
     * memo private key
     */
    private String memoPrivateKey;
    /**
     * gxchain account id
     */
    private String accountId;
    /**
     * witness url
     */
    private String witness;
    /**
     * the latest irreversible block num
     */
    private long latestIrreversibleBlock;
    /**
     * fetching latest irreversible block task start or not
     */
    private boolean isTaskStart = false;

    GxchainWebSocketClient webSocketClient;

    GxchainApiRestClient apiRestClient;

    OkHttpClient httpClient = new OkHttpClient();

    public GxchainClient() {
    }

    public GxchainClient(String activePrivateKey, String accountId) {
        this(activePrivateKey, accountId, "wss://node1.gxb.io", activePrivateKey);
    }

    public GxchainClient(String activePrivateKey, String accountId, String witness) {
        this(activePrivateKey, accountId, witness, activePrivateKey);
    }

    public GxchainClient(String activePrivateKey, String accountId, String witness, String memoPrivateKey) {
        this.accountId = accountId;
        this.activePrivateKey = activePrivateKey;
        this.witness = witness;
        this.memoPrivateKey = memoPrivateKey;
        this.webSocketClient = GxchainClientFactory.getInstance().newWebSocketClient(this.witness);
        this.apiRestClient = GxchainClientFactory.getInstance().newRestCLient(this.witness.replace("wss://", "https://").replace("ws://", "http://"));
    }

    /**
     * generate key pair locally
     *
     * @return
     * @throws IOException
     */
    public static KeyPair generateKey() throws IOException {
        return generateKey(null);
    }

    /**
     * generate key pair locally
     *
     * @param brainKey
     * @return
     * @throws IOException
     */
    public static KeyPair generateKey(String brainKey) throws IOException {
        if (StringUtils.isBlank(brainKey)) {
            brainKey = KeyUtil.suggestBrainKey();
        }
        PrivateKey privateKey = KeyUtil.getBrainPrivateKey(brainKey, 0);
        PublicKey publicKey = KeyUtil.getPublicKey(privateKey);
        return KeyPair.builder().brainKey(brainKey).privateKey(privateKey.toWif()).publicKey(publicKey.getAddress()).build();
    }

    /**
     * register gxchain account
     * @param accountName
     * @param activePrivateKey
     * @return
     */
    public String register(String accountName, String activePrivateKey) {
        return register(accountName, activePrivateKey, "", "", "");
    }

    /**
     * register gxchain account
     *
     * @param accountName
     * @param activePrivateKey
     * @param ownerKey
     * @param memoKey
     */
    public String register(String accountName, String activePrivateKey, String ownerKey, String memoKey, String faucet) {
        if (StringUtils.isBlank(activePrivateKey)) {
            throw new GxchainApiException("active key is required");
        }

        if (StringUtils.isBlank(ownerKey)) {
            ownerKey = activePrivateKey;
        }

        if (StringUtils.isBlank(memoKey)) {
            memoKey = activePrivateKey;
        }
        if (StringUtils.isBlank(faucet)) {
            faucet = "https://opengateway.gxb.io";
        }

        JSONObject param = new JSONObject();
        JSONObject account = new JSONObject();
        account.put("name", accountName);
        account.put("active_key", activePrivateKey);
        account.put("owner_key", ownerKey);
        account.put("memo_key", memoKey);
        param.put("account", account);
        return postJson(faucet + "/account/register", param.toString());
    }

    private String postJson(String url, String content) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                log.error("请求失败：code:{},message:{},body:{}", response.code(), response.message(), response.body().string());
            }
        } catch (Exception e) {
            log.error("http请求发生异常：" + e.getMessage(), e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * fetching latest irreversible block each 3 seconds
     */
    public void latestIrreversibleBlockTask() {
        if (this.isTaskStart) {
            return;
        }
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        es.scheduleAtFixedRate(() -> {
            DynamicGlobalProperties properties = apiRestClient.getDynamicGlobalProperties();
            long latestBlock = properties.last_irreversible_block_num;
            if (this.latestIrreversibleBlock != latestBlock) {
                this.latestIrreversibleBlock = latestBlock;
                log.info("latest irreversible block：{}", this.latestIrreversibleBlock);
            }
        }, 0, 3, TimeUnit.SECONDS);
        this.isTaskStart = true;
    }

    /**
     * detect new transactions related to this.accountId
     *
     * @param blockHeight
     * @param callBack
     */
    public void detectTransaction(long blockHeight, GxchainCallBack callBack) {
        while (true) {
            try {
                Block block = apiRestClient.getBlock(blockHeight);
                if (block != null) {
                    if (block.transactions != null && block.transactions.length > 0) {
                        for (int i = 0; i < block.transactions.length; i++) {
                            String txid = block.transaction_ids[i];
                            Transaction transaction = block.transactions[i];
                            for (BaseOperation operation : transaction.getOperations()) {
                                boolean exist = false;
                                JsonArray op = operation.toJsonObject().getAsJsonArray();
                                for (Map.Entry<String, JsonElement> val : op.get(1).getAsJsonObject().entrySet()) {
                                    log.info(StringUtils.strip(val.getValue().toString(), "\""));
                                    if (StringUtils.equals(StringUtils.strip(val.getValue().toString(), "\""), this.accountId)) {
                                        exist = true;
                                    }
                                }

                                if (exist && callBack != null) {
                                    callBack.response(blockHeight, txid, op);
                                }
                            }
                        }
                    }
                    if (blockHeight < this.latestIrreversibleBlock) {
                        blockHeight++;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    /**
     * send transfer request to witness node
     *
     * @param toAccountName
     * @param memo
     * @param assetAmount
     * @param isBroadcast
     */
    public Transaction transfer(String toAccountName, String memo, GxcAssetAmount assetAmount, boolean isBroadcast) throws MalformedAddressException {
        AccountProperties toAccount = apiRestClient.getAccountByName(toAccountName);
        if (toAccount == null) {
            throw new GxchainApiException("to account not exist");
        }
        AccountProperties fromAccount = apiRestClient.getAccounts(Arrays.asList(this.accountId)).get(0);
        String memoFromPublicKey = fromAccount.getMemoPublicKey();
        String memoToPublicKey = toAccount.getMemoPublicKey();
        if (StringUtils.isNotBlank(memo)) {
            // The 1s are base58 for all zeros (null)
            if (Pattern.matches(memoFromPublicKey, "111111111111111111111")) {
                memoFromPublicKey = null;
            }

            if (Pattern.matches(memoToPublicKey, "111111111111111111111")) {
                memoToPublicKey = null;
            }
            Wif wif = new Wif(this.memoPrivateKey);
            PrivateKey pk = wif.getPrivateKey();
            if (!StringUtils.equals(KeyUtil.getPublicKey(pk).getAddress(), memoFromPublicKey)) {
                throw new GxchainApiException("memo signer not exist");
            }
        }
        Memo memoObject = null;
        if (StringUtils.isNotBlank(memo) && StringUtils.isNotBlank(memoFromPublicKey) && StringUtils.isNotBlank(memoToPublicKey)) {
            long nonce = RandomUtils.nextLong(0L, Long.MAX_VALUE);
            String message = MsgCryptUtil.encrypt(this.memoPrivateKey, memoToPublicKey, nonce, memo);
            memoObject = new Memo(new Address(memoFromPublicKey), new Address(memoToPublicKey), BigInteger.valueOf(nonce), Util.hexToBytes(message));
        }

        //构建转账交易对象
        UserAccount from = new UserAccount(this.accountId);//发起方
        UserAccount to = new UserAccount(toAccount.id);//接收方

        TransferOperation transferOperation =
                new TransferOperationBuilder().
                        setTransferAmount(new AssetAmount(assetAmount.getAmount().multiply(new BigDecimal(Math.pow(10, assetAmount.getPrecision()))).longValue(), assetAmount.getAssetId())).//交易金额
                        setSource(from).
                        setDestination(to).
                        setFee(new AssetAmount(0L, assetAmount.getAssetId())).
                        setMemo(memoObject).
                        build();
        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(transferOperation);
        Transaction transaction = new Transaction(this.activePrivateKey, null, operations);

        processTransaction(transaction, assetAmount.getAssetId(), isBroadcast);
        return transaction;
    }

    /**
     * process transaction
     *
     * @param transaction
     * @param isBroadcast
     */
    private void processTransaction(Transaction transaction, String assetId, boolean isBroadcast) {
        DynamicGlobalProperties dynamicProperties = apiRestClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;
        //最新的区块信息
        transaction.setBlockData(new BlockData(headBlockNumber, headBlockId, expirationTime));
        //设置交易费用
        transaction.setFees(apiRestClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));
        transaction.setChainId(apiRestClient.getChainId());
        if (!isBroadcast) {
            return;
        }
        WitnessResponse<JsonElement> response = webSocketClient.broadcastTransaction(transaction);
        if (response.error != null) {
            throw new GxchainApiException(response.error);
        }
    }
}
