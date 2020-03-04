# 新手教程-安装

 仅在CentOS7/ ubuntu 16 + 测试如下安装过程

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
  root /opt/jar/web;
                
  location /api {
    proxy_pass http://127.0.0.1:9091/;
  }

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
 
 [前端服务-releases页面](https://github.com/master-coder-ll/v2ray-manager-console/releases)

```bash
# 创建目录
mkdir /opt/jar -p
cd /opt/jar 

# 下载releases包
wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/admin -O admin.jar
wget -c https://glare.now.sh/master-coder-ll/v2ray-manager-console/dist -O dist.zip
wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/v2ray-proxy -O v2ray-proxy.jar

# 解压前端到web文件夹
unzip dist.zip  -d web

#前端项目部署完成
```


####  4. 配置
     
```bash
# 下载管理服务的配置文件
wget -c --no-check-certificate https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/admin.yaml

# 下载代理服务的配置文件
wget -c --no-check-certificate https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/proxy.yaml

# 下载v2ray的专用配置文件
wget -c --no-check-certificate https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/config.json
```  

按自己的情况，配置 admin/proxy 配置文件,可以下载到你电脑修改后在上传`/opt/jar/`，并且保持UTF-8的编码。
  
- admin.yaml 需要你手动配置的部分：

          # 所有参数:（冒号）后面都要有空格 
            email: 
               #stmp地址
               host: 
               #用户名称
               userName: 
               #密码
               password:
               #端口
               port: 
               #默认false ,邮件不支持startTls不要开启
               startTlsEnabled: false
            
            proxy:
             #与porxy交互的密码，也是各种token的私钥
             authPassword: ''
             
            admin:
              #第一次启动时候的账号和密码
              email: admin@admin.com
              password: 123456

- proxy.yaml 需要你手动配置的如下：
         
            proxy:
              authPassword: ''
              
            manager:
                # 如果admin端不在本机需要修改
                address:  http://127.0.0.1:9091
 
 我这里不需要注册用户，并且admin和proxy 都在本机 ，我只需要修改2个配置文件的`proxy.authPassword`为一个随机的字符串如`1234abc` 。并且管理员账号密码我也使用默认。
 
####  5. 配置v2ray

```bash
# 备份默认v2ray默认配置
mv /etc/v2ray/config.json /etc/v2ray/config.json.bak

# 复制配置到v2ray目录
cp /opt/jar/config.json /etc/v2ray/

# 重启v2ray
service v2ray stop
service v2ray start
```
     
####  6. 运行java
     
```bash
# 创建默认数据库目录
mkdir /opt/jar/db -p

# 运行 admin
nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m  /opt/jar/admin.jar --spring.config.location=/opt/jar/admin.yaml > /dev/null 2>&1 &

# 运行 v2ray-proxy
nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m /opt/jar/v2ray-proxy.jar --spring.config.location=/opt/jar/proxy.yaml > /dev/null 2>&1 &
```

####  7. 查看日志
```bash
# 查看admin日志
tail -100f /opt/jar/logs/admin.log
    
# 查看 v2ray-proxy日志
tail -f /opt/jar/logs/v2ray-proxy.log
    
# ctrl+c 退出查看日志
```


### 全部完成，部署成功！
访问 http://`ip` 出现登录页面，成功登录管理员账号，则服务运行成功  
