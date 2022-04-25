package tech.sud.mgp.hello.ui.scenes.custom.viewmodel;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;

public class CustomGameViewModel extends GameViewModel {

    //加入游戏
    public void joinGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfIn(true, -1, true, 1);
    }

    //退出游戏
    public void exitGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfIn(false, -1, true, 1);
    }

    //准备
    public void readyGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(true);
    }

    //取消准备
    public void cancelReadyGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(false);
    }

    //开始游戏
    public void startGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(true, "");
    }

    //逃跑
    public void escapeGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(false, "");
    }

    //结束游戏
    public void finishGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfEnd();
    }
}
