# 구성
- SpringBoot(2.3.1) + Gradle(6.4.1) + JPA + MongoDB(4.2.2)을 사용하여 API 서버 구성

# 구조
    ```
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
    ```

# API	

- PUT /api/spread : 뿌리기API
    ```
    POST http://localhost:8083/api/spread
    Content-Type: application/json
    X-ROOM-ID: test1
    X-USER-ID: testUser1
    
    {
      "money": 13000,
      "memberNum": 2
    }    
    ```
    
- PUT /api/receive : 받기API		
    ```
    POST http://localhost:8080/api/receive
    Content-Type: application/json
    X-ROOM-ID: test1
    X-USER-ID: testUser1
    
    {
      "apiToken": "hGN"
    }
    ```

- PUT /api/list : 조회API
    ```
    POST http://localhost:8080/api/list
    Content-Type: application/json
    X-ROOM-ID: test1
    X-USER-ID: testUser1
    
    {
    "apiToken": "hGN"
    }
    ```


# 개발 환경 SPEC
- os max os
- spring boot 2.3.1
- jdk 11
- gradle 6.4.1
- MongoDB 4.2.2

# 환경셋팅
docker mongo - 요청시 첨부파일 제공
intellij - 2019.3.3

# 문제해결 방법
    1. token 발급
       3자리 문자열 생성 - 아스키코드 65~122(알파벳 대소문자) 중 91~96 사이 특수문자를 필터조건을 넣어 알파벳 구성으로만 토큰이 랜덤으로 생성되게 함
    2. token발급 후 10분 지난 요청
       token발급시 expire time을 설정하여 체크
    3. 뿌리기시 분배 로직 - 랜덤 + forLoop, 이전 분배한 머니를 저장하여 멤버수 만큼 리스트 생성
      ```
        public List<Integer> SpreadMoney(int money, int memberNum) {
            List<Integer> spreadMoneyList = new ArrayList<>();
            int prevMoney = 0;
            Random random = new Random();
    
            for(var i = 0; i < (memberNum-1); i++) {
                if( (money - prevMoney) < 1) {
                    break;
                }
                int randomMoney = random.ints(1, (money - prevMoney)).findFirst().getAsInt();
                prevMoney += randomMoney;
                spreadMoneyList.add(randomMoney);
            }
    
            int lastMoney = money - prevMoney;
            spreadMoneyList.add(lastMoney);
    
            return spreadMoneyList;
        }
      ```
    4. 뿌린 건에 대한 조회 7일동안 조건 - mongoDB TTL 설정 값으로 해결
    
# api 명세 
http://localhost:8080/swagger-ui.html#/list-controller
