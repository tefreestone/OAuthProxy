#!/bin/sh
cd /opt/oAuthProxy/
/usr/bin/java -Djava.security.egd=file:/dev/./urandom \
              -Dspring.profiles.active=dev \
              -jar ./oAuthProxy.jar