
 ### 从release中开始
 #### web部署
  
   1. 安装nginx/apache等
      * 配置nginx.conf

      ```
         root /var/www/admin;  
          #api相关代理到admin端口
         location /api {
                      proxy_pass http://127.0.0.1:9091/;
                    }
          #/ws/*相关的代理到 proxy端口  
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
      ```
   2. 从[releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases) 下载dist.zip解压到/var/www/admin
  
   3. 重新加载nginx  
  #### java部署
  ##### 系统要求
   * java8 以上
   * 内存大于等于300M
   * cpu vCPU 1核心
     
   1. 安装java 环境
     
      ```
         Debian, Ubuntu, etc.
         sudo apt-get install openjdk-8-jre
      
         Fedora, Oracle Linux, Red Hat Enterprise Linux, etc.
          su -c "yum install java-1.8.0-openjdk"
        
         验证: java -version
        ```
   
   2. 从[releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases) 下载admin-1.0.jar 、v2ray-proxy-1.0.jar
        * 新建目录 `mkdir -p /opt/jar`  
        * 保存到 /opt/jar
        
   3. java配置，文件在conf目录下,`你应复制conf下的文件进行修改`。`所有properties文件应该保持UTF-8编码`，避免乱码
      1. admin.properties 需要你手动配置的如下：
        ```
        
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

        ```
      2. proxy.properties 需要你手动配置的如下：
       ```
            #proxy访问认证密码和admin端相同
            proxy.auth=
            #admin 管理地址和端口，如果admin端也在本机不需要修改
            manager.address=http://127.0.0.1:9091
            #adminApi 地址端口改变这也需要修改
            manager.getProxyAccountUrl=http://127.0.0.1:9091/proxy/proxyAccount/ac?accountNo={accountNo}
            #adminApi 地址端口改变这也需要修改
            manager.reportFlowUrl=http://127.0.0.1:9091/report/flowStat

      
      ```
   4. v2ray配置
       1. 安装v2ray
       2. 复制 `/conf` 下的config.json 覆盖 v2ray默认配置`var/v2ray/`
       3. config.json 会开通2个监听在127.0.0.1上的端口 `api端口62789`，`ws端口 6001`。这2个端口不会发布到外网请放心。
       4. 重启v2ray   
   5. 最后一步，运行java应用
       1. 运行admin端: `nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m  [jar所在位置如:/opt/jar/admin-1.0.jar] 
         --spring.config.location=[配置文件所在位置如：/opt/jar/admin.properties] > /dev/null 2>&1 &`
       
       2. 运行proxy端： `nohup java -jar -Xms40m -Xmx40m -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=80m  [jar所在位置如:/opt/jar/v2ray-proxy-1.0.jar] 
                            --spring.config.location=[配置文件所在位置如：/opt/jar/proxy.properties] > /dev/null 2>&1 &`
       

 ### 从编译开始
 
 从git下载源码
 1. java 编译
    * 依赖java环境:java8+,maven3+
    * 执行 mvn clean install -Dmaven.test.skip=true
    * 各子项目 `target` 目录为编译好的jar包
 2. web端编译
    * 环境 nodejs ,vue环境
    * `cd web/ `  
    * npm run build:prod
    * `web/dist/`目录下为输出文件
 3. 部署 参考上面
 