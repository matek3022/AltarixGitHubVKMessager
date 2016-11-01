package com.example.vk_mess_demo_00001;

/**
 * Created by Каракатица on 07.10.2016.
 */

public class ServerResponse<T> {
    private T response;
    private Error error;

    public class Error {}

    public T getResponse() {
        return response;
    }
}
