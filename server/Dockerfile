FROM amazoncorretto:17-alpine
COPY build/libs/*.jar app.war
ENTRYPOINT java -jar /app.war -Duser.timezone=Asia/Seoul --spring.profiles.active=dev