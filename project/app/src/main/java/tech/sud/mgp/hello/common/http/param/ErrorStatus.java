package tech.sud.mgp.hello.common.http.param;

/**
 * 错误状态枚举
 */
public enum ErrorStatus {

    HTTP_TIMEOUT("连接超时"),
    HTTP_UNCONNECTED("请检查网络"),
    HTTP_EXCEPTION("网络异常"),
    DATA_EXCEPTION("数据解析错误"),
    OTHER_EXCEPTION("未知错误");

    ErrorStatus(String message) {
        this.mErrorMessage = message;
    }

    private int mErrorCode;
    private String mErrorMessage;

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public ErrorStatus setErrorCode(int errorCode) {
        mErrorCode = errorCode;
        return this;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
