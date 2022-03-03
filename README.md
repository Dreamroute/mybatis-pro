# MyBatis-Pro，持久化框架最终兵器
![img](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/Wmeona977bbQnXxj/img/696fcaff-4228-45cd-9f1f-12573108eeb9.png)
### **大道至简，优雅解决MyBatis单表的一切问题**

## **源码地址**
- [GitHub](https://github.com/Dreamroute/mybatis-pro.git)
- [码云](https://gitee.com/Dreamroute/mybatis-pro)

## 使用文档

- [完整中文文档](https://github.com/Dreamroute/mybatis-pro/wiki/MyBatis-Pro%EF%BC%8C%E6%8C%81%E4%B9%85%E5%8C%96%E6%A1%86%E6%9E%B6%E6%9C%80%E7%BB%88%E5%85%B5%E5%99%A8)

## **开发此框架的初衷**
- 让单表查询更加优雅
- 基本告别单表SQL语句
- 媲美JPA的单表查询优势
- 拒绝重复造轮子
- 钢铁直男的终极选择

## 框架功能

- 无需手动编写单表增删改查方法，框架自动生成
- 与通用Mapper、MyBatis-Plus等三方框架兼容（三者选其一即可，功能类似）
- 【可选】内置枚举类型处理器，优雅解决枚举类型问题，无需手动转换
- 【可选】内置泛型Service，避免重复造轮子编写大量类似的Service方法代码
- 【可选】内置两种方式逻辑删除，可放心大胆的在生产环境进行delete操作，不用担心误删数据
- 【可选】[分页插件](https://github.com/Dreamroute/pager)支持单表、多表关联查询、支持复杂的多表分页查询
- 【可选】[sql打印插件](https://github.com/Dreamroute/sqlprinter)已经用实际参数替换了?占位符，可以从日志文件拷贝出来直接执行
- 【可选】[乐观锁插件](https://github.com/Dreamroute/locker)透明解决乐观锁问题
- 
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

- [完整中文文档](https://github.com/Dreamroute/mybatis-pro/wiki/MyBatis-Pro%EF%BC%8C%E6%8C%81%E4%B9%85%E5%8C%96%E6%A1%86%E6%9E%B6%E6%9C%80%E7%BB%88%E5%85%B5%E5%99%A8)

## 作者信息
Email: 342252328@qq.com
