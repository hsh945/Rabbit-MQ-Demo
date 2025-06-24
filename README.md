## RabbitMQ 利用死信队列来实现延迟消息

基于 TTL（Time-To-Live）+ 死信队列（DLX）的方式来实现延迟消息

首先消息会被推送到普通队列中，该消息设置了TTL，当TTL到期未被消费掉，则会自动进入死信队列（DLQ）中，由死信队列消费者消费，来达到延迟消息的效果

---- 
#### 首先让我们来安装 Rabbit MQ 服务端
由于服务器基本都是使用 Linux 系统
以下介绍常见的 Ubuntu/Debian 和 CentOS 系统安装 RabbitMQ 的方法

> Ubuntu / Debian
```bash
# 添加 Erlang 仓库（RabbitMQ 依赖）
curl -fsSL https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc | sudo apt-key add -
echo "deb https://dl.bintray.com/rabbitmq-erlang/debian $(lsb_release -sc) erlang-23.x" | sudo tee /etc/apt/sources.list.d/rabbitmq.list

# 添加 RabbitMQ 仓库
echo "deb https://dl.bintray.com/rabbitmq/debian $(lsb_release -sc) main" | sudo tee /etc/apt/sources.list.d/rabbitmq.list

# 安装 RabbitMQ
sudo apt-get update
sudo apt-get install rabbitmq-server

# 启动服务
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server  # 设置开机自启
```

```bash
# 然后是开放 Linux 的防火墙端口

# Ubuntu/Debian
sudo ufw allow 5672/tcp # 5672 是 RabbitMQ 服务端口
sudo ufw allow 15672/tcp # 15672 是 RabbitMQ 的后台管理端口

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=15672/tcp
sudo firewall-cmd --reload
```
----

> CentOS 7

```bash
# 添加Erlang仓库
curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash

# 安装兼容版本的Erlang（例如25.3.2）
sudo yum install -y erlang-25.3.2.8-1.el7

curl -s https://packagecloud.io/install/repositories/rabbitmq/rabbitmq-server/script.rpm.sh | sudo bash
sudo yum install -y rabbitmq-server-3.12.7-1.el7

# 启动RabbitMQ服务
sudo systemctl start rabbitmq-server

# 设置开机自启
sudo systemctl enable rabbitmq-server

# 检查服务状态
sudo systemctl status rabbitmq-server
```

```bash
# 开放AMQP协议端口（默认5672）
sudo firewall-cmd --permanent --add-port=5672/tcp

# 开放管理界面端口（默认15672）
sudo firewall-cmd --permanent --add-port=15672/tcp

# 重新加载防火墙规则
sudo firewall-cmd --reload
```
----

**到这里你已经安装完成了， 然后该开始初始化 Rabbit MQ 设置一些账号相关的配置了**


```bash
# 安装完后用 rabbitmqctl 命令来查看服务状态
sudo rabbitmqctl status
```

```bash
# 然后是安装后台管理页面，虽然都是用代码来操作MQ，但是业务上难免是需要看MQ运行状态的，所以建议是装上可视化界面
sudo rabbitmq-plugins enable rabbitmq_management
```

```bash
# 接下来是创建账号，创建虚拟主机，给用户授权，这些账号是要配置在 application.properties 中的

# 创建新用户
sudo rabbitmqctl add_user yourUserName yourPassword # 替换成你的账号密码

# 创建虚拟主机
sudo rabbitmqctl add_vhost yourvhost # 替换成你的虚拟主机

# 为用户授予虚拟主机的权限
sudo rabbitmqctl set_permissions -p yourvhost yourUserName ".*" ".*" ".*"

# 查看用户权限
sudo rabbitmqctl list_user_permissions yourUserName

# 设置 yourUserName 为管理员角色
# 这一步很重要，你要设置成管理员，后面才能登录后台管理页面
sudo rabbitmqctl set_user_tags yourUserName administrator

# 设置完后你就可以通过浏览器 访问 http://localhost:15672/ 登录后台（自己替换成 Linux 的IP)
```

> 顺带提一嘴，权限级别有多级, administrator 为最高权限

- none：无特殊权限（默认角色）
- management：可以访问管理 API 和 Web 界面
- policymaker：包含management权限，还能管理策略和参数
- monitoring：包含management权限，还能查看节点和集群信息
- administrator：最高权限，可管理所有资源和用户

----

**接下来接入到 Spring 工程里**

**properties**
```yml
spring.application.name=RabbitMQDemo

spring.rabbitmq.host=localhost # 替换为你的RabbitMQ服务IP
spring.rabbitmq.port=5672
spring.rabbitmq.username=yourUserName
spring.rabbitmq.password=yourPassword
```
