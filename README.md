# zeta-kotlin-module 基础开发框架(多模块版)

## 简介
zeta-kotlin-module是[zeta-kotlin](https://gitee.com/xia5800/zeta-kotlin)项目的多模块版本。

## 项目结构

| 模块              | 说明                                                        |
|-----------------|-----------------------------------------------------------|
| zeta-front      | zeta框架前端模块。存放前端项目启动类、Controller、配置文件等                     |
| zeta-common     | zeta框架通用模块模块，包含BaseController、BaseResult、BaseException等文件 |
| zeta-components | zeta框架组件模块，包含sa-token、redis、mybatis-plus、knife4j等框架的配置    |
| zeta-model      | zeta框架实体类模块，存放实体类、Dto、枚举等文件                               |
| zeta-service    | zeta框架业务模块。存放Service、Dao、xml等文件                           |


## 技术选型

| 技术           | 说明                                                        |
|--------------|-----------------------------------------------------------|
| spring boot  | 核心框架                                                      |
| sa-token     | 权限认证框架                                                    |
| mybatis-plus | [MyBatis扩展](https://doc.xiaominfo.com/)                   |
| Redis        | 分布式缓存数据库                                                  |
| knife4j      | [一个增强版本的Swagger 前端UI](https://doc.xiaominfo.com/knife4j/) |
| hutool       | [Java工具类大全](https://hutool.cn/docs/#/)                    |
| RedisUtil    | [最全的Java操作Redis的工具类](https://gitee.com/whvse/RedisUtil)   |
| EasyPoi      | [简单方便的导入导出Excel](https://gitee.com/lemur/easypoi)         |

## 配套项目

| 名称             | 说明                              | 项目地址                                                                                                   |
|----------------|---------------------------------|--------------------------------------------------------------------------------------------------------|
| zeta-web-layui | 本项目前端，使用vue3、vite5、typescript开发 | [gitee](https://gitee.com/xia5800/zeta-web-layui)  [github](https://github.com/xia5800/zeta-web-layui) |

## 后端访问地址
[http://localhost:8080/doc.html](http://localhost:8080/doc.html)

账号：zetaAdmin

密码：dDEWFk6fJKwZ55cL3zVUsQ==

## 已有功能

- 用户管理
- 角色管理
- 操作日志
- 登录日志
- 数据字典
- 文件管理
- websocket
- XSS防护
- Ip2region离线IP地址查询
- Excel导入导出
- 数据脱敏
- 定时任务（基于quartz)


## 写在后面

本人的初衷只是想用kotlin写一个简单的curd项目练练手，可是后面写着写着就偏离了初心，于是便有了这个开源项目。

就如同上面简介中说的，本项目相当“精简”。所以我尽量控制项目功能数量，不想给本项目添加太多的功能和业务代码。

可惜事与愿违，还是添加了几个我本不想添加的功能。因为并非所有功能都是你的业务所需要的，我个人认为需要用到的时候再去开发与集成才是最适合的。

使用别人开发好的功能，它不一定适合你的业务，你只能按照它制定的规则去使用，不能灵活更改成符合业务需要的。

所以，你已经是一个成熟的程序员了，需要啥功能自己去实现吧。（笑


## 友情链接 & 特别鸣谢

- lamp-boot：[https://github.com/zuihou/lamp-boot](https://github.com/zuihou/lamp-boot)
- sa-token [https://sa-token.dev33.cn/](https://sa-token.dev33.cn/)
- mybatis-plus：[https://baomidou.com/](https://baomidou.com/)
- knife4j：[https://doc.xiaominfo.com/](https://doc.xiaominfo.com/)
- Hutool：[https://hutool.cn/](https://hutool.cn/)
- EasyPoi：[http://www.wupaas.com/](http://doc.wupaas.com/docs/easypoi)
