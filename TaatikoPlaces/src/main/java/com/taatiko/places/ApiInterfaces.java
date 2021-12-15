package com.taatiko.places;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterfaces {
    @Headers("Content-Type: application/json")
    @POST("query")
    Call<JsonObjectModalResponse> get_query(@Body RequestBody requestBody);
}
