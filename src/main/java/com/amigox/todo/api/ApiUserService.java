package com.amigox.todo.api;

import com.google.gson.JsonArray;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiUserService {
    @Headers({
            "Accept: application/json",
            "User-Agent: Todo-App"
    })
    @GET("api/users/{username}")
    Call<ApiUser> getByUsername(@Path("username") String username);

    @GET("api/user/todo")
    Call<JsonArray> getMyTodos(@Query("type") String type);

    @GET("api/users/{username}/repos")
    Call<JsonArray> getUserTodo(@Path("username") String username);

}
