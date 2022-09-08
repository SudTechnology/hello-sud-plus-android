package tech.sud.mgp.hello.ui.main.nft.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 钱包信息描述
 */
public class WalletInfoModel implements Serializable {

    public long type; // 钱包类型
    public String name; // 钱包名称
    public String icon; // 钱包图标
    public int zoneType; // 区域类型 0国外 1国内
    public List<WalletChainInfo> chainInfoList; // 该钱包的所有链信息

    // region 自定义参数
    public String walletToken;
    public String phone;
    public String walletAddress;
    public WalletChainInfo chainInfo; // 选中的链信息
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

    /** 获取默认的链 */
    public WalletChainInfo getDefaultChainInfo() {
        if (chainInfoList != null && chainInfoList.size() > 0) {
            return chainInfoList.get(0);
        }
        return null;
    }

}
