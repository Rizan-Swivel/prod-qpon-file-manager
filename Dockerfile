FROM openjdk:11-jre-slim
COPY /target/qpon-file-manager-service-0.0.1-SNAPSHOT.jar /home/qpon-file-manager-service-0.0.1-SNAPSHOT.jar
ENV DB_USERNAME qponadmin
ENV DB_PASSWORD QPonpD42cR8823
ENV AWS_ACCESS_KEY_ID AKIAY7UAEJ5DD2G6XDZC
ENV AWS_SECRET_ACCESS_KEY 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
ENV AWS_REGION ap-south-1
ENV AWS_BUCKET_NAME objects-qpon-prod1
WORKDIR /home
EXPOSE 8083
CMD ["java", "-jar", "qpon-file-manager-service-0.0.1-SNAPSHOT.jar"]
