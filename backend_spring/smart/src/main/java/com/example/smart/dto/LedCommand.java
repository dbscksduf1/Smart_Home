package com.example.smart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LedCommand {
    private String color;
    private int brightness;
    private boolean blink;
    private String reason;
}
