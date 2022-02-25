## MyBatis-Pro
### [优雅的解决MyBatis单表的一切问题]

<p align="left">
    <img src="https://gitee.com/Dreamroute/mybatis-pro/raw/master/mybatis.png">
</p>

## 地址
- [GitHub](https://github.com/Dreamroute/mybatis-pro)
- [Gitee](https://gitee.com/Dreamroute/mybatis-pro)


## 使用文档

- [完整中文文档](https://github.com/Dreamroute/mybatis-pro/wiki)

## 开发此框架的初衷
- 让单表查询更加优雅
- 基本告别单表SQL语句
- 拥有JPA的单表查询优势
- 只兼容mysql和H2
- 规范造就无需重复造轮子
- 万千钢铁直男的终极选择

## 框架的功能
- 包含单表增删改查方法
- 根据Mapper方法名自动生成SQL，无需编写sql语句
- 与通用Mapper、MyBatis-Plus等三方框架兼容（三者选其一即可，功能类似）
- 【可选】内置枚举类型处理器，自动优雅解决枚举类型的转换
- 【可选】内置泛型Service，简化重复造轮子
- 【可选】内置逻辑删除，可放心大胆的在生产环境进行delete操作
- 【可选】[分页插件](https://github.com/Dreamroute/pager) 支持单表、多表关联查询
- 【可选】[sql打印插件](https://github.com/Dreamroute/sqlprinter) 已经用实际参数替换了"?"占位符，可从控制台复制出来直接执行
- 【可选】[乐观锁插件](https://github.com/Dreamroute/locker) 透明解决乐观锁问题
## 设计原则
  框架本身依赖mybatis-spring，仅在应用启动时织入框架逻辑，不破坏任何mybatis核心，原则上可以兼容任何mybatis版本
  
## 版本要求
  - JDK 1.8+
  - maven-compiler-plugin编译插件不要关闭`<arg>-parameters</arg>`，因为需要通过反射获取参数名称（这是jdk8的特性）

## 使用方式
- SpringBoot
```
    <dependency>
        <groupId>com.github.dreamroute</groupId>
        <artifactId>mybatis-pro-boot-starter</artifactId>
        <version>latest version</version>
    </dependency>
```
### 最新版本：[点击查看](https://search.maven.org/artifact/com.github.dreamroute/mybatis-pro-boot-starter)

## 功能展示

- 单表条件查询
```$xslt
public interface UserMapper {

    // 这是一个根据用户名、密码查询单个用户的查询，方法名只需要以findBy打头，接着方法名为: NameAndPassword
    User findByNameAndPassword(String name, String password);

}
```
你无需在xml文件中编写sql，也无需使用注解@Select("xxx")的sql，框架自动根据方法名：

**findByNameAndPassword**切割成`findBy`,`Name`, `And`, `Password`，组成如下sql：

`select * from user where name = #{name} and password = #{password}`

## 对比（mybatis-plus、通用mapper）
> **需求：查询字段version字段大小在2~4之间，并且根据id反向排序**

- mybatis-pro：
```
@Test
void proTest() {
    List<User> users = findByVersionBetweenOrderByIdDesc(2L, 4L);
}
```
- mybatis-plus：
```
@Test
void plusTest() {
    LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
    LambdaQueryWrapper<User> query = qw.between(User::getVersion, 2L, 4L);
    query.orderByDesc(User::getId);
    List<User> users = userMapper.selectList(query);
}
```

- 通用mapper
```
@Test
void mapperTest() {
    Example e = new Example(User.class);
    e.orderBy("id").desc();
    Criteria criteria = e.createCriteria().andBetween("version", 2L, 4L);
    List<User> users = userMapper.selectByExample(e);
}
```

## 全部文档

- [完整中文文档](https://github.com/Dreamroute/mybatis-pro/wiki)

## 作者信息
Email: 342252328@qq.com
