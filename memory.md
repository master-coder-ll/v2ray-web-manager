## java内存使用情况


### 堆与线程
   

![堆与内存](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/thread-heap.png)


上图为Java proxy端服务的堆与线程情况。

可以看出CPU占用极低，几乎可以忽略不记。

堆的使用也是占用也很低, 基本都是基本都是minor gc。趋势向上到瓶颈会触发 Major gc，回收内存。
 
线程看来占有较多，因为有10多条线程是JMX远程连接，监控使用。正常线程数为15。（Linux 线程内存占用约 2MB/条）


![直接内存](https://raw.githubusercontent.com/master-coder-ll/v2ray-web-manager/master/static/direct-memory.png)

上图为`直接内存`占用情况，可以看出回收及时。

 