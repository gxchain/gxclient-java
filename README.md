# gxclient
A client to interact with gxchain implemented
# Supported Versions
java 8
# Install

```
 add to pom.xml
 <dependency>
    <groupId>com.gxchain.common</groupId>
    <artifactId>gxchain-client</artifactId>
    <version>2.0.0-RELEASE</version>
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
//init GxchainClient
public GxchainClient(String activePrivateKey, String accountIdOrName, String entryPoint, String memoPrivateKey);
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

```

## Contract API

``` java
//get contract abi by contract_name
public Abi getContractABI(String contractName);
//get contract table by contract_name
public List<Table> getContractTable(String contractName);
// call smart contract method
public TransactionResult callContract(String contractName, String methodName, JsonElement param, String assetAmount, boolean isBroadcast);
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
GxchainClient client = new GxchainClient();
KeyPair keyPair = GxchainClient.generateKey();
String result = client.register("lirb-test001", keyPair.getPublicKey()));
log.info(result);
//{"ref_block_num":18490,"ref_block_prefix":827801284,"expiration":"2018-07-10T08:18:18","operations":[[5,{"fee":{"amount":114746,"asset_id":"1.3.0"},"registrar":"1.2.6","referrer":"1.2.6","referrer_percent":0,"name":"lirb-test002","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"active":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"options":{"memo_key":"GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"extensions":{}}]],"extensions":[],"signatures":["1f3a0c4cbeda10d5387296b1d6ecff8e2e47250427daad4efd30c2ff975ff43ce311d667f4e833c218f97dfc591b5370a0ca13d20e50a1cca84cb00d9cc2bdf1c3"]}
```
## 3. Transfer
``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.323";
int assetPrecicion = 5;
// set broadcast to false so we could calculate the fee before broadcasting
boolean broadcast = true;
GxchainClient client = new GxchainClient(privateKey, accountId);
//Sending 0.01GXS to gxb456 with memo "GXChain NB"
Transaction transaction = client.transfer("gxb456", "GXChain NB",
                GxcAssetAmount.builder().amount(new BigDecimal(0.01)).assetId("1.3.1").precision(assetPrecicion).build(), broadcast);
     
log.info(transaction.toJsonString());
log.info("txid:{},fee:{}",transaction.calculateTxid(),((TransferOperation)transaction.getOperations().get(0)).getFee().getAmount().longValue()/ Math.pow(10, assetPrecicion));
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
# Other

- It's very welcome for developers to translate this project into different programing languages
- We are looking forward to your pull requests
