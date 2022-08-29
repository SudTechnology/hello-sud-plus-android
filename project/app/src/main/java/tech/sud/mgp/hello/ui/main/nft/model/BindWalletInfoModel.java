package tech.sud.mgp.hello.ui.main.nft.model;

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
    public int walletType; // 钱包类型
    public String walletToken; // 钱包token
    public String phone; // 绑定的手机号码
    public int zoneType; // 区域类型 0国外 1国内

    public WalletChainInfo chainInfo; // 选中的链信息
    public List<WalletChainInfo> chainInfoList; // 该钱包的所有链信息
    public String walletAddress; // 钱包地址
    // endregion 当前使用的钱包

    /** 绑定的钱包列表 */
    public List<WalletInfoModel> walletList;

    public int getChainType() {
        if (chainInfo != null) {
            return chainInfo.type;
        }
        return 0;
    }

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

    public void removeBindWallet(WalletInfoModel model) {
        if (model == null) {
            return;
        }
        if (walletList != null) {
            walletList.remove(model);
        }
    }

    public void removeBindWallet(int walletType) {
        if (walletList != null) {
            ListIterator<WalletInfoModel> iterator = walletList.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().type == walletType) {
                    iterator.remove();
                }
            }
        }
    }

    public boolean isContainer(int walletType) {
        if (walletList != null) {
            for (WalletInfoModel model : walletList) {
                if (model.type == walletType) {
                    return true;
                }
            }
        }
        return false;
    }

    public WalletInfoModel getWalletInfoModel() {
        if (walletList != null && walletList.size() > 0) {
            return walletList.get(0);
        }
        return null;
    }

    public WalletInfoModel getWalletInfoModel(int walletType) {
        if (walletList != null && walletList.size() > 0) {
            for (WalletInfoModel walletInfoModel : walletList) {
                if (walletInfoModel.type == walletType) {
                    return walletInfoModel;
                }
            }
        }
        return null;
    }

    public WalletChainInfo getDefaultChainInfo() {
        if (chainInfoList != null && chainInfoList.size() > 0) {
            return chainInfoList.get(0);
        }
        return null;
    }

}
