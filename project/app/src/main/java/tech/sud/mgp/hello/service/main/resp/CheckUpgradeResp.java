package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;

/**
 * 检查更新返回参数
 */
public class CheckUpgradeResp implements Serializable {

    public static final int FORCE_UPGRADE = 1; // 强制升级
    public static final int GUIDE_UPGRADE = 2; // 引导升级

    public Long upgradeId; // 升级ID
    public String channel; // 渠道
    public String oldVersion; // 老版本
    public int deviceType; // 设备类型（1.安卓，2.IOS）
    public String targetVersion; // 目标版本
    public String packageUrl; // 包路径
    public int upgradeType; // 升级类型(1强制升级，2引导升级)
    public String dialogTitle; // 弹窗标题（暂未用到）
    public String dialogContent; // 弹窗内容（暂未用到）
}