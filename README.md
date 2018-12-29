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
    <version>1.0.9-RELEASE</version>
 </dependency>
 add a repository to pom.xml
 <repositories>
     <repository>
         <id>gxchain</id>
         <url>http://repo.gxchain.cn/repository/maven-public/</url>
     </repository>
 </repositories>
```

# Usage

## 1. Transaction detect

``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.323";
GxchainClient client = new GxchainClient(privateKey, accountId);
client.latestIrreversibleBlockTask();

// start to detect new transactions related to my account from the indicated block
client.detectTransaction(11042137, (blockHeight, txid, operation) -> {
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
```

## 2. KeyPair generation
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
## 3. Account register
``` java
GxchainClient client = new GxchainClient();
KeyPair keyPair = GxchainClient.generateKey();
String result = client.register("lirb-test001", keyPair.getPublicKey()));
log.info(result);
//{"ref_block_num":18490,"ref_block_prefix":827801284,"expiration":"2018-07-10T08:18:18","operations":[[5,{"fee":{"amount":114746,"asset_id":"1.3.0"},"registrar":"1.2.6","referrer":"1.2.6","referrer_percent":0,"name":"lirb-test002","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"active":{"weight_threshold":1,"account_auths":[],"key_auths":[["GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB",1]],"address_auths":[]},"options":{"memo_key":"GXC84N2ckGU7UwzqZUYxGS1Bm47o4poofUKno2RJ15xU2ZDwwrSsB","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"extensions":{}}]],"extensions":[],"signatures":["1f3a0c4cbeda10d5387296b1d6ecff8e2e47250427daad4efd30c2ff975ff43ce311d667f4e833c218f97dfc591b5370a0ca13d20e50a1cca84cb00d9cc2bdf1c3"]}
```
## 4. Transfer
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
