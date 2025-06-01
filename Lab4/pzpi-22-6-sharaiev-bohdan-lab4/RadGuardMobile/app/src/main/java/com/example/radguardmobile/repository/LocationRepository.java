package com.example.radguardmobile.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.radguardmobile.api.ApiService;
import com.example.radguardmobile.api.RetrofitClient;
import com.example.radguardmobile.models.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private final ApiService apiService;

    public LocationRepository() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public void getLocations(Context context, LocationCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.getLocations("Bearer " + token).enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Не вдалося отримати локації: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                callback.onError("Мережева помилка: " + t.getMessage());
            }
        });
    }

    public interface LocationCallback {
        void onSuccess(List<Location> locations);

        void onError(String errorMessage);
    }
}
