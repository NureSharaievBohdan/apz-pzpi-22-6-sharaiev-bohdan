package com.example.radguardmobile.domain;

import android.os.Build;

import com.example.radguardmobile.models.RadiationData;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class DangerIndexCalculator {

    private final double R_SAFE;
    private final double P_MAX;

    public DangerIndexCalculator(double rSafe, double pMax) {
        this.R_SAFE = rSafe;
        this.P_MAX = pMax;
    }

    public double calculate(List<RadiationData> measurements) {
        if (measurements == null || measurements.size() < 2) return 0.0;

        Collections.sort(measurements, (a, b) -> Long.compare(parseTimestamp(a.getMeasured_at()), parseTimestamp(b.getMeasured_at())));

        double P = 0.0;

        for (int i = 1; i < measurements.size(); i++) {
            RadiationData prev = measurements.get(i - 1);
            RadiationData curr = measurements.get(i);

            double R_prev = parseRadiationLevel(prev.getRadiation_level());
            double R_curr = parseRadiationLevel(curr.getRadiation_level());
            double R_avg = (R_prev + R_curr) / 2.0;

            long t_prev = parseTimestamp(prev.getMeasured_at());
            long t_curr = parseTimestamp(curr.getMeasured_at());
            long deltaMillis = t_curr - t_prev;
            double deltaHours = deltaMillis / 3600000.0;

            if (deltaHours <= 0) continue;

            double excess = Math.max(0.0, (R_avg - R_SAFE) / R_SAFE);
            P += excess * deltaHours;
        }

        double I = P / P_MAX;
        return Math.max(0, Math.min(I, 1));
    }

    private double parseRadiationLevel(String level) {
        try {
            return Double.parseDouble(level);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private long parseTimestamp(String iso8601) {
        try {
            OffsetDateTime odt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                odt = OffsetDateTime.parse(iso8601, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return odt.toInstant().toEpochMilli();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
        return 0;
    }
}
