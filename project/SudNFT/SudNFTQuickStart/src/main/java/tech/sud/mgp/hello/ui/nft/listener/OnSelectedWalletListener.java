package tech.sud.mgp.hello.ui.nft.listener;

import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

public interface OnSelectedWalletListener {

    void onSelected(SudNFTGetWalletListModel.WalletInfo walletInfo);

}
