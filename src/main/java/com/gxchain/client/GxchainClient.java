package com.gxchain.client;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.domian.KeyPair;
import com.gxchain.client.domian.TransactionResult;
import com.gxchain.client.graphenej.models.contract.Abi;
import com.gxchain.client.graphenej.models.contract.ContractAccountProperties;
import com.gxchain.client.graphenej.models.contract.Table;
import com.gxchain.client.rpc.GxchainClientFactory;
import com.gxchain.client.util.GXGsonUtil;
import com.gxchain.client.util.TxSerializerUtil;
import com.gxchain.common.signature.KeyUtil;
import com.gxchain.common.signature.MsgCryptUtil;
import com.gxchain.common.signature.utils.PrivateKey;
import com.gxchain.common.signature.utils.PublicKey;
import com.gxchain.common.signature.utils.Util;
import com.gxchain.common.signature.utils.Wif;
import com.gxchain.client.rpc.GxchainApiRestClient;
import com.gxchain.client.exception.GxchainApiException;
import com.gxchain.client.graphenej.Address;
import com.gxchain.client.graphenej.errors.MalformedAddressException;
import com.gxchain.client.graphenej.models.AccountProperties;
import com.gxchain.client.graphenej.models.Block;
import com.gxchain.client.graphenej.models.DynamicGlobalProperties;
import com.gxchain.client.graphenej.objects.*;
import com.gxchain.client.graphenej.operations.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * entry point network address
     */
    private String entryPoint;
    /**
     * the latest irreversible block num
     */
    private long latestIrreversibleBlock;
    /**
     * fetching latest irreversible block task start or not
     */
    private boolean isTaskStart = false;
    @Getter
    private GxchainApiRestClient apiRestClient;

    private OkHttpClient httpClient = new OkHttpClient();

    public GxchainClient() {
    }

    /**
     * @param activePrivateKey - private key
     * @param accountIdOrName  - account id or account name ,e.g: '1.2.423'|'account1'
     */
    public GxchainClient(String activePrivateKey, String accountIdOrName) {
        this(activePrivateKey, accountIdOrName, "wss://node1.gxb.io", activePrivateKey);
    }

    /**
     * @param activePrivateKey - private key
     * @param accountIdOrName  - account id or account name ,e.g: '1.2.423'|'account1'
     * @param entryPoint       - entry point network address
     */
    public GxchainClient(String activePrivateKey, String accountIdOrName, String entryPoint) {
        this(activePrivateKey, accountIdOrName, entryPoint, activePrivateKey);
    }

    /**
     * @param activePrivateKey - private key
     * @param accountIdOrName  - account id or account name ,e.g: '1.2.423'|'account1'
     * @param entryPoint       - entry point network address
     * @param memoPrivateKey   - account memo private key
     */
    public GxchainClient(String activePrivateKey, String accountIdOrName, String entryPoint, String memoPrivateKey) {
        this.activePrivateKey = activePrivateKey;
        this.entryPoint = entryPoint;
        this.memoPrivateKey = memoPrivateKey;
        this.apiRestClient = GxchainClientFactory.getInstance().newRestCLient(this.entryPoint.replace("wss://", "https://").replace("ws://", "http://"));
        if (Pattern.matches("^1\\.2\\.\\d+$", accountIdOrName)) {
            this.accountId = accountIdOrName;
        } else {
            AccountProperties accountProperties = getAccount(accountIdOrName);
            if (accountProperties == null) {
                throw new GxchainApiException("Account " + accountIdOrName + " not exist");
            }
            this.accountId = accountProperties.getId();
        }
    }

    //////////////////
    ////Keypair API///
    //////////////////

    /**
     * generate key pair locally
     *
     * @return key pair
     * @throws IOException
     */
    public static KeyPair generateKey() throws IOException {
        return generateKey(null);
    }

    /**
     * generate key pair locally
     *
     * @param brainKey
     * @return key pair
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
     * export public key from private key
     *
     * @param privateKey
     * @return publicKey
     */
    public static String privateToPublic(String privateKey) {
        return KeyUtil.getPublicKey(new Wif(privateKey).getPrivateKey()).getAddress();
    }

    /**
     * check if public key is valid
     *
     * @param publicKey
     * @return boolean
     */
    public static boolean isValidPublic(String publicKey) {
        try {
            new Address(publicKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * check if private key is valid
     *
     * @param privateKey
     * @return boolean
     */
    public static boolean isValidPrivate(String privateKey) {
        try {
            new Wif(privateKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    //////////////////
    ////Faucet API////
    //////////////////

    /**
     * register gxchain account
     *
     * @param accountName      - Account name
     * @param activePrivateKey - Public Key for account operator
     * @return
     */
    public String register(String accountName, String activePrivateKey) {
        return register(accountName, activePrivateKey, "", "", "");
    }

    /**
     * register gxchain account
     *
     * @param accountName      - Account name
     * @param activePrivateKey - Public Key for account operator
     * @param ownerKey         - Public Key for account owner
     * @param memoKey          - Public Key for memo
     * @param faucet           - faucet url
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
    /////////////////
    ////chain API////
    /////////////////

    /**
     * get block object
     * 1.2.* account
     * 1.3.* asset
     * 2.1.0 the newest block
     *
     * @param objectId object id
     * @return block object
     */
    public JsonElement getObject(String objectId) {
        return apiRestClient.getObjects(Arrays.asList(objectId)).getAsJsonArray().get(0);
    }

    /**
     * get block objects
     * 1.2.* account
     * 1.3.* asset
     * 2.1.0 the newest block
     *
     * @param objectIds object ids
     * @return block objects
     */
    public JsonElement getObjects(List<String> objectIds) {
        return apiRestClient.getObjects(objectIds);
    }

    /**
     * get account info by account name
     *
     * @param accountName
     * @return account info
     */
    public AccountProperties getAccount(String accountName) {
        return apiRestClient.getAccountByName(accountName);
    }

    /**
     * get account_ids by public key
     *
     * @param publicKey
     * @return account_ids
     */
    public List<String> getAccountByPublicKey(String publicKey) {
        return apiRestClient.getAccountByPublicKey(publicKey);
    }

    /**
     * get account balances by account name
     *
     * @param accountName
     * @return balances
     */
    public List<AssetAmount> getAccountBalances(String accountName) {
        AccountProperties accountProperties = getAccount(accountName);
        return apiRestClient.getAccountBalances(accountProperties.getId(), Collections.emptyList());
    }

    /**
     * get asset info by symbol
     *
     * @param symbol e.g: 'GXC'
     * @return asset
     */
    public Asset getAsset(String symbol) {
        return apiRestClient.getAssets(Arrays.asList(symbol)).get(0);
    }

    /**
     * get block by block height
     *
     * @param blockHeight
     * @return block info
     */
    public Block getBlock(long blockHeight) {
        return apiRestClient.getBlock(blockHeight);
    }

    /**
     * get current blockchain id
     *
     * @return blockchain id
     */
    public String getChainID() {
        return apiRestClient.getChainId();
    }

    /**
     * get dynamic global properties
     * @return
     */
    public DynamicGlobalProperties getDynamicGlobalProperties(){
        return apiRestClient.getDynamicGlobalProperties();
    }

    /**
     * send transfer request to entryPoint node
     *
     * @param toAccountName - to account name
     * @param memo          - memo
     * @param assetAmount   - transfer amount ,e.g: '1 GXC'
     * @param isBroadcast
     */
    public TransactionResult transfer(String toAccountName, String memo, String assetAmount, boolean isBroadcast) throws MalformedAddressException {
        return transfer(toAccountName, memo, assetAmount, null, isBroadcast);
    }

    /**
     * send transfer request to entryPoint node
     *
     * @param toAccountName - to account name
     * @param memo          - memo
     * @param assetAmount   - transfer amount ,e.g: '1 GXC'
     * @param feeAsset      - fee asset symbol ,e.g: 'GXC'
     * @param isBroadcast   - Broadcast the transaction or just return a serialized transaction
     */
    public TransactionResult transfer(String toAccountName, String memo, String assetAmount, String feeAsset, boolean isBroadcast) throws MalformedAddressException {
        String[] assets = assetAmount.split(" ");
        if (assets.length != 2 || !Pattern.matches("^\\d+(\\.\\d+)?$", assets[0])) {
            throw new GxchainApiException("Incorrect format of asset, eg. '100 GXC'");
        }
        //查询to账户信息
        AccountProperties toAccount = getAccount(toAccountName);
        if (toAccount == null) {
            throw new GxchainApiException("Account " + toAccountName + " not exist");
        }
        Asset asset = getAsset(assets[1]);
        if (asset == null) {
            throw new GxchainApiException("Asset " + assets[1] + " not exist");
        }
        //手续费币种id
        String feeAssetId = asset.getObjectId();
        if (StringUtils.isNotBlank(feeAsset) && !StringUtils.equals(assets[1], feeAsset)) {
            Asset asset2 = getAsset(feeAsset);
            if (asset == null) {
                throw new GxchainApiException("Asset " + assets[1] + " not exist");
            }
            feeAssetId = asset2.getObjectId();
        }
        //查询from账户信息
        AccountProperties fromAccount = apiRestClient.getAccounts(Arrays.asList(this.accountId)).get(0);
        //构建memo
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
        UserAccount to = new UserAccount(toAccount.getId());//接收方

        TransferOperation transferOperation =
                new TransferOperationBuilder().
                        setTransferAmount(new AssetAmount(new BigDecimal(assets[0]).multiply(new BigDecimal(Math.pow(10, asset.getPrecision()))).longValue(), asset.getObjectId())).//交易金额
                        setSource(from).
                        setDestination(to).
                        setFee(new AssetAmount(0L, feeAssetId)).
                        setMemo(memoObject).
                        build();

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(transferOperation);
        return processTransaction(operations, feeAssetId, isBroadcast);
    }

    /**
     * vote for accounts
     *
     * @param accountNames   - An array of account_name to vote
     * @param feePayingAsset - The asset to pay the fee e.g: 'GXC'
     * @param isBroadcast    - Broadcast the transaction or just return a serialized transaction
     */
    public TransactionResult vote(List<String> accountNames, String feePayingAsset, boolean isBroadcast) {
        List<String> accountIds = new ArrayList<>();
        for (String account : accountNames) {
            AccountProperties accountProperties = getAccount(account);
            if (accountProperties == null) {
                throw new GxchainApiException("account [" + account + "] not exist");
            }
            accountIds.add(accountProperties.getId());
        }
        return voteForIds(accountIds, feePayingAsset, isBroadcast);
    }

    /**
     * vote for accounts
     *
     * @param accountIds     - An array of account_id to vote
     * @param feePayingAsset - The asset to pay the fee e.g: 'GXC'
     * @param isBroadcast    - Broadcast the transaction or just return a serialized transaction
     */
    public TransactionResult voteForIds(List<String> accountIds, String feePayingAsset, boolean isBroadcast) {

        AccountProperties account = apiRestClient.getAccounts(Arrays.asList(this.accountId)).get(0);
        if (account == null) {
            throw new GxchainApiException("account_id [" + this.accountId + "] not exist");
        }
        //获取资产
        Asset feeAsset = getAsset(feePayingAsset);
        if (feeAsset == null) {
            throw new GxchainApiException("Asset " + feePayingAsset + " not exist");
        }
        JsonElement globalObject = getObject("2.0.0");

        AccountOptions accountOptions = new AccountOptions(account.getOptions().getMemoKey());
        if (account.getOptions().getVotingAccount() != null) {
            accountOptions.setVotingAccount(account.getOptions().getVotingAccount());
        }

        List<Vote> votes = new ArrayList<>();
        for (String accountId : accountIds) {
            JsonArray param = new JsonArray();
            param.add(accountId);
            JsonObject result1 = GXGsonUtil.fromJson(apiRestClient.query("get_witness_by_account", param), JsonObject.class);
            if (result1 != null) {
                votes.add(new Vote(result1.get("vote_id").getAsString()));
            }
            JsonObject result2 = GXGsonUtil.fromJson(apiRestClient.query("get_committee_member_by_account", param), JsonObject.class);
            if (result2 != null) {
                votes.add(new Vote(result2.get("vote_id").getAsString()));
                votes.add(new Vote(result2.get("vote_id").getAsString()));
            }
        }
        // only merge you votes into current selections
        // if you want cancel your votes, please operate it in your wallet
        // eg. Visit https://wallet.gxb.io
        votes.addAll(Arrays.asList(account.getOptions().getVotes()));
        votes = votes.stream().distinct().collect(Collectors.toList());
        int num_witness = 0;
        int num_committee = 0;
        for (Vote vote : votes) {
            if (vote.getType() == 0) {
                num_witness++;
            } else if (vote.getType() == 1) {
                num_committee++;
            }
        }
        num_witness = Math.min(num_witness, globalObject.getAsJsonObject().getAsJsonObject("parameters").get("maximum_committee_count").getAsInt());
        num_committee = Math.min(num_committee, globalObject.getAsJsonObject().getAsJsonObject("parameters").get("maximum_witness_count").getAsInt());
        votes.sort((a, b) -> {
            if (a.getInstance() > b.getInstance()) {
                return 1;
            } else if (a.getInstance() == b.getInstance()) {
                return 0;
            } else {
                return -1;
            }
        });
        accountOptions.setVotes(votes.toArray(new Vote[1]));
        accountOptions.setNumWitness(num_witness);
        accountOptions.setNumComittee(num_committee);

        AccountUpdateOperation accountUpdateOperation = new AccountUpdateOperationBuilder().
                setAccount(new UserAccount(this.accountId)).
                setOptions(accountOptions).
                setFee(new AssetAmount(0L, feeAsset.getObjectId())).
                build();

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(accountUpdateOperation);
        return processTransaction(operations, feeAsset.getObjectId(), isBroadcast);
    }

    /**
     * broadcast transaction
     *
     * @param transaction
     * @return
     */
    public TransactionResult broadcast(Transaction transaction) {
        return new TransactionResult(transaction, apiRestClient.broadcast(transaction.toJsonObject()));
    }

    /**
     * process transaction
     *
     * @param operations
     * @param feeAssetId
     * @param isBroadcast
     */
    private TransactionResult processTransaction(ArrayList<BaseOperation> operations, String feeAssetId, boolean isBroadcast) {
        Transaction transaction = new Transaction(this.activePrivateKey, operations);
        DynamicGlobalProperties dynamicProperties = apiRestClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;
        //最新的区块信息
        transaction.setBlockData(new BlockData(headBlockNumber, headBlockId, expirationTime));
        //设置交易费用
        transaction.setFees(apiRestClient.getRequiredFees(transaction.getOperations(), new Asset(feeAssetId)));
        transaction.setChainId(apiRestClient.getChainId());

        JsonElement result = null;
        if (isBroadcast) {
            result = apiRestClient.broadcast(transaction.toJsonObject());
        }
        return new TransactionResult(transaction, result);
    }

    /**
     * get contract abi by contract_name
     *
     * @param contractName - contract name
     * @return
     */
    public Abi getContractABI(String contractName) {
        return apiRestClient.getContractABI(contractName);
    }

    /**
     * get contract table by contract_name
     *
     * @param contractName - contract name
     * @return
     */
    public List<Table> getContractTable(String contractName) {
        return apiRestClient.getContractTable(contractName);
    }

    /**
     * get contract table rows
     *
     * @param contractName - contract_name
     * @param tableName    - table name
     * @param lowerBound   - key min value,default 0
     * @param upperBound   - key max value,default -1,unsigned max value
     * @return
     */
    public JsonElement getTableRows(String contractName, String tableName, Number lowerBound, Number upperBound) {
        return apiRestClient.getTableRows(contractName, tableName, lowerBound, upperBound);
    }

    /**
     * call smart contract method
     *
     * @param contractName - The name of the smart contract
     * @param methodName   - Method/Action name
     * @param param        - parameters
     * @param assetAmount  - payable method required ,e.g: '1 GXC'
     * @param isBroadcast  - Broadcast the transaction or just return a serialized transaction
     * @return
     */
    public TransactionResult callContract(String contractName, String methodName, JsonElement param, String assetAmount, boolean isBroadcast) {
        if (StringUtils.isBlank(assetAmount)) {
            assetAmount = "0 GXC";
        }
        String[] assets = assetAmount.split(" ");
        if (assets.length != 2 || !Pattern.matches("^\\d+(\\.\\d+)?$", assets[0])) {
            throw new GxchainApiException("Incorrect format of asset, eg. '100 GXC'");
        }

        Asset asset = getAsset(assets[1]);
        if (asset == null) {
            throw new GxchainApiException("Asset " + assets[1] + " not exist");
        }

        ContractAccountProperties contractAccount = apiRestClient.getContractAccountByName(contractName);
        if (contractAccount == null) {
            throw new GxchainApiException("Contract " + contractName + " not found");
        }
        String data = TxSerializerUtil.serializeCallData(methodName, param, GXGsonUtil.mapJson(contractAccount.getAbi(), JsonElement.class));

        AssetAmount amount = new AssetAmount(new BigDecimal(assets[0]).multiply(new BigDecimal(Math.pow(10, asset.getPrecision()))).longValue(), asset.getObjectId());

        CallContractOperation operation = new CallContractOperation(new AssetAmount(0L, asset.getObjectId()),
                new UserAccount(this.accountId), new UserAccount(contractAccount.getId()),
                methodName, amount, data);

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(operation);
        return processTransaction(operations, asset.getObjectId(), isBroadcast);
    }


    //////////////////////////////////////////////////////////////////

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
    //////////////////////////////////////////////////////////////////////////

    /**
     * proxy transfer
     *
     * @param proxyMemo
     * @param feeAssetId
     * @param requestParams
     * @param isBroadcast
     * @return
     */
    public TransactionResult proxyTransfer(String proxyMemo, String feeAssetId, BroadcastRequestParams requestParams, boolean isBroadcast) {
        BroadcastOperation broadcastOperation = new BroadcastOperation();
        broadcastOperation.setRequestParams(requestParams);
        broadcastOperation.setExtensions(new Extensions());
        broadcastOperation.setProxyMemo(proxyMemo);
        broadcastOperation.setFee(new AssetAmount(0L, feeAssetId));
        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(broadcastOperation);
        return processTransaction(operations, feeAssetId, isBroadcast);
    }

    /**
     * proxy transfer
     *
     * @param proxyMemos
     * @param feeAssetId
     * @param requestParams
     * @param isBroadcast
     * @return
     */
    public TransactionResult proxyTransfer(List<String> proxyMemos, String feeAssetId, List<BroadcastRequestParams> requestParams, boolean isBroadcast) {
        ArrayList<BaseOperation> operations = new ArrayList<>();
        int i = 0;
        for (BroadcastRequestParams broadcastRequestParams : requestParams) {
            BroadcastOperation broadcastOperation = new BroadcastOperation();
            broadcastOperation.setRequestParams(broadcastRequestParams);
            broadcastOperation.setExtensions(new Extensions());
            broadcastOperation.setProxyMemo(proxyMemos.get(i++));
            broadcastOperation.setFee(new AssetAmount(0L, feeAssetId));
            operations.add(broadcastOperation);
        }
        return processTransaction(operations, feeAssetId, isBroadcast);
    }

    /**
     * diy operation
     *
     * @param data
     * @param feeAssetId
     * @param isBroadcast
     * @return
     */
    public TransactionResult diyOperation(String data, String feeAssetId, boolean isBroadcast) {
        DiyOperation diyOperation = new DiyOperation();
        diyOperation.setPayer(new UserAccount(this.accountId));
        diyOperation.setD(0);
        diyOperation.setRequiredAuths(new Extensions());
        diyOperation.setData(data);
        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(diyOperation);
        return processTransaction(operations, feeAssetId, isBroadcast);
    }
}
