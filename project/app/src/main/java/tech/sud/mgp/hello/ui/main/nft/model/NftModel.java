package tech.sud.mgp.hello.ui.main.nft.model;

import java.io.Serializable;
import java.util.Objects;


/**
 * NFT 数据
 */
public class NftModel implements Serializable {

    public static final int FILE_TYPE_IMAGE = 1;

    /** 合约地址 */
    public String contractAddress;

    /** id */
    public String tokenId;

    /** nft 标准 */
    public String tokenType;

    /** 名称 */
    public String name;

    /** 描述 */
    public String description;

    /** 作品地址 */
    public String fileUrl;

    /** 作品类型，1=图片 */
    public int fileType;

    /** 封面地址 */
    public String coverUrl;

    // region 自定义参数
    public boolean isDressedIn; // 是否是穿戴中
    // endregion 自定义参数

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
