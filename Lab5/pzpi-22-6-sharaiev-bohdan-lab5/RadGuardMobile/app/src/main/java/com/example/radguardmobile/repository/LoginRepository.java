package com.example.radguardmobile.repository;

import com.example.radguardmobile.api.ApiService;
import com.example.radguardmobile.api.RetrofitClient;
import com.example.radguardmobile.api.response.AuthResponse;
import com.example.radguardmobile.models.User;

import retrofit2.Call;

public class LoginRepository {

    private final ApiService apiService;

    public LoginRepository() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public Call<AuthResponse> login(User user) {
        return apiService.loginUser(user);
    }
}
