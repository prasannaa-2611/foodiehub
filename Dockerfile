FROM tomcat:10.1-jdk17

COPY web/ /usr/local/tomcat/webapps/FoodieHub/

COPY lib/mysql-connector-j-26.7.0.jar /usr/local/tomcat/lib/

RUN mkdir -p /usr/local/tomcat/webapps/FoodieHub/WEB-INF/classes

COPY src/servlet/OrderServlet.java /tmp/

RUN javac -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/lib/mysql-connector-j-26.7.0.jar" \
    -d /usr/local/tomcat/webapps/FoodieHub/WEB-INF/classes \
    /tmp/OrderServlet.java