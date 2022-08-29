package tech.sud.mgp.hello.ui.main.nft.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 钱包信息描述
 */
public class WalletInfoModel implements Serializable {

    public int type;
    public String name;
    public String icon;
    public int zoneType;
    public List<WalletChainInfo> chainList;

    // region 自定义参数
    public String walletToken;
    public String phone;
    public String walletAddress;
    // endregion 自定义参数

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletInfoModel that = (WalletInfoModel) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }


}
