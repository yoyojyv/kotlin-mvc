# kotlin spring mvc
database read/write routing 이 transaction 적용시 정상적으로 이루어지는지 확인

## url (handler) 
1. http://localhost:8080/products/{id}
- read test

1. http://localhost:8080/products/saveExample
- write test


## 소스에서 봐야할 것들

DataSourceConfig 설정의 datasource 설정 부분들

- read, write 가 나누어져 있음 
1. routingDataSource(AbstractRoutingDataSource 상) 를 default(primary)로 설정한 경우와

1. LazyConnectionDataSourceProxy 로 감싼 datasource 를 default(primary)로 사용한 경우를 비교 할 것

service 구현 부분

1. @Transactional(readOnly = true)
2. @Transactional

- 두 어노테이션 사용부분에서 각각 read/write 를 해당 transaction 에 맞게 잘 타는지 확인 


## 결론 
- routingDataSource 만 사용하는 경우
스프링은 트랜잭션 시작시 컨넥션의 실제 사용여부와 무관하게 커넥션을 확보함 
(미리 가져옴. 따라서 routingDataSource 에 디폴트로 지정한 write 를... 사용하게됨)
- LazyConnectionDataSourceProxy 를 사용하는경우만 정상적으로 read, write 분기가 됨
- LazyConnectionDataSourceProxy 를 사용하면 트랜잭션이 시작 되더라도 실제로 커넥션이 필요한 경우에만 데이터소스에서 커넥션 반환

## 로깅 설정에 spring data, jdbc, transaction 관련 부분은 debug 로 설정
밑의 예처럼 설정하여 transaction 과 어떤 datasource 에서 connection 을 가져오는지 확인
```
    <logger name="org.springframework.data" level="DEBUG"/>
    <logger name="org.springframework.jdbc" level="DEBUG"/>
    <logger name="org.springframework.transaction" level="DEBUG"/>
```
