package com.caner.bean;

import java.time.LocalDateTime;

public class RequestLog extends IdObject {

    private String logSource;

    private LocalDateTime requestDate;

    private String requestHeader;

    private String requestBody;

    private int responseStatus;

    private String requestMethod;

    private String path;

    private long elapsedTime;

    private String xAuthToken;

    private String clientIp;

    private String applicationName;

    private String responseBody;

    private String responseHeader;

    private long userTenantId;

    private String userName;

    private String messageDirection;

    private String customerDeliveryNo;

    public RequestLog() {
    }

    public RequestLog(Long id) {
        setId(id);
    }

    public String getLogSource() {
        return logSource;
    }

    public void setLogSource(String logSource) {
        this.logSource = logSource;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getxAuthToken() {
        return xAuthToken;
    }

    public void setxAuthToken(String xAuthToken) {
        this.xAuthToken = xAuthToken;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getUserTenantId() {
        return userTenantId;
    }

    public void setUserTenantId(long userTenantId) {
        this.userTenantId = userTenantId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(String messageDirection) {
        this.messageDirection = messageDirection;
    }

    public String getCustomerDeliveryNo() {
        return customerDeliveryNo;
    }

    public void setCustomerDeliveryNo(String customerDeliveryNo) {
        this.customerDeliveryNo = customerDeliveryNo;
    }
}
