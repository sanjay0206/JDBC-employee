package com.employee.utils;

import com.employee.enums.ResponseStatus;

public class ServerResponseDto {
    private ResponseStatus responseStatus;
    private String responseMessage;

    public ServerResponseDto() {
    }

    public ServerResponseDto(ResponseStatus responseStatus, String responseMessage) {
        this.responseStatus = responseStatus;
        this.responseMessage = responseMessage;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        return "ServerResponseDto{" +
                "responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
