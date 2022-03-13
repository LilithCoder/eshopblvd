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

停止/删除某一docker容器/镜像
$ docker stop ${CONTAINER_ID}
$ docker rm ${CONTAINER_ID}
$ docker image rm 
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

## 后台管理系统前端项目

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

## 创建公共库eshopblvd-common

放置公共的依赖、bean、工具类，每个微服务都来依赖公共库  

lombok依赖: @Data标注的实体类在编译期间自动加上getter、setter方法

#### Response 响应封装工具类

继承hashmap，key分别有code, msg, data

用来封装请求响应，功能包括快速构造500响应、200响应，响应内容的自定义

亮点：获取响应的时候可以通过泛型、fastjson的typereference来反序列化得到特定自定义类型的对象数据，使用TypeReference可以明确的指定[反序列化](https://so.csdn.net/so/search?q=%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96&spm=1001.2101.3001.7020)的类型

【面试】java泛型

[Java 泛型 | 菜鸟教程](https://www.runoob.com/java/java-generics.html)

【面试】Java泛型中T和问号（通配符）的区别

[Java泛型中T和问号（通配符）的区别_ikownyou的博客-CSDN博客_泛型通配符?和泛型t区别](https://blog.csdn.net/ikownyou/article/details/65630385)

【面试】java继承、重写override、重载overload

[Java 继承 | 菜鸟教程](https://www.runoob.com/java/java-inheritance.html)

[Java 重写(Override)与重载(Overload) | 菜鸟教程](https://www.runoob.com/java/java-override-overload.html)

## mybatis开发环境配置

TODO: 整合mybatis、page-helper实现分页功能[Mybatis 数据库物理分页插件 PageHelper - digdeep - 博客园](https://www.cnblogs.com/digdeep/p/4608933.html)，分页工具类、查询，common库里所有的工具类

- 引入mybatis相关依赖：mybatis、数据库驱动  

```xml
<!-- MyBatis-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.7</version>
</dependency>
<!--Mysql数据库驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.17</version>
</dependency>
<!-- https://mvnrepository.com/artifact/junit/junit -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

- mybatis - springboot整合

springboot作为ioc容器管理所有的组件，解决组件的动态依赖注入，控制事务

整合的目的是service业务逻辑层利用autowired自动装配dao层的组件来增删改查

官方docs：[GitHub - mybatis/spring-boot-starter: MyBatis integration with Spring Boot](https://github.com/mybatis/spring-boot-starter)

[SpringBoot整合MyBatis实战 | 包包的Tech Pool](https://www.baobao555.tech/posts/628531b3/)

引入springboot整合mybatis适配包(场景启动器)

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.2</version>
</dependency>
```

- 配置

在application.yml配置数据源

```yml
spring:
  datasource:
    username: root
    password: root
    url: jdbc:mysql://47.103.8.41:3306/eshopblvd_pms
    driver-class-name: com.mysql.cj.jdbc.Driver
```

mybatis配置

@MapperScan注解告诉mapper接口的位置`

`@MapperScan("com.hatsukoi.eshopblvd.product.dao")`

配置xml映射文件位置

```yml
mybatis:
  mapper-locations: classpath*:**/mapper/*.xml
```

- 接下俩就是实现service业务层、controller了

【面试】mybatis的优缺点？

【面试】# MyBatis 中#{}和${}区别

[MyBatis 中#{}和${}区别_w3cschool](https://www.w3cschool.cn/mybatis/mybatis-yta93bpj.html)

【面试】 @Mapper 与 @MapperScan 的区别

[@Mapper 与 @MapperScan 的区别_那年那些事儿-CSDN博客_mapper和mapperscan](https://blog.csdn.net/xiaojin21cen/article/details/103273172)

参考wiki：

[SpringBoot | 3.2 &#x6574;&#x5408;MyBatis](https://www.wcqblog.com/article/detail/212298947711074304)

[SpringBoot整合MyBatis实战 | 包包的Tech Pool](https://www.baobao555.tech/posts/628531b3/)

https://github.com/mybatis/spring-boot-starter/wiki/Quick-Start

[# Spring Boot入门系列（十一）如何整合Mybatis](https://mp.weixin.qq.com/s?__biz=MzAxMTY5NDAwOA==&mid=2651415559&idx=1&sn=8b8f6aeaaee93923fd0fd6f90fa8ac74&chksm=8040fed0b73777c62886f7d932fbac447b6f7b482cffd650fcacc93f9ae03e451a55d8a32554&scene=21#wechat_redirect)

## mybatis分页插件

官方docs：

[Mybatis-PageHelper/README_zh.md at master · pagehelper/Mybatis-PageHelper · GitHub](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/README_zh.md)

[GitHub - pagehelper/pagehelper-spring-boot: pagehelper-spring-boot](https://github.com/pagehelper/pagehelper-spring-boot)

- 引入依赖

```xml
<!-- pagehelper -->
<dependency>    
    <groupId>com.github.pagehelper</groupId>    
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.3.0</version>
</dependency>
```

- 使用分页功能

```java
//在查询之前开启分页，加了这个之后pagehelper 插件就会通过其内部的拦截器，将执行的sql语句，转化为分页的sql语句
PageHelper.startPage(pageNum, pageSize);pageNum页码、pageSize每页多少条

//之后进行查询操作将自动进行分页
List<PmsBrand> brandList = brandMapper.selectByExample(new PmsBrandExample());

//通过构造PageInfo对象获取分页信息，如当前页码，总页数，总条数
//当前导航分页的个数，navigatePages，举例：3 4 「5」 6 7
PageInfo<PmsBrand> pageInfo = new PageInfo<PmsBrand>(brandList, 5);
```

common基础库添加通用分页数据封装类utils.CommonPageInfo，将pagehelper分页查询结果封装为通用分页封装结果

使用例子：

```java
public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum,pageSize);
    PmsProductExample example = new PmsProductExample();
    example.createCriteria().andDeleteStatusEqualTo(0)
                .andBrandIdEqualTo(brandId);
    List<PmsProduct> productList = productMapper.selectByExample(example);
    return CommonPageInfo.convertToCommonPage(productList);
}
```

参考wiki：

[Spring Boot入门系列（十六）整合pagehelper，一秒实现分页功能！ - 云+社区 - 腾讯云](https://cloud.tencent.com/developer/article/1669256)

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
   
   3. generator.properties里配置下接下来需要生成逆向工程的微服务信息
      
      ```json
      serviceName=product 微服务名称
      databaseName=Pms 数据库前缀
      ```
      
      并运行Mybatis Generator
   
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

[Mybatis——Example用法 - 简书](https://www.jianshu.com/p/335960d6db6a)

## 验证环境是否搭建成功

```java
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public Brand selectBrandById(long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPageInfo<Brand> queryBrandsByShowStatus(int pageNum, int pageSize, byte showStatus) {
        PageHelper.startPage(pageNum, pageSize);
        BrandExample brandExample = new BrandExample();
        brandExample.createCriteria().andShowStatusEqualTo(showStatus);
        List<Brand> brandList = brandMapper.selectByExample(brandExample);
        return CommonPageInfo.convertToCommonPage(brandList);
    }
}
```

```java
@SpringBootTest
class EshopblvdProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        Brand brand = brandService.selectBrandById(1L);
        System.out.println("获取到的品牌是：" + brand);
        CommonPageInfo<Brand> brandCommonPageInfo = brandService.queryBrandsByShowStatus(2, 2, (byte) 1);
        System.out.println("目前能显示的第" + brandCommonPageInfo.getCurrPage() + "页的品牌是：" + brandCommonPageInfo.getListData());
    }
}
```

运行成功～！输出结果符合预期

## 分布式系统环境搭建

[Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba)

[spring-cloud-alibaba/README-zh.md at 2.2.x · alibaba/spring-cloud-alibaba · GitHub](https://github.com/alibaba/spring-cloud-alibaba/blob/2.2.x/README-zh.md)

SpringCloud Alibaba - Nacos：服务发现/注册、配置中心

~~SpringCloud - Ribbon：负载均衡~~

SpringCloud - Gateway：API网关

Apache Dubbo: RPC框架

SpringAlibaba - Sentinel: 服务容错（限流、降级、熔断）

SpringCloud - Sleuth:调用链监控

SpringCloud Alibaba - Seata：分布式事务解决方案

接下来就是将各个依赖引入common基础库中

引入依赖前，先看springcloud-alibaba中组件的版本兼容关系：随便选版本很容易启动项目时候报错

[版本说明 · alibaba/spring-cloud-alibaba Wiki · GitHub](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

#### SpringCloud-alibaba依赖引入

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.7.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### SpringCloud Alibaba-Nacos

接入注册中心，引入 Nacos Discovery Starter

```xml
 <dependency>
     <groupId>com.alibaba.cloud</groupId>
     <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
 </dependency>
```

1. 本地启动nacos-server，下载2.0.2[Releases · alibaba/nacos · GitHub](https://github.com/alibaba/nacos/releases)

直接运行nacos/bin/startup.sh，完事儿～

2. 通过docker启动nacos-server，来到我们的centos，先拉取docker镜像

https://hub.docker.com/r/nacos/nacos-server

```shell
无视这条
$ docker pull nacos/nacos-server
```

快速启动docker容器

```shell
$ docker run --name nacos-server -e MODE=standalone -p 8848:8848 -p 9849:9849 -p 9848:9848 -d nacos/nacos-server:2.0.3
$ docker update nacos-server --restart=always
```

- 注意⚠️：Nacos2.0版本相比1.X新增了gRPC的通信方式，因此需要增加2个端口。新增端口是在配置的主端口(server.port)基础上，进行一定偏移量自动生成。
- 

最后别忘了去安全组暴露下8848端口(主端口)，9848 9849

现在可以通过`服务器地址:8848/nacos`访问注册中心啦，用户名/密码均为nacos

需要注册的微服务下yml里配置：

```yml
  cloud:
    nacos:
      discovery:
        server-addr: 服务器地址:8848
```

使用 @EnableDiscoveryClient 注解开启服务注册与发现功能

```java
@SpringBootApplication
 @EnableDiscoveryClient
 public class ProviderApplication {

     public static void main(String[] args) {
         SpringApplication.run(ProviderApplication.class, args);
     }

     @RestController
     class EchoController {
         @GetMapping(value = "/echo/{string}")
         public String echo(@PathVariable String string) {
                 return string;
         }
     }
 }
```

为微服务起名，这样才能被注册

```yml
   application:
    name: eshopblvd-product
```

服务一启动，就能在服务列表中看到我们的微服务了

![](./docs/assets/1.png)

#### springboot整合dubbo

[Dubbo3 简介 | Apache Dubbo](https://dubbo.apache.org/zh/docs/introduction/)

[Dubbo 融合 Nacos 成为注册中心](https://nacos.io/zh-cn/docs/use-nacos-with-dubbo.html)

[Dubbo 外部化配置 - 小马哥的技术博客](https://mercyblitz.github.io/2018/01/18/Dubbo-%E5%A4%96%E9%83%A8%E5%8C%96%E9%85%8D%E7%BD%AE/)

[版本说明 · alibaba/spring-cloud-alibaba Wiki · GitHub](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

[注解配置 | Apache Dubbo](https://dubbo.apache.org/zh/docs/references/configuration/annotation/)

1. 依赖导入dubbo-starter、其他依赖

https://github.com/apache/dubbo-spring-boot-project/blob/0.2.x/README_CN.md

版本选择要按照上面的版本说明wiki来，不然很容易报错

```xml
        <!--Nacos注册中心-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>com.alibaba.spring</groupId>
                    <artifactId>spring-context-support</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.dubbo/dubbo -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.13</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba.spring</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>1.0.11</version>
        </dependency>
```

2. 配置provider

假设 Nacos Server 同样运行在服务器 `10.20.153.10` 上，并使用默认 Nacos 服务端口 `8848`，您只需将 `dubbo.registry.address` 属性调整如下：

```yml
## Nacos registry address
dubbo.registry.address = nacos://10.20.153.10:8848
```

完成业务服务层的逻辑，暴露服务

```java
在服务上加注解
@com.alibaba.dubbo.config.annotation.Service
```

在主程序开始基于注解的dubbo功能

```java
@EnableDubbo
```

这样配置的服务就会注册到nacos了

![](./docs/assets/2.png)

3. 编写接口&配置consumer

common基础库里定义好provider service的接口（类似于远程服务接口的声明、签名），消费者引用远程provider服务时用，基础库service包下，后续会从注册中心自动发现provider地址

```java
@Reference
ProviderTest providerTest;
```

配置好yml

在主程序开始基于注解的dubbo功能

```java
@EnableDubbo
```

demo具体例子可参考：[GitHub - 7Savage/DubboStudy: 尚硅谷Dubbo学习](https://github.com/7Savage/DubboStudy)

[springboot-dubbo: springboot整合dubbo，yml配置dubbo，完全抛弃xml配置 - Gitee.com](https://gitee.com/chrismayday/springboot-dubbo/tree/master)

[Dubbo 融合 Nacos 成为注册中心](https://nacos.io/zh-cn/docs/use-nacos-with-dubbo.html)

[官方wiki](https://dubbo.apache.org/zh/docs/v3.0/references/configuration/references/metadata/)

最终实现：

[dubbo-consumer-demo](./dubbo-consumer-demo)

[dubbo-provider-demo](./dubbo-provider-demo)

demo的逻辑是provider提供服务，返回字符串“You get response from provider!”，consumer新增一个controller，调用cosumer的服务，其中远程调用了provider的服务，最终返回provider服务返回的字符串

踩坑记录：

1. com.alibaba.dubbo.rpc.RpcException: Fail to start server(url: dubbo://xxx.xxx.xxx.xxx:20880/, Failed to bind NettyServer on /xxx.xxx.xxx.xxx:20880, cause: Failed to bind to: /0.0.0.0:20880

解决：报错信息显示绑定到本机的20880端口失败，本地绑定的地址已经被使用，将dubbo.protocol.port的端口号其他非占用端口

2. com.alibaba.dubbo.rpc.RpcException: No provider available from registry xxx for service

解决：provider的接口我定义在基础库了，基础库的包路径和实际provider的包路径不同了，由于provider是根据service的报路径来命名的，例如providers:com.hatsukoi.eshopblvd.provider.Service.ProviderService，所以根因就是consumer在引用provider的接口时发现nacos中没有这个命名的服务，解决方案就是provider的service实现和consumer引用reference就直接用导入common库包下的接口

以后服务接口声明只放在common基础库了

最终，问题都解决了～nacos成功服务发现，返回结果符合预期

![](./docs/assets/5.png)

![](./docs/assets/4.png)

### Nacos配置中心

官方文档: [spring-cloud-alibaba/readme-zh.md at 2.2.x · alibaba/spring-cloud-alibaba · GitHub](https://github.com/alibaba/spring-cloud-alibaba/blob/2.2.x/spring-cloud-alibaba-examples/nacos-example/nacos-config-example/readme-zh.md)

先导入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

springboot 规定bootstrap.properties优先于application.properties加载

在应用的 /src/main/resources/bootstrap.properties 配置文件中配置 Nacos Config 元数据

```properties
spring.application.name=nacos-config-example
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
```

```java
@Value注解可以直接获取application.properties中的配置
@Value("${spring.application.name}")
private String name;
```

目的是在配置中心动态改配置，而不是在本地改完配置文件重新打包部署

在nacos创建dataid为eshopblvd-coupon.properties的配置，默认以服务名来命名

![](./docs/assets/7.png)

为了nacos中修改的配置能够被动态地刷新，在控制器应用加上注解@RefreshScope

```java
@RefreshScope
@RestController
@RequestMapping("/coupon")
public class CouponController {
    // ...
}
```

再根据`@Value("${xxx}")`来获取配置的值

如果配置中心和当前应用的配置文件中都配置了相同的项，优先使用配置中心的配置

#### 命名空间

作用是为了配置隔离

- 基于环境进行隔离

默认：public(保留空间)；默认新增的所有配置都在public空间。  

用途举例：开发，测试，生产：利用命名空间来做环境隔离。  

注意：在bootstrap.properties；配置上，需要使用哪个命名空间下的配置

```properties
spring.cloud.nacos.config.namespace=9de62e44-cd2a-4a82-bf5c-95878bd5e871
```

9de62e44-cd2a-4a82-bf5c-95878bd5e871为命名空间自动生成的id

![](./docs/assets/8.png)

- 基于微服务之间进行隔离

当然每一个微服务之间为了互相隔离配置，每一个微服务也可以创建自己的命名空间，只加载自己命名空间下的所有配置  

#### 配置集

一组相关或者不相关的配置项的集合称为配置集，类似于一个配置yml文件

#### 配置集ID

类似配置文件名，在nacos中就是Data ID

#### 配置分组

默认所有的配置集都属于：DEFAULT_GROUP，后续可以根据业务来定制

![](./docs/assets/9.png)

```properties
指定配置的group
spring.cloud.nacos.config.group=CUSTOM_GROUP
```

本项目中的使用：每个微服务创建自己的命名空间，使用配置分组group区分环境，dev，test，prod，这些在bootstrap.properties都可以制定

![](./docs/assets/10.png)

#### 同时加载多个配置集

当微服务数量很庞大时，将所有配置都书写到一个配置文件中，显然不是太合适。对此我们可以将配置按照功能的不同，拆分为不同的配置文件。可以将数据源有关的配置写到一个配置文件中，框架有关的写到另外一个配置文件中

微服务任何配置信息，任何配置文件都可以放在配置中心中，只需要在bootstrap.properties说明加载配置中心中哪些配置文件即可

datasource.yml、mybatis.yml、other.yml为nacos的配置集

bootstrap.properties举例:

```yml
spring.application.name=gulimall-coupon

spring.cloud.nacos.config.server-addr=127.0.0.1:8848
spring.cloud.nacos.config.namespace=1986f4f3-69e0-43bb-859c-abe427b19f3a

spring.cloud.nacos.config.ext-config[0].data-id=datasource.yml
spring.cloud.nacos.config.ext-config[0].group=dev
spring.cloud.nacos.config.ext-config[0].refresh=true

spring.cloud.nacos.config.ext-config[1].data-id=mybatis.yml
spring.cloud.nacos.config.ext-config[1].group=dev
spring.cloud.nacos.config.ext-config[1].refresh=true

spring.cloud.nacos.config.ext-config[2].data-id=other.yml
spring.cloud.nacos.config.ext-config[2].group=dev
spring.cloud.nacos.config.ext-config[2].refresh=true
```

获取配置项的值使用这两个注解：`@Value，@ConfigurationProperties`

微服务只需要保留bootstrap.properties，一启动自动来配置中心获取配置，可以将所有配置都放在配置中心

## API网关

作用：

1. 动态地将请求路由到各个微服务，能从注册中心实时感知服务的上/下线

2. 鉴权、监控、限流、日志输出、统一功能的处理

### SpringCloud Gateway

官方wiki：[Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

- 路由 (route)

路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组 Filter 组成。如果断言路由为真，则说明请求的 URL 和配置匹配

- 断言 (predicate)

Java8 中的断言函数。Spring Cloud Gateway 中的断言函数输入类型是 Spring5.0 框
架中的 ServerWebExchange。Spring Cloud Gateway 中的断言函数允许开发者去定义匹配
来自于 http request 中的任何信息，比如请求头和参数等

- 过滤器 (filter)

一个标准的 Spring webFilter。过滤器 Filter 将会对请求和响应进行修改
处理

### 工作流程

<img title="" src="./docs/assets/11.png" alt="" width="309">

客户端发送请求给网关，通过 HandlerMapping 判断是否请求满足某个路由，满足就发给网关的 WebHandler。这个 WebHandler 将请求交给一个过滤器链，然后请求再到达目标服务

新建网关模块，引入gateway依赖以及common库 

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

网关需要注册到nacos，需要发现其他服务的位置，添加服务注册发现注解

```java
@EnableDiscoveryClient
```

配置文件照常配置nacos注册中心地址、应用名称、配置中心地址

启动服务时可能会报错，请务必确认springboot, springcloud的版本映射关系正确

参考：[版本说明 · alibaba/spring-cloud-alibaba Wiki · GitHub](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

报错显示缺少负载均衡的依赖，添加相关依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

遇到报错：Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource，依赖中有mybatis

DataSourceAutoConfiguration会自动加载.可以排除此类的自动配置，在启动类中加入

```java
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
```

ok~服务启动，网关配置完成✅注册中心里已经有eshopblvd-gateway服务了

## 前端ES6 & Vue

[ES6语法&Vue基础知识总结](./docs/es6_vue.pdf)

## 业务开发

### 商品服务

#### 三级分类查询

插入所有的商品分类数据pms_category.sql 

功能#1：树形展示三级分类：查出所有的分类以及其子分类，并且以父子树形结构组装起来，最终能够展示在后台管理系统以及电商网站

- 查出所有的一级分类，可以根据属性parent_cid、cat_level来判断分类层级，parent_cid为0或者cat_level为1表示目前分类为第一级分类

- 为分类实体添加children属性，由于Category实体类实现了Serilizable接口，Category对象就可以被序列化，其中children不需要被序列化，换句话说就是仅存于调用者的内存中而不会写到磁盘里持久化，那么我们就可以在children字段前添加关键字transient`private transient List<Category> children;`，序列化对象的时候，这个属性就不会序列化到指定的目的地中
  
  【面试】[Java transient关键字使用小记 - Alexia(minmin) - 博客园](https://www.cnblogs.com/lanxuezaipiao/p/3369962.html)

- 递归树形结构：在所有分类中查找到所有一级分类的子分类，继续遍历递归找子分类的子分类直到叶层级分类，并用setChildren组装树结构，并排序
  
  【面试】这个递归查找的时间复杂度是？有没有优化空间？

- 前端：后台管理系统来到商品管理->分类管理的时候，created()生命周期钩子函数里发起请求获取三级分类

- 跨源请求：同源策略
  
  现在前端发的请求都是走API网关了，由于API网关的端口为80，和localhost:8080不同源，浏览器就会因为同源策略拦截跨源请求（同源策略：是指协议、域名、端口都要相同，其中有一个不同都会产生跨域）

- 跨域流程：
  
  ![](./docs/assets/31.png)

- 解决跨域：
  
  【面试】如何解决浏览器同源策略下无法跨域请求的问题？
  
  发预检请求问能不能跨域，服务器响应能跨域即可，即在预检请求的响应里配置相关的响应头
  
  ![](./docs/assets/32.png)
  
  所有请求响应都需要添加这些字段，所以在网关里添加一个过滤器去完成这个工作
  
  springboot提供了corswebfilter，将其放入容器就能起作用，在网关新建一个配置类CorsConfig用来做过滤，允许所有的请求跨域。
  
  ```java
  @Configuration
  public class CorsConfig {
      /**
       * Bean注解使其加入容器中
       * @return
       */
      @Bean
      public CorsWebFilter corsWebFilter() {
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          // 跨域配置，*表示允许全部
          CorsConfiguration corsConfiguration = new CorsConfiguration();
          corsConfiguration.addAllowedHeader("*");
          corsConfiguration.addAllowedMethod("*");
          corsConfiguration.addAllowedOrigin("*");
          // 是否允许携带cookie进行跨域
          corsConfiguration.setAllowCredentials(true);
          // /**表示任意路径都要跨域配置
          source.registerCorsConfiguration("/**", corsConfiguration);
          return new CorsWebFilter(source);
      }
  }
  ```

- API网关添加商品服务的路由
  
  断言到路径匹配的，将其路径重写，去除/api
  
  ```yml
  spring: 
    cloud:
      gateway:
        routes:
          - id: product-route
            uri: lb://eshopblvd-product
            predicates:
              - Path=/api/product/**
            filters:
              - RewritePath=/api/(?<segment>.*),/$\{segment}
  ```

- 验证
  
  刷新网页～
  
  先发送预检请求：
  
  <img src="./docs/assets/34.png" title="" alt="" width="650">
  
  网关能够发现商品服务的地址，所以发给88端口API网关的请求成功负载均衡路由转发给商品服务，并返回三级分类的树形数据
  
  ![](./docs/assets/35.png)
  
  接下来就是通过vue展示出三级分类的内容了，这里不细展开了，直接去看前端代码就好
  
  ![](./docs/assets/37.png)

#### 三级分类删除/添加/修改

只有当前分类没有子分类的时候，才可以delete

只有当前分类是一级或二级分类的时候，才可以append

所有层级的分类都可以edit

- 批量删除
  
  这次批量删除是post请求
  
  @RequestBody:获取请求体，必须发送POST请求
  
  SpringMVC自动将请求体的数据（json），转为对应的对象
  
  ```java
  @RequestMapping("/delete")
  public CommonResponse deleteCategories(@RequestBody List<Long> catIds) {
      // ...    
  }
  ```
  
  这里的删除不是物理删除，而是逻辑删除，字段show_status为0代表这个分类被删除了，为1反之
  
  mybatis没有mybatis-plus那样逻辑删除的注解`@TableLogic`，这次就先不搞逻辑删除了
  
  删除逻辑是先生成example后，再根据这个criteria去删除数据
  
  ```java
      @Override
      public void removeCategoriesByIds(List<Long> catIds) {
          // TODO: 先检查当前删除的分类是否已经没有子分类或者是否被其他地方引用，没有才可以删
          // 根据catIds批量删除分类
          CategoryExample example = new CategoryExample();
          example.createCriteria().andCatIdIn(catIds);
          categoryMapper.deleteByExample(example);
      }
  ```
  
  成功删除！
  
  ![](./docs/assets/38.png)