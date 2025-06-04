package com.example.radguardmobile.api;


import com.example.radguardmobile.api.request.AddSensorRequest;
import com.example.radguardmobile.api.response.AlertResponse;
import com.example.radguardmobile.api.response.AuthResponse;
import com.example.radguardmobile.models.Location;
import com.example.radguardmobile.models.RadiationData;
import com.example.radguardmobile.models.Sensor;
import com.example.radguardmobile.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/auth/login/")
    Call<AuthResponse> loginUser(@Body User user);

    @GET("sensors/")
    Call<List<Sensor>> getSensors(@Header("Authorization") String token);

    @POST("sensors/")
    Call<Sensor> addSensor(
            @Header("Authorization") String token,
            @Body AddSensorRequest body
    );

    @GET("sensors/{id}/")
    Call<Sensor> getSensor(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @PUT("sensors/{id}/")
    Call<Sensor> updateSensor(@Header("Authorization") String token, @Path("id") int id, @Body AddSensorRequest body);

    @DELETE("sensors/{id}/")
    Call<Void> deleteSensor(
            @Header("Authorization") String authorization,
            @Path("id") int sensorId
    );


    @GET("sensors/{id}/radiation-data/")
    Call<List<RadiationData>> getSensorData(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("locations/")
    Call<List<Location>> getLocations(@Header("Authorization") String token);


    @GET("alerts/")
    Call<List<AlertResponse>> getAlerts(@Header("Authorization") String token);

    @DELETE("alerts/{id}/")
    Call<Void> deleteAlert(
            @Header("Authorization") String authorization,
            @Path("id") int alertId
    );
}

