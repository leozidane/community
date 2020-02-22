# nowcoder社交论坛
***项目开发目的是建立一个论坛交流性质的社交网站，主要功能包括发帖，评论，点赞，关注，私信，搜索帖子等功能。***

### 技术核心架构：
- SpringBoot
- Spring，SpringMVC，MyBatis
- Redis，Kafka，Elasticsearch
- Spring Security， Spring Actuator
为SpringMVC， MyBatis以及Spring Security.

### 开发环境
- 构建工具：Apache Maven
-  集成开发工具：IntelliJ IDEA
-  数据库：MySQL、Redis
-  应用服务器：Apache Tomcat
- 版本控制工具：Git

### 项目部署：
  - 硬件环境为VMware CentOS，
  - 使用Nginx对Tomcat服务器进行反向代理

# 主要业务模块

### 登录及权限模块：
- 功能：注册，登录，退出，登录状态管理，账号权限设置等功能。

- 实现方案：使用Spring  mail完成注册时邮箱激活功能，使用cookie以及拦截器，实现自动登录；使用Spring security完成权限管理。

### 核心模块：
- 功能：首页展示，发帖，私信，评论，统一异常，日志处理

- 实现方案：使用SpringMVC实现前后端交互；自定义前缀树实现对敏感词过滤；使用Spring AOP进行统一异常，日志处理。

### 高性能模块：
 - 帖子点赞，用户关注，网站UV、DAU统计，分布式session以及缓存用户信息

- 实现方案：使用redis完成点赞，关注等高频请求功能；redis的高级数据结构HyperLogLog及BitMap实现UV，DAU统计；使用redis实现分布式session，以及缓存用户信息功能。

### 消息队列模块：
- 功能：系统通知，更新Elasticsearch内容

- 实现方案：使用Kafka消息队列。

### 搜索模块
 
- 功能：根据关键词搜索相关帖子内容

- 实现方案：使用Elasticsearch搜索引擎

### 项目优化模块：
- 功能：上传文件到云服务器，热帖排行，服务器本地缓存

- 使用七牛云服务器完成静态资源的快速响应；使用quartz分布式线程池技术实现热帖排行；使用Caffeine对热帖排行请求进行缓存，通过Jmeter模拟100个用户线程测试缓存性能，加入缓存前服务器QPS为4.7，加入缓存后服务器QPS为188.
