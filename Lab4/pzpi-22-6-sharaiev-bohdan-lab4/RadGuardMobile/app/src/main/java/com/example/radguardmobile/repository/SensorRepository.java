package com.example.radguardmobile.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.radguardmobile.api.ApiService;
import com.example.radguardmobile.api.RetrofitClient;
import com.example.radguardmobile.api.request.AddSensorRequest;
import com.example.radguardmobile.models.Sensor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorRepository {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private final ApiService apiService;

    public SensorRepository() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    public void getSensors(Context context, SensorCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.getSensors("Bearer " + token).enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(Call<List<Sensor>> call, Response<List<Sensor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження сенсорів: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sensor>> call, Throwable t) {
                callback.onError("Помилка завантаження сенсорів: " + t.getMessage());
            }
        });
    }

    public void addSensor(Context context, String name, String status, int locationId, AddSensorCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        AddSensorRequest request = new AddSensorRequest(name, status, locationId);
        apiService.addSensor("Bearer " + token, request)
                .enqueue(new Callback<Sensor>() {
                    @Override
                    public void onResponse(Call<Sensor> call, Response<Sensor> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Помилка створення сенсора: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Sensor> call, Throwable t) {
                        callback.onError("Помилка мережі: " + t.getMessage());
                    }
                });
    }

    public void updateSensor(Context context, int sensorId, String name, String status, int locationId, UpdateSensorCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        AddSensorRequest request = new AddSensorRequest(name, status, locationId);
        apiService.updateSensor("Bearer " + token, sensorId, request)
                .enqueue(new Callback<Sensor>() {
                    @Override
                    public void onResponse(Call<Sensor> call, Response<Sensor> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Помилка оновлення сенсора: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Sensor> call, Throwable t) {
                        callback.onError("Помилка мережі: " + t.getMessage());
                    }
                });
    }

    public void getSensorById(Context context, int sensorId, GetSensorCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.getSensor("Bearer " + token, sensorId).enqueue(new Callback<Sensor>() {
            @Override
            public void onResponse(Call<Sensor> call, Response<Sensor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження сенсора: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Sensor> call, Throwable t) {
                callback.onError("Помилка мережі: " + t.getMessage());
            }
        });
    }


    public void deleteSensor(Context context, int sensorId, DeleteSensorCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null || token.isEmpty()) {
            callback.onError("Токен не знайдено");
            return;
        }

        apiService.deleteSensor("Bearer " + token, sensorId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Не вдалося видалити сенсор");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Помилка мережі: " + t.getMessage());
            }
        });
    }

    public interface DeleteSensorCallback {
        void onSuccess();

        void onError(String message);
    }

    public interface GetSensorCallback {
        void onSuccess(Sensor sensor);

        void onError(String errorMessage);
    }

    public interface AddSensorCallback {
        void onSuccess(Sensor sensor);

        void onError(String errorMessage);
    }

    public interface SensorCallback {
        void onSuccess(List<Sensor> sensors);

        void onError(String errorMessage);
    }

    public interface UpdateSensorCallback {
        void onSuccess(Sensor sensor);

        void onError(String errorMessage);
    }
}
