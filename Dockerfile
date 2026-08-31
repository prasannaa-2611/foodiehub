FROM tomcat:10.1-jdk17

# Copy website files
COPY web/ /usr/local/tomcat/webapps/ROOT/

# Copy MySQL driver
COPY lib/mysql-connector-j-26.7.0.jar /usr/local/tomcat/lib/

# Create servlet classes directory
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

# Copy ALL servlet source files
COPY src/servlet/ /tmp/servlet/

# Compile ALL servlets
RUN javac \
    -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/lib/mysql-connector-j-26.7.0.jar" \
    -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    /tmp/servlet/*.java

EXPOSE 8080
COPY src/servlet/OrderServlet.java /tmp/OrderServlet.java

RUN javac ... /tmp/OrderServlet.java