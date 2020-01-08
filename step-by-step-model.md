## v2ray-web-manager模式和配置

1. 分散式负载
    
    此模式，每台机器都有proxy端。用户可以直接访问每台服务器的proxy端.

   
![模型1](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/model1.png)
  
   admin管理端配置:
        
            服务器名称：节点1
            访问域名: 192.168.1.1(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地默认
           v2ray地址/端口/管理端口:都在本地默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
        
            服务器名称：节点2
            访问域名: 192.168.1.2(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地默认
            v2ray地址/端口/管理端口:都在本地默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
           
            服务器名称：节点3
            访问域名: 192.168.1.3(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地默认
           v2ray地址/端口/管理端口:都在本地默认
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）

2. 一对多（中转）

    ![模型2](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/model2.png)
      
    此模式，所有流量都会经过 `192.168.2.1`,v2ray配置文件`config.json`中所有"listen": "127.0.0.1" 去掉重启v2ray服务。*这会使得v2ray
    的api端口发布在外网，请自行设置防火墙限仅特定ip范围*
    
        
            服务器名称：节点1
            访问域名: 192.168.2.1(真实ip/域名)
            访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
            支持TLS: 否 (看你情况 proxy端不提供tls支持)
            中间件地址/端口:都在本地默认
            v2ray地址/端口/管理端口:192.168.2.2 6001 62789
            v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
            
             服务器名称：节点2
             访问域名: 192.168.2.1(真实ip/域名)
             访问端口: 8081(访问端口 如果使用nginx反向代理就是nignx端口)
             支持TLS: 否 (看你情况 proxy端不提供tls支持)
             中间件地址/端口:都在本地默认
             v2ray地址/端口/管理端口:192.168.2.3 6001 62789
             v2rayTag: 6001（v2ray 配置文件config.json中的tag,默认6001）
    
    


 
        
      
    
    
