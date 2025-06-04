package com.example.radguardmobile.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.radguardmobile.api.ApiService;
import com.example.radguardmobile.api.RetrofitClient;
import com.example.radguardmobile.api.response.AlertResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertRepository {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";

    private final ApiService apiService;

    public AlertRepository() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    private String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    public void getAlerts(Context context, AlertListCallback callback) {
        String token = getToken(context);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.getAlerts("Bearer " + token).enqueue(new Callback<List<AlertResponse>>() {
            @Override
            public void onResponse(Call<List<AlertResponse>> call, Response<List<AlertResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження сповіщень: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AlertResponse>> call, Throwable t) {
                callback.onError("Помилка завантаження сповіщень: " + t.getMessage());
            }
        });
    }

    public void deleteAlert(Context context, int alertId, DeleteAlertCallback callback) {
        String token = getToken(context);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.deleteAlert("Bearer " + token, alertId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Не вдалося видалити сповіщення: код " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Помилка мережі: " + t.getMessage());
            }
        });
    }

    public interface AlertListCallback {
        void onSuccess(List<AlertResponse> alerts);

        void onError(String errorMessage);
    }

    public interface DeleteAlertCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
}


