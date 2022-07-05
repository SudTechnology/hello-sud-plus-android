package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 蹦迪贡献
 */
public class ContributionModel implements Comparable<ContributionModel> {
    public UserInfo fromUser;
    public long count; // 贡献总值

    @Override
    public int compareTo(ContributionModel o) {
        if (count > o.count) {
            return -1;
        }
        if (count == o.count) {
            if (fromUser != null && fromUser.userID != null && o.fromUser != null) {
                return fromUser.userID.compareTo(o.fromUser.userID);
            }
            return 0;
        }
        return 1;
    }

}