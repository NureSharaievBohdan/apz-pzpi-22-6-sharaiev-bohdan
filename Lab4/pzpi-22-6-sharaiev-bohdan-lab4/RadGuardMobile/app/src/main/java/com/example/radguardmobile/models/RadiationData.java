package com.example.radguardmobile.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadiationData {
    private int id;
    private int sensor;
    private String radiation_level;
    private String measured_at;
    private boolean alert_triggered;
}
