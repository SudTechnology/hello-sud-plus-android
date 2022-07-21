package tech.sud.mgp.hello.ui.scenes.disco.model;

/**
 * 蹦迪交互类型
 */
public enum DiscoInteractionType {
    UNDEFINED, // 未定义
    JOIN_ANCHOR, // 上主播位
    LEAVE_ANCHOR, // 下主播位
    UP_DJ, // 上DJ台
    MOVE, // 移动
    GOD, // 上天
    BIG, // 变大
    CHANGE_ROLE, // 更换角度
    FOCUS, // 聚焦
    TITLE, // 称号
    EFFECTS, // 特效
    POP_BIG_FOCUS, // 气泡变大聚焦
    POP_BIG_FOCUS_EFFECTS, // 气泡变大聚焦特效
}
