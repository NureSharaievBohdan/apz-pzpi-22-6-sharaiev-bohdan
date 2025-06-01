package com.example.radguardmobile.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddSensorRequest {
    private String sensor_name;
    private String status;
    private int location_id;
}

