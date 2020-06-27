# 구성
- SpringBoot(2.3.1) + Gradle(6.4.1) + JPA + MongoDB(4.2.2)을 사용하여 API 서버 구성

# 구조
main
    -> java
        -> com.pay.api
          -> config
            -> MongoDBConfiguration : 몽고디비 커스텀 컨피그
            -> SwaggerConfig : 스웨거 설정
          -> constant
            -> MoneyStatus : 받기상태
            -> ResCode : 결과코드
          -> controller
            -> ListController : 조회API 컨트롤러
            -> ReceiveController : 받기API 컨트롤러
            -> SpreadController : 뿌리기API 컨트롤러
          -> data
            -> ApiListResponse : 조회API 완료된정보 클래
            -> ApiResponse : API반환 클래스스
            -> RequestData : 요청 파라미터 클래스
            -> ResponseData : 반환 ResponseEntity 클래스
          -> dto
            -> ApiToken : token발급 데이터 dto
            -> Money : token과 매핑하여 유저에게 뿌릴 머니 데이터 dto
          -> repository
            -> MoneyRepository : money컬렉션 mongo query 
            -> TokenRepository : apiToken컬렉션 mongo query
          -> service
            -> ApiService : api서비스
            -> ApiServiceBase : api서비스 로직 베이스
    -> resources
        -> http
            -> api.http : resfApi 호출 테스트
test
    -> java
        -> com.pay.api
            -> ListAPITest : 조회API 모듈 테스트
            -> ReceiveAPITest : 받기API 모듈 테스트
            -> SpreadAPITest : 뿌리기API 모듈 테스트                        

# API	
- PUT /api/spread : 뿌리기API
    '''
    POST http://localhost:8083/api/spread
    Content-Type: application/json
    X-ROOM-ID: test1
    X-USER-ID: testUser1
    
    {
      "money": 13000,
      "memberNum": 2
    }
    
    
    '''
- PUT /api/receive : 받기API		
    ```
        PUT localhost:8080/api/region/  
        Body:
        {
            "region":"경상남도", 
            "target": "경상남도 소재 중소기업으로서 경상남도지사가 추천한 자",
            "usage": "운전 및 시설test",
            "limit": "8억원 이내",
            "rate": "1.5%~3.5%",
            "institute": "경상남도",
            "mgmt": "창원지점",
            "reception": "전 영업점"
         }
    ```
- GET /api/region/{req} : 파라미터로 넘어온 지자체명으로 조회
    ```
        GET localhost:8080/api/region/{"region":"강릉시"}
    ```
- GET /region/list/{req} : 지원한도 컬럼에서 지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비 율이 적은 순서)하여 특정 개수만 출력		
    ``` 
        GET localhost:8080/api/region/list/{"outcnt":"20"}
    ```
- GET /region/rcmd : 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력
    ```
        GET localhost:8080/api/region/rcmd
    ```
- GET /api/csv : csv파일을 읽어 데이터베이스에 파일 값 insert
    ```
       GET localhost:8080/api/csv
    ```
- 테스트용 크롬 익스텐션 앱 : Postman, RestClient
# 개발 환경 SPEC
- os windows10
- sts 3.9.10
- mysql 8.0
- spring boot 2.1.8
- jdk 1.8
- gradle 5.6.2
# 환경셋팅
* mysql install
	- 설치 참고 사이트 => https://m.blog.naver.com/tipsware/221303627201
	- 설치파일 다운로드 경로 => https://dev.mysql.com/downloads/file/?id=488055
	- 설치파일 => mysql-installer-community-8.0.17.0.msi
	- 설치대상 => install : MySQL Server 8.0.17 , MySQL Shell 8.0.17
* MySQL Workbench 프로그램 실행 (*중요)
	- 설치시 루트계정 확인하여 ex) id/passwd : root/root로 설정이 되어있지 않은경우 아래내용 참고
	```
		application.properties 설정파일 중 아래 내용 알맞게 수정
		spring.datasource.username=root
		spring.datasource.password=root
	```
	- create new_schema 
	- name : kakaopaytest
	- charset/colleation : utf8 / Default Collation
* STS install
	- 설치파일 다운로드 경로 => https://spring.io/tools3/sts/all
* lombok install
	- 설치 참고 사이트 => https://duzi077.tistory.com/142
	- 설치파일 다운로드 경로 => https://projectlombok.org/download
* gradle 프로젝트 생성방법(초기프로젝트 생성 참고용)
	- Spring Initializr 사용하여 spring boot, gradle project 생성 (https://start.spring.io/)
* 기본셋팅 및 빌드
	- c:/data01/data 디렉토리 경로에 csv 파일 복사(사전과제1.cvs) (*중요)
	- git 경로에서 프로젝트 설치 : https://github.com/jeonbosol/kkptest 
	- 설치후 build.gradle 우클릭 후 -> Gradle -> Refresh Gradle Project 실행
	- 소스코드 에러가 보일경우 프로젝트 clean
	- 위에 (*중요) 표시 되어있는 내역들 선 반영되었다면 Ex1Application.java -> Run As -> Spring Boot App 실행
	- application.properties파일 설정 중 spring.jpa.hibernate.ddl-auto=create 옵션에 주의(자동 테이블 생성)
	- 처음 빌드하고 기능들 수행하고난 이후엔 update로 설정 변경하는 것을 권장
# 테스트 방법
* JUnit test 실행방법 (Ex1ApplicationTests => Run As => Junit Test 실행)
* MVC TEST
    - BgetRegionTest() : {"region":"강릉시"} 입력받아 조회 Test
    - CputRegionTest() : usage영역 수정내역 Test
    ```
        {
            "region":"경상남도", 
            "target": "경상남도 소재 중소기업으로서 경상남도지사가 추천한 자",
            "usage": "운전 및 시설test",
            "limit": "8억원 이내",
            "rate": "1.5%~3.5%",
            "institute": "경상남도",
            "mgmt": "창원지점",
            "reception": "전 영업점"
         }
    ```
    - DgetRegionListTest() : {"outcnt":"1"} 지원한도 컬럼에서 지원금액으로 내림차순 정렬 테스트 
    - EgetRegionRcmdTest() : 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력 테스트
* JPA TEST
    - AInsertCsvFileToMysqlTest() : csv 파일 파싱하여 mysql db insert Test
    - FselectRegionTest() : 지자체명에 대한 내용 조회 테스트
    - GupdateRegionTest() : 지자체정보 수정 테스트
# 문제해결 방법
* csv파일 파싱 및 db insert
	- 1.read line -> \" \" 사이에 있는 문자열 중 "," -> "^" 치환
	- 2.결과 문자열 "," split
	- 3.\" -> 삭제 , ^ -> "," 재치환	
	- 지자체 엔티티 지자체정보 엔티티를 분리하여 resion, support_info 2개의 테이블로 구성
	- 지원금액으로 내림차순 정렬을 위하여 지원금액(limit) 문자를 숫자로 저장할 필드 추가 num_limit(문자열 중 숫자만 필터링하여 저장)
	- 지원금액이 동일하면 이차보전 평균 비율이 적은 순서를 위하여 이차보전(rate) 평균값을 저장할 필드 추가 rate_avg(문자열 중 숫자만 파싱하여 평균값 저장)
	- 지원금액 이차보전 영역 문자열만 있을 경우에 대한 로직 추가(문자만 있는경우 rate_avg = 100.0f 셋팅)		
* response를 위한 처리
	- Serializer 및 ObjectMapper추가 (JPA로 받아온 dto를 api 요구사항 스펙에 맞춰 response 구성을 하기위함)
	- 리퀘스트매핑 어노테이션 영역 produces = "application/json" 추가
	- RestFul한 API 제공 : Get방식 조회기능, Post방식 데이터 삽입 ,Put방식 데이터 수정기능

api 명세 
http://localhost:8080/swagger-ui.html#/list-controller