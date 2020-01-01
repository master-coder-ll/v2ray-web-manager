# v2ray-web-manager
 一个v2ray的管理后台和一个v2ray中间件，有以下重要的特征：
 * 流量控制(qos)-无敌的带宽控制功能、连接数控制
 * 账号管理
 * 流量管理-到期自动、流量超标断开连接
 * 服务器管理 
 * 公告管理
 
 v2ray-web-manager 在原有的 tls+ws+vemss+v2ray 的基础上，增强了流量、账号的管理。
 
 ## 开始使用 
  
 ### 编译java
 编译环境要求
   * java8 以上
   * maven 3
    
 总体步骤：从GitHub上clone源码,修改配置文件,生成你特有的jar文件。
 
    
### 部署

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
