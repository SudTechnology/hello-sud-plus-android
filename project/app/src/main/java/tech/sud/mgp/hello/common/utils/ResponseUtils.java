package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseResponse;

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
            case 1019:
                msg = Utils.getApp().getString(R.string.nft_code_1019);
                break;
            case 1020:
                msg = Utils.getApp().getString(R.string.nft_code_1020);
                break;
            case 1021:
                msg = Utils.getApp().getString(R.string.nft_code_1021);
                break;
            case 1022:
                msg = Utils.getApp().getString(R.string.nft_code_1022);
                break;
            case 1023:
                msg = Utils.getApp().getString(R.string.nft_code_1023);
                break;
            case 1024:
                msg = Utils.getApp().getString(R.string.nft_code_1024);
                break;
            case 1025:
                msg = Utils.getApp().getString(R.string.nft_code_1025);
                break;
            case 1026:
                msg = Utils.getApp().getString(R.string.nft_code_1026);
                break;
            case 1027:
                msg = Utils.getApp().getString(R.string.nft_code_1027);
                break;
            case 1028:
                msg = Utils.getApp().getString(R.string.nft_code_1028);
                break;
            case 1029:
                msg = Utils.getApp().getString(R.string.nft_code_1029);
                break;
            case 1030:
                msg = Utils.getApp().getString(R.string.nft_code_1030);
                break;
            case 1035:
                return msg;
        }
        return conver(code, msg);
    }

}
