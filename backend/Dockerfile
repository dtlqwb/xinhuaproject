FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV DB_URL=jdbc:mysql://localhost:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV JWT_SECRET=salesCustomerSystemSecretKey2024Production
ENV FILE_UPLOAD_PATH=/data/uploads/

VOLUME /data

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
