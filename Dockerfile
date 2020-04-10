FROM openjdk:8-jre

RUN set -x && \
  echo "Asia/Shanghai" > /etc/timezone && \
  apt-get update && \
  apt-get install -y nginx && \
  mkdir -p /opt/jar/db/ && \
  chown 1000:nogroup /opt/jar/db/ && \
  mkdir -p /opt/jar/web/ && \
  chown 1000:nogroup /opt/jar/web/ && \
  mkdir -p /opt/conf/ && \
  chown 1000:nogroup /opt/conf/

ADD --chown=1000:nogroup ./admin.jar /opt/jar/admin.jar
ADD --chown=1000:nogroup ./dist.zip /opt/jar/dist.zip
ADD --chown=1000:nogroup ./v2ray-proxy.jar /opt/jar/v2ray-proxy.jar
ADD --chown=1000:nogroup ./conf/admin.yaml /opt/conf/admin.yaml
ADD --chown=1000:nogroup ./conf/proxy.yaml /opt/conf/proxy.yaml
ADD --chown=1000:nogroup ./conf/config.json /opt/jar/config.json
ADD --chown=1000:nogroup ./init.sh /opt/jar/run.sh
ADD --chown=root:root ./conf/v2ray-mng.conf /etc/nginx/conf.d/default.conf

RUN cd /opt/jar/ && \
  unzip dist.zip -d /opt/jar/web/ && \
  chmod +x /opt/jar/admin.jar && \
  chmod +x /opt/jar/v2ray-proxy.jar && \
  chmod +x /opt/jar/run.sh

WORKDIR /opt/jar/
CMD ["/bin/sh", "run.sh"]