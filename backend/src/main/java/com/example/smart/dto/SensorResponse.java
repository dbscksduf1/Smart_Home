package com.example.smart.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class SensorResponse {
    private double temperature;
    private double humidity;
    private double light;
    private double gas;
    private double noise;
    private double air;
    private String time;
}
