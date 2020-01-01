# v2ray-web-manager
 一个v2ray的管理后台和一个v2ray中间件，有以下重要的特征：
 * 流量控制(qos)-无敌的带宽控制功能、连接数控制
 * 账号管理
 * 流量管理-到期自动、流量超标断开连接
 * 服务器管理 
 * 公告管理
 * 分权限
 
 v2ray-web-manager 在原有的 tls+ws+vemss+v2ray 的基础上，增强了流量、账号的管理。
 
 ## 简要视图
 服务器配置
 ![服务器](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_index.png)
 
 管理员帐号页面 
 ![管理员账号](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_account.png)
 
普通用户看到页面
 ![管理员账号]( https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/my-account.png)

更多页面，请自己尝试。
 ## 开始使用 
 
 ### 从release中开始
 #### web部署
  
  1. 安装nginx
      * 配置nginx.conf

      ```
         root /var/www/admin;   
          location /api {
                      proxy_pass http://127.0.0.1:9091/;
                    } 
      ```
   2. 从[releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases) 下载web 的zip.解压到/var/www/admin
  #### java部署
   1. 安装java 环境
   
        ```
         Debian, Ubuntu, etc.
         sudo apt-get install openjdk-8-jre
      
         Fedora, Oracle Linux, Red Hat Enterprise Linux, etc.
         $ su -c "yum install java-1.8.0-openjdk"

    
        ```
   
   2. 从[releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases) 下载admin-1.0.jar 、v2ray-proxy-1.0.jar
        * 新建目录 `mkdir -p /opt/jar`  
        * 保存到 /opt/jar
        
   3. java配置
   
    从编译环境开始    

 
   * java8 以上
   * maven 3
    
 总体步骤：从GitHub上clone源码,修改配置文件,生成你特有的jar文件。
 
    
### 部署

## 架构
现在架构：

![架构1](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/now.png)

未来架构(不继续开发)：

![架构1](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/future.png)

## 项目结构
   * common 公有
   * proxy 代理中间件-核心
        * 提供 账号解析支持
        * 提供 流量控制
        * 提供 流量上报
        * 提供 流量转发
   * v2ray-jdk v2ray rpc 支持包
   * vpn-admin 管理后台api 端
        * 提供 用户/账号等功能的管理
   
   
## 警告
请遵循你国家的法律下使用。
## License
This project is licensed under the MIT License
