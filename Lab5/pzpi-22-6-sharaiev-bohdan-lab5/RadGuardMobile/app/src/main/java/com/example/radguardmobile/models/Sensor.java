package com.example.radguardmobile.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    private int id;
    private int user;
    private String sensor_name;
    private String status;
    private String last_update;
    private Location location;
}
