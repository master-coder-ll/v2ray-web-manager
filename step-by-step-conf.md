## 新手教程-网站配置

1. 创建服务器
    
   因没有配置tls，所以我们的端口是80
   
   ![服务器配置](https://github.com/master-coder-ll/v2ray-web-manager/raw/master/static/server-conf.png)
     
2. 我的账号-点击 `选择服务器` 选择刚刚创建的服务器。在通过各个v2ray客户端 扫码/粘贴 就可以上网。
        
 
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
 ## 参数管理
   
   * 开启邀请码注册(release 大于 v2.0)
   
            管理员登录->参数列表->需要邀请码才能注册吗？默认 `false` ，开启 `true`  
            
            次要选项: 用户能邀请其他人注册吗？默认 false ，开启 true 
   
   * 订阅地址配置
   
     管理员登录->参数列表->`订阅地址访问前缀`的value，127.0.0.1替换为你的ip/域名
     
     如果你的域名已经配置`HTTPS`，订阅地址也需要把`http`修改为`https`.
    
    
