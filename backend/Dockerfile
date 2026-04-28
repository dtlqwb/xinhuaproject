FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"
ENV DB_URL=jdbc:mysql://mysql:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV JWT_SECRET=salesCustomerSystemSecretKey2024Production
ENV FILE_UPLOAD_PATH=/data/uploads/

VOLUME /data

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
