package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributionModel that = (ContributionModel) o;
        return Objects.equals(fromUser, that.fromUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUser);
    }
}