[![Build Status](https://api.shippable.com/projects/56ce5d4922366a0c003ea90e/badge?branchName=master)](https://app.shippable.com/projects/56ce5d4922366a0c003ea90e/builds/latest)

# security-sample

This project is related to below StackOverflow questions; 

* [Spring security OAuth2 clientDetailsService circular reference?](http://stackoverflow.com/questions/29069121/spring-security-oauth2-clientdetailsservice-circular-reference?noredirect=1#comment46435461_29069121)
* [Spring OAuth2 2.0.8 upgrade](http://stackoverflow.com/questions/35496545/spring-oauth2-2-0-8-upgrade?noredirect=1#comment58875905_35496545)


```
git clone https://github.com/aug70/security-sample.git
cd security-sample
mvn tomcat7:run-war

```


Execute curls below to test.


### Grant type client credentials


```
curl -k -i -H "Accept: application/json" -X POST -d "grant_type=client_credentials&client_id=sample-client&client_secret=11111111-1111-1111-1111-111111111111&scope=trust" https://localhost:8443/security_sample/oauth/token
```

```
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
X-Frame-Options: DENY
Cache-Control: no-store
Pragma: no-cache
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 20 Mar 2015 01:20:41 GMT

{"access_token":"8f0ef4d9-041b-4cee-8a4c-0b3c86c0bf04","token_type":"bearer","expires_in":5999,"scope":"trust"}
```

### Grant type client credentials


```
curl -k -i -H "Accept: application/json" -H "Authorization: Basic c2FtcGxlLWNsaWVudDoxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTE=" -X POST -d "grant_type=client_credentials&scope=trust" https://localhost:8443/security_sample/oauth/token
```

```
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
X-Frame-Options: DENY
Cache-Control: no-store
Pragma: no-cache
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 20 Mar 2015 01:21:15 GMT

{"access_token":"8f0ef4d9-041b-4cee-8a4c-0b3c86c0bf04","token_type":"bearer","expires_in":5965,"scope":"trust"}
```


### Grant type password


```
curl -k -i -H "Accept: application/json" -H "Authorization: Basic c2FtcGxlLWNsaWVudDoxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTE=" -X POST -d "grant_type=password&scope=trust&username=tester&password=121212" https://localhost:8443/security_sample/oauth/token
```

```
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
X-Frame-Options: DENY
Cache-Control: no-store
Pragma: no-cache
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 20 Mar 2015 01:21:50 GMT

{"access_token":"d7ae3343-671b-4927-8eed-77fe58dc10d1","token_type":"bearer","refresh_token":"e30f33ca-eb31-4ca8-8997-d523be41f551","expires_in":5999,"scope":"trust"}
```

### Grant type password

```
curl -k -i -H "Accept: application/json" -X POST -d "grant_type=password&client_id=sample-client&client_secret=11111111-1111-1111-1111-111111111111&scope=trust&username=tester&password=121212" https://localhost:8443/security_sample/oauth/token
```

```
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
X-Frame-Options: DENY
Cache-Control: no-store
Pragma: no-cache
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 20 Mar 2015 01:22:18 GMT

{"access_token":"d7ae3343-671b-4927-8eed-77fe58dc10d1","token_type":"bearer","refresh_token":"e30f33ca-eb31-4ca8-8997-d523be41f551","expires_in":5971,"scope":"trust"}
```


### Using token

Request with a valid token

```
curl -k -i -H "Accept: application/json" --header "Authorization: Bearer d7ae3343-671b-4927-8eed-77fe58dc10d1" https://localhost:8443/security_sample/secured

HTTP/1.1 200 OK
.....

```

Request without a valid token

```
curl -k -i -H "Accept: application/json" https://localhost:8443/security_sample/secured

HTTP/1.1 401
.....

```