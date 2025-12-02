package com.example.smart.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class SensorResponse {
    private double temperature;
    private double humidity;
    private int light;
    private int gas;
    private int noise;
    private String time;
}
