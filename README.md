## mybatis-pro

- [中文文档](https://github.com/Dreamroute/mybatis-pro/wiki/%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3/)
- [English Document](https://github.com/Dreamroute/mybatis-pro/wiki/English-Document/)

## 开发此框架的初衷
- 彻底告别单表SQL语句
- 毫无无底线的优雅查询
- 钢铁直男的终极选择

## 框架的功能
- 包含单表增删改查方法
- 根据Mapper方法名自动生成SQL
- 内置物理分页插件
- 内置乐观锁插件
- 内置优美SQL语句打印插件
- 与通用Mapper、MyBatis-Plus等三方框架兼容

## 设计原则
  框架本身依赖mybatis-spring，仅在应用启动时织入框架逻辑，不破坏任何mybatis核心，原则上可以兼容任何mybatis版本
  
## 版本要求
  - JDK 1.8+

## 使用方式
- SpringBoot
```
    <dependency>
        <groupId>com.github.dreamroute</groupId>
        <artifactId>mybatis-pro-boot-starter</artifactId>
        <version>latest version</version>
    </dependency>
```
- Spring MVC
```$xslt
    
```

## 功能展示

- 单表条件查询
```$xslt
public interface UserMapper {

    // 这是一个根据用户名、密码查询单个用户的查询，方法名只需要以findBy打头
    User findByNameAndPassword(String name, String password);

}
```
你无需在xml文件中编写sql，也无需使用注解@Select("xxx")的sql，框架自动根据方法名：

**findByNameAndPassword**切割成`findBy`,`NameAndPassword`，组成如下sql：

`select * from user where name = #{name} and password = #{password}`