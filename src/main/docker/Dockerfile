FROM  java:7-jre
MAINTAINER "vip@qq.com"

# install MAVEN
ADD apache-maven-3.3.9 /usr/local/maven3
ENV MAVEN_HOME  /usr/local/maven3
ENV PATH $PATH:$MAVEN_HOME/bin
RUN mkdir -p $MAVEN_HOME

# install tomcat
ADD apache-tomcat-7.0.68  /usr/local/tomcat7
ENV CATALINA_HOME /usr/local/tomcat7
ENV PATH $PATH:$CATALINA_HOME/bin
RUN mkdir -p $CATALINA_HOME

WORKDIR $CATALINA_HOME

# for test mvn command
RUN mvn -version

EXPOSE 8080

# run shell:  sh catelina.sh run
CMD ["/usr/local/tomcat7/bin/catalina.sh", "run"]