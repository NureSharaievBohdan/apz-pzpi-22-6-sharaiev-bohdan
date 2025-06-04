package com.example.radguardmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.adapters.AlertAdapter;
import com.example.radguardmobile.api.response.AlertResponse;
import com.example.radguardmobile.repository.AlertRepository;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {
    private final List<AlertResponse> alertList = new ArrayList<>();
    private RecyclerView recyclerViewAlerts;
    private AlertAdapter alertAdapter;
    private LinearLayout btnSensors, btnNotifications;
    private AlertRepository alertRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        btnSensors = findViewById(R.id.btnSensors);
        btnNotifications = findViewById(R.id.btnNotifications);

        btnSensors.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Ви на сторінці Мої сповіщення", Toast.LENGTH_SHORT).show()
        );

        recyclerViewAlerts = findViewById(R.id.recyclerViewAlerts);
        recyclerViewAlerts.setLayoutManager(new LinearLayoutManager(this));

        alertAdapter = new AlertAdapter(this, alertList, alert -> {
            alertRepository.deleteAlert(this, alert.getId(), new AlertRepository.DeleteAlertCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AlertsActivity.this,
                            "Сповіщення видалено", Toast.LENGTH_SHORT).show();
                    loadAlerts();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AlertsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        recyclerViewAlerts.setAdapter(alertAdapter);

        alertRepository = new AlertRepository();
        loadAlerts();
    }

    private void loadAlerts() {
        alertRepository.getAlerts(this, new AlertRepository.AlertListCallback() {
            @Override
            public void onSuccess(List<AlertResponse> alerts) {
                alertList.clear();
                alertList.addAll(alerts);
                alertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AlertsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
