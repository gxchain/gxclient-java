package com.gxchain.client.rpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gxchain.client.domian.params.GetTableRowsParams;
import com.gxchain.client.graphenej.models.AccountProperties;
import com.gxchain.client.graphenej.models.Block;
import com.gxchain.client.graphenej.models.DynamicGlobalProperties;
import com.gxchain.client.graphenej.models.contract.Abi;
import com.gxchain.client.graphenej.models.contract.ContractAccountProperties;
import com.gxchain.client.graphenej.models.contract.Table;
import com.gxchain.client.graphenej.objects.Asset;
import com.gxchain.client.graphenej.objects.AssetAmount;
import com.gxchain.client.graphenej.operations.BaseOperation;

import java.util.List;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:39
 */
public interface GXChainApiRestClient {

    //////////////////////
    /// chain api
    /////////////////////
    /**
     * 链上查询
     *
     * @param method 方法名
     * @param params 参数
     * @return JsonElement
     */
    JsonElement query(String method, JsonArray params);

    /**
     * 查询gxchain chainId
     *
     * @return
     */
    String getChainId();

    /**
     * 查询全局动态参数
     *
     * @return
     */
    DynamicGlobalProperties getDynamicGlobalProperties();

    /**
     * 根据区块高度获取区块信息
     *
     * @param blockHeight 区块高度
     * @return
     */
    Block getBlock(long blockHeight);

    /**
     * 查询oject
     * 1.2.* 账号
     * 1.3.* 资产
     * 2.1.0 最新区块
     *
     * @param objectIds
     * @return
     */
    JsonElement getObjects(List<String> objectIds);

    ///////////////////
    ////account api
    //////////////////

    /**
     * 查询账户余额
     *
     * @param accountId 账户id
     * @param assetIds  资产id list
     * @return
     */
    List<AssetAmount> getAccountBalances(String accountId, List<String> assetIds);

    /**
     * 根据名称获取公链账户信息
     *
     * @param accountName
     * @return
     */
    AccountProperties getAccountByName(String accountName);

    /**
     * 根据名称获取智能合约账户信息
     * @param accountName
     * @return
     */
    ContractAccountProperties getContractAccountByName(String accountName);

    /**
     * 根据公钥查询账户id
     * @param publicKey 公钥
     * @return 账户id列表
     */
    List<String> getAccountByPublicKey(String publicKey);

    /**
     * 根据accountId查询公链账户信息
     *
     * @param accountIds
     * @return
     */
    List<AccountProperties> getAccounts(List<String> accountIds);

    /**
     * 获取交易费率
     *
     * @param operations 交易操作
     * @param feeAsset   费用资产
     */
    List<AssetAmount> getRequiredFees(List<BaseOperation> operations, Asset feeAsset);

    /**
     * 根据资产标识获取资产信息
     * @param symbols [GXC]
     * @return
     */
    List<Asset> getAssets(List<String> symbols);

    /**
     * get contract abi by contract_name
     * @param contractName
     * @return
     */
    Abi getContractABI(String contractName);

    /**
     * get contract table by contract_name
     * @param contractName
     * @return
     */
    List<Table> getContractTable(String contractName);

    /**
     * 查询智能合约表数据
     * @param contractName 合约名称
     * @param tableName 表名
     * @param lowerBound 查询时指定的key最小值, 默认为0
     * @param upperBound 查询时指定的key最大值，默认为-1，即最大的无符号整形
     * @return
     */
    JsonElement getTableRows(String contractName, String tableName, Number lowerBound, Number upperBound);

    /**
     * 查询智能合约表数据（扩展）
     * @param contractName 合约名称
     * @param tableName 表名
     * @param getTableRowsParams 查询扩展字段
     * @return
     */
    JsonElement getTableRowsEx(String contractName, String tableName, GetTableRowsParams getTableRowsParams);

    /**
     * 广播交易
     * @param transaction
     * @return
     */
    JsonElement broadcast(JsonObject transaction);
}
