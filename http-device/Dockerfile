FROM openjdk:8-jre-alpine

ADD target/http-device.jar /

CMD ["java", "-Dvertx.cacheDirBase=/tmp", "-Dvertx.disableDnsResolver=true", "-jar", "http-device.jar"]