## 新手教程-Ubuntu16.04+-安装

  1. 必要软件安装
        ```
        $ sudo su
        # apt-get update  更新软件源
        # apt install vim  -y  编辑器
        # apt install nginx -y 安装nginx
        # apt install  openjdk-8-jre -y  安装java
        # bash <(curl -L -s https://install.direct/go.sh) 安装v2ray -来源官网
        ```
   
  2. 配置nginx
   
     ```
      # cd /etc/nginx/sites-enabled  进入到nginx配置文件夹
      # mv /etc/nginx/sites-enabled/default /opt/ 移动默认的配置到/opt目录
      # touch v2ray-manager
      # vi v2ray-manager  复制下面的配置 ,`i编辑`,`右键粘贴`各个ssh客户端可能不同。
      # `ESC ` `:wq` 退出并保存
         server {
                 listen 80 ;
                 server_name _; #或者域名
                 root /opt/v2ray-manager/web;
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
      # nginx -s reload  没有报错，配置成功
     ```
  3. 下载文件releases文件
  
     [java服务-releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases)
     
     [前端服务-releases页面](https://github.com/master-coder-ll/v2ray-manager-console/releases)
    
     ```
     # mkdir /opt/v2ray-manager -p  创建目录
     # cd /opt/v2ray-manager 
     下载releases 包,选择最新的release进行下载`wget -c [url] `,下面地址为自动获取最新的release,特定版本访问releases页面下载
     # wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/admin-1.0.jar
     # wget -c https://glare.now.sh/master-coder-ll/v2ray-manager-console/dist.zip   
     # wget -c https://glare.now.sh/master-coder-ll/v2ray-web-manager/v2ray-proxy-1.0.jar
      
     # unzip dist.zip  -d web  先解压web
      前端项目部署完成
     
     ```
  4. 配置
     
        ```
    
      下载配置文件->https://github.com/master-coder-ll/v2ray-web-manager/tree/master/conf
      使用 `wget -c 下载 conf目录下的3个配置文件`,下载完成 
      # wget -c  https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/admin.yaml
      # wget -c https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/admin.yaml
      # wget -c https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/config.json
       ```  
      按自己的情况，配置admin/proxy 配置文件,可以下载到你电脑修改后在上传`/opt/v2ray-manager/`,并且保持UTF-8的编码。
      
      1. admin.yaml 需要你手动配置的如下：
        
                email:
                  #stmp地址
                  host:
                  #用户名称
                  userName:
                  #密码
                  password:
                  #端口
                  port:
                
                proxy:
                 #与porxy交互的密码，也是各种token的私钥
                 authPassword: ''
                 
                admin:
                  #第一次启动时候的账号和密码
                  email: admin@admin.com
                  password: 123456
                
      2. proxy.yaml 需要你手动配置的如下：
         
            proxy:
              authPassword: ''
              
            manager:
                #如果admin端不在本机需要修改
                address: http://127.0.0.1:9091
     
     我这里不需要注册用户，并且admin和proxy 都在本机 ，我只需要修改2个配置文件的`proxy.authPassword`为一个随机的字符串如`1234abc` 。并且管理员账号密码我也使用默认。
     
     配置v2ray
    
       ```
        # mv /etc/v2ray/config.json config.json.bak 备份默认v2ray默认配置
        # cp /opt/v2ray-manager/config.json /etc/v2ray/ 复制配置到v2ray目录
        # service v2ray stop
        # service v2ray start   重启v2ray
       ```
     
  5. 运行java
     
     ```
      运行 admin
      #  mkdir /opt/jar/db -p  创建默认数据库目录
      # nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m  /opt/v2ray-manager/admin-版本号.jar --spring.config.location=/opt/v2ray-manager/admin.properties > /dev/null 2>&1 &
      --- 
      运行 v2ray-proxy
      # nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m /opt/v2ray-manager/v2ray-proxy-版本号.jar --spring.config.location=/opt/v2ray-manager/proxy.properties > /dev/null 2>&1 &
      
     ```
     
     全部完成，部署成功。
             
    
  

      
    
    
