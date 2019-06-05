# gxclient
A client to interact with gxchain implemented in Java
<p>
 <a href='javascript:;'>
   <img width="300px" src='https://raw.githubusercontent.com/gxchain/gxips/master/assets/images/task-gxclient.png'/>
 </a>
 <a href='javascript:;'>
   <img width="300px" src='https://raw.githubusercontent.com/gxchain/gxips/master/assets/images/task-gxclient-en.png'/>
 </a>
</p> 

# Supported Versions
java 8
# Install

```
 add to pom.xml
 <dependency>
    <groupId>com.gxchain.common</groupId>
    <artifactId>gxchain-client</artifactId>
    <version>2.0.7-RELEASE</version>
 </dependency>
 add a repository to pom.xml
 <repositories>
     <repository>
         <id>gxchain</id>
         <url>http://repo.gxchain.cn/repository/maven-public/</url>
     </repository>
 </repositories>
```
# APIs
- [x] [Keypair API](#keypair-api)
- [x] [Chain API](#chain-api)
- [x] [Faucet API](#faucet-api)
- [x] [Account API](#account-api)
- [x] [Asset API](#asset-api)
- [x] [Contract API](#contract-api)


## Constructors

``` java
//init GXChainClient
public GXChainClient(String activePrivateKey, String accountIdOrName, String entryPoint, String memoPrivateKey);
```

## Keypair API

``` java
//generate key pair locally
public static KeyPair generateKey(String brainKey) throws IOException;
//export public key from private key
public static String privateToPublic(String privateKey);
//check if public key is valid
public static boolean isValidPublic(String publicKey);
//check if private key is valid
public static boolean isValidPrivate(String privateKey);

```

## Chain API

``` java
//query from gxchain
public JsonElement query(String method, JsonArray params)
//get current blockchain id
public String getChainID();
//get dynamic global properties 
public DynamicGlobalProperties getDynamicGlobalProperties();
//get block object
public JsonElement getObject(String objectId);
//get block objects
public JsonElement getObjects(List<String> objectIds);
// get block by block height
public Block getBlock(long blockHeight);
//send transfer request to entryPoint node
public TransactionResult transfer(String toAccountName, String memo, String assetAmount, String feeAsset, boolean isBroadcast);
//vote for accounts
public TransactionResult vote(List<String> accountNames, String feePayingAsset, boolean isBroadcast);
//broadcast transaction
public JsonElement broadcast(Transaction transaction);
```

## Faucet API

``` java
//register gxchain account
public String register(String accountName, String activePrivateKey, String ownerKey, String memoKey, String faucet);

```
## Account API

``` java
// get account info by account name
public AccountProperties getAccount(String accountName);
//get account_ids by public key
public List<String> getAccountByPublicKey(String publicKey);
//get account balances by account name
public List<AssetAmount> getAccountBalances(String accountName);
```

## Asset API

``` java
//get asset info by symbol
public Asset getAsset(String symbol);
//get assets info by symbols
public List<Asset> getAssets(List<String> symbol)
```

## Contract API

``` java
//get contract abi by contract_name
public Abi getContractABI(String contractName);
//get contract table by contract_name
public List<Table> getContractTable(String contractName);
// call smart contract method
public TransactionResult callContract(String contractName, String methodName, JsonElement param, String assetAmount, boolean isBroadcast);
//get contract table rows
public JsonElement getTableRows(String contractName, String tableName, Number lowerBound, Number upperBound);
//get contract table rows by extra params
public JsonElement getTableRowsEx(String contractName, String tableName, GetTableRowsParams getTableRowsParams)
```

# Usage

## 1. KeyPair generation
``` java
KeyPair keyPair = GxchainClient.generateKey();
```
eg.
```json
{
  "brainKey": "plass niche banian hurter spadone ligular fancify hayseed theres proxysm slub chess talisay orillon steam curtail",
  "privateKey": "5JTHkfd8gH6ebsSjRRJbVhEa7u5vh2YZTJH3qC2osjUM9XxKvKR",
  "publicKey": "GXC5bgYX7xNDt1YG7DjD178nK6x9phHAHjZJA7Ug3dkeefLsATiCQ"
}
```
## 2. Account register
``` java
GXChainClient client = new GXChainClient();
KeyPair keyPair = GXChainClient.generateKey();
String result = client.register("lirb-test001", keyPair.getPublicKey()));
log.info(result);
//{"ref_block_num":18490,"ref_block_prefix":827801284,"expiration":"2018-07-10T08:18:18","operations":[[5,{"fee":{"amount":114746,"asset_id":"1.3.0"},"registrar":"1.2.6","referrer":"1.2.6","referrer_percent":0,"name":"lirb-test002","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"active":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"options":{"memo_key":"GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"extensions":{}}]],"extensions":[],"signatures":["1f3a0c4cbeda10d5387296b1d6ecff8e2e47250427daad4efd30c2ff975ff43ce311d667f4e833c218f97dfc591b5370a0ca13d20e50a1cca84cb00d9cc2bdf1c3"]}
```
## 3. Transfer
``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.323";
// set broadcast to false so we could calculate the fee before broadcasting
boolean broadcast = true;
GXChainClient client = new GxchainClient(privateKey, accountId,"wss://testnet.gxchain.org");
//Sending 1 GXC to gxb456 with memo "GXChain NB"
TransactionResult transactionResult = client.transfer("gxb456", "GXChain NB","1 GXC"，"GXC", broadcast);
     
log.info(transactionResult.getTransaction().toJsonString());
log.info("txid:{},fee:{}",transactionResult.getTransaction().calculateTxid(),((TransferOperation)transaction.getOperations().get(0)).getFee().getAmount().longValue()/ Math.pow(10, assetPrecicion));
// > txid:2f9532ebc9ba12c285a0240f7fcc2ec24d4aa6d2,fee:0.0118
// Since gxchain implemented dpos consensus, the transaction will be confirmed until the block becomes irreversible
// You can find the logic when a transfer operation was confirmed in the example of detectTransaction
```
eg.
```json
{
  "ref_block_num": 18620,
  "ref_block_prefix": 2030968667,
  "expiration": "2018-07-10T08:24:57",
  "operations": [
    [
      0,
      {
        "fee": {
          "amount": 1180,
          "asset_id": "1.3.1"
        },
        "from": "1.2.323",
        "to": "1.2.21",
        "amount": {
          "amount": 1000,
          "asset_id": "1.3.1"
        },
        "memo": {
          "from": "GXC4ywUcU8h6zPqESvAMkGREmmg9r54etHTpEtBHp8Rg2WYAcmFnD",
          "to": "GXC67KQNpkkLUzBgDUkWqEBtojwqPgL78QCmTRRZSLugzKEzW4rSm",
          "nonce": "5056877481733866496",
          "message": "47faa383d54a16a94c7c7f46d084e1c6"
        },
        "extensions": []
      }
    ]
  ],
  "extensions": [],
  "signatures": [
    "1b511725e36baf16c8dbaaea9af01e2edc97a5c10b3aebe02e48d8edeb077b7a0c1a69d64bf3365285df4a99ca283532555a82a3d01fa78edce3e887fd5f22430e"
  ],
  "operation_results": [
    [
      0,
      {}
    ]
  ],
  "current_block_number": 8997053
}
```
## 4. Vote
``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.521";
GXChainClient client = new GXChainClient(privateKey, accountId,"wss://testnet.gxchain.org");
TransactionResult transactionResult = client.vote(Arrays.asList("zhuliting", "bob"), "GXC", true);
log.info(transactionResult.getTransaction().toJsonString());
```

eg.
```json
{
  "ref_block_num": 43594,
  "ref_block_prefix": 778560704,
  "expiration": "2019-02-18T03:10:36",
  "operations": [
    [
      6,
      {
        "fee": {
          "amount": 106,
          "asset_id": "1.3.1"
        },
        "account": "1.2.521",
        "new_options": {
          "memo_key": "GXC6rAtkQUGJoxRR3gCEnYo2PxqtVNwD4zw9zg64qgrEpYqmjf2kh",
          "num_committee": 2,
          "num_witness": 2,
          "voting_account": "1.2.5",
          "votes": [
            "1:24",
            "0:60",
            "0:97",
            "1:98"
          ],
          "extensions": []
        },
        "extensions": []
      }
    ]
  ],
  "signatures": [
    "1c597f2e26a4a21d260c951880a4f60efb3131af59d1d0fd45d2191c5976289f352a275c631e0294a710f83631d3502e7ee55bc784751dc9f6b0ebdbfbfe91297b"
  ],
  "extensions": []
}
```

## 5. Call contract
``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.521";
GXChainClient client = new GXChainClient(privateKey, accountId,"wss://testnet.gxchain.org");
//init contract param
JsonObject param = new JsonObject();
param.addProperty("user", "robin");
//call contract method
TransactionResult transactionResult = client.callContract("hello22", "hi", param, null, true);
log.info(transactionResult.getTransaction().toJsonString());
```

eg.
```json
{
  "ref_block_num": 57488,
  "ref_block_prefix": 2895890777,
  "expiration": "2019-02-16T07:29:23",
  "operations": [
    [
      75,
      {
        "fee": {
          "amount": 100,
          "asset_id": "1.3.1"
        },
        "account": "1.2.521",
        "contract_id": "1.2.2072",
        "method_name": "hi",
        "data": "05726f62696e",
        "extensions": []
      }
    ]
  ],
  "extensions": [],
  "signatures": [
    "1c262cc8a943b24a216a300006c4dde21b7391a56142d96d48b9220f08391a93d11334e20b8b540efc444556f08b306af4417dd7688d5603938501b5453b0b95f1"
  ],
  "operation_results": [
    [
      4,
      {
        "billed_cpu_time_us": 107,
        "fee": {
          "amount": 100,
          "asset_id": "1.3.1"
        },
        "ram_receipts": []
      }
    ]
  ]
}
```
# Other

- It's very welcome for developers to translate this project into different programing languages
- We are looking forward to your pull requests
