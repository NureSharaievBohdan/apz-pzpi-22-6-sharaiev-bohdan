package com.example.radguardmobile.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int id;
    private String latitude;
    private String longitude;
    private String city;
    private String description;
}
