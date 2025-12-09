package com.performance.lab.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;

@Service
public class HeapLoadService {

    // 부하를 주기 위해 단순 Integer가 아닌 래퍼 객체 사용
    static class Person {
        @Getter
        int id;
        byte[] payload; // 메모리 점유율을 높이기 위한 더미 데이터

        public Person(int id) {
            this.id = id;
            this.payload = new byte[1024]; // 1KB 객체
        }

    }

    public int solveJosephus(int n, int k) {
        List<Person> list = new LinkedList<>();

        // 객체 대량 생성
        for (int i = 1; i <= n; i++) {
            list.add(new Person(i));
        }

        int index = 0;

        // 2. 객체 반복 제거 (Deallocation triggers GC)
        while (list.size() > 1) {
            index = (index + k - 1) % list.size();
            Person retrieved = list.remove(index);
        }

        return list.get(0).id;
    }
}
