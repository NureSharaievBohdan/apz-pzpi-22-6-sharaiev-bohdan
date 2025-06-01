package com.example.radguardmobile.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.radguardmobile.api.ApiService;
import com.example.radguardmobile.api.RetrofitClient;
import com.example.radguardmobile.models.RadiationData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RadiationRepository {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";

    private final ApiService apiService;

    public RadiationRepository() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    private String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    public void getRadiationLevel(Context context, int sensorId, RadiationCallback callback) {
        String token = getToken(context);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.getSensorData("Bearer " + token, sensorId).enqueue(new Callback<List<RadiationData>>() {
            @Override
            public void onResponse(Call<List<RadiationData>> call, Response<List<RadiationData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка отримання рівня радіації: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RadiationData>> call, Throwable t) {
                callback.onError("Помилка мережі: " + t.getMessage());
            }
        });
    }

    public interface RadiationCallback {
        void onSuccess(List<RadiationData> data);

        void onError(String message);
    }
}
