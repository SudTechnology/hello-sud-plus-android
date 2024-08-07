package tech.sud.mgp.hello.ui.nft.model;

import java.util.ArrayList;

import tech.sud.nft.core.model.resp.SudNFTGetCnNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetNFTListModel;

public class NftListResultModelConvertor {

    public static NftListResultModel conver(long chainType, SudNFTGetNFTListModel src) {
        if (src == null) {
            return null;
        }
        NftListResultModel dest = new NftListResultModel();
        dest.totalCount = src.totalCount;
        dest.pageKey = src.pageKey;
        if (src.nftList != null) {
            dest.list = new ArrayList<>();
            for (SudNFTGetNFTListModel.NFTInfo srcNFTInfo : src.nftList) {
                dest.list.add(conver(chainType, srcNFTInfo));
            }
        }
        return dest;
    }

    private static NftModel conver(long chainType, SudNFTGetNFTListModel.NFTInfo srcNftInfo) {
        NftModel destNftInfo = new NftModel();
        destNftInfo.contractAddress = srcNftInfo.contractAddress;
        destNftInfo.tokenId = srcNftInfo.tokenId;
        destNftInfo.tokenType = srcNftInfo.tokenType;
        destNftInfo.name = srcNftInfo.name;
        destNftInfo.description = srcNftInfo.desc;
        destNftInfo.fileUrl = srcNftInfo.fileUrl;
        destNftInfo.fileType = srcNftInfo.fileType;
        destNftInfo.coverUrl = srcNftInfo.coverUrl;
        destNftInfo.extension = srcNftInfo.extension;
        destNftInfo.chainType = chainType;
        return destNftInfo;
    }

    public static NftListResultModel conver(long chainType, SudNFTGetCnNFTListModel src) {
        if (src == null) {
            return null;
        }
        NftListResultModel dest = new NftListResultModel();
        dest.totalCount = src.totalCount;
        if (src.list != null) {
            dest.list = new ArrayList<>();
            for (SudNFTGetCnNFTListModel.CnNFTInfo srcNFTInfo : src.list) {
                dest.list.add(conver(chainType, srcNFTInfo));
            }
        }
        return dest;
    }

    private static NftModel conver(long chainType, SudNFTGetCnNFTListModel.CnNFTInfo srcNftInfo) {
        NftModel destNftInfo = new NftModel();
        destNftInfo.name = srcNftInfo.name;
        destNftInfo.description = srcNftInfo.desc;
        destNftInfo.fileUrl = srcNftInfo.fileUrl;
        destNftInfo.fileType = srcNftInfo.fileType;
        destNftInfo.coverUrl = srcNftInfo.coverUrl;
        destNftInfo.contractAddress = srcNftInfo.cardHash;
        destNftInfo.tokenId = srcNftInfo.chainAddr;
        destNftInfo.cardId = srcNftInfo.cardId;
        destNftInfo.chainType = chainType;
//        destNftInfo.tokenType = srcNftInfo.tokenType;
        return destNftInfo;
    }

}
