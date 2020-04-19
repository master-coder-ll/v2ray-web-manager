# v2ray-web-manager 
 [![Build Status](https://travis-ci.com/master-coder-ll/v2ray-web-manager.svg?branch=master)](https://travis-ci.com/master-coder-ll/v2ray-web-manager) 
 [![reliability_rating]( https://sonarcloud.io/api/project_badges/measure?project=master-coder-ll_v2ray-web-manager&metric=reliability_rating)](https://sonarcloud.io/dashboard/index/master-coder-ll_v2ray-web-manager)
 [![bugs](https://sonarcloud.io/api/project_badges/measure?project=master-coder-ll_v2ray-web-manager&metric=bugs)](https://sonarcloud.io/dashboard/index/master-coder-ll_v2ray-web-manager)
 
 v2ray-web-manager 项目包含admin管理端和proxy端，admin端提供管理功能。proxy端提供核心的流量控制、账号识别、流量转发功能，
 同时支持多种转发流量模型（1对1，1对多）。
 
 项目核心是工作在传输层的中间件，位于用户与v2ray链路之间。通过转发流量实现。理论上支持上层所有的协议，现适配了ws协议。 
 
 ssl/tls 支持使用nginx 等工具提供，也可以套cdn提供。

 
  ## 特征：
  * 流量控制(qos)-无敌的速率、流量、连接数控制 ，一切都可以灵活定制
  * 账号管理
  * 流量管理-到期自动断开、流量超标自动断开
  * 服务器管理 
  * 公告管理
  * 分权限
  * 邀请码注册
  * 订阅
  * 等级
 
 
 
#### 如果能帮助到你，请`watch` `star` `Fork`
  
 ## 前端项目
   [前端项目->v2ray-manager-console](https://github.com/master-coder-ll/v2ray-manager-console)
 

 
 ## 简要视图
 服务器配置
 ![服务器](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_index.png)
 
 管理员帐号页面 
 ![管理员账号](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/admin_account.png)
 
普通用户看到页面
 ![管理员账号]( https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/my-account.png)

更多页面，请自己尝试。
 
 ## 1.开始使用 
   
 #### 系统要求
 
    * java8 以上
    * 内存大于等于300M
    * cpu vCPU 1核心
    * nginx 或者其他具有相同功能


   
 #### 教程
 
  [一步一步跟着我从零安装](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-install.md)
  
  [一步一步跟着我从零配置网站](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-conf.md)
  
  [中级-为服务提供tls(https/wss)支持](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/support-https.md)
    
  [高级-模式](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-model.md)
  
  [高级-实践集群模式](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/cluster.md)

  ##### 其他版本
  
  [从docker中开始](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-docker.md)

## 2.维护与治理 

### 数据库

   数据库-默认情况下会在 `/opt/jar/db` 生成admin.db 定时保存就好

### 升级
   升级前先看更新日志
  #### [更新日志](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/updated-log.md)



   * 下载对应的release包

     [java服务-releases页面](https://github.com/master-coder-ll/v2ray-web-manager/releases)
         
     [前端服务-releases页面](https://github.com/master-coder-ll/v2ray-manager-console/releases)
       

   * 关闭java服务

```
    关闭 admin
    # ps -ef | grep admin-
    # kill [进程号]
    # ps -ef | grep v2ray-proxy-
    # kill [进程号]
 ```

   * [重新启动服务](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-install.md#6-%E8%BF%90%E8%A1%8Cjava)

### 重置管理员密码
> 根据配置文件重置密码或者创建新账号。
```bash
# x.y.z 为版本号，版本大于3.1.4提供支持
java -jar admin-x.y.z.jar --spring.config.location=/opt/jar/admin.yaml restpwd
```
    



     
### 优化

   #### [->内存占用情况<-](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/memory.md)
   
   #### 减少java 内存占用
   
   1. 使用其他jre 如：[openj9-eclipse](https://www.eclipse.org/openj9/),
   减低内存占用明显，并且不影响性能。

        
      
        
## 测试

### 限速测试
    
   说明: 本地带宽下行50Mpbs,上行约8Mpbs。admin端限速2MB/S, 测试结果如图：
    
![测试1](https://www.speedtest.net/result/8927382635.png)
   
   测试结果：下行16.36/8=2.1 刚刚好是admin端配置的2MB/S。
## 参与项目
  * 查看项目的 [todo-list](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/todo-list.md),如果你有能力并且有时间可以实现 欢迎 pull request
  * 任何issue/文档错误，你都可以pull request
  * 想法/优化/新功能，先讨论在pull request 

   
## 架构
现在架构：

![now](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/now.png)

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
