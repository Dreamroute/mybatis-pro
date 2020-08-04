## mybatis-pro

## Links
- [中文文档](https://www.mdeditor.com/)
- [English Document](https://www.mdeditor.com/)

## 开发此框架的初衷
- 彻底告别单表SQL语句

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
### 简单查询
```$xslt
public interface User {

    // 无需在xml文件中编写sql，也无需使用注解@Select("xxx")的sql
    User findByNameAndPassword(String name, String password);
}
```
```$xslt
select * from user where name = #{name} and password = #{password}
```
### 其他
```$xslt

```