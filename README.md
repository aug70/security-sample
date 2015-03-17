# security-sample

This is related to Stackoverflow question 
[Spring security OAuth2 “clientDetailsService” circular reference?](http://stackoverflow.com/questions/29069121/spring-security-oauth2-clientdetailsservice-circular-reference?noredirect=1#comment46435461_29069121)


```
git clone https://github.com/aug70/security-sample.git
cd security-sample
mvn clean install
```



You should see below test failures.


```
Caused by: org.springframework.beans.factory.BeanCreationException: Could not autowire field: private org.springframework.security.oauth2.provider.ClientDetailsService com.aug70.security.sample.config.SecurityConfig.clientDetailsService; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'clientDetailsService': Requested bean is currently in creation: Is there an unresolvable circular reference?
...........
...........
...........
Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'clientDetailsService': Requested bean is currently in creation: Is there an unresolvable circular reference?
```