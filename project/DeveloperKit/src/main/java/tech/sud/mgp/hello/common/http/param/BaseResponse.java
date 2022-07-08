package tech.sud.mgp.hello.common.http.param;

public class BaseResponse<T> {

    private int retCode;
    private String retMsg;
    private T data;

    public BaseResponse(int retCode, String retMsg, T data) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.data = data;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
