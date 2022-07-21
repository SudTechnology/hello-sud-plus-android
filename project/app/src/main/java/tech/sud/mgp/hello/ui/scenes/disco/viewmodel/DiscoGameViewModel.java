package tech.sud.mgp.hello.ui.scenes.disco.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;

/**
 * 蹦迪场景的游戏业务
 */
public class DiscoGameViewModel extends AppGameViewModel {

    private final DiscoActionHelper helper = new DiscoActionHelper();
    public final MutableLiveData<Boolean> gameStartedLiveData = new MutableLiveData<>();

    /**
     * 切歌
     *
     * @param musicId field1:歌曲ID（1：歌曲1；默认随机）
     * @param speed   field2:节奏快慢的数值，用于控制角色跳舞的快慢（0.1-10，保留1位小数点，1为正常速度，超出范围会取默认值）；默认1
     */
    public void switchSong(String musicId, String speed) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.switchSong(musicId, speed));
    }

    /**
     * 角色是否绑定性别
     *
     * @param bind field1:0-不绑定（会在所有角色中随机）；1-绑定（会在对应性别的角色中随机）；默认0不绑定
     */
    public void bindGender(boolean bind) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.bindGender(bind));
    }

    /**
     * 更换角色
     *
     * @param roleId field1:角色ID（1：角色1（男）；2：角色2（男）；3：角色3（男）；4：角色4（男）；5：角色5（男）；6：角色6（男）；7：角色7（男）；
     *               8：角色8（男）；9：角色9（男）；10：角色10（男）；11：角色11（男）；12：角色12（女）；13：角色13（女）；14：角色14（女）；15：角色15（女）
     *               ；16：角色16（女）；17：角色17（女）；18：角色18（女）；19：角色19（女）；20：角色20（女）；21：角色21（女））；默认随机
     */
    public void changeRole(String roleId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.changeRole(roleId));
    }

    /**
     * 加入主播位
     *
     * @param position field1:0-0号主播位；1-1号主播位；2-2号主播位；3-3号主播位；4-4号主播位；5-5号主播位；6-6号主播位；7-7号主播位；-1-随机，默认随机
     * @param userId   加入主播位的userId，不填，默认是自己
     */
    public void joinAnchor(String position, String userId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.joinAnchor(position, userId));
    }

    /**
     * 离开主播位
     *
     * @param userId field1:playerId（离开主播位的玩家id），默认自己离开，如果该玩家本来就不在主播位则没有任何效果
     */
    public void leaveAnchor(String userId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.leaveAnchor(userId));
    }

    /**
     * 是否隐藏中央舞台
     *
     * @param isHide field1:0-显示；1-隐藏（隐藏后不再有主播位，也不能和主播跳舞），默认显示
     */
    public void hideCenterStage(boolean isHide) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.hideCenterStage(isHide));
    }

    /**
     * 加入舞池
     *
     * @param color field1:昵称的颜色色值（比如#ffffff就是昵称颜色为白色）默认白色
     */
    public void joinDancingFloor(String color) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.joinDancingFloor(color));
    }

    /**
     * 离开舞池
     *
     * @param userId field1:playerId（离开舞池的玩家id），默认自己离开，如果该玩家本来就不在舞池则没有任何效果
     */
    public void leaveDancingFloor(String userId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.leaveDancingFloor(userId));
    }

    /**
     * 清场，强制所有人离开舞池，全部变成观众
     */
    public void clearSite() {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.clearSite());
    }

    /**
     * 角色移动
     *
     * @param cooldown 移动的持续时间，单位秒（范围为3-300，超出范围会取默认值）默认10秒
     * @param speed    field1:移动速度快慢的数值（范围为0.1-10，保留1位小数点，1为正常速度，超出范围会取默认值）；默认1
     */
    public void roleMove(Integer cooldown, String speed) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleMove(cooldown, speed));
    }

    /**
     * 角色变大
     *
     * @param cooldown 变大的持续时间，单位秒（-1为永久）默认60秒
     * @param multiple field1:放大倍数的数值（范围为0.1-5，保留1位小数点，1为原始大小，超出范围会取默认值）；默认2
     */
    public void roleBig(Integer cooldown, float multiple) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleBig(cooldown, multiple));
    }

    /**
     * 角色飞天
     *
     * @param cooldown cooldown:飞天的持续时间，单位秒（-1为永久）默认30秒
     */
    public void roleFly(Integer cooldown) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleFly(cooldown));
    }

    /**
     * 角色特效
     *
     * @param cooldown 特效的持续时间，单位秒（-1为永久）默认60秒
     * @param effectId field1:特效ID（1：特效1；2：特效2；3：特效3；4：特效4；5：特效5；6：特效6）；默认随机
     */
    public void roleEffects(Integer cooldown, String effectId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleEffects(cooldown, effectId));
    }

    /**
     * 角色特写
     *
     * @param cooldown cooldown:特写的持续时间，单位秒（范围为1-30，超出范围会取默认值）默认舞池角色1秒,DJ台角色3秒,跳舞的角色5秒
     * @param isTop    isTop:false-不置顶；true-置顶
     */
    public void roleFocus(Integer cooldown, Boolean isTop) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleFocus(cooldown, isTop));
    }

    /**
     * 文字气泡
     *
     * @param cooldown 气泡的持续时间，单位秒（-1为永久）默认3秒
     * @param content  field1:气泡的文字内容；默认为空
     */
    public void textPop(Integer cooldown, String content) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.textPop(cooldown, content));
    }

    /**
     * 角色称号
     *
     * @param cooldown 称号的持续时间，单位秒（-1为永久）默认永久
     * @param text     field1:称号的文字内容（6个汉字的长度）；默认在“全场最靓”，“最强王者”和“元宇宙砂砂舞”中随机
     * @param effectId field2:称号特效ID（1：称号1；2：称号2；3：称号3）默认随机
     */
    public void roleTitle(Integer cooldown, String text, String effectId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.roleTitle(cooldown, text, effectId));
    }

    /**
     * 上DJ台
     *
     * @param cooldown cooldown:上DJ台的持续时间，单位秒（-1为永久）默认180秒
     */
    public void upDJ(Integer cooldown) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.upDJ(cooldown));
    }

    /**
     * 跳舞模式
     *
     * @param mode field1:0-单对单（单个玩家只能和单个主播跳舞）；1-单对多（单个玩家可以和多个主播跳舞）默认0）
     */
    public void danceMode(int mode) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.danceMode(mode));
    }

    /**
     * 和主播跳舞
     *
     * @param cooldown 和主播跳舞的持续时间，单位秒（-1为永久）默认30秒
     * @param isTop    isTop:false-不置顶；true-置顶
     * @param userId   field1:playerId（主播玩家的id）；该参数必传，不传则没有任何效果
     */
    public void danceWithAnchor(Integer cooldown, Boolean isTop, String userId) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.danceWithAnchor(cooldown, isTop, userId));
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();
        danceMode(1);
        gameStartedLiveData.setValue(true);
    }

    @Override
    public void wrapMicModel(AudioRoomMicModel model) {
        // 不展示任何游戏相关的状态
    }

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        super.getGameRect(gameViewInfoModel);
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 145) + BarUtils.getStatusBarHeight();
    }

    /**
     * 执行动作
     *
     * @param model
     */
    public void exeAction(LifecycleOwner owner, long roomId, DiscoInteractionModel model, ActionListener listener) {
        actionDeduction(owner, roomId, model, new CompletedListener() {
            @Override
            public void onCompleted() {
                deductionSuccess(model, owner, roomId, listener);
            }
        });
    }

    /** 执行动作之前，进行扣费 */
    private void actionDeduction(LifecycleOwner owner, long roomId, DiscoInteractionModel model, CompletedListener listener) {
        if (model.price == null || model.price == 0) {
            listener.onCompleted();
            return;
        }
        RoomRepository.deductionCoin(owner, model.price, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                listener.onCompleted();
            }
        });
    }

    /** 扣费成功之后，执行动作 */
    private void deductionSuccess(DiscoInteractionModel model, LifecycleOwner owner, long roomId, ActionListener listener) {
        switch (model.type) {
            case JOIN_ANCHOR:
                switchAnchor(owner, roomId, true, listener);
                break;
            case LEAVE_ANCHOR:
                switchAnchor(owner, roomId, false, listener);
                break;
            case UP_DJ:
                upDJ(null);
                break;
            case MOVE:
                roleMove(null, null);
                break;
            case GOD:
                roleFly(null);
                break;
            case BIG:
                roleBig(null, 2);
                break;
            case CHANGE_ROLE:
                changeRole(null);
                break;
            case FOCUS:
                roleFocus(null, null);
                break;
            case TITLE:
                roleTitle(60, null, null);
                break;
            case EFFECTS:
                roleEffects(null, null);
                break;
            case POP_BIG_FOCUS:
                textPop(null, Utils.getApp().getString(R.string.disco_nick_name));
                roleBig(null, 2);
                roleFocus(null, null);
                break;
            case POP_BIG_FOCUS_EFFECTS:
                textPop(null, Utils.getApp().getString(R.string.disco_nick_name));
                roleBig(null, 2);
                roleFocus(null, null);
                roleEffects(null, null);
                break;
        }
    }

    /**
     * 上下主播位
     *
     * @param owner  生命周期对象
     * @param isJoin true为上主播位 false为下主播位
     */
    private void switchAnchor(LifecycleOwner owner, long roomId, boolean isJoin, ActionListener listener) {
        if (isJoin) {
            joinAnchor(null, HSUserInfo.userId + "");
        } else {
            leaveAnchor(HSUserInfo.userId + "");
        }
        RoomRepository.discoSwitchAnchor(owner, roomId, isJoin ? 1 : 2, HSUserInfo.userId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                listener.onAnchorChange(isJoin);
            }
        });
    }

    public interface ActionListener {
        void onAnchorChange(boolean isAnchor);
    }

}
