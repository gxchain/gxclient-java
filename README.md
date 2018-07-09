# gxclient
A client to interact with gxchain implemented

# Install

```
add to settings.xml
 <mirror>
    <id>gxchain</id>
    <mirrorOf>*</mirrorOf>
    <url>http://repo.gxchain.cn/repository/maven-public/</url>
 </mirror>
add to pom.xml
 <dependency>
    <groupId>com.gxchain.common</groupId>
    <artifactId>gxchain-client</artifactId>
    <version>1.0.0-RELEASE</version>
 </dependency>
```

# Usage

## 1. Transaction detect

``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.323";
GxchainClient client = new GxchainClient(privateKey, accountId);
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
```

## 2. KeyPair generation
``` java
KeyPair keyPair = GxchainClient.generateKey();
```
## 3. Account register
``` java
GxchainClient client = new GxchainClient();
KeyPair keyPair = GxchainClient.generateKey();
client.register("lirb-test001", keyPair.getPublicKey()));
```
## 4. Transfer
``` java
String privateKey = "5K8iH1jMJxn8TKXXgHJHjkf8zGXsbVPvrCLvU2GekDh2nk4ZPSF";
String accountId = "1.2.323";
// set broadcast to false so we could calculate the fee before broadcasting
boolean broadcast = true;
GxchainClient client = new GxchainClient(privateKey, accountId);
Transaction transaction = client.transfer("gxb456", "GXChain NB",
                GxcAssetAmount.builder().amount(new BigDecimal(0.01)).assetId("1.3.1").precision(assetPrecicion).build(), broadcast);
        log.info(transaction.toJsonString());
log.info(transaction.toJsonString());
        
```

# Other

It's very welcome for developers to translate this project into different programing languages
We are looking forward to your pull requests
