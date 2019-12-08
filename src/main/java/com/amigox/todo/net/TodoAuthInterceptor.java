package com.amigox.todo.net;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TodoAuthInterceptor implements Interceptor {
    private final static String API_TOKEN = "7e50f047a97da38093006304f04cfc63a20dd129";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Headers headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Authorization", "token " +  API_TOKEN)
                .add("User-Agent", "Todo-App")
                .build();

        Request newRequest = originalRequest.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .headers(headers)
                .build();

        return chain.proceed(newRequest);
    }
}
