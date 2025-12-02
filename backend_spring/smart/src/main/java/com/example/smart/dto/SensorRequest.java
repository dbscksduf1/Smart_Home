package com.example.smart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorRequest {
    private double temperature;
    private double humidity;
    private int light;
    private int gas;
    private int noise;
    private String time;
}
