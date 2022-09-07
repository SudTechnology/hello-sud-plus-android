# 功能介绍
1. 支持海外以太坊主链主流钱包Metamask、TrustWallet、Rainbow等钱包端来授权绑定钱包地址
2. 可以选择各种正式链、测试链、侧链获取对应链NFT列表数据、NFT详情数据
3. 可以生成NFT的使用token，用于应用端唯一使用NFT的使用凭证
4. 支持国内（稀物）平台授权绑定、藏品数据列表、详情数据获取及使用

# SudNFT快速接入
- 申请平台账号及秘钥
  <details>
  <summary>详细描述</summary>

      1.向平台申请appId、appKey

  </details>
- 集成SDK
  <details>
  <summary>详细描述</summary>

      1.通过Android Studio的Import Module功能，导入SudNFT/SudNFTSDK；
      2.APP主工程文件build.gradle中，添加 SudNFTSDK 依赖;
      3.依赖后即可使用tech.sud.nft.core.SudNFT相关接口

  </details>

# 相关接口描述
- 初始化SDK
```
/**
 * 初始化, 必须初始化后使用
 * @param model    初始化参数
 * @param listener 回调
 */
public static void initNFT(SudInitNFTParamModel model, ISudNFTListenerInitNFT listener);
```
- 获取支持的钱包列表
```
/**
 * 获取支持钱包列表
 * @param listener 回调
 */
public static void getWalletList(ISudNFTListenerGetWalletList listener);
```
## 海外以太坊链钱包相关接口
- 绑定钱包
```
/**
 * 绑定钱包
 * @param model    参数
 * @param listener 回调
 */
public static void bindWallet(SudNFTBindWalletParamModel model, ISudNFTListenerBindWallet listener);
```
- 获取NFT列表
```
/**
 * 获取NFT列表,必须授权成功之后才能获取NFT列表
 * @param model    参数
 * @param listener 回调
 */
public static void getNFTList(SudNFTGetNFTListParamModel model, ISudNFTListenerGetNFTList listener);
```
- 生成元数据使用唯一认证token
```
/**
 * 生成元数据使用唯一认证token
 * @param model    参数
 * @param listener 回调
 */
public static void genNFTCredentialsToken(SudNFTCredentialsTokenParamModel model, ISudNFTListenerGenNFTCredentialsToken listener);
```


## 国内藏品平台相关接口（稀物）
- 发送短信验证码
```
/**
 * 发送短信验证码
 * @param model    参数
 * @param listener 回调
 */
public static void sendSmsCode(SudNFTSendSmsCodeParamModel model, ISudNFTListenerSendSmsCode listener);
```
- 绑定国内钱包
```
/**
 * 绑定国内钱包
 * @param model    参数
 * @param listener 回调
 */
public static void bindCnWallet(SudNFTBindCnWalletParamModel model, ISudNFTListenerBindCnWallet listener);
```
- 获取国内NFT列表
```
/**
 * 获取国内NFT列表
 * @param model    参数
 * @param listener 回调
 */
public static void getCnNFTList(SudNFTGetCnNFTListParamModel model, ISudNFTListenerGetCnNFTList listener);
```
- 生成国内NFT使用唯一认证token
```
/**
 * 生成国内NFT使用唯一认证token
 * @param model    参数
 * @param listener 回调
 */
public static void genCnNFTCredentialsToken(SudNFTCnCredentialsTokenParamModel model, ISudNFTListenerGenCnNFTCredentialsToken listener);
```
- 解绑国内钱包
```
/**
 * 解绑国内钱包
 * @param model    参数
 * @param listener 回调
 */
public static void unbindCnWallet(SudNFTUnbindCnWalletParamModel model, ISudNFTListenerUnbindCnWallet listener);
```

# 接入文档

- [接入文档](https://docs.sud.tech/zh-CN/)
