package tech.sud.mgp.hello.ui.scenes.disco.viewmodel;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;

public class DiscoActionHelper {

    public final int ACTION_SWITCH_SONG = 9; // 切歌
    public final int ACTION_BIND_GENDER = 10; // 角色是否绑定性别
    public final int ACTION_CHANGE_ROLE = 11; // 更换角色
    public final int ACTION_JOIN_ANCHOR = 12; // 加入主播位
    public final int ACTION_LEAVE_ANCHOR = 13; // 离开主播位
    public final int ACTION_HIDE_CENTER_STAGE = 14; // 是否隐藏中央舞台
    public final int ACTION_JOIN_DANCING_FLOOR = 15; // 加入舞池
    public final int ACTION_LEAVE_DANCING_FLOOR = 16; // 离开舞池
    public final int ACTION_CLEAR_SITE = 17; // 清场，强制所有人离开舞池，全部变成观众
    public final int ACTION_ROLE_MOVE = 18; // 角色移动
    public final int ACTION_ROLE_BIG = 19; // 角色变大
    public final int ACTION_ROLE_FLY = 20; // 角色飞天
    public final int ACTION_ROLE_EFFECTS = 22; // 角色特效
    public final int ACTION_ROLE_FOCUS = 23; // 角色特写
    public final int ACTION_TEXT_POP = 24; // 文字气泡
    public final int ACTION_ROLE_TITLE = 25; // 角色称号
    public final int ACTION_UP_DJ = 26; // 上DJ台
    public final int ACTION_DANCE_MODE = 27; // 跳舞模式
    public final int ACTION_ANCHOR_DANCE = 28; // 和主播跳舞

    /**
     * 9.切歌
     *
     * @param musicId field1:歌曲ID（1：歌曲1；默认随机）
     * @param speed   field2:节奏快慢的数值，用于控制角色跳舞的快慢（0.1-10，保留1位小数点，1为正常速度，超出范围会取默认值）；默认1
     */
    public String switchSong(String musicId, String speed) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = musicId;
        String field2 = speed;
        return buidlJson(ACTION_SWITCH_SONG, cooldown, isTop, field1, field2);
    }

    /**
     * 10.角色是否绑定性别
     *
     * @param bind field1:0-不绑定（会在所有角色中随机）；1-绑定（会在对应性别的角色中随机）；默认0不绑定
     */
    public String bindGender(boolean bind) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = null;
        String field2 = null;
        if (bind) {
            field1 = "1";
        } else {
            field1 = "0";
        }
        return buidlJson(ACTION_BIND_GENDER, cooldown, isTop, field1, field2);
    }

    /**
     * 11.更换角色
     *
     * @param roleId field1:角色ID（1：角色1（男）；2：角色2（男）；3：角色3（男）；4：角色4（男）；5：角色5（男）；6：角色6（男）；7：角色7（男）；
     *               8：角色8（男）；9：角色9（男）；10：角色10（男）；11：角色11（男）；12：角色12（女）；13：角色13（女）；14：角色14（女）；15：角色15（女）
     *               ；16：角色16（女）；17：角色17（女）；18：角色18（女）；19：角色19（女）；20：角色20（女）；21：角色21（女））；默认随机
     */
    public String changeRole(String roleId) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = roleId;
        String field2 = null;
        return buidlJson(ACTION_CHANGE_ROLE, cooldown, isTop, field1, field2);
    }

    /**
     * 12.加入主播位
     *
     * @param position field1:0-0号主播位；1-1号主播位；2-2号主播位；3-3号主播位；4-4号主播位；5-5号主播位；6-6号主播位；7-7号主播位；-1-随机，默认随机
     */
    public String joinAnchor(String position) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = position;
        String field2 = null;
        return buidlJson(ACTION_JOIN_ANCHOR, cooldown, isTop, field1, field2);
    }

    /**
     * 13.离开主播位
     *
     * @param userId field1:playerId（离开主播位的玩家id），默认自己离开，如果该玩家本来就不在主播位则没有任何效果
     */
    public String leaveAnchor(String userId) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = userId;
        String field2 = null;
        return buidlJson(ACTION_LEAVE_ANCHOR, cooldown, isTop, field1, field2);
    }

    /**
     * 14.是否隐藏中央舞台
     *
     * @param isHide field1:0-显示；1-隐藏（隐藏后不再有主播位，也不能和主播跳舞），默认显示
     */
    public String hideCenterStage(boolean isHide) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = null;
        String field2 = null;
        if (isHide) {
            field1 = "1";
        } else {
            field1 = "0";
        }
        return buidlJson(ACTION_HIDE_CENTER_STAGE, cooldown, isTop, field1, field2);
    }

    /**
     * 15.加入舞池
     *
     * @param color field1:昵称的颜色色值（比如#ffffff就是昵称颜色为白色）默认白色
     */
    public String joinDancingFloor(String color) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = color;
        String field2 = null;
        return buidlJson(ACTION_JOIN_DANCING_FLOOR, cooldown, isTop, field1, field2);
    }

    /**
     * 16.离开舞池
     *
     * @param userId field1:playerId（离开舞池的玩家id），默认自己离开，如果该玩家本来就不在舞池则没有任何效果
     */
    public String leaveDancingFloor(String userId) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = userId;
        String field2 = null;
        return buidlJson(ACTION_LEAVE_DANCING_FLOOR, cooldown, isTop, field1, field2);
    }

    /**
     * 17.清场，强制所有人离开舞池，全部变成观众
     */
    public String clearSite() {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = null;
        String field2 = null;
        return buidlJson(ACTION_CLEAR_SITE, cooldown, isTop, field1, field2);
    }

    /**
     * 18.角色移动
     *
     * @param cooldown 移动的持续时间，单位秒（范围为3-300，超出范围会取默认值）默认10秒
     * @param speed    field1:移动速度快慢的数值（范围为0.1-10，保留1位小数点，1为正常速度，超出范围会取默认值）；默认1
     */
    public String roleMove(Integer cooldown, String speed) {
        Boolean isTop = null;
        String field1 = speed;
        String field2 = null;
        return buidlJson(ACTION_ROLE_MOVE, cooldown, isTop, field1, field2);
    }

    /**
     * 19.角色变大
     *
     * @param cooldown 变大的持续时间，单位秒（-1为永久）默认60秒
     * @param multiple field1:放大倍数的数值（范围为0.1-5，保留1位小数点，1为原始大小，超出范围会取默认值）；默认2
     */
    public String roleBig(Integer cooldown, float multiple) {
        Boolean isTop = null;
        String field1 = multiple + "";
        String field2 = null;
        return buidlJson(ACTION_ROLE_BIG, cooldown, isTop, field1, field2);
    }

    /**
     * 20.角色飞天
     *
     * @param cooldown cooldown:飞天的持续时间，单位秒（-1为永久）默认30秒
     */
    public String roleFly(Integer cooldown) {
        Boolean isTop = null;
        String field1 = null;
        String field2 = null;
        return buidlJson(ACTION_ROLE_FLY, cooldown, isTop, field1, field2);
    }

    /**
     * 22.角色特效
     *
     * @param cooldown 特效的持续时间，单位秒（-1为永久）默认60秒
     * @param effectId field1:特效ID（1：特效1；2：特效2；3：特效3；4：特效4；5：特效5；6：特效6）；默认随机
     */
    public String roleEffects(Integer cooldown, String effectId) {
        Boolean isTop = null;
        String field1 = effectId;
        String field2 = null;
        return buidlJson(ACTION_ROLE_EFFECTS, cooldown, isTop, field1, field2);
    }

    /**
     * 23.角色特写
     *
     * @param cooldown cooldown:特写的持续时间，单位秒（范围为1-30，超出范围会取默认值）默认舞池角色1秒,DJ台角色3秒,跳舞的角色5秒
     * @param isTop    isTop:false-不置顶；true-置顶
     */
    public String roleFocus(Integer cooldown, Boolean isTop) {
        String field1 = null;
        String field2 = null;
        return buidlJson(ACTION_ROLE_FOCUS, cooldown, isTop, field1, field2);
    }

    /**
     * 24.文字气泡
     *
     * @param cooldown 气泡的持续时间，单位秒（-1为永久）默认3秒
     * @param content  field1:气泡的文字内容；默认为空
     */
    public String textPop(Integer cooldown, String content) {
        Boolean isTop = null;
        String field1 = content;
        String field2 = null;
        return buidlJson(ACTION_TEXT_POP, cooldown, isTop, field1, field2);
    }

    /**
     * 25.角色称号
     *
     * @param cooldown 称号的持续时间，单位秒（-1为永久）默认永久
     * @param text     field1:称号的文字内容（6个汉字的长度）；默认在“全场最靓”，“最强王者”和“元宇宙砂砂舞”中随机
     * @param effectId field2:称号特效ID（1：称号1；2：称号2；3：称号3）默认随机
     */
    public String roleTitle(Integer cooldown, String text, String effectId) {
        Boolean isTop = null;
        String field1 = text;
        String field2 = effectId;
        return buidlJson(ACTION_ROLE_TITLE, cooldown, isTop, field1, field2);
    }

    /**
     * 26.上DJ台
     *
     * @param cooldown cooldown:上DJ台的持续时间，单位秒（-1为永久）默认180秒
     */
    public String upDJ(Integer cooldown) {
        Boolean isTop = null;
        String field1 = null;
        String field2 = null;
        return buidlJson(ACTION_UP_DJ, cooldown, isTop, field1, field2);
    }

    /**
     * 27.跳舞模式
     *
     * @param mode field1:0-单对单（单个玩家只能和单个主播跳舞）；1-单对多（单个玩家可以和多个主播跳舞）默认0）
     */
    public String danceMode(int mode) {
        Integer cooldown = null;
        Boolean isTop = null;
        String field1 = mode + "";
        String field2 = null;
        return buidlJson(ACTION_DANCE_MODE, cooldown, isTop, field1, field2);
    }

    /**
     * 28.和主播跳舞
     *
     * @param cooldown 和主播跳舞的持续时间，单位秒（-1为永久）默认30秒
     * @param isTop    isTop:false-不置顶；true-置顶
     * @param userId   field1:playerId（主播玩家的id）；该参数必传，不传则没有任何效果
     */
    public String danceWithAnchor(Integer cooldown, Boolean isTop, String userId) {
        String field1 = userId;
        String field2 = null;
        return buidlJson(ACTION_ANCHOR_DANCE, cooldown, isTop, field1, field2);
    }

    public String buidlJson(int actionId, Integer cooldown, Boolean isTop, String field1, String field2) {
        SudMGPAPPState.AppCommonGameDiscoAction state = new SudMGPAPPState.AppCommonGameDiscoAction();
        state.actionId = actionId;
        state.cooldown = cooldown;
        state.isTop = isTop;
        state.field1 = field1;
        state.field2 = field2;
        return SudJsonUtils.toJson(state);
    }

}
