# 1. QuickStart
依赖SudMGPSDK、SudMGPWrapper快速接入实现。<br>
参考文档：[StartUp-Android](https://docs.sud.tech/zh-CN/app/Client/StartUp-Android.html) <br>
Run此项目：![Run](QuickStartRun.png) <br>
核心类：
- tech.sud.mgp.hello.ui.game.GameViewModel
	- 职责： login --> initSdk --> loadMG
- tech.sud.mgp.hello.GameActivity
	- 职责： addGameView

# 2. SudMGPWrapper
- 位于SDK与应用之间，对SDK进一步封装，便于开发者快速接入。

- 对接口回调进行封装
  - App调用游戏的接口封装：`tech.sud.mgp.hello.SudMGPWrapper.decorator.SudFSTAPPDecorator` <br>
    在调用`SudMGP.loadMG`时会返回`ISudFSTAPP`对象，该对象被`SudFSTAPPDecorator`封装。 <br>
    参考文档：[ISudFSTAPP](https://docs.sud.tech/zh-CN/app/Client/API/ISudFSTAPP.html) <br>
  
  - 游戏调用App的接口封装：`tech.sud.mgp.hello.SudMGPWrapper.decorator.SudFSMMGDecorator` <br>
    在调用`SudMGP.loadMG`时需要传入`ISudFSMMG`对象，`SudFSMMGDecorator`实现了`ISudFSMMG`接口。 <br>
    参考文档：[ISudFSMMG](https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG.html) <br>
    其中`SudFSMMGListener`是`SudFSMMGDecorator`的回调接口，回调装饰之后的游戏状态等等。<br>
    `SudFSMMGListener`类中有标明哪些是需要实现的方法。可参考`tech.sud.mgp.hello.ui.game.GameViewModel`的用法。<br>

- 定义游戏状态的数据结构

  App调用游戏或者游戏调用App都是参用Json数据格式进行通信，`SudMGPWrapper`将这些数据(状态)定义成Model，可便于开发者快速接入。
  - App调用游戏的状态Model：`tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPAPPState` <br>
    `SudFSTAPPDecorator`已将其封装为方法参数，开发者通常情况下只需要关注方法参数即可。<br>
	参考文档：[APPFST](https://docs.sud.tech/zh-CN/app/Client/APPFST/) <br>

  
  - 游戏调用App的状态Model：`tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState`<br>
	参考文档：[MGFSM](https://docs.sud.tech/zh-CN/app/Client/MGFSM/) <br>
	游戏通过`ISudFSMMG`接口将状态通知到App。<br>
	- 1. onGameStateChange
	```
    /**
     * 游戏状态变化
     * APP接入方需要调用handle.success或handle.fail
     * @param handle
     * @param state
     * @param dataJson
     */
    void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson);
	```
	- 参考文档：[CommonStateGame](https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStateGame.html)<br><br>

	- 2. onPlayerStateChange
	```
    /**
     * 游戏玩家状态变化
     * APP接入方需要调用handle.success或handle.fail
     * @param handle
     * @param userId
     * @param state
     * @param dataJson
     */
    void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson);
	```
	- 参考文档：[CommonStatePlayer](https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStatePlayer.html)
	- 参考文档('你画我猜'专用)：[DrawGuess](https://docs.sud.tech/zh-CN/app/Client/MGFSM/DrawGuess.html)

# 3. SudMGPSDK
- SDK提供游戏接入的能力

# 4. 依赖关系
- SudMGPSDK --> SudMGPWrapper --> QuickStart
- SudMGPSDK --> SudMGPWrapper --> app

# 5. 接入方app
- 依赖SudMGPSDK、SudMGPWrapper，在各类场景下的接入。

# 6. 项目目录介绍
- QuickStart      :快速接入示例
- app             :游戏在各种场景下的接入应用
- SudMGPSDK       :存放sdk aar包
- SudMGPWrapper   :对SDK的封装

# 7. 接入文档
[Client](https://docs.sud.tech/zh-CN/app/Client/)
