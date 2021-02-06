package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

public class Result implements Serializable {

    private ResultStatus status = ResultStatus.OK;
    private String message;
    private String errorCode;

    @JsonIgnore
    private HashMap<String, Object> params = new HashMap<>();

    public Result() {
    }

    public Result(ResultStatus status) {
        this.status = status;
    }

    public Result(ResultStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public Result setStatus(ResultStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    @JsonIgnore
    public boolean isOk() {
        return getStatus() == ResultStatus.OK;
    }

    @JsonIgnore
    public boolean isNotOk() {
        return !isOk();
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }


    public <X> X getParam(String key) {
        return (X) getParams().get(key);
    }

    public <T extends Result> T addParam(String key, Object value) {
        getParams().put(key, value);
        return (T) this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Result setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
