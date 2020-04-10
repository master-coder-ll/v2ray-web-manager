cp /opt/conf/v2ray-mng.conf /etc/nginx/sites-enabled/default
nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=100M -XX:MaxMetaspaceSize=180m /opt/jar/admin.jar --spring.config.location=/opt/conf/admin.yaml >> /opt/admin.log &
nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=100M -XX:MaxMetaspaceSize=180m /opt/jar/v2ray-proxy.jar --spring.config.location=/opt/conf/proxy.yaml >> /opt/proxy.log &
nginx -g 'daemon off;'