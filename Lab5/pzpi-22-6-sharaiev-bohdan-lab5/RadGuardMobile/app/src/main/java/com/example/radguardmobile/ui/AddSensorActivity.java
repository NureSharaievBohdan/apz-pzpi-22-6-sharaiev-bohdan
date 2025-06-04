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

public class AddSensorActivity extends AppCompatActivity {
    private final List<Location> locations = new ArrayList<>();
    private EditText etSensorName;
    private EditText etSensorStatus;
    private Spinner spinnerLocations;
    private Button btnSaveSensor;
    private ArrayAdapter<String> spinnerAdapter;
    private SensorRepository sensorRepository;
    private LocationRepository locationRepository;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);
        locationRepository = new LocationRepository();
        sensorRepository = new SensorRepository();
        etSensorName = findViewById(R.id.etSensorName);
        etSensorStatus = findViewById(R.id.etSensorStatus);
        spinnerLocations = findViewById(R.id.spinnerLocations);
        btnSaveSensor = findViewById(R.id.btnSaveSensor);

        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocations.setAdapter(spinnerAdapter);

        loadLocations();

        btnSaveSensor.setOnClickListener(v -> attemptSave());
    }

    private void loadLocations() {
        locationRepository.getLocations(this, new LocationRepository.LocationCallback() {
            @Override
            public void onSuccess(List<Location> locationsList) {
                locations.clear();
                locations.addAll(locationsList);

                List<String> labels = new ArrayList<>();
                for (Location loc : locations) {
                    labels.add(loc.getCity() + " — " + loc.getDescription());
                }
                spinnerAdapter.clear();
                spinnerAdapter.addAll(labels);
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddSensorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void attemptSave() {
        String name = etSensorName.getText().toString().trim();
        String status = etSensorStatus.getText().toString().trim();
        int pos = spinnerLocations.getSelectedItemPosition();

        if (name.isEmpty()) {
            etSensorName.setError("Введіть назву");
            return;
        }
        if (status.isEmpty()) {
            etSensorStatus.setError("Введіть статус");
            return;
        }
        if (pos < 0 || pos >= locations.size()) {
            Toast.makeText(this, "Оберіть локацію", Toast.LENGTH_SHORT).show();
            return;
        }

        int locationId = locations.get(pos).getId();

        sensorRepository.addSensor(this, name, status, locationId, new SensorRepository.AddSensorCallback() {
            @Override
            public void onSuccess(Sensor sensor) {
                Toast.makeText(AddSensorActivity.this, "Сенсор успішно додано", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, new Intent());
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddSensorActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

}
