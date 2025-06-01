package com.example.radguardmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.adapters.RadiationDataAdapter;
import com.example.radguardmobile.domain.DangerIndexCalculator;
import com.example.radguardmobile.models.RadiationData;
import com.example.radguardmobile.models.Sensor;
import com.example.radguardmobile.repository.RadiationRepository;
import com.example.radguardmobile.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;

public class SensorDetailActivity extends AppCompatActivity {

    List<RadiationData> measurements;
    private Button btnCalculateIndex;
    private TextView tvName, tvStatus, tvLocation, tvLastUpdate, tvDangerIndex;
    private Button btnEdit, btnDelete;
    private EditText etRSafe, etPMax;
    private RecyclerView rvMeasurements;
    private RadiationDataAdapter measurementsAdapter;
    private int sensorId;
    private Sensor sensor;
    private SensorRepository sensorRepository;
    private RadiationRepository radiationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        sensorId = getIntent().getIntExtra("sensor_id", -1);
        if (sensorId < 0) {
            Toast.makeText(this, "Помилковий ID сенсора", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCalculateIndex = findViewById(R.id.btnCalculateIndex);
        sensorRepository = new SensorRepository();
        radiationRepository = new RadiationRepository();
        tvName = findViewById(R.id.tvDetailName);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvLastUpdate = findViewById(R.id.tvDetailLastUpdate);
        tvDangerIndex = findViewById(R.id.tvDangerIndex);
        btnEdit = findViewById(R.id.btnEditSensor);
        btnDelete = findViewById(R.id.btnDeleteSensor);

        etRSafe = findViewById(R.id.etRSafe);
        etPMax = findViewById(R.id.etPMax);

        etRSafe.setText("0.3");
        etPMax.setText("100.0");

        rvMeasurements = findViewById(R.id.recyclerMeasurements);

        measurementsAdapter = new RadiationDataAdapter(new ArrayList<>());
        rvMeasurements.setLayoutManager(new LinearLayoutManager(this));
        rvMeasurements.setAdapter(measurementsAdapter);

        loadSensorInfo();
        loadMeasurements();

        btnEdit.setOnClickListener(v -> {
            if (sensor == null) {
                Toast.makeText(SensorDetailActivity.this, "Дані сенсора не завантажені", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SensorDetailActivity.this, EditSensorActivity.class);
            intent.putExtra("sensor_id", sensorId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> deleteSensor());

        btnCalculateIndex.setOnClickListener(v -> {
            if (measurements == null || measurements.isEmpty()) {
                Toast.makeText(SensorDetailActivity.this, "Відсутні дані вимірювань для розрахунку", Toast.LENGTH_SHORT).show();
                return;
            }
            double dangerIndex = calculateDangerIndex(measurements);
            tvDangerIndex.setText(String.format("Індекс потенційної небезпеки: %.3f", dangerIndex));
            Toast.makeText(SensorDetailActivity.this, "Індекс оновлено", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSensorInfo();
        loadMeasurements();
    }

    private void loadSensorInfo() {
        sensorRepository.getSensorById(this, sensorId, new SensorRepository.GetSensorCallback() {
            @Override
            public void onSuccess(Sensor sensorData) {
                sensor = sensorData;
                runOnUiThread(() -> {
                    tvName.setText(sensor.getSensor_name());
                    tvStatus.setText("Статус: " + sensor.getStatus());
                    tvLocation.setText("Локація: " + sensor.getLocation().getCity() + ", " + sensor.getLocation().getDescription());
                    tvLastUpdate.setText("Оновлено: " + sensor.getLast_update());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SensorDetailActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadMeasurements() {
        radiationRepository.getRadiationLevel(this, sensorId, new RadiationRepository.RadiationCallback() {
            @Override
            public void onSuccess(List<RadiationData> data) {
                measurements = data;
                runOnUiThread(() -> {
                    measurementsAdapter.setItems(data);
                    double dangerIndex = calculateDangerIndex(data);
                    tvDangerIndex.setText(String.format("Індекс потенційної небезпеки: %.3f", dangerIndex));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SensorDetailActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private double calculateDangerIndex(List<RadiationData> measurements) {
        double R_SAFE = getRSafe();
        double P_MAX = getPMax();

        DangerIndexCalculator calculator = new DangerIndexCalculator(R_SAFE, P_MAX);
        return calculator.calculate(measurements);
    }

    private double getRSafe() {
        try {
            return Double.parseDouble(etRSafe.getText().toString());
        } catch (NumberFormatException e) {
            return 0.3;
        }
    }

    private double getPMax() {
        try {
            return Double.parseDouble(etPMax.getText().toString());
        } catch (NumberFormatException e) {
            return 100.0;
        }
    }

    private void deleteSensor() {
        sensorRepository.deleteSensor(this, sensorId, new SensorRepository.DeleteSensorCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(SensorDetailActivity.this, "Сенсор видалено", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SensorDetailActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
