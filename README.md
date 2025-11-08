Ujjain Darshan - skeleton project (updated)
Stack: Vert.x, RxJava, Ebean ORM, Java 17
Features added:
- JWT authentication (issue token on /api/v1/auth/login). Set env JWT_SECRET for production.
- Route protection (AuthMiddleware). Protected routes: /api/v1/trips
- Validation (Jakarta Validation + Hibernate Validator)
- Global error handling (consistent JSON errors)
- MySQL-compatible configuration: set JDBC_URL, DB_USER, DB_PASS, DB_DRIVER env vars.

Run (dev):
- mvn package
- java -javaagent:ebean-agent.jar -jar target/darshan-1.0.0.jar
Env examples:
  export JDBC_URL='jdbc:mysql://db-host:3306/ujjain_darshan?useSSL=false&serverTimezone=UTC'
  export DB_USER='dbuser'
  export DB_PASS='dbpass'
  export DB_DRIVER='com.mysql.cj.jdbc.Driver'
  export JWT_SECRET='replace-with-a-long-secret'
