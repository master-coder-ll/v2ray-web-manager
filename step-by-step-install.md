## 新手教程-Ubuntu16.04-安装
  1. 系统要求
     * java8 以上
     * 内存大于等于300M
     * cpu vCPU 1核心
  2. 必要软件安装
        ```
        $ sudo su
        # apt-get update  更新软件源
        # apt install vim  -y  编辑器
        # apt install nginx -y 安装nginx
        # apt install  openjdk-8-jre -y  安装java
        # bash <(curl -L -s https://install.direct/go.sh) 安装v2ray -来源官网
        ```
   
  3. 配置nginx
   
     ```
      # cd /etc/nginx/sites-enabled  进入到nginx配置文件夹
      # mv /etc/nginx/sites-enabled/default /opt/ 移动默认的配置到/opt目录
      # touch v2ray-manager
      # vi v2ray-manager  复制下面的配置 ,`i编辑`,`右键粘贴`各个ssh客户端可能不同。
      # `ESC ` `:wq` 退出并保存
         server {
                 listen 80 ;
                 server_name _;
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
  4. 下载文件releases文件
    
     ```
     # mkdir /opt/v2ray-manager -p  创建目录
     # cd /opt/v2ray-manager 
     下载releases 包,应该选择最新的release进行下载`wget -c [url] `
     # wget -c https://github.com/master-coder-ll/v2ray-web-manager/releases/download/v1.1/admin-1.0.jar
     # wget -c https://github.com/master-coder-ll/v2ray-web-manager/releases/download/v1.1/dist.zip   
     # wget -c https://github.com/master-coder-ll/v2ray-web-manager/releases/download/v1.1/v2ray-proxy-1.0.jar
     
     # unzip dist.zip  -d web  先解压web
     
     ```
  5. 配置
     
        ```
    
      下载配置文件->https://github.com/master-coder-ll/v2ray-web-manager/tree/master/conf
      使用 `wget -c 下载 conf目录下的3个配置文件`,下载完成 
      # wget -c  https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/admin.properties
      # wget -c https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/proxy.properties
      # wget -c https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/conf/config.json
       ```  
      按自己的情况，配置admin/proxy 配置文件,可以下载到你电脑修改后在上传`/opt/v2ray-manager/`,并且保持UTF-8的编码。
      
      1. admin.properties 需要你手动配置的如下：
        
                #邮箱用户名
                email.userName=
                #密码
                email.password=
                #smtp地址
                email.host=
                #端口
                email.port=465
                
                #发送验证码模板
                email.vCodeTemplate=你的验证码为: %s,请在10分钟内使用.
                #和proxy端通讯的认证密码.
                proxy.api.auth=
                
                #admin 初始化生成的账号
                admin.email=admin@admin.com
                admin.password=1234567
                
      2. proxy.properties 需要你手动配置的如下：
         
              proxy访问认证密码和admin端相同
              proxy.auth=
              #admin 管理地址和端口，如果admin端也在本机不需要修改
              manager.address=http://127.0.0.1:9091
              #adminApi 地址端口改变这也需要修改
              manager.getProxyAccountUrl=http://127.0.0.1:9091/proxy/proxyAccount/ac?accountNo={accountNo}
              #adminApi 地址端口改变这也需要修改
              manager.reportFlowUrl=http://127.0.0.1:9091/report/flowStat
     
     我这里不需要注册用户，并且admin和proxy 都在本机 ，我只需要修改`proxy.api.auth` 和 `proxy.auth` 。并且管理员账号密码我也使用默认。
     
     配置v2ray
    
       ```
        # mv /etc/v2ray/config.json config.json.bak 备份默认v2ray默认配置
        # cp /opt/v2ray-manager/config.json /etc/v2ray/ 复制配置到v2ray目录
        # service v2ray stop
        # service v2ray start   重启v2ray
       ```
     
     运行java
     
     ```
      运行 admin
      #  mkdir /opt/jar/db -p  创建默认数据库目录
      # nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m  /opt/v2ray-manager/admin-1.0.jar --spring.config.location=/opt/v2ray-manager/admin.properties > /dev/null 2>&1 &
      运行 v2ray-proxy
      # nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m /opt/v2ray-manager/v2ray-proxy-1.0.jar --spring.config.location=/opt/v2ray-manager/proxy.properties > /dev/null 2>&1 &
      
     ```
     
     全部完成，部署成功。
        
      
    
    
