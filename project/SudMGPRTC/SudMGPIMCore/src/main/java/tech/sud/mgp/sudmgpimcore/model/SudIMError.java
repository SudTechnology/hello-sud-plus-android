package tech.sud.mgp.sudmgpimcore.model;

public class SudIMError {

    public int code = -1;
    public String message;

    public SudIMError() {
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "ZIMError{code=" + this.code + ", message='" + this.message + '\'' + '}';
    }

}
