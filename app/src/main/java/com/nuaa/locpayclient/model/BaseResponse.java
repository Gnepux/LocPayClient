package com.nuaa.locpayclient.model;

public class BaseResponse<T> {

    private HeaderModel header;

    private T body;

    public HeaderModel getHeader() {
        return header;
    }

    public void setHeader(HeaderModel header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
