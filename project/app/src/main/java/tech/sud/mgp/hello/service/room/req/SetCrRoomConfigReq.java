package tech.sud.mgp.hello.service.room.req;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;

public class SetCrRoomConfigReq {

    public long roomId; // 房间id

    public int platformRotate; // 立方体是否自转  0:不旋转 1：旋转
    public int rotateDir; // 立方体自转方向 0:从右往左转  1:从左往右转
    public int rotateSpeed; // 立方体自转速度（整形类型）0:使用默认速度每秒6度  x>0:每秒旋转x度
    public int gameMusic; // 音乐控制  0:关  1:开
    public int gameSound; // 音效控制  0:关  1:开
    public int flashVFX; // 是否开启爆灯边框效果  0:关  1:开
    public int micphoneWave; // 是否开启麦浪边框效果  0:关  1:开
    public int showGiftValue; // 是否显示心动值  0:隐藏  1:显示

    public void setConfig(SudMGPAPPState.AppCustomCrSetRoomConfig config) {
        if (config == null) {
            return;
        }
        platformRotate = config.platformRotate;
        rotateDir = config.rotateDir;
        rotateSpeed = config.rotateSpeed;
        gameMusic = config.gameMusic;
        gameSound = config.gameSound;
        flashVFX = config.flashVFX;
        micphoneWave = config.micphoneWave;
        showGiftValue = config.showGiftValue;
    }

}
