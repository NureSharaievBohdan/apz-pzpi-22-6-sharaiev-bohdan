package com.example.radguardmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.adapters.SensorAdapter;
import com.example.radguardmobile.models.Sensor;
import com.example.radguardmobile.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Sensor> sensorList = new ArrayList<>();
    private RecyclerView recyclerSensors;
    private SensorAdapter sensorAdapter;
    private LinearLayout btnSensors, btnNotifications;
    private ActivityResultLauncher<Intent> addSensorLauncher;
    private SensorRepository sensorRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerSensors = findViewById(R.id.recyclerSensors);
        btnSensors = findViewById(R.id.btnSensors);
        btnNotifications = findViewById(R.id.btnNotifications);

        recyclerSensors.setLayoutManager(new LinearLayoutManager(this));
        sensorAdapter = new SensorAdapter(MainActivity.this, sensorList);
        recyclerSensors.setAdapter(sensorAdapter);

        sensorRepository = new SensorRepository();

        addSensorLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadSensors();
                    }
                }
        );

        findViewById(R.id.btnAddSensor).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSensorActivity.class);
            startActivity(intent);
        });

        btnSensors.setOnClickListener(v -> {
            loadSensors();
        });

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlertsActivity.class);
            startActivity(intent);
        });

        loadSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSensors();
    }

    private void loadSensors() {
        sensorRepository.getSensors(this, new SensorRepository.SensorCallback() {
            @Override
            public void onSuccess(List<Sensor> sensors) {
                sensorList.clear();
                sensorList.addAll(sensors);
                sensorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
