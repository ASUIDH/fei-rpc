# fei-rpc

一个java rpc框架

## 特性

使用java动态代理实现远程调用框架 

实现了java原生Socket传输和Netty传输两种传输方法,并且两者可互通 使用Nacos作为注册中心,管理服务提供者信息,服务侧基于注解自动注册服务,在消费者侧实现随机和 轮询的负载均衡策略 

实现了自定义的通信协议,避免了TCP粘包问题 

实现了四种序列化方法，Json 方式、Kryo 方式、Hessian 方式与 Google Protobuf 方式
