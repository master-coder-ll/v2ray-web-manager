

# 教程：使 V2ray 支持 WebService + TLS 模式

配置服务器HTTPS访问，使 V2ray 支持 WebService + TLS 模式

## 1. 跟着新手教程安装前后端服务

 [一步一步跟着我从零安装](https://github.com/master-coder-ll/v2ray-web-manager/blob/master/step-by-step-install.md)
 
 安装成功，并可以使用管理员账号正常登陆；

## 2. 配置服务端支持HTTPS

前提：
- 开放服务器443端口
- 域名能够解析到当前 Linux 机器 IP；

### 2.1 申请SSL证书

申请SSL证书有以下两种方式：

#### 2.1.1（不推荐）网页自行申请

 略...
 
#### 2.1.2 私自颁发证书，套cloudflareCDN
代教程-PR
#### 2.1.3 Linux命令行下使用acme.sh申请配置免费证书

**1. 安装 acme.sh**

```bash
curl https://get.acme.sh | sh
```

**2. 安装后的配置**

把 acme.sh 安装到根目录下: ~/.acme.sh/ 并创建 一个 bash 的 alias, 方便后续使用

```bash
alias acme.sh=~/.acme.sh/acme.sh
echo 'alias acme.sh=~/.acme.sh/acme.sh' >>/etc/profile
```

**3. 创建 cronjob, 每天 0:00 点自动检测证书, 如果快过期了, 需要更新, 则会自动更新证书(可执行 **crontab -l** 查看)**

```bash
00 00 * * * root /root/.acme.sh/acme.sh --cron --home /root/.acme.sh &>/var/log/acme.sh.logs
```

**4. 证书的安装**

> 使用下面的安装方式，证书将每60天更新一次

使用 --installcert 命令,并指定目标位置, 然后证书文件会被copy到此位置！！

具体看示例（示例域名： **XXXX.com**）

- 先创建用于存放证书的文件夹

```bash
mkdir -p /etc/nginx/ssl_cert/XXXX.com
```

- 安装证书

```bash
acme.sh --install-cert -d XXXX.com \
--key-file /etc/nginx/ssl_cert/XXXX.com/XXXX.com.key \
--fullchain-file /etc/nginx/ssl_cert/XXXX.com/XXXX.com.cer \
--reloadcmd  "service nginx force-reload"
```

执行成功后，终端最终显示：**Reload success**


### 2.2 Nginx配置SSL证书

> 80默认重定向至443

> 配置文件目录： **/etc/nginx/conf.d/v2ray-manager.conf**

 **配置文件示例：**
```
server {
    listen 443 ssl http2;
    server_name XXXX.com;
    root /opt/jar/web;
    ssl_certificate       /etc/nginx/ssl_cert/XXXX.com/XXXX.com.cer;
    ssl_certificate_key   /etc/nginx/ssl_cert/XXXX.com/XXXX.com.key;
    ssl_protocols TLSv1.1 TLSv1.2 TLSv1.3;
    ssl_ciphers TLS13-AES-128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;

  
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
       
server {
    listen 80;
    server_name XXXX.com;
    return 301 https:///$http_host$request_uri;
}
```

**使配置文件生效：**

```bash
nginx -s reload
```


## 3. 最后记得

访问你的域名，使用管理员登录，修改服务信息中的域名：

左菜单栏：【服务器】--【服务器列表】，

寻找ip对应的服务器

   * 访问域名-修改为：XXXX.com
   * 访问端口-修改为：443
   * 支持TLS-修改为： 是





## 4. 最后使用

添加用户，然后登录管理系统，

获取你的v2ray链接 或 订阅地址 吧！

此时可以发现你的v2ray配置已经为 ws + tls 模式了！

RP by @xifanu




