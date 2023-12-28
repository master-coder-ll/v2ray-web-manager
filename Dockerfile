# Stage 1: Fetch dependencies using a smaller base image with wget
FROM alpine:latest AS fetcher

RUN apk add --no-cache wget unzip

# Download external files efficiently
RUN wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/admin -O admin.jar \
  && wget -c https://glare.now.sh/master-coder-ll/v2ray-manager-console/dist -O dist.zip \
  && wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/v2ray-proxy -O v2ray-proxy.jar \
  && unzip dist.zip -d ./web/

# Stage 2: Main application image
FROM openjdk:8-jre-slim

# Install nginx and configure timezone
RUN set -x && \
  echo "Asia/Shanghai" > /etc/timezone && \
  apt-get update && \
  apt-get install -y nginx

# Copy downloaded files from the first stage
COPY --from=fetcher admin.jar v2ray-proxy.jar /opt/jar/
COPY --from=fetcher web /opt/jar/web/

# Create directories efficiently
RUN mkdir -p /opt/jar/db /opt/conf && \
  chown 1000:nogroup /opt/jar/db /opt/jar/web /opt/conf

# Copy local files directly
COPY conf/admin.yaml conf/proxy.yaml conf/config.json /opt/conf/
COPY conf/v2ray-mng.conf /etc/nginx/conf.d/default.conf
COPY init.sh /opt/jar/run.sh

# Unzip and set permissions in a single RUN command
RUN cd /opt/jar/ && \
  chmod +x /opt/jar/admin.jar /opt/jar/v2ray-proxy.jar /opt/jar/run.sh

# Set working directory and default command
WORKDIR /opt/jar/
CMD ["/bin/sh", "run.sh"]
