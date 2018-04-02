FROM nimmis/java-centos:oracle-9-jdk
VOLUME /tmp
EXPOSE 80

RUN \
  yum update -y && \
  yum install -y centos-release-dotnet && \
  yum install -y rh-dotnet20 && \
  yum install -y tomcat && \
  yum clean all

ARG JAR_FILE
RUN echo "JAR_FILE : $JAR_FILE"
ADD target/${JAR_FILE} /opt/oAuthProxy/${JAR_FILE}
COPY src/main/docker/run.sh /opt/oAuthProxy/run.sh
COPY src/main/docker/bootstrap.yml /opt/oAuthProxy/config/bootstrap.yml
RUN /bin/chmod 755 /opt/oAuthProxy/run.sh
LABEL maintainer "Tom Freestone" "tefreestone@ldschurch.org"

#ENTRYPOINT "/opt/oAuthProxy/run.sh"