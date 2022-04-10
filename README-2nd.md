# 商城业务概览

![](./docs/assets/85.png)

- nginx

  反向代理、前端静态资源部署（动静分离）

  动：服务器需要处理的请求

  静：图片，js、css等静态资源(以实际文件存在的方式)

  只有动态请求会来到微服务，这样可以分担微服务的压力

- 网关

  鉴权、限流、认证

# 商城首页

商品微服务

### 导入依赖

前端使用了thymeleaf开发，因此要导入该依赖,并且为了改动页面实时生效导入devtools

```xml
<!--页面修改不重启服务器实时更新-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
	  <optional>true</optional>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

springboot static目录下放置静态资源，templates里存放html页面文件

开发期间加上配置来禁止thymeleaf缓存

```yaml
spring:  
  thymeleaf:
    cache: false
```

### 新增【接口】首页初始请求

目标获得的三级分类的数据模型

```json
{
  catelog1Id: {
    catelog1Id: xxx,
    catelog1Name: xxx,
    catelog2List: [
      {
        catelog2Id: xxx,
        catelog2Name: xxx,
        catelog3List: [
          {
            catelog3Id: xxx,
		        catelog3Name: xxx,
          },
          ...
        ],
      },
      ...
    ],
  },
  ...
}
```



# 商城检索页

检索微服务
