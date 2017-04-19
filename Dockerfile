FROM clouway/java:8
MAINTAINER TelcoNG Devs <telcong@clouway.com>

WORKDIR /app
ADD pos-print/build/libs/pos-print-all.jar pos-print.jar

EXPOSE 8080/tcp
ENTRYPOINT ["java", "-Xms128M", "-Xmx2048M", "-Dfile.encoding=UTF-8", "-jar", "pos-print.jar"]
