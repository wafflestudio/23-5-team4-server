FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle build --no-daemon -x test && \
  BOOT_JAR=$(ls build/libs/*.jar | grep -v plain) && \
  jar tf "$BOOT_JAR" | grep -q "oci-java-sdk-secrets" || (echo "ERROR: OCI SDK missing from fat JAR" && exit 1)
#RUN --mount=type=secret,id=github_token GITHUB_TOKEN=$(cat /run/secrets/github_token) ./gradlew :api:bootJar
#github_token을 받아서 waffle-spring에서 oci vault 라이브러리를 받아옴.


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
