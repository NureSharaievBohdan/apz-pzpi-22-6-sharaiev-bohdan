package com.example.radguardmobile.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private int id;
    private Sensor sensor;
    private String alert_message;
    private String alert_level;
    private String triggered_at;
    private boolean resolved;
}
