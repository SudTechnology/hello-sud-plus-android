package tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 竞猜下注通知
 */
public class QuizBetModel extends RoomCmdBaseModel {
    public List<UserInfo> recUser; // 被下注用户列表

    public QuizBetModel(UserInfo sendUser) {
        super(RoomCmd.CMD_QUIZ_BET, sendUser);
    }

    public static QuizBetModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, QuizBetModel.class);
    }

}
