package tech.sud.mgp.hello.ui.main.nft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 已绑定钱包信息
 */
public class BindWalletInfoModel implements Serializable {

    public long walletType; // 当前使用的钱包类型

    /** 绑定的钱包列表 */
    public List<WalletInfoModel> walletList;

    /** 穿戴的nft */
    public List<NftModel> wearNftList;

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

    /** 添加一个穿戴了的NFT */
    public void addWearNft(NftModel model) {
        if (model == null) {
            return;
        }
        if (wearNftList == null) {
            wearNftList = new ArrayList<>();
        }
        wearNftList.remove(model);
        wearNftList.add(model);
    }

    /** 删除一个穿戴的NFT */
    public void removeWearNft(NftModel model) {
        if (model == null) {
            return;
        }
        if (wearNftList != null) {
            wearNftList.remove(model);
        }
    }

    public NftModel getWearNft() {
        if (wearNftList != null && wearNftList.size() > 0) {
            return wearNftList.get(0);
        }
        return null;
    }

    public void clearWearNft() {
        if (wearNftList != null) {
            wearNftList.clear();
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
    public WalletInfoModel getFirstWalletInfoModel() {
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

    /** 获取当前使用的钱包token */
    public String getWalletToken() {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            return model.walletToken;
        }
        return null;
    }

    public int getZoneType() {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            return model.zoneType;
        }
        return ZoneType.NO;
    }

    public String getWalletAddress() {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            return model.walletAddress;
        }
        return null;
    }

    public WalletChainInfo getChainInfo() {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            return model.chainInfo;
        }
        return null;
    }

    public List<WalletChainInfo> getChainInfoList() {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            return model.chainInfoList;
        }
        return null;
    }

    public void setChainInfo(WalletChainInfo chainInfo) {
        WalletInfoModel model = getWalletInfoModel(walletType);
        if (model != null) {
            model.chainInfo = chainInfo;
        }
    }

    public long getChainType() {
        WalletChainInfo chainInfo = getChainInfo();
        if (chainInfo != null) {
            return chainInfo.type;
        }
        return 0;
    }


}
