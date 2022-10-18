package tech.sud.mgp.hello.ui.main.nft.model;

import android.text.TextUtils;

import org.json.JSONObject;

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

    /** 扩展字段 */
    public JSONObject extension;

    // region 国内钱包
    /** 藏品id */
    public String cardId;
    // endregion 国内钱包

    // region 自定义参数
    public boolean isDressedIn; // 是否是穿戴中

    public String detailsToken; // 穿戴token

    public long walletType; // 当前使用的钱包类型

    public int zoneType; // 区域类型 0国外 1国内

    public long chainType; // 链类型
    // endregion 自定义参数

    /** 获取显示的URL */
    public String getShowUrl() {
        if (!TextUtils.isEmpty(coverUrl)) {
            return coverUrl;
        }
        if (fileType == FILE_TYPE_IMAGE) {
            return fileUrl;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NftModel nftModel = (NftModel) o;
        return Objects.equals(contractAddress, nftModel.contractAddress) && Objects.equals(tokenId, nftModel.tokenId)
                && Objects.equals(chainType, nftModel.chainType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractAddress, tokenId, chainType);
    }
}
