package tech.sud.mgp.hello.ui.main.nft.model;

import java.util.ArrayList;

import tech.sud.nft.core.model.resp.SudNFTGetNFTListModel;

public class NftListResultModelConvertor {

    public static NftListResultModel conver(SudNFTGetNFTListModel src) {
        if (src == null) {
            return null;
        }
        NftListResultModel dest = new NftListResultModel();
        dest.totalCount = src.totalCount;
        dest.pageKey = src.pageKey;
        if (src.nftList != null) {
            dest.list = new ArrayList<>();
            for (SudNFTGetNFTListModel.NFTInfo srcNFTInfo : src.nftList) {
                dest.list.add(conver(srcNFTInfo));
            }
        }
        return dest;
    }

    public static NftModel conver(SudNFTGetNFTListModel.NFTInfo srcNftInfo) {
        NftModel destNftInfo = new NftModel();
        destNftInfo.contractAddress = srcNftInfo.contractAddress;
        destNftInfo.tokenId = srcNftInfo.tokenId;
        destNftInfo.tokenType = srcNftInfo.tokenType;
        destNftInfo.name = srcNftInfo.name;
        destNftInfo.description = srcNftInfo.desc;
        destNftInfo.fileUrl = srcNftInfo.fileUrl;
        destNftInfo.fileType = srcNftInfo.fileType;
        destNftInfo.coverUrl = srcNftInfo.coverUrl;
        return destNftInfo;
    }

}
