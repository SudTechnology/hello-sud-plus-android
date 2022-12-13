package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.ui.main.nft.model.NftRetCode;

/**
 * 网络请求返回工具类
 */
public class ResponseUtils {

    /**
     * 转换成展示的文案
     */
    public static <T> String conver(BaseResponse<T> response) {
        if (response == null) {
            return null;
        }
        return conver(response.getRetCode(), response.getRetMsg());
    }

    public static String conver(int code, String msg) {
        return msg + "(" + code + ")";
    }

    /** 转换NFT相关的错误码 */
    public static String nftConver(int code, String msg) {
        switch (code) {
            case NftRetCode.NFT_DETAILS_TOKEN_EXPIRE:
                msg = Utils.getApp().getString(R.string.nft_code_1019);
                break;
            case NftRetCode.WALLET_TYPE_NOT_SUPPORT:
                msg = Utils.getApp().getString(R.string.nft_code_1020);
                break;
            case NftRetCode.SEND_PHONE_CODE_FAILED:
                msg = Utils.getApp().getString(R.string.nft_code_1021);
                break;
            case NftRetCode.BIND_USER_FAILED:
                msg = Utils.getApp().getString(R.string.nft_code_1022);
                break;
            case NftRetCode.CARD_LIST_GET_FAILED:
                msg = Utils.getApp().getString(R.string.nft_code_1023);
                break;
            case NftRetCode.CARD_GET_FAILED:
                msg = Utils.getApp().getString(R.string.nft_code_1024);
                break;
            case NftRetCode.CARD_NOT_BELONG_TO_USER:
                msg = Utils.getApp().getString(R.string.nft_code_1025);
                break;
            case NftRetCode.SMS_TOO_FREQUENT:
                msg = Utils.getApp().getString(R.string.nft_code_1026);
                break;
            case NftRetCode.PHONE_NUMBER_HAS_BEEN_BOUND:
                msg = Utils.getApp().getString(R.string.nft_code_1027);
                break;
            case NftRetCode.PHONE_NUMBER_BELONG_TO_OTHERS_USER:
                msg = Utils.getApp().getString(R.string.nft_code_1028);
                break;
            case NftRetCode.USER_UNBIND_FAILED:
                msg = Utils.getApp().getString(R.string.nft_code_1029);
                break;
            case NftRetCode.SMS_CODE_INVALID:
                msg = Utils.getApp().getString(R.string.nft_code_1030);
                break;
            case NftRetCode.VERIFY_CODE_TIP:
                return msg;
        }
        return conver(code, msg);
    }

}
