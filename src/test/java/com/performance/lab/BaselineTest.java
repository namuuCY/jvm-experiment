package com.performance.lab;


import com.performance.lab.service.CpuLoadService;
import com.performance.lab.service.HeapLoadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BaselineTest {

    @Autowired
    private CpuLoadService cpuLoadService;

    @Autowired
    private HeapLoadService heapLoadService;

    @DisplayName("N-Queens 알고리즘 정합성 검증")
    @ParameterizedTest(name = "Case {index}: N={0}일 때 정답은 {1}이어야 함")
    @CsvSource({
            "4, 2",     // 가장 작은 유효 케이스
            "8, 92",    // 표준 벤치마크 케이스
            "10, 724"   // 연산량이 조금 더 있는 케이스
    })
    void testNQueensCorrectness(int n, int expectedCount) {
        // When
        long start = System.nanoTime();
        int result = cpuLoadService.solveNQueens(n);
        long duration = System.nanoTime() - start;

        // Then
        System.out.printf("[Test/CPU] N=%d -> Found: %d (Time: %.4f ms)%n", n, result, duration / 1_000_000.0);
        assertEquals(expectedCount, result, () -> "N=" + n + "일 때 해답 개수가 틀립니다.");
    }

    @DisplayName("Josephus 알고리즘 정합성 검증")
    @ParameterizedTest(name = "Case {index}: N={0}, K={1}일 때 생존자는 {2}번")
    @CsvSource({
            "5, 2, 3",      // 소규모 검증
            "7, 3, 4",      // 표준 예제
            "10, 3, 4",     // 추가 검증
            "41, 3, 31"     // 역사적 요세푸스 문제 (N=41, K=3)
    })
    void testJosephusCorrectness(int n, int k, int expectedSurvivor) {
        // When
        long start = System.nanoTime();
        int result = heapLoadService.solveJosephus(n, k);
        long duration = System.nanoTime() - start;

        // Then
        System.out.printf("[Test/Heap] N=%d, K=%d -> Survivor: %d (Time: %.4f ms)%n", n, k, result, duration / 1_000_000.0);
        assertEquals(expectedSurvivor, result, () -> "N=" + n + ", K=" + k + "일 때 생존자가 틀립니다.");
    }
}
