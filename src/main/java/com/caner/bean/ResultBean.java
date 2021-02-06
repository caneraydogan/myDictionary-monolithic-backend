package com.caner.bean;

public class ResultBean<T> extends Result {

    private T data;

    public ResultBean() {
    }

    public ResultBean(T data) {
        this.data = data;
    }

    public ResultBean(ResultStatus status) {
        super(status);
    }

    public ResultBean(T data, ResultStatus status) {
        super(status);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public ResultBean<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public ResultBean<T> setStatus(ResultStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public ResultBean<T> setMessage(String message) {
        super.setMessage(message);
        return this;
    }

    public ResultBean<T> setResultCodeAndMessage(ResultStatus status, String message) {
        this.setStatus(status);
        this.setMessage(message);
        return this;
    }
}
