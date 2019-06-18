package data;

import java.io.Serializable;

import network.HttpErrorCode;

public class Result<T> implements Serializable {


    private int errno;

    private String errmsg;

    private T data;

    private int status;

    private String url;

    public Result() {
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOk() {
        return errno == HttpErrorCode.SUCCESS;
    }
    @Override
    public String toString() {
        return "Result{" +
                "errno=" + errno +
                ", errmsg='" + errmsg + '\'' +
                ", data=" + data +
                '}';
    }
}
