FROM tomcat:10.1-jdk17

COPY web/ /usr/local/tomcat/webapps/ROOT/

COPY lib/mysql-connector-j-26.7.0.jar /usr/local/tomcat/lib/
COPY lib/jakarta.mail-api-2.1.3.jar /usr/local/tomcat/lib/
COPY lib/angus-mail-2.0.5.jar /usr/local/tomcat/lib/
COPY lib/jakarta.activation-api-2.1.3.jar /usr/local/tomcat/lib/

RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

COPY src/servlet/ /tmp/servlet/

RUN javac \
    -cp "/usr/local/tomcat/lib/*" \
    -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    /tmp/servlet/*.java

RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

EXPOSE 8080