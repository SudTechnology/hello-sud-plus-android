package tech.sud.mgp.hello.ui.nft.model;

import java.util.ArrayList;
import java.util.List;

import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

public class WalletChainInfoConvertor {

    public static WalletChainInfo conver(SudNFTGetWalletListModel.ChainInfo src) {
        if (src == null) {
            return null;
        }
        WalletChainInfo dest = new WalletChainInfo();
        dest.type = src.type;
        dest.name = src.name;
        dest.icon = src.icon;
        return dest;
    }

    public static List<WalletChainInfo> conver(List<SudNFTGetWalletListModel.ChainInfo> src) {
        if (src == null) {
            return null;
        }
        List<WalletChainInfo> list = new ArrayList<>();
        for (SudNFTGetWalletListModel.ChainInfo srcInfo : src) {
            list.add(conver(srcInfo));
        }
        return list;
    }

}
