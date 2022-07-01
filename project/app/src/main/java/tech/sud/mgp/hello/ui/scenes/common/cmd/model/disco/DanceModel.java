package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import com.google.gson.annotations.Expose;

import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 跳舞 队列数据
 * 1，beginTime>0 && (duration+beginTime<System.currentTimeMillis()) 则表示跳舞已完成
 * 2，beginTime>0 表示正在执行中
 * 3，表示正在排队当中
 */
public class DanceModel {
    public UserInfo fromUser; // 发起方
    public UserInfo toUser; // 主播方
    public int duration; // 持续时间
    public long beginTime; // 开始时间(秒)，开始执行的时间戳

    @Expose(serialize = false, deserialize = false) // 既不序列化也不反序列化
    public int countdown; // 倒计时

    @Expose(serialize = false, deserialize = false) // 既不序列化也不反序列化
    public CustomCountdownTimer countdownTimer; // 倒计时器

    @Expose(serialize = false, deserialize = false) // 既不序列化也不反序列化
    public boolean isCompleted; // 是否已完成

    @Expose(serialize = false, deserialize = false) // 既不序列化也不反序列化
    public boolean isShowCompletedTitle; // 是否要显示已完成的title
}
