# eshopblvd

## 概述

电商项目

## 云服务器

添置一台阿里云ECS服务器(CentOS 7.9)，安全组设置中添加常用端口：80(http), 22(ssh), 3306(mysql), 6379(redis)等，重置实例密码后重启服务器

通过ssh建立会话远程连接云服务器

```shell
$ ssh root@远程服务器公网ip地址
根据prompt输入实例密码
```

localhost免密码登陆远程云服务器

```shell
本地: 生成本地公钥和私钥，并通过scp指令将公钥安全拷贝到远程服务器.ssh目录下
$ ssh-keygen -t rsa
$ scp id_rsa.pub root@远程服务器公网ip地址:~/.ssh/id_rsa.pub

远程: 将公钥内容追加到~/.ssh/authorized_keys中
$ cd ~/.ssh && cat id_rsa.pub >> authorized_keys

这样下次ssh登陆服务器时就不用输入密码
```

ssh连接长时间不操作保持不断开

```shell
配置下服务器的/etc/ssh/sshd_config文件
Host *
    # 断开时重试连接的次数
    ServerAliveCountMax 600
    # 每隔30秒自动发送一个空的请求以保持连接
    ServerAliveInterval 10

重启一下ssh服务
$ systemctl restart sshd
```

## Docker

### Docker上手

使用Docker虚拟化容器技术来隔离软件(mysql, redis, elasticsearch, rabbitmq等)的运行时环境

centos上安装docker并启动，可参考文档

[Install Docker Engine on CentOS | Docker Documentation](https://docs.docker.com/engine/install/centos/)

docker相关指令

```shell
启动docker
$ systemctl start docker

检查docker镜像
$ docker images

docker开机自动启动
$ systemctl enable docker

查看正在运行的docker容器
$ docker ps

查看所有的docker容器
$ docker ps -a

重启某一docker容器
$ docker restart xxx
```

配置docker阿里云镜像加速

```bash
mkdir -p /etc/docker
tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://uizv1b1t.mirror.aliyuncs.com"]
}
EOF
systemctl daemon-reload
systemctl restart docker
```

### docker容器启动mysql

```shell
拉取镜像
$ docker pull mysql:5.7

启动mysql容器，3306:3306将容器3306端口映射到主机3306端口
-v表示容器内部目录挂载到centos的对应目录（日志、持久化数据、配置）
$ docker run -p 3306:3306 --name mysql \
-v /mount_data/mysql/log:/var/log/mysql \
-v /mount_data/mysql/data:/var/lib/mysql \
-v /mount_data/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7

进入mysql容器内部控制台
$ docker exec -it mysql /bin/bash
$ exit;
```

mysql配置修改

```shell
$ cd /mount_data/mysql/conf
$ vi my.cnf

插入下面的配置，设置编码方式
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

$ docker restart mysql

设置启动docker时，即运行mysql
$ docker update mysql --restart=always
```

### docker容器启动redis

拉取镜像、启动容器

```shell
$ docker pull redis
$ mkdir -p /mount_data/redis/conf
$ touch /mount_data/redis/conf/redis.conf

配置redis启用AOF持久化方式
$ echo "appendonly yes"  >> /mydata/redis/conf/redis.conf

$ docker run -p 6379:6379 --name redis -v /mount_data/redis/data:/data \
-v /mount_data/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis redis-server /etc/redis/redis.conf

设置启动docker时，即运行redis
$ docker update redis --restart=always
```

进入redis容器的client命令行，这样就可以快乐地玩耍redis命令了

```shell
$ docker exec -it redis redis-cli
```

## 项目结构初始化

商品服务、用户服务、订单服务、优惠券服务、仓储服务、后台管理系统

最外层通过module来聚合所有微服务

每个微服务对应的数据库的建立

TODO：所有数据库表结构的解析

## 后台系统

前端系统Vue  [GitHub - weilingao/eshopblvd-admin-vue: 电商网站后台管理系统](https://github.com/weilingao/eshopblvd-admin-vue)

https://cn.vuejs.org/

[Vue CLI](https://cli.vuejs.org/#getting-started)

依赖安装&脚手架初始化&启动

```shell
$ npm install -g @vue/cli
$ npm install vue
$ npm install webpack -g
$ npm install -g @vue/cli-init
$ vue init webpack eshopblvd-admin-vue
$ npm run dev[![@weilingao](https://avatars.githubusercontent.com/u/43017798?s=40&v=4)](https://github.com/weilingao)
```

vue脚手架初始化工程，结合vue-router,element-ui，完成基础的侧菜单和展示内容的路由

## mybatis代码生成器

MyBatis Generator可以通过配置生成基本的crud代码，包含了数据库表对应的实体类，Mapper接口类，XML映射文件和Example对象等

1. resource中创建generatorConfig.xml，用来配置数据库连接，生成的pojo、mapper.xml、dao接口存放的位置，配置数据库表
   
   - context的targetRuntime属性设置为MyBatis3，会有生成Example相关的代码和方法，反之可用MyBatis3Simple
   
   - Example类指定如何构建一个动态的where子句，属于*QBC*（Query By Criteria）风格的增删改查，和mybatis-plus的queryWrapper类似
     
     ```java
     TestTableExample example = new TestTableExample();
     example.createCriteria().andField1EqualTo(5);
     等于
     where field1 = 5
     ```
   
   - defaultModelType="flat"目的是使每个表只生成一个实体类
   
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE generatorConfiguration
           PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
           "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
   
   <!-- 配置生成器 -->
   <generatorConfiguration>
       <properties resource="generator.properties"/>
       <!-- 配置对象环境 -->
       <context id="MySqlContext" targetRuntime="MyBatis3" defaultModelType="flat">
           <!-- 配置起始与结束标识符 -->
           <property name="beginningDemiliter" value="`"/>
           <property name="endingDemiliter" value="`"/>
           <!-- 配置注释生成器 -->
           <commentGenerator>
               <property name="suppressDate" value="true"/>
               <property name="addRemarkComments" value="true"/>
           </commentGenerator>
           <!--生成mapper.xml时覆盖原文件-->
           <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin" />
           <!-- 为模型生成序列化方法-->
           <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
           <!-- 为生成的Java模型创建一个toString方法 -->
           <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
           <!--配置数据库连接-->
           <jdbcConnection driverClass="${jdbc.driverClass}"
                           connectionURL="${jdbc.connectionURL}"
                           userId="${jdbc.userId}"
                           password="${jdbc.password}">
               <!--解决mysql驱动升级到8.0后不生成指定数据库代码的问题-->
               <property name="nullCatalogMeansCurrent" value="true" />
           </jdbcConnection>
           <!--指定生成model的路径-->
           <javaModelGenerator targetPackage="com.hatsukoi.eshopblvd.${serviceName}.entity" targetProject="eshopblvd-mbg/src/main/java"/>
           <!--指定生成mapper.xml的路径-->
           <sqlMapGenerator targetPackage="dao.${serviceName}" targetProject="eshopblvd-mbg/src/main/resources"/>
           <!--指定生成mapper接口的的路径-->
           <javaClientGenerator targetPackage="com.hatsukoi.eshopblvd.${serviceName}.dao"
                                targetProject="eshopblvd-mbg/src/main/java"/>
           <!-- 配置数据库表，生成全部表tableName设为% -->
           <table tableName="%">
               <generatedKey column="id" sqlStatement="Mysql"/>
           </table>
       </context>
   </generatorConfiguration>
   ```
   
   2. 引入相关依赖
   
   ```xml
           <dependency>
               <groupId>org.mybatis</groupId>
               <artifactId>mybatis</artifactId>
               <version>3.5.5</version>
           </dependency>
           <!--MyBatis分页插件-->
           <dependency>
               <groupId>com.github.pagehelper</groupId>
               <artifactId>pagehelper-spring-boot-starter</artifactId>
               <version>1.3.0</version>
           </dependency>
           <!--集成druid连接池-->
           <dependency>
               <groupId>com.alibaba</groupId>
               <artifactId>druid-spring-boot-starter</artifactId>
               <version>1.1.23</version>
           </dependency>
           <!-- MyBatis 生成器 -->
           <dependency>
               <groupId>org.mybatis.generator</groupId>
               <artifactId>mybatis-generator-core</artifactId>
               <version>1.4.0</version>
           </dependency>
           <!--Mysql数据库驱动-->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>8.0.17</version>
           </dependency>
       </dependencies>
   ```
   
   3. 运行Mybatis Generator
   
   ```java
   /**
    * 用MyBatisGenerator生成逆向工程
    */
   public class EshopblvdMbgApplication {
       public static void main(String[] args) throws IOException, XMLParserException, SQLException, InterruptedException, InvalidConfigurationException {
           // 告警信息
           List<String> warnings = new ArrayList<>();
           // 当生成的代码重复时，覆盖原代码
           boolean overwrite = true;
           // 解析读取MybatisGenerator配置文件
           InputStream inputStream = EshopblvdMbgApplication.class.getResourceAsStream("/generatorConfig.xml");
           ConfigurationParser configurationParser = new ConfigurationParser(warnings);
           Configuration configuration = configurationParser.parseConfiguration(inputStream);
           inputStream.close();
   
           DefaultShellCallback callback = new DefaultShellCallback(overwrite);
           // 创建 MBG
           MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
           // 执行生成代码
           myBatisGenerator.generate(null);
           // 输出警告信息
           for (String warning : warnings) {
               System.out.println(warning);
           }
       }
   }
   ```

遇到的问题

1. The content of element type "context" must match "(property*,plugin*,commen...意思是代码生成配置文件要按照错误信息给出的顺序来写

2. com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure. javax.net.ssl.SSLHandshakeException: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)
   
   一开始怀疑服务器端口没开放，查看mysql 3306端口是否打开`netstat -an|grep 3306`
   
   可能是mysql-connecter的驱动版本与数据库不一致的问题？这个也排除了
   
   看来是JDK8版本过高引起MySQL连接失败的，在jdbc连接url后拼接参数useSSL=false，问题解决～

3. 生成文件有数据库名字前缀，想要删除
   
   在配置文件中加上`<domainObjectRenamingRule searchString="^${databaseName}" replaceString=""/>`
   
   



参考文档：

https://segmentfault.com/a/1190000038622464

[参考文档](http://www.macrozheng.com/#/architect/mall_arch_01?id=mybatis-generator-%e9%85%8d%e7%bd%ae%e6%96%87%e4%bb%b6)

[MyBatis学习笔记（五）：代码生成器 | 程序人生](https://zjxkenshine.github.io/2018/03/29/MyBatis%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%88%E4%BA%94%EF%BC%89%EF%BC%9A%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8/)

[Example类使用说明 · Java 开源项目中文文档 · 看云](https://www.kancloud.cn/wizardforcel/java-opensource-doc/153016)

## mybatis分页插件

引入依赖

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
</dependency>
```

使用分页功能

```java
//在查询之前开启分页
PageHelper.startPage(pageNum, pageSize);

//之后进行查询操作将自动进行分页
List<PmsBrand> brandList = brandMapper.selectByExample(new PmsBrandExample());

//通过构造PageInfo对象获取分页信息，如当前页码，总页数，总条数
//当前导航分页的个数，navigatePages，举例：3 4 「5」 6 7
PageInfo<PmsBrand> pageInfo = new PageInfo<PmsBrand>(brandList, 5);
```
