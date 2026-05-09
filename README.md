# 개인 성적 관리 시스템

Java Swing으로 구현한 개인 성적 관리 GUI 프로그램입니다. 사용자는 텍스트 파일에 저장된 성적 데이터를 불러와 학기별로 확인하고, 과목 성적을 추가, 수정, 삭제, 검색할 수 있습니다.

기존 README는 C 언어 프로젝트처럼 작성되어 있었지만, 실제 저장소의 핵심 구현은 Java 기반 `UniversityGradeManager` GUI 프로그램입니다.

## 프로젝트 개요

이 프로젝트는 대학 성적표처럼 과목명, 년도/학기, 이수구분, 등급, 학점을 표 형태로 관리하는 프로그램입니다. 성적 데이터를 텍스트 파일에서 읽어와 학기별로 분류하고, Swing 테이블 UI를 통해 직관적으로 확인할 수 있습니다.

성적 데이터는 공백으로 구분된 텍스트 파일 형식을 사용합니다. 프로그램에서 파일을 열면 학기별 데이터가 자동으로 묶이고, 하단의 학기 선택 버튼과 이전/다음 버튼을 통해 학기별 성적을 탐색할 수 있습니다.

## 주요 기능

- 성적 텍스트 파일 열람
- 학기별 성적 목록 표시
- 과목 성적 추가
- 선택한 성적 수정
- 선택한 성적 삭제
- 전체 성적 검색
- 학기 선택 콤보박스
- 이전 / 다음 학기 이동
- Swing 기반 GUI 화면 제공

## 화면 구성

```text
개인 성적 관리 프로그램
│
├─ 상단
│  ├─ 프로그램 제목
│  └─ 열람 버튼
│
├─ 왼쪽 메뉴
│  ├─ 추가
│  ├─ 수정
│  └─ 삭제
│
├─ 중앙 테이블
│  ├─ 교과목명
│  ├─ 년도/학기
│  ├─ 이수구분
│  ├─ 등급
│  └─ 학점
│
└─ 하단
   ├─ 이전 / 다음 학기 이동
   ├─ 학기 선택 콤보박스
   └─ 검색 입력창
```

## 프로젝트 구조

```text
student-grade-management-system/
│
├─ README.md
│
└─ java/
   │
   ├─ 성적.txt
   ├─ cspell.config.yaml
   │
   ├─ demo/
   │  ├─ pom.xml
   │  └─ src/main/java/com/example/Main.java
   │
   └─ text/
      │
      ├─ README.md
      ├─ src/
      │  ├─ grades.txt
      │  └─ target/test-classes/
      │     ├─ 성적.txt
      │     └─ text/src/
      │        ├─ UniversityGradeManager.java
      │        ├─ ADD.java
      │        └─ HelloHello.java
      │
      └─ bin/
         └─ 컴파일된 class 파일
```

## 주요 파일 설명

| 파일 | 설명 |
| --- | --- |
| `java/text/src/target/test-classes/text/src/UniversityGradeManager.java` | Swing 기반 성적 관리 프로그램의 핵심 소스 코드 |
| `java/성적.txt` | 성적 데이터 예시 파일 |
| `java/text/src/grades.txt` | 성적 데이터 파일 위치 예시 |
| `java/demo/src/main/java/com/example/Main.java` | Maven 기본 예제 형태의 Hello World 코드 |
| `java/demo/pom.xml` | Java 17 기준 Maven 설정 파일 |
| `java/text/bin/` | 컴파일된 `.class` 파일이 포함된 폴더 |

## 개발 환경

- 언어: Java
- GUI: Java Swing
- 빌드 도구: Maven 일부 포함
- 권장 JDK: JDK 17 이상
- 데이터 저장 방식: TXT 파일 기반
- 주요 Java API:
  - `javax.swing`
  - `java.awt`
  - `java.io`
  - `java.util`

## 성적 데이터 형식

성적 데이터는 한 줄에 하나의 과목 정보를 기록합니다.

```text
교과목명 년도/학기 이수구분 등급 학점
```

예시는 다음과 같습니다.

```text
드림프로젝트 2023/1학기 교양필수 A+ 4.5
프로그래밍Ⅰ 2023/1학기 전공선택 B 3
영어회화Ⅰ 2023/2학기 교양필수 B 3
```

프로그램은 공백을 기준으로 데이터를 분리합니다. 따라서 과목명에 공백이 포함되면 정상적으로 읽히지 않을 수 있습니다.

## 실행 방법

### Java 파일 직접 실행

`UniversityGradeManager.java`가 있는 폴더로 이동합니다.

```bash
cd java/text/src/target/test-classes/text/src
```

컴파일합니다.

```bash
javac UniversityGradeManager.java
```

실행합니다.

```bash
java UniversityGradeManager
```

### 이미 컴파일된 class 파일을 사용하는 경우

저장소에는 컴파일된 class 파일도 포함되어 있습니다. 다음 위치에서 실행할 수 있습니다.

```bash
cd java/text/bin/target/test-classes
java UniversityGradeManager
```

### Maven 예제 실행

`java/demo` 폴더에는 별도의 Maven 기본 예제가 포함되어 있습니다.

```bash
cd java/demo
mvn compile
mvn exec:java -Dexec.mainClass="com.example.Main"
```

> 실제 성적 관리 GUI는 `demo/Main.java`가 아니라 `UniversityGradeManager.java`입니다.

## 사용 방법

1. 프로그램을 실행합니다.
2. 상단의 `열람` 버튼을 누릅니다.
3. `성적.txt` 같은 성적 데이터 텍스트 파일을 선택합니다.
4. 성적이 학기별로 분류되어 테이블에 표시됩니다.
5. `추가`, `수정`, `삭제` 버튼으로 성적을 관리합니다.
6. 하단 검색창에 과목명, 학기, 이수구분, 등급, 학점 등을 입력해 검색합니다.
7. 이전/다음 버튼 또는 콤보박스로 학기를 이동합니다.

## 기능 상세

### 파일 열람

`JFileChooser`를 통해 `.txt` 파일을 선택합니다. 선택된 파일은 UTF-8로 읽고, 각 줄을 공백 기준으로 분리해 성적 데이터로 저장합니다.

### 학기별 분류

성적은 `HashMap<String, ArrayList<String[]>>` 구조로 저장됩니다. key는 `년도/학기`, value는 해당 학기의 과목 목록입니다.

### 성적 추가

과목명, 년도/학기, 이수구분, 등급, 학점을 입력하면 현재 프로그램 메모리에 성적이 추가됩니다.

### 성적 수정

테이블에서 성적을 선택한 뒤 수정 버튼을 누르면 기존 값이 입력된 수정 창이 열립니다. 확인을 누르면 선택한 행이 수정됩니다.

### 성적 삭제

테이블에서 성적을 선택한 뒤 삭제 버튼을 누르면 확인 창이 나타납니다. 확인하면 선택한 성적이 목록에서 제거됩니다.

### 성적 검색

검색어가 과목명, 학기, 이수구분, 등급, 학점 중 하나에 포함되면 검색 결과 창에 표시됩니다.

## 현재 구현상의 특징

- Swing `JFrame` 기반 GUI 프로그램입니다.
- 성적 데이터는 프로그램 실행 중 메모리에 저장됩니다.
- 파일을 열 때 기존 데이터는 초기화되고 새 파일 데이터로 교체됩니다.
- 추가, 수정, 삭제 결과를 원본 텍스트 파일에 다시 저장하는 기능은 현재 구현되어 있지 않습니다.
- 검색 결과는 별도 팝업 테이블로 표시됩니다.
- 학기 이동은 콤보박스와 이전/다음 버튼을 함께 사용합니다.

## 주의 사항

- 현재 핵심 Java 소스가 일반적인 `src/main/java` 위치가 아니라 `java/text/src/target/test-classes/text/src/` 아래에 있습니다.
- 성적 파일은 공백 구분 방식이므로 과목명에 공백이 있으면 파싱이 깨질 수 있습니다.
- 추가, 수정, 삭제한 내용은 텍스트 파일에 자동 저장되지 않습니다.
- `java/demo` 폴더의 Maven 프로젝트는 성적 관리 GUI가 아니라 기본 `Hello world` 예제입니다.
- 저장소에는 컴파일 결과물인 `.class` 파일과 `target` 폴더가 포함되어 있습니다.

## 개선 아이디어

- 핵심 소스를 `src/main/java`로 이동해 표준 Java 프로젝트 구조로 정리
- Maven 또는 Gradle 빌드 설정 통합
- 추가, 수정, 삭제 결과를 TXT 파일에 저장하는 기능 추가
- CSV 형식 지원
- 과목명 공백 처리를 위한 `CSV` 또는 탭 구분 파서 적용
- 학기별 평균 평점 계산
- 전체 평균 평점 계산
- 이수구분별 학점 합계 계산
- 성적 등급별 색상 표시
- 데이터 파일 자동 저장 / 다른 이름으로 저장 기능 추가

## 활용 분야

- Java Swing GUI 학습
- JTable 사용법 실습
- 파일 입출력 실습
- 텍스트 기반 데이터 파싱 실습
- 개인 성적 관리 프로그램 구현
- 학교 과제 및 Java 프로젝트 발표

## 개발자

김아영
