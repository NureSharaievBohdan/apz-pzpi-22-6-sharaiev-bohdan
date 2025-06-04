package com.example.radguardmobile.api.response;

import lombok.Data;

@Data
public class AlertResponse {
    private int id;
    private SensorInfo sensor;
    private String alert_message;
    private String alert_level;
    private String triggered_at;
    private boolean resolved;

    @Data
    public static class SensorInfo {
        private int id;
        private String sensorName;
    }
}
