#FROM adoptopenjdk/openjdk11
#
#ADD logicaScoolBot-0.0.1-SNAPSHOT.jar /app/bot.jar
#
#ENTRYPOINT ["java","-jar","app/bot.jar"]


FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/traderBot-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]