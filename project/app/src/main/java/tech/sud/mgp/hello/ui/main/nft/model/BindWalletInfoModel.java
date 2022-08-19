package tech.sud.mgp.hello.ui.main.nft.model;

import java.io.Serializable;
import java.util.List;

import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * 已绑定钱包信息
 */
public class BindWalletInfoModel implements Serializable {
    public int walletType; // 钱包类型
    public String walletToken; // 钱包token
    public SudNFTGetWalletListModel.ChainInfo chainInfo; // 选中的链信息
    public List<SudNFTGetWalletListModel.ChainInfo> chainInfoList; // 该钱包的所有链信息
    public String walletAddress; // 钱包地址
    public NftModel wearNft; // 穿戴的nft

    public int getChainType() {
        if (chainInfo != null) {
            return chainInfo.type;
        }
        return 0;
    }
}
