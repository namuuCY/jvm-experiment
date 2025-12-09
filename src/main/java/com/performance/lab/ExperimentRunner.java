package com.performance.lab;

import com.performance.lab.service.CpuLoadService;
import com.performance.lab.service.HeapLoadService;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ExperimentRunner implements ApplicationRunner {

    private final CpuLoadService cpuLoadService;
    private final HeapLoadService heapLoadService;

    public ExperimentRunner(CpuLoadService cpuLoadService, HeapLoadService heapLoadService) {
        this.cpuLoadService = cpuLoadService;
        this.heapLoadService = heapLoadService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // TODO :  CDS 트레이닝 모드인지 확인 (Step 5용)
        // 실행 시 -Dcds.training.mode=true 옵션이 있으면 로직 수행 없이 바로 종료
        if ("true".equals(System.getProperty("cds.training.mode"))) {
            System.out.println("[CDS] Training mode detected. Application loaded successfully. Exiting...");
            return;
        }

        // 2. 실행 모드 파싱 (기본값: none)
        // 예: java -jar app.jar --mode=cpu --n=13
        String mode = getOptionValue(args, "mode", "none");

        if ("none".equals(mode)) {
            System.out.println("=== 대기 모드 (No experiment mode selected) ===");
            System.out.println("Usage: --mode=cpu --n=14 OR --mode=heap --n=100000 --k=3");
            return; // 웹 서버 등을 띄워놓고 싶다면 여기서 return, CLI 도구라면 System.exit(0)
        }

        System.out.println("=== 실험 시작: " + mode.toUpperCase() + " Mode ===");
        long start = System.nanoTime();

        if ("cpu".equals(mode)) {
            int n = Integer.parseInt(getOptionValue(args, "n", "12")); // Default N=12
            System.out.printf("[CPU] N-Queens (N=%d) calculating...%n", n);
            int result = cpuLoadService.solveNQueens(n);
            System.out.println("[CPU] Result: " + result);

        } else if ("heap".equals(mode)) {
            int n = Integer.parseInt(getOptionValue(args, "n", "100000")); // Default N=10만
            int k = Integer.parseInt(getOptionValue(args, "k", "3"));
            System.out.printf("[Heap] Josephus (N=%d, K=%d) simulating...%n", n, k);
            int result = heapLoadService.solveJosephus(n, k);
            System.out.println("[Heap] Survivor: " + result);
        }

        long duration = System.nanoTime() - start;
        System.out.printf("=== 실험 종료 (Time: %.4f sec) ===%n", duration / 1_000_000_000.0);

        // 실험 종료 후 프로세스 강제 종료 (CLI 환경)
        System.exit(0);
    }

    private String getOptionValue(ApplicationArguments args, String key, String defaultValue) {
        List<String> values = args.getOptionValues(key);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return defaultValue;
    }
}
