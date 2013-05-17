package com.github.avereshchagin.ciscolab;

public class ConnectionParameters {

    private String accessToken = "";
    private int deviceId;

    public ConnectionParameters() {
    }

    public ConnectionParameters(String accessToken, int deviceId) {
        this.accessToken = accessToken;
        this.deviceId = deviceId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getDeviceId() {
        return deviceId;
    }
}
