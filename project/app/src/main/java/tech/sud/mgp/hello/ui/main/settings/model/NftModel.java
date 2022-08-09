package tech.sud.mgp.hello.ui.main.settings.model;

import java.io.Serializable;

import tech.sud.nft.core.model.SudNFTGetMetadataModel;

/**
 * NFT 数据
 */
public class NftModel implements Serializable {

    public String contractAddress; // 合约地址
    public String tokenId; // nft id

    public SudNFTGetMetadataModel metadataModel; // 元数据

    public String getImageUrl() {
        if (metadataModel == null) {
            return null;
        } else {
            return metadataModel.image;
        }
    }

    public String getName() {
        if (metadataModel == null) {
            return null;
        } else {
            return metadataModel.name;
        }
    }

    public String getTokenType() {
        if (metadataModel == null) {
            return null;
        } else {
            return metadataModel.tokenType;
        }
    }

}
