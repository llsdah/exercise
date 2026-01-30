package com.example.scheduler.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Slf4j
@Service
public class SystemMonitorService {

    private final SystemInfo systemInfo = new SystemInfo();

    // CPU 사용률 체크 (임계치: 80% 이상이면 위험)
    public boolean isSystemOverloaded() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        double cpuLoad = processor.getSystemCpuLoad(1000) * 100; // 1초간 측정
        
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long availableMem = memory.getAvailable();
        long totalMem = memory.getTotal();
        double memUsage = (1.0 - (double) availableMem / totalMem) * 100;

        // log.info("System Status - CPU: {}%, Memory: {}%", String.format("%.2f", cpuLoad), String.format("%.2f", memUsage));

        // 정책: CPU 80% 이상이거나 메모리 90% 이상 사용 중이면 과부하로 판단
        return cpuLoad > 80.0 || memUsage > 90.0;
    }
}