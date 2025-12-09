# Step 1: 환경 구축 및 로직 검증 (Baseline)

1. 알고리즘(N-Queens, Josephus)의 정확성을 JUnit 테스트로 검증합니다.

- N-Queen(재귀 깊이, cpu 집약) 문제를 위한 `CpuLoadService`
- Josephus(다수의 객체로 인한 Heap 부하) 문제를 위한 `HeapLoadService`

<br>

## BaselineTest + JUnit을 통한 검증
![알고리즘 정합성 테스트](/documents/step1_image1.png)

<br>

## Intellij Profiler를 JVM 프로파일링

- Intellij Profiler는 `Async-Profiler`와 JFR(Java Flight Recorder)를 병렬로 실행하고 있습니다 [🔗링크](https://www.jetbrains.com/help/idea/custom-profiler-configurations.html)
- JFR의 Raw Data(원시 데이터)를 제공하여 코드 레벨의 상세 이벤트나 특정 시점의 스택 트레이스를 추적하는 데에는 유용합니다.
- 아래의 스크린샷으로 확인할 수 있듯이, 현재 저의 Intellij Profiler 에서는 `Async Profiler (ver 4.0)`를 사용하고 있습니다.
![프로파일러](/documents/step1_image2.png)


## VisualVM을 통한 추가적인 JVM 프로파일링

- Intellij Profiler 만으로는 전체적인 흐름을 파악하기에는 정보가 너무 파편화되어 있습니다
- 반면 VisualVM은 프로파일링중 메모리 사용추이를 시각적으로 확인 가능하고, GC 발생 횟수, 유형별 분류, 그리고 총 중단 시간과 같은 집계된 지표를 직관적으로 제공합니다.

<br>

## Spring Boot 환경에서 구동 직후 테스트 작동을 위한 설정

- 다음의 이유로 `ApplicationRunner`를 사용합니다.
  - 스프링 Container 안에서 작동하기 때문에, 빈(Bean)을 활용한 실험이 가능해집니다. 
    - 즉, 실제 운영 환경에서 스프링이 관리하는 객체로 실험이 가능합니다.
  - Controller를 통한 API 호출 없이도 실험이 자동적으로 실행되어, 자동화가 가능합니다.
  - `ApplicationArguments args`를 받아 바로 사용할 수 있는 객체를 제공합니다.
    - `CommandLineRunner`의 경우, `String[] args`를 받기 때문에 이를 파싱하는 작업이 필요합니다. 

<br>

# Intellij Profiler + ApplicationRunner + VisualVM 을 통한 성능 측정


## NQueen(n = 15)

- jvm 옵션의 변경 없이, 콘솔 상으로 n = 15일때 걸리는 시간은 **21.6348초** 입니다.
![콘솔 출력](/documents/step1_image5.png)
- flame graph, Call tree를 통해 병목 지점 시각화
  - solveNQueens 메서드가 전체 어플리케이션 시간의 98.9% 를 차지하고 있어 병목임을 알 수 있습니다.
![flame graph](/documents/step1_image3.png)
![call tree](/documents/step1_image4.png)
- Timeline을 통해 리소스(CPU)패턴을 알 수 있습니다.
  - Spring Boot DevTools 환경에서 실행하였으므로, 실제 연산은 restartedMain 스레드에서 수행되었습니다. 해당 단일 스레드만이 CPU를 점유하고 있는 상태임을 확인했습니다.
![timeline](/documents/step1_image6.png)



## Josephus(n = 100000, k = 3)

- jvm 옵션의 변경 없이, 콘솔상으로 n = 100000, k = 3 일때 걸리는 시간은 **8.7443초** 입니다.
![콘솔 출력](/documents/step1_image7.png)
- VisualVM을 통해 Heap 메모리 사용 추이를 아래의 그래프와 같이 확인할 수 있습니다.
![VisualVM 그래프](/documents/step1_image8.png)
- VisualVM의 GC 탭에서는 어떤 GC가 발생했고, 이 GC로 인해 Pause(G1 GC의 모든 GC는 STW가 포함되어 있으므로)가 걸린 시간을 종합해 보여줍니다.
![VisualVM GC](/documents/step1_image9.png)
- 물론, Intellij Profiler 에서도 Events > Java Virtual Machine > GC > Heap을 통해서 GC 관련 정보를 확인할 수 있습니다.
![Intellij Profiler GC](/documents/step1_image10.png)