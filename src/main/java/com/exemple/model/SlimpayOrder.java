package com.exemple.model;

public class SlimpayOrder {

    private String orderId;
    private String redirectUrl;

    public String getOrderId() {
        return orderId;
    }

    public SlimpayOrder setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public SlimpayOrder setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }
}
