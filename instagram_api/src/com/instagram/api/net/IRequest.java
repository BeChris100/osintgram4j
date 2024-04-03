package com.instagram.api.net;

public interface IRequest {

    void onRequestComplete(byte[] data);

    void onRequestFailed(int code, byte[] data);

}
