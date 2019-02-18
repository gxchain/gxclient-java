package com.gxchain.client.rpc.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gxchain.client.domian.params.GetTableRowsParams;
import com.gxchain.client.exception.GXChainApiException;
import com.gxchain.client.graphenej.RPC;
import com.gxchain.client.graphenej.models.*;
import com.gxchain.client.graphenej.models.contract.Abi;
import com.gxchain.client.graphenej.models.contract.ContractAccountProperties;
import com.gxchain.client.graphenej.models.contract.Table;
import com.gxchain.client.graphenej.objects.Asset;
import com.gxchain.client.graphenej.objects.AssetAmount;
import com.gxchain.client.graphenej.operations.BaseOperation;
import com.gxchain.client.rpc.GXChainApiRestClient;
import com.gxchain.client.rpc.api.GxbApiFactory;
import com.gxchain.client.rpc.api.GXChainApiService;
import com.gxchain.client.util.GXGsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:39
 */
public class GXChainApiRestClientImpl implements GXChainApiRestClient {

    private GXChainApiService apiService;

    private String chainId = "";

    private Integer sequenceId = 0;

    public GXChainApiRestClientImpl(String url) {
        apiService = GxbApiFactory.builder().baseUrl(url).build().newApi(GXChainApiService.class);
    }

    private JsonElement execute(ApiCall apiCall) {
        try {
            WitnessResponse<JsonElement> response = apiService.call(apiCall).execute().body();
            if (response.error != null) {
                throw new GXChainApiException(response.error);
            }
            return response.getResult();
        } catch (IOException e) {
            throw new GXChainApiException(e);
        }
    }

    @Override
    public JsonElement query(String method, JsonArray params) {
        ApiCall apiCall = new ApiCall(method, params, RPC.VERSION, sequenceId++);
        return execute(apiCall);
    }

    @Override
    public String getChainId() {
        if (!StringUtils.isBlank(chainId)) {
            return chainId;
        }
        JsonArray emptyParams = new JsonArray();
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_CHAIN_ID, emptyParams, RPC.VERSION, sequenceId++);
        chainId = execute(apiCall).toString().replace("\"", "");
        return chainId;
    }

    @Override
    public DynamicGlobalProperties getDynamicGlobalProperties() {
        JsonArray emptyParams = new JsonArray();
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_DYNAMIC_GLOBAL_PROPERTIES, emptyParams, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), DynamicGlobalProperties.class);
    }

    @Override
    public Block getBlock(long blockHeight) {
        JsonArray accountParams = new JsonArray();
        accountParams.add(blockHeight);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_BLOCK, accountParams, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), Block.class);
    }

    @Override
    public JsonElement getObjects(List<String> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) {
            return null;
        }
        JsonArray params = new JsonArray();
        JsonArray params2 = new JsonArray();
        for (String objectId : objectIds) {
            params2.add(objectId);
        }
        params.add(params2);

        ApiCall apiCall = new ApiCall(0, RPC.GET_OBJECTS, params, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), JsonElement.class);
    }

    @Override
    public List<AssetAmount> getRequiredFees(List<BaseOperation> operations, Asset feeAsset) {
        JsonArray accountParams = new JsonArray();
        JsonArray operationParams = new JsonArray();
        for (BaseOperation baseOperation : operations) {
            operationParams.add(baseOperation.toJsonObject());
        }
        accountParams.add(operationParams);
        accountParams.add(feeAsset.getObjectId());
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_REQUIRED_FEES, accountParams, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), new TypeToken<List<AssetAmount>>() {
        }.getType());

    }

    @Override
    public List<AssetAmount> getAccountBalances(String accountId, List<String> assetIds) {
        JsonArray params = new JsonArray();
        JsonArray assetList = GXGsonUtil.mapJson(assetIds, JsonArray.class);
        params.add(accountId);
        params.add(assetList);
        ApiCall apiCall = new ApiCall(0, RPC.GET_ACCOUNT_BALANCES, params, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), new TypeToken<List<AssetAmount>>() {
        }.getType());
    }

    @Override
    public AccountProperties getAccountByName(String accountName) {
        JsonArray accountParams = new JsonArray();
        accountParams.add(accountName);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_ACCOUNT_BY_NAME, accountParams, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), AccountProperties.class);
    }

    @Override
    public ContractAccountProperties getContractAccountByName(String accountName) {
        JsonArray accountParams = new JsonArray();
        accountParams.add(accountName);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_ACCOUNT_BY_NAME, accountParams, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), ContractAccountProperties.class);
    }

    @Override
    public List<String> getAccountByPublicKey(String publicKey) {
        JsonArray params = new JsonArray();
        JsonArray publicKeyParams = new JsonArray();
        publicKeyParams.add(publicKey);
        params.add(publicKeyParams);
        ApiCall apiCall = new ApiCall(RPC.GET_KEY_REFERENCES, params, RPC.VERSION, sequenceId++);
        List<List<String>> accountIds = GXGsonUtil.mapJson(execute(apiCall), new TypeToken<List<List<String>>>() {
        }.getType());
        if (CollectionUtils.isEmpty(accountIds)) {
            return null;
        }
        return accountIds.get(0).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<AccountProperties> getAccounts(List<String> accountIds) {
        JsonArray params = new JsonArray();
        JsonArray accountIdList = GXGsonUtil.mapJson(accountIds, JsonArray.class);
        params.add(accountIdList);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_ACCOUNTS, params, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), new TypeToken<List<AccountProperties>>() {
        }.getType());
    }

    @Override
    public List<Asset> getAssets(List<String> symbols) {
        JsonArray params = new JsonArray();
        JsonArray symbolList = GXGsonUtil.mapJson(symbols, JsonArray.class);
        params.add(symbolList);
        return GXGsonUtil.mapJson(query(RPC.LOOKUP_ASSET_SYMBOLS, params), new TypeToken<List<Asset>>() {
        }.getType());
    }

    @Override
    public Abi getContractABI(String contractName) {
        ContractAccountProperties accountProperties = getContractAccountByName(contractName);
        return accountProperties == null ? null : accountProperties.getAbi();
    }

    @Override
    public List<Table> getContractTable(String contractName) {
        Abi abi = getContractABI(contractName);
        return abi == null ? null : abi.getTables();
    }

    @Override
    public JsonElement getTableRows(String contractName, String tableName, Number lowerBound, Number upperBound) {
        JsonArray params = new JsonArray();
        params.add(contractName);
        params.add(tableName);
        params.add(lowerBound);
        params.add(upperBound);
        return GXGsonUtil.mapJson(query(RPC.GET_TABLE_ROWS, params), JsonElement.class);
    }

    @Override
    public JsonElement getTableRowsEx(String contractName, String tableName, GetTableRowsParams getTableRowsParams) {
        JsonArray params = new JsonArray();
        params.add(contractName);
        params.add(tableName);
        params.add(GXGsonUtil.mapJson(getTableRowsParams, JsonObject.class));

        return GXGsonUtil.mapJson(query(RPC.GET_TABLE_ROWS, params), JsonElement.class);
    }

    @Override
    public JsonElement broadcast(JsonObject transaction) {
        JsonArray params = new JsonArray();
        params.add(transaction);
        ApiCall apiCall = new ApiCall(2, RPC.BROADCAST_TRANSACTION_SYNCHRONOUS, params, RPC.VERSION, sequenceId++);
        return GXGsonUtil.mapJson(execute(apiCall), JsonElement.class);
    }
}
