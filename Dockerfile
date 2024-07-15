FROM openjdk:11-jre-slim

ADD tradeBot-0.0.1-SNAPSHOT.jar /app/bot.jar

ENTRYPOINT ["java","-jar","app/bot.jar"]
