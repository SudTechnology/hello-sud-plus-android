package tech.sud.mgp.hello.common.http.param;

public class BaseResponse<T> {
    public int ret_code;
    public int ret_msg;
    public T data;

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public int getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(int ret_msg) {
        this.ret_msg = ret_msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
