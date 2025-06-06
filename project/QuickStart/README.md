[English](README_en.md)
# 开启快速接入和联调SUD游戏之旅

- 第一步：APP客户端集成SUD游戏（三分钟集成代码）
  <details>
  <summary>详细描述</summary>

      1.appId、appKey和isTestEnv=true，请使用QuickStart客户端的；
      2.iOS bundleId、Android applicationId，请使用APP客户端自己的；(接入信息表中的bundleId/applicationId)；
      3.短期令牌code，请使用QuickStart的后端服务（login/getCode获取的）；
      4.完成集成，游戏跑起来;

      *** SUD平台支持一个appId绑定多个bundleId和applicationId；***
      *** 填完接入信息表后，SUD会将APP的bundleId和applicationId，绑定到QuickStart的appId上，仅支持测试环境；***
  QuickStart 后端服务[hello-sud-java代码仓库](https://github.com/SudTechnology/hello-sud-java) ，`如果访问不了代码仓库，请联系SUD添加，github账号`；
  </details>


- 第二步：APP客户端和APP服务端联调
  <details>
  <summary>详细描述</summary>

      1.APP服务端实现4个HTTP API；（接入信息表填的）
      2.APP服务端实现login/getCode接口，获取短期令牌code；
      3.请使用APP客户端自己的appId、appKey、isTestEnv=true、bundleId(iOS)、applicationId(Android)；
      4.请使用APP自己的服务端login/getCode接口，获取短期令牌code；
      5.APP客户端和APP服务端联调5个HTTP API；
      6.完成HTTP API联调；
  </details>


- 第三步：APP专注于自身业务需求
  <details>
  <summary>详细描述</summary>

      1.参考SudGIP文档、SudGIPWrapper、QuickStart、HelloSud体验Demo（展示多场景，Custom自定义场景）；
      2.专注于APP UI交互、功能是否支持、如何实现
      比如：
      调整游戏View大小、位置；
      调整APP和游戏交互流程，UI元素是否可隐藏，按钮是否可隐藏APP实现，点击事件是否支持拦截回调；
      
      3.专注于APP业务逻辑流程、实现
      比如：
      一局游戏开始如何透传数值类型参数、Key类型参数；（结算）
  ![Android](doc/hello_sudplus_android.png)
  ![iPhone](doc/hello_sudplus_iphone.png)
  </details>

# 三分钟集成代码

- 第一步：导入模块SudGIPWrapper
  <details>
  <summary>导入模块SudGIPWrapper</summary>

      1.通过Android Studio的Import Module功能，导入SudGIPWrapper；
      2.APP主工程文件build.gradle中，添加 SudGIPWrapper 依赖;
      3.此模块包含了SDK的依赖，如需上架Google Play，请查看SudGIPwrapper/build.gradle当中的注释说明
  ``` java
  build.gradle
  
  dependencies {
    // 引入 SudGIPWrapper
    implementation project(':SudGIPWrapper')
  }
  ```
  </details>

  <details>
  <summary>ASR识别支持（可选 v1.2.7及后续版本支持）</summary>

  此为扩展功能，如果不需要ASR识别，可以忽略此库的集成，请访问：[SudGIP-Android](https://github.com/SudTechnology/sud-mgp-android)
  </details>


- 第二步：拷贝QuickStart 3个文件，并保持配置参数不变
  <details>
  <summary>详细描述</summary>

      1.拷贝3个文件:
        BaseGameViewModel.java
        QuickStartGameViewModel.java
        QuickStartUtils.java
      2.特别注意：和游戏交互的重点在于QuickStartGameViewModel.java这个文件里，请花时间查看其中的每一行注释及代码实现，其中标注为TODO的是需要开发者修改或特别关心的内容
  </details>


- 第三步：布局文件中定义一个游戏View容器，例如：activity_game.xml
    <details>
    <summary>详细描述 activity_game.xml</summary>

    ``` xml
    <!-- 游戏View容器，android:visibility属性不能设置为gone -->
    <FrameLayout
        android:id="@+id/game_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    ```
    </details>


- 第四步：创建QuickStartGameViewModel实例，及生命周期对应，例如：QuickStartActivity
    <details>
    <summary>详细描述 QuickStartActivity.java</summary>

      1.实现游戏View的添加与移除；
      2.生命周期(可选)
      3.代码；
    ``` java
    private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel(); // 创建ViewModel
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ......
        FrameLayout gameContainer = findViewById(R.id.game_container); // 获取游戏View容器
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
    }
  
    @Override
    protected void onResume() {
        super.onResume();
        // 注意：要在此处调用onResume()方法
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注意：要在此处调用onPause()方法
        gameViewModel.onPause();
    }
    ```
    </details>


- 第五步：加载游戏
    <details>
    <summary>详细描述 QuickStartActivity.java</summary>

    ``` java
    // 加载游戏，参数定义可查看BaseGameViewModel.switchGame()方法注释
  
    // App的房间ID
    String appRoomId = "10000";       
    // SudGIP平台64bit游戏ID
    long mgId = 1461227817776713818L; // 这是'碰碰我最强'的mgId，加载不同游戏填不同的mgId即可
    gameViewModel.switchGame(this, appRoomId, mgId);       
    ```
    </details>


- 第六步：销毁游戏
    <details>
    <summary>详细描述 QuickStartActivity.java</summary>

    ``` java
    // 页面销毁之前，先销毁游戏
    gameViewModel.destroyMG();
    finish();    
    ```
    </details>

# QuickStart 架构图

![QuickStartArch.png](doc/QuickStartArch.png)

# 1. SudGIP SDK

### 1.1 SudGIP Client SDK

- [Android SDK](https://github.com/SudTechnology/sud-mgp-android)
- [iOS SDK](https://github.com/SudTechnology/sud-mgp-ios)

### 1.2 接入文档

- [接入文档](https://docs.sud.tech/zh-CN/app/Client/API/)
- [FAQ](https://docs.sud.tech/zh-CN/app/Client/FAQ/)

# 2. SudGIPWrapper

- `SudGIPWrapper封装SudGIP，简化App和游戏相互调用`；
- `SudGIPWrapper长期维护和保持更新`；
- `推荐APP接入方使用SudGIPWrapper`；
- `SudGIPAPPState`、`SudGIPMGState`、`SudFSMMGListener`、`SudFSMMGDecorator`、`SudFSTAPPDecorator核心类`；

### 2.1 App调用游戏

- `SudGIPAPPState` 封装 [App通用状态](https://docs.sud.tech/zh-CN/app/Client/APPFST/CommonState.html) ；
- `SudFSTAPPDecorator` 封装 [ISudFSTAPP](https://docs.sud.tech/zh-CN/app/Client/API/ISudFSTAPP.html)
  两类接口，[notifyStateChange](https://docs.sud.tech/zh-CN/app/Client/APPFST/CommonState.html) 、 foo；
- `SudFSTAPPDecorator` 负责把每一个App通用状态封装成接口；
    <details>
    <summary>代码框架 java class SudFSTAPPDecorator</summary>

    ``` java
    public class SudFSTAPPDecorator {
        // iSudFSTAPP = SudGIP.loadMG(SudLoadMGParamModel model, ISudFSMMG fsmMG);
        public void setISudFSTAPP(ISudFSTAPP iSudFSTAPP);
        // 1. 加入状态
        public void notifyAPPCommonSelfIn(boolean isIn, int seatIndex, boolean isSeatRandom, int teamId);
        ...
    
        // 16. 设置游戏中的AI玩家（2022-05-11新增）
        public void notifyAPPCommonGameAddAIPlayers(List<SudGIPAPPState.AIPlayers> aiPlayers, int isReady);
        public void startMG();
        public void pauseMG();
        public void playMG();
        public void stopMG();
        public void destroyMG();
        public void updateCode(String code, ISudListenerNotifyStateChange listener);
        public void pushAudio(ByteBuffer buffer, int bufferLength);
        ...
    }
    ```
    </details>

### 2.2 游戏调用App

- `SudGIPMGState` 封装 [通用状态-游戏](https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStateGame.html)
  和 [通用状态-玩家](https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStatePlayer.html) ；
- `SudFSMMGListener` 封装[ISudFSMMG](https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG.html) 三类回调函数，onGameStateChange、onPlayerStateChange、onFoo；
- `SudFSMMGListener` 负责把游戏每一个状态封装成单独的回调函数；
    <details>
    <summary>代码框架 java interface SudFSMMGListener</summary>

    ``` java
    public interface SudFSMMGListener {
    default void onGameLog(String str) {}
    void onGameStarted();
    void onGameDestroyed();
    void onExpireCode(ISudFSMStateHandle handle, String dataJson);
    void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson);
    void onGetGameCfg(ISudFSMStateHandle handle, String dataJson);
    
        // 通用状态 - 游戏
        // void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson)；
        // 文档: https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStateGame.html
        // 1.游戏公屏消息
        default void onGameMGCommonPublicMessage(ISudFSMStateHandle handle, SudGIPMGState.MGCommonPublicMessage model);
        ...
    
        // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
        default void onGameMGCommonGameAddAIPlayers(ISudFSMStateHandle handle, SudGIPMGState.MGCommonGameAddAIPlayers model);
        
        // 通用状态 - 玩家
        // void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson);
        // 文档: https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStatePlayer.html
        // 1.加入状态
        default void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudGIPMGState.MGCommonPlayerIn model);
        ...
    
        // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
        default void onPlayerMGCommonGameCountdownTime(ISudFSMStateHandle handle, String userId, SudGIPMGState.MGCommonGameCountdownTime model);
    
        // 游戏个性化状态：你画我猜
        // 文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/DrawGuess.html
        // 1. 选词中状态
        default void onPlayerMGDGSelecting(ISudFSMStateHandle handle, String userId, SudGIPMGState.MGDGSelecting model);
        ...
    }
    ```
    </details>
- [ISudFSMMG](https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG.html) 的装饰类`SudFSMMGDecorator`，负责派发每一个游戏状态，缓存需要的游戏状态；
    <details>
    <summary>代码框架 java class SudFSMMGDecorator</summary>

    ``` java
    public class SudFSMMGDecorator implements ISudFSMMG {
        // 设置回调
        public void setSudFSMMGListener(SudFSMMGListener listener);
        // 游戏日志
        public void onGameLog(String dataJson);
        // 游戏加载进度
        public void onGameLoadingProgress(int stage, int retCode, int progress);
        // 游戏已开始，游戏长连接完成
        public void onGameStarted();
        // 游戏销毁
        public void onGameDestroyed();
        // Code过期，必须实现；APP接入方必须调用handle.success，释放异步回调对象
        public void onExpireCode(ISudFSMStateHandle handle, String dataJson);
        // 获取游戏View信息，必须实现；APP接入方必须调用handle.success，释放异步回调对象
        // GameViewInfoModel文档: https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameViewInfo.html
        public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson);
        // 获取游戏Config，必须实现；APP接入方必须调用handle.success，释放异步回调对象
        // GameConfigModel文档: https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameCfg.html
        public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson);
        // 游戏状态变化；APP接入方必须调用handle.success，释放异步回调对象
        public void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson);
        // 游戏玩家状态变化，APP接入方必须调用handle.success，释放异步回调对象
        public void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson);
  
        ...
    }
    ```
    </details>

# 3. QuickStart

- 请使用QuickStart项目运行；
- QuickStart使用SudGIPWrapper实现快速接入游戏；
- 快速接入文档：[StartUp-Android](https://docs.sud.tech/zh-CN/app/Client/StartUp-Android.html)
  和 [StartUp-iOS](https://docs.sud.tech/zh-CN/app/Client/StartUp-iOS.html) ；
- `QuickStartGameViewModel` 负责login(App getCode) --> SudGIP.initSDK --> SudGIP.loadMG；
- `GameActivity` 负责addGameView；
- `QuickStart 服务端` [hello-sud-java](https://github.com/SudTechnology/hello-sud-java) ，login(App getCode 获取短期令牌code)
  ，`如果访问不了代码仓库，请联系SUD添加，github账号`；

# 4. QuickStart运行效果图

![QuickStartHome.png](doc/QuickStartHome.png)
![QuickStartGame.png](doc/QuickStartGame.png)

- HelloSud体验Demo（展示多业务场景）

![Android](doc/hello_sudplus_android.png)
![iPhone](doc/hello_sudplus_iphone.png)

# 5. 接入方客户端和SudGIP SDK调用时序图

![AppCallSudMGPSeqDiag.png](doc/AppCallSudMGPSeqDiag.png)

# 6. 其它注意事项

- 首次集成，请根据自身要接入的游戏不同，来选择不同的SDK。地址：[SudGIPSDK](https://github.com/SudTechnology/sud-mgp-android)
- 如果项目开启了资源混淆，请添加以下混淆白名单，针对AndResGuard   
  注意：如果使用的是AabResGuard，那么需要在每条名单前面加上"*."，比如"*.R.id.reload_btn"；  
  其他的混淆工具参考其具体的白名单设置规则
    <details>
    <summary>资源混淆白名单列表</summary>

    ``` groovy
    "R.drawable.fsm_*",  
    "R.string.fsm_*",  
    "R.layout.fsm_*"',  
    "R.color.fsm_*",  
    "R.id.fsm_*,  
    "R.style.fsm_*",  
    "R.dimen.fsm_*",           
    "R.array.fsm_*",  
    "R.integer.fsm_*"',
    "R.bool.fsm_*",
    "R.mipmap.fsm_*",
    "R.styleable.fsm_*",
    "R.id.*loading*",
    "R.id.container_progress",
    "R.id.reload_btn",
    "R.id.unitySurfaceView",
    "R.string.game_view_content_description"  
    ```
    </details>