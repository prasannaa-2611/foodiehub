FROM tomcat:10.1-jdk17

# Copy website files
COPY web/ /usr/local/tomcat/webapps/ROOT/

# Copy required libraries
COPY lib/mysql-connector-j-26.7.0.jar /usr/local/tomcat/lib/
COPY lib/jakarta.mail-api-2.1.3.jar /usr/local/tomcat/lib/
COPY lib/angus-mail-2.0.5.jar /usr/local/tomcat/lib/
COPY lib/jakarta.activation-api-2.1.3.jar /usr/local/tomcat/lib/

# Create servlet classes directory
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

# Copy all servlet source files
COPY src/servlet/ /tmp/servlet/

# Compile all servlets
RUN javac \
    -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/lib/mysql-connector-j-26.7.0.jar:/usr/local/tomcat/lib/jakarta.mail-api-2.1.3.jar:/usr/local/tomcat/lib/angus-mail-2.0.5.jar:/usr/local/tomcat/lib/jakarta.activation-api-2.1.3.jar" \
    -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    /tmp/servlet/*.java
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml


EXPOSE 8080