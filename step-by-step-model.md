## v2ray-web-manager模式和配置

## 模式
*  分散式负载
    
    此模式，每台服务器都有proxy端。用户可以直接访问每台服务器的proxy端.

   
![模型1](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/model1.png)
  
   admin管理端配置:
        
            服务器名称：节点1
            访问域名: 192.168.1.1(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地，默认
           v2ray地址/端口/管理端口:都在本地，默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
        
            服务器名称：节点2
            访问域名: 192.168.1.2(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地，默认
            v2ray地址/端口/管理端口:都在本地，默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
           
            服务器名称：节点3
            访问域名: 192.168.1.3(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地，默认
           v2ray地址/端口/管理端口:都在本地，默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）

* 一对多（中转）

    ![模型2](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/model2.png)
      
    此模式，所有流量都会经过 `192.168.2.1`,需要v2ray配置文件`config.json`中所有"listen": "127.0.0.1" 去掉重启v2ray服务。*这会使得v2ray
    的api端口发布在外网，请自行设置防火墙限仅特定ip范围*
    
        
            服务器名称：节点1
            访问域名: 192.168.2.1(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地，默认
            v2ray地址/端口/管理端口:192.168.2.2 6001 62789
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
            
             服务器名称：节点2
             访问域名: 192.168.2.1(真实ip/域名)
             访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
             支持TLS: 否 (看你情况 proxy端不提供tls支持)
             中间件地址/端口:都在本地，默认
             v2ray地址/端口/管理端口:192.168.2.3 6001 62789
             v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）

## 其他功能参数配置
    
  * 服务器配置参数
  
       1.  访问域名 如：test.com ,v2ray客户端显示的名称，可以是域名/IP
        
       2. 访问端口  https tls ->443 ,或者其他80 etc
       
       3. v2rayTag  当前v2ray config.json下默认`6001` 
    
  * 账号参数
  
       1. 周期  结算周期 30 表示每30天，重置用户的流量统计
       
       2. 速率  1024KB/s ,单位KB/S
       
       3. 账号  组成v2ray path ，在当前配置下客户端会生成 path:/ws/账号/
       
       4. 流量 周期内可用流量数，单位GB
  
  * 开启邀请码注册(release 大于 v2.0)
  
           管理员登录->参数列表->需要邀请码才能注册吗？默认 `false` ，开启 `true`  
           
           次要选项: 用户能邀请其他人注册吗？默认 false ，开启 true 
  
  * 订阅地址配置
  
    管理员登录->参数列表->`订阅地址访问前缀`的value，127.0.0.1替换为你的ip/域名


 
        
      
    
    
