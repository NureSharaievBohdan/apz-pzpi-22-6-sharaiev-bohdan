package com.example.radguardmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.radguardmobile.R;
import com.example.radguardmobile.models.Location;
import com.example.radguardmobile.models.Sensor;
import com.example.radguardmobile.repository.LocationRepository;
import com.example.radguardmobile.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;

public class EditSensorActivity extends AppCompatActivity {
    private final List<Location> locations = new ArrayList<>();
    private EditText etSensorName, etSensorStatus;
    private Spinner spinnerLocations;
    private Button btnSaveSensor;
    private ArrayAdapter<String> spinnerAdapter;
    private Sensor currentSensor;
    private int sensorId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        etSensorName = findViewById(R.id.etSensorName);
        etSensorStatus = findViewById(R.id.etSensorStatus);
        spinnerLocations = findViewById(R.id.spinnerLocations);
        btnSaveSensor = findViewById(R.id.btnSaveSensor);

        btnSaveSensor.setText("Оновити сенсор");

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocations.setAdapter(spinnerAdapter);

        sensorId = getIntent().getIntExtra("sensor_id", -1);
        if (sensorId == -1) {
            Toast.makeText(this, "ID сенсора не передано", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadLocationsAndThenSensor();

        btnSaveSensor.setOnClickListener(v -> sensorUpdate());
    }

    private void loadLocationsAndThenSensor() {
        LocationRepository locationRepo = new LocationRepository();
        locationRepo.getLocations(this, new LocationRepository.LocationCallback() {
            @Override
            public void onSuccess(List<Location> locationList) {
                locations.clear();
                locations.addAll(locationList);

                List<String> labels = new ArrayList<>();
                for (Location loc : locations) {
                    labels.add(loc.getCity() + " — " + loc.getDescription());
                }

                spinnerAdapter.clear();
                spinnerAdapter.addAll(labels);
                spinnerAdapter.notifyDataSetChanged();

                loadSensorDetails();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditSensorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSensorDetails() {
        SensorRepository sensorRepo = new SensorRepository();
        sensorRepo.getSensorById(this, sensorId, new SensorRepository.GetSensorCallback() {
            @Override
            public void onSuccess(Sensor sensor) {
                currentSensor = sensor;
                etSensorName.setText(sensor.getSensor_name());
                etSensorStatus.setText(sensor.getStatus());
                setSpinnerSelectionByLocationId(sensor.getLocation().getId());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditSensorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setSpinnerSelectionByLocationId(int locationId) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getId() == locationId) {
                spinnerLocations.setSelection(i);
                break;
            }
        }
    }

    private void sensorUpdate() {
        String name = etSensorName.getText().toString().trim();
        String status = etSensorStatus.getText().toString().trim();
        int selectedPosition = spinnerLocations.getSelectedItemPosition();

        if (name.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPosition < 0 || selectedPosition >= locations.size()) {
            Toast.makeText(this, "Невірна локація", Toast.LENGTH_SHORT).show();
            return;
        }

        Location selectedLocation = locations.get(selectedPosition);

        SensorRepository sensorRepository = new SensorRepository();
        sensorRepository.updateSensor(
                this,
                currentSensor.getId(),
                name,
                status,
                selectedLocation.getId(),
                new SensorRepository.UpdateSensorCallback() {
                    @Override
                    public void onSuccess(Sensor updatedSensor) {
                        Toast.makeText(EditSensorActivity.this, "Сенсор оновлено", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(EditSensorActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}

