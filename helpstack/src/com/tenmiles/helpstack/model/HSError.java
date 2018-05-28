package com.tenmiles.helpstack.model;

/**
 * Created by muthukumarkrishnan on 10/15/15.
 */
public class HSError implements Cloneable {
    private enum ErrorCode {
        AppError,
        HttpError,
        NetworkError
    }

    public String message;
    public int httpResponseCode;
    public ErrorCode errorCode;

    public void initWithAppError(String msg) {
        message = msg;
        errorCode = ErrorCode.AppError;
    }

    public void initWithHttpError(int responseCode, String msg) {
        errorCode = ErrorCode.HttpError;
        httpResponseCode = responseCode;
        message = msg;
    }

    public void initWithNetworkError(String msg) {
        errorCode = ErrorCode.NetworkError;
        message = msg;
    }

    public boolean isAppError() {
        return (errorCode == ErrorCode.AppError);
    }

    public boolean isNetworkError() {
        return (errorCode == ErrorCode.NetworkError);
    }

    public boolean isHttpError() {
        return (errorCode == ErrorCode.HttpError);
    }
}
