package tech.sud.mgp.hello.ui.nft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 已绑定钱包信息
 */
public class BindWalletInfoModel implements Serializable {

    public NftModel wearNft; // 穿戴的nft

    // region 当前使用的钱包
    public long walletType; // 钱包类型
    public String walletToken; // 钱包token
    public String phone; // 绑定的手机号码
    public int zoneType; // 区域类型 0国外 1国内

    public WalletChainInfo chainInfo; // 选中的链信息
    public List<WalletChainInfo> chainInfoList; // 该钱包的所有链信息
    public String walletAddress; // 钱包地址
    // endregion 当前使用的钱包

    /** 绑定的钱包列表 */
    public List<WalletInfoModel> walletList;

    public long getChainType() {
        if (chainInfo != null) {
            return chainInfo.type;
        }
        return 0;
    }

    /** 添加一个绑定了的钱包 */
    public void addBindWallet(WalletInfoModel model) {
        if (model == null) {
            return;
        }
        if (walletList == null) {
            walletList = new ArrayList<>();
        }
        walletList.remove(model);
        walletList.add(model);
    }

    /** 删除一个绑定了的钱包 */
    public void removeBindWallet(WalletInfoModel model) {
        if (model == null) {
            return;
        }
        if (walletList != null) {
            walletList.remove(model);
        }
    }

    /** 删除一个绑定了的钱包 */
    public void removeBindWallet(long walletType) {
        if (walletList != null) {
            ListIterator<WalletInfoModel> iterator = walletList.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().type == walletType) {
                    iterator.remove();
                }
            }
        }
    }

    /** 绑定的包名是否包含该钱包类型 */
    public boolean isContainer(long walletType) {
        if (walletList != null) {
            for (WalletInfoModel model : walletList) {
                if (model.type == walletType) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 如果有，则获取第一个绑定了的钱包 */
    public WalletInfoModel getWalletInfoModel() {
        if (walletList != null && walletList.size() > 0) {
            return walletList.get(0);
        }
        return null;
    }

    /** 根据钱包类型获取钱包 */
    public WalletInfoModel getWalletInfoModel(long walletType) {
        if (walletList != null && walletList.size() > 0) {
            for (WalletInfoModel walletInfoModel : walletList) {
                if (walletInfoModel.type == walletType) {
                    return walletInfoModel;
                }
            }
        }
        return null;
    }

    /** 获取默认的链 */
    public WalletChainInfo getDefaultChainInfo() {
        if (chainInfoList != null && chainInfoList.size() > 0) {
            return chainInfoList.get(0);
        }
        return null;
    }

}
