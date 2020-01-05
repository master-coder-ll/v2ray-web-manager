# v2ray-web-manager
 一个v2ray的管理后台和一个v2ray中间件，有以下重要的特征：
 * 流量控制(qos)-无敌的速率、流量、连接数控制 ，一切都可以灵活定制
 * 账号管理
 * 流量管理-到期自动、流量超标断开连接
 * 服务器管理 
 * 公告管理
 * 分权限
 
 v2ray-web-manager 在原有的 tls+ws+vemss+v2ray 的基础上，增强了流量、账号的管理等功能。
 
 #### 如果能帮助到你，请给我个`star` `Fork`
 
 ## 简要视图
 服务器配置
 ![服务器](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_index.png)
 
 管理员帐号页面 
 ![管理员账号](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_account.png)
 
普通用户看到页面
 ![管理员账号]( https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/my-account.png)

更多页面，请自己尝试。
 
 ## 开始使用 
 
 ### 新手
 
  [一步一跟着我从零安装](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-install.md)
  
  [一步一跟着我从零配置网站](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-conf.md)
 
 ### 熟悉linux的用户
 
 [安装简要](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/old.md)
### 维护
   java 不需要维护
   
   数据库-默认情况下会在 `/opt/jar/db` 生成admin.db 定时保存就好
   
   java日志-自动维护
### 优化
   #### 减少java 内存占用
   
   1. 使用其他jre 如：[openj9-eclipse](https://www.eclipse.org/openj9/),
   使用 `引用压缩` 把64位引用指针压缩到32位。减低内存占用明显，并且不影响性能。
 sad
   2. `激进` ~~java:JIT特性是提高java性能的重要编译器，其动态编译优化性能更甚于c++等一些静态编译语言。但是也是内存占有用的大户。
               如果你需要减少内存占用，在运行java 命令上加 `-Djava.compiler=NONE` ，会大幅减少java内存占用(约30~50%)，同时降低性能,大幅度增加启动时间。
               出现一些动态代理的问题不要用。~~
        
     
  ## 参数说明
  ### 服务器配置参数
   1.  访问域名 如：test.com ,v2ray客户端显示的名称，可以是域名/IP
    
   2. 访问端口  https tls ->443 ,或者其他80 etc
   
   3. v2rayTag  当前v2ray config.json下默认`6001` 
    
  ### 账号参数
   1. 周期  结算周期 30 表示每30天，重置用户的流量统计
   
   2. 速率  1024KB/s ,单位KB/S
   
   3. 账号  组成v2ray path ，在当前配置下客户端会生成 path:/ws/账号/
   
   4. 流量 周期内可用流量数，单位GB
   
  ## 其他场景
  
  ### 中转
    
  仅需要 `proxy端` 就可以实现流量中转。在某些地区可能更加隐蔽，配置相同。
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
请遵循你国家的法律下使用。仅供学习研究
## License
This project is licensed under the MIT License
