# JVM Performance & Docker Optimization Lab

본 프로젝트는 JVM 성능 튜닝 및 Docker 환경 최적화(CDS) 실험을 위한 코드입니다.

## 🛠 실험 환경
- **Java:** JDK 17
- **Build Tool:** Gradle
- **Framework:** Spring Boot 3.5.8
- **Container:** Docker

## 📖 JVM 및 GC 동작 원리 시각화
- JVM 실행의 기본 원리 [🔗링크](https://namuucy.github.io/jvm-experiment/jvm_default.html)
- G1GC 시각화 분석 [🔗링크](https://namuucy.github.io/jvm-experiment/g1_gc.html)
- ZGC 시각화 분석 [🔗링크](https://namuucy.github.io/jvm-experiment/zgc.html)

## 🚀 실험 로드맵 및 실행 방법

단순한 이론 학습을 넘어, 코드로 직접 검증하고 눈으로 확인하는 과정을 기록합니다.

### Step 1: 환경 구축 및 로직 검증 (Baseline)
[🔗 링크를 통해 자세한 정보를 확인할 수 있습니다.](/documents/step1.md)
- 알고리즘(N-Queens, Josephus)의 정확성 확인
  - BaselineTest + JUnit을 통한 검증 
- 실험 측정을 위한 프로파일링 도구 확인
  - Intellij Profiler
  - VisualVM
- Spring Boot 환경에서 구동 직후 테스트 작동을 위한 설정
- Intellij Profiler + ApplicationRunner + VisualVM 을 통한 성능 측정

### Step 2: JVM 부하 유형별 맞춤형 튜닝 실험

#### 2-A : N-Queen 에서의 JVM 옵션 튜닝
[🔗 링크를 통해 자세한 실험 정보를 확인할 수 있습니다.](/documents/step2.md)
- Baseline 데이터 : 13.6585 초
- `CompileThreshold`를 통한 빠른 개입 시 : 13.2325 초 (**약 0.4초 단축**)
- `Xss`옵션을 통한 메모리 할당 최적화 : 13.4373 초 (**약 0.2초 단축**) / 스택 메모리 할당 **2048Kb > 208Kb** 로 최적화 판단
- `MaxInlineSize` 옵션을 통한 Inlining 임계값 변화에 따른 성능 영향력 분석 : 13.4350 초 (**약 0.2초 단축**)


#### 2-B : Josephus 문제에서의 JVM 옵션 튜닝 (⚠️ 현재 진행 중!)
[🔗 링크를 통해 자세한 실험 정보를 확인할 수 있습니다.](/documents/step2b.md)
- Baseline 데이터 : 45.1624 초




---

# ⚠️ 아래는 진행 예정입니다.

### Step 3: 로컬에서 기동 시간 단축
- **Goal:** JVM의 Class Loading 비용이 기동 시간에 미치는 영향을 분석하고, **CDS(Class Data Sharing)** 를 통해 이를 얼마나 단축할 수 있는지 정량화합니다.
- **Hypothesis:** "애플리케이션 종료 시점에 클래스 메타데이터를 덤프(`ArchiveClassesAtExit`)해두면, 재기동 시 파싱 비용이 제거되어 실행 속도가 획기적으로 단축될 것이다."
- **Plan:**
    1. **대조군:** 일반적인 `java -jar` 실행 시의 기동 시간 측정 (Cold Start).
    2. **실험군:** `SharedArchiveFile` 옵션을 적용하여 미리 생성된 아카이브로 실행했을 때의 시간 측정.
    3. **검증:** 두 케이스 간의 기동 시간 단축률(%) 데이터를 확보하여 효율성 입증.

### Step 4: 컨테이너 환경 검증
- **Goal:** Docker 컨테이너의 리소스 제한(Limit)과 JVM의 메모리 할당 정책(Ergonomics) 간의 괴리를 파악하고, 클라우드 환경에서의 운영 안정성을 확보합니다.
- **Hypothesis:** "최신 JVM은 `-XX:+UseContainerSupport`를 통해 Host OS가 아닌 컨테이너의 메모리 제한(Cgroup limit)을 기준으로 힙 크기를 자동 설정할 것이다."
- **Plan:**
    1. **대조군:** 컨테이너 지원 옵션 없이 실행하여 Host 전체 메모리를 힙 기준으로 인식하는 위험 상황 재현.
    2. **실험군:** Docker 메모리 제한(예: 512MB) 환경에서 JVM이 계산한 `MaxHeapSize` 확인.
    3. **검증:** 제한된 리소스 내에서 OOM 없이 안정적으로 힙이 할당되는지 확인.

### Step 5: Docker 환경 내 CDS 적용
- **Goal:** **"Build-Time CDS Generation"** 전략을 수립하여, 배포 직후 첫 실행(Cold Start)부터 최적화된 성능을 보장하는 컨테이너 이미지를 구축합니다.
- **Hypothesis:** "이미지 빌드 시점에 CDS 캐시를 내장해두면, 런타임에 별도의 워밍업 없이도 즉각적인 기동 속도 향상을 얻을 수 있을 것이다."
- **Plan:**
    1. **대조군:** 일반적인 진입점(`entrypoint: java -jar app.jar`)을 가진 Docker 이미지.
    2. **실험군:** 빌드 단계에서 생성된 CDS 덤프를 포함하고, 이를 로드하여 실행하는 Docker 이미지.
    3. **검증:** 컨테이너 환경에서 CDS 적용 유무에 따른 기동 시간 차이를 측정하여, 프로덕션 도입 타당성 최종 증명.