package com.example.cattleapp.network;

import com.example.cattleapp.models.Animal;
import com.example.cattleapp.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("register")
    Call<ResponseBody> register(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);

    @POST("verify")
    Call<ResponseBody> verifyCode(@Body User user);

    @GET("animals")
    Call<List<Animal>> getAnimals(@Query("ownerId") int ownerId);

    @POST("animals")
    Call<Animal> addAnimal(@Body Animal animal);

    @PUT("animals/{id}")
    Call<Animal> updateAnimal(@Path("id") int id, @Body Animal animal);
}
