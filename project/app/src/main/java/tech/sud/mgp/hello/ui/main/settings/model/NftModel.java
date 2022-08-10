package tech.sud.mgp.hello.ui.main.settings.model;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NftModel nftModel = (NftModel) o;
        return Objects.equals(contractAddress, nftModel.contractAddress) && Objects.equals(tokenId, nftModel.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractAddress, tokenId);
    }
}
