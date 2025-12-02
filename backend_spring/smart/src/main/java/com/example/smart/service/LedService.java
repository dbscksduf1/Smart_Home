package com.example.smart.service;

import com.example.smart.dto.LedCommand;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class LedService {

    @Getter
    private LedCommand current = new LedCommand();

    public void update(LedCommand cmd) {
        this.current = cmd;
    }
}
