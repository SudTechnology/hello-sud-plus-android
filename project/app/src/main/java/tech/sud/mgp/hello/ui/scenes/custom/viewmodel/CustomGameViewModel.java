package tech.sud.mgp.hello.ui.scenes.custom.viewmodel;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

public class CustomGameViewModel extends AppGameViewModel {

    // 加入游戏
    public void actionJoinGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfInV2(true, -1, true, 1);
    }

    // 退出游戏
    public void actionExitGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfInV2(false, -1, true, 1);
    }

    // 准备
    public void actionReadyGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(true);
    }

    // 取消准备
    public void actionCancelReadyGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(false);
    }

    // 开始游戏
    public void actionStartGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(true, null, null);
    }

    // 逃跑
    public void actionEscapeGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(false, null, null);
    }

}
