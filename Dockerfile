#FROM adoptopenjdk/openjdk11
#
#ADD logicaScoolBot-0.0.1-SNAPSHOT.jar /app/bot.jar
#
#ENTRYPOINT ["java","-jar","app/bot.jar"]


FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY traderBot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]