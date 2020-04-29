>本片教程-你将会学到如何将一台新的机器加入到已有的集群中。

## 前置要求
 [一步一步跟着我从零安装](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-install.md)
 
 [一步一步跟着我从零配置](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-conf.md)
 
 已经能运行单机器模式。
 
 ## 新机器中安装
 
 #### 1. 必要软件安装
  
 - ubuntu    
 ```bash
 # 获得root权限
 sudo su
 # 更新软件源
 apt-get update
 # 安装必要软件
 apt install vim nginx openjdk-8-jre wget unzip  -y
 # 安装v2ray -来源官网
 bash <(curl -L -s https://install.direct/go.sh)
 
 ```
 - CentOS
 ```bash
 sudo su
 yum update
 yum makecache
 yum install epel-release
 # 安装必要软件
 yum install vim nginx java-1.8.0-openjdk wget unzip -y
 # 安装v2ray -来源官网
 bash <(curl -L -s https://install.direct/go.sh)
 ```
    
####  2. 配置nginx
```bash
# 进入到nginx配置文件夹
cd /etc/nginx/conf.d
vi v2ray-manager.conf
```
> 复制下面的配置 ,`i编辑`,`右键粘贴` （各个ssh客户端可能不同）
> `ESC ` `:wq` 退出并保存

```
server {

  listen 80 ;
  server_name 127.0.0.1; #修改为自己的IP/域名
 

  location /ws/ {
    proxy_redirect off;
    proxy_pass http://127.0.0.1:8081;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_set_header Host $http_host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  } 

}
```

```bash
# 使nginx配置生效
# 执行没有报错，则配置成功
nginx -s reload
```        
####  3. 下载文件releases文件

 [java服务-releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases)
    
 ```bash
 # 创建目录
 mkdir /opt/jar -p
 cd /opt/jar 
 
 # 下载releases包
 wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/v2ray-proxy -O v2ray-proxy.jar

 ```
 
 
 ####  4. 配置
      
 ```bash

 # 下载代理服务的配置文件
 wget -c --no-check-certificate https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/proxy.yaml
 
 # 下载v2ray的专用配置文件
 wget -c --no-check-certificate https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/config.json
 ```  
 
 按自己的情况，配置proxy 配置文件,可以下载到你电脑修改后在上传`/opt/jar/`，并且保持UTF-8的编码。
    
 - proxy.yaml 需要你手动配置的如下：
          
             proxy:
             # 修改为admin.yaml中相同的字符串
               authPassword: ''
               
             manager:
                 # 【注意】修改为admin所在机器的地址，并且能telnet通
                 address:  http://127.0.0.1:9091
  
  
 ####  5. 配置v2ray
 
 ```bash
 # 备份v2ray默认配置
 mv /etc/v2ray/config.json /etc/v2ray/config.json.bak
 
 # 复制配置到v2ray目录
 cp /opt/jar/config.json /etc/v2ray/
 
 # 重启v2ray
 service v2ray restart
 ```
      
 ####  6. 运行java
      
 ```bash

 # 运行 v2ray-proxy
 nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m /opt/jar/v2ray-proxy.jar --spring.config.location=/opt/jar/proxy.yaml > /dev/null 2>&1 &
 ```
 
 ####  7. 查看日志
 ```bash

 #查看java 进程是否已经存在
 ps -ef |grep java 
 
 # 查看 v2ray-proxy日志
 tail -100f /opt/jar/logs/v2ray-proxy.log
 
 # 查看v2ray-proxy的错误日志(version > v3.1.5)
 tail -100f /opt/jar/logs/v2ray-proxy.log.ERROR
     
 # ctrl+c 退出查看日志
 ```
 
 ### 8.管理端-页面
 
参考： [一步一步跟着我从零配置](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-conf.md)

其中**访问域名**为指向当前的机器的IP/域名

### 9.HTTPS支持

参考：[中级-为服务提供tls(https/wss)支持](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/support-https.md)

如果说TLS支持泛域名，那么复制TLS文件到当前机器，并且配置就好。

注意：域名是不能相同的，比喻机器1的域名是`test.test.com`,机器2的域名**不能**也是`test.test.com`，只能是`test2.test.com`等不同的域名。
admin端根据IP/域名的不同区分服务器。

理论： [高级-模式](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-model.md)

      
 
  
