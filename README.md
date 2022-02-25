 
大道至简，优雅解决MyBatis单表的一切问题

源码地址
● GitHub
● gitee

开发此框架的初衷
● 让单表查询更加优雅
● 基本告别单表SQL语句
● 媲美JPA的单表查询优势
● 拒绝重复造轮子
● 钢铁直男的终极选择

简单接入
Spring Boot，引入依赖，即可完成接入，查看最新版本
    <dependency>
        <groupId>com.github.dreamroute</groupId>
        <artifactId>mybatis-pro-boot-starter</artifactId>
        <version>最新版本</version>
    </dependency>

开始使用
● 定义你的实体对象User，使用@Table指定表名，@Id指定主键
@Data
@Table("smart_user")
public class User {
    @Id
    private Long id;
    private String name;
    private String password;
      private Long version;
}

● 定义Mapper接口UserMapper以及接口方法，无需UserMapper.xml、也无需mybatis注解定义SQL语句
● @MapperScan注解指明你的Mapper接口路径
public interface UserMapper extends BaseMapper<User, Long> {

    // 将自动生成SQL: select '全部列' from xx where name = #{name} and password = #{password}
    User findByNameAndPassword(String name, String password);

    // 将自动生成SQL: select count(*) c from xx where name = #{name}
    int countByName(String name);

    // 将自动生成SQL: select 'cols参数指定的列' from xx where name = #{name} and password like '%#{password}%'
    List<User> findByNameAndPasswordLike(String name, String password, String... cols);

    // 将自动生成SQL: delete from xx where name = #{name} and version = #{version}
    int deleteByNameAndVersion(String name, Long version);

}

● 查询，于是就可以使用Mapper中的方法进行查询，包括findBy、countBy、existBy、deleteBy的方法以及BaseMapper的所有基础方法
框架功能
● 无需手动编写单表增删改查方法，框架自动生成
● 与通用Mapper、MyBatis-Plus等三方框架兼容（三者选其一即可，功能类似）
● 【可选】内置枚举类型处理器，优雅解决枚举类型问题，无需手动转换
● 【可选】内置泛型Service，避免重复造轮子编写大量类似的Service方法代码
● 【可选】内置两种方式逻辑删除，可放心大胆的在生产环境进行delete操作，不用担心误删数据
● 【可选】分页插件支持单表、多表关联查询、支持复杂的多表分页查询
● 【可选】sql打印插件已经用实际参数替换了?占位符，可以从日志文件拷贝出来直接执行
● 【可选】乐观锁插件透明解决乐观锁问题
目前支持的配置
# 是否开启内置枚举转换器，默认false
mybatis.pro.enable-enum-type-handler = false

# 是否开启逻辑删除，默认false
mybatis.pro.enable-logical-delete = false

# 逻辑删除开启时有效：逻辑删除类型，backup-备份方式；update-更新方式，默认backup
mybatis.pro.logical-delete-type=backup

# 逻辑删除开启时，并且删除类型是backup时有效：逻辑删除表名，默认logical_delete
mybatis.pro.logical-delete-table=

# 逻辑删除开启，删除类型是update时有效，状态列，默认status：
mybatis.pro.logical-delete-column=xxx

# 逻辑删除开启，删除类型是update时有效，表示未被删除，默认1
mybatis.pro.logical-delete-active=

# 逻辑删除开启，删除类型是update时有效，表示被删除，默认0
mybatis.pro.logical-delete-in-active=

内置方法
1. 将你的Mapper接口继承BaseMapper接口
2. 在启动类上使用@MapperScan注解指明你的Mapper接口路径
3. 此时你的接口就拥有了BaseMapper接口的所有通用方法，如下：
> 方法参数cols为可变参数,代表列名,指定select需要查询的哪些列,不传则代表查询全部列，除了下列内置基础查询方法外，所有的findBy方法也支持String... cols这种动态列方式，cols必须是方法的最后一个参数
    T selectById(ID id, String... clos);                       // 根据主键id查询单个对象
    List<T> selectByIds(List<ID> ids, String... clos);         // 根据主键id集合查询多个对象
    List<T> selectAll(String... clos);                         // 查询全部

    int insert(T entity);                                      // 新增
    int insertExcludeNull(T entity);                           // 新增，值为null的属性不进行保存，使用数据库默认值
    int insertList(List<T> entityList);                        // 批量新增

    int updateById(T entity);                                  // 根据主键id修改
    int updateByIdExcludeNull(T entity);                       // 根据主键id修改，值为null的属性不进行修改

    int deleteById(ID id);                                     // 根据id删除（物理删除）
    int deleteByIds(List<ID> ids);                             // 根据id列表进行删除（物理删除）

实体对象注解
@Data
@Table("smart_user")
public class User {

    @Id
    private Long id;
    private String name;
    private String password;
    private Long version;
    @Transient
    private Integer gender;
    
    // 列的别名，数据库列和Java实体的属性不一致时使用此属性
    @Column("mobile")
    private String phoneNo;
}


说明：
● @Table：（必填）数据库表名
● @Id：（必填）主键，默认为自增，可根据@Id的属性type属性修改主键策略
● @Transient：（可选）表示在新增时此字段不持久化到数据库
● @Column：（可选）实体属性与数据列的映射关系（mybatis的mybatis.configuration.map-underscore-to-camel-case=true时会自动进行下划线转驼峰,默认是false）
特别说明
框架内部由于使用了findBy,existBy, deleteBy,  countBy这几个xxxBy开头的方法,在框架中属于是特殊方法，因此你的mapper普通查询方法不能使用这种关键字开头,会与框架冲突，你可以使用selectBy, getBy, queryBy等打头
灵魂功能
> 1、Mapper接口的方法名根据特定的书写规则进行查询，用户无需编写sql语句
> 2、方法名以findBy、countBy、existBy、deleteBy开头，属性首字母大写，多个属性使用And或者Or连接
> 3、对于findByXxxIn和findByXxxNotIn这种传入的参数是List类型的，那么方法参数名也定义成list，否则可能会报错，比如findByNameIn(List&lt;String&gt; list)，如果不清楚某些Mapper方法的参数应该如何命名，那么请参考下方【全部功能】举的例子，比如between查询的两个参数就应该使用(start, end)，而不能用其他的名字
比如：
public interface UserMapper extends BaseMapper<User, Long> {

    // 将自动生成SQL: select '全部列' from xx where name = #{name} and password = #{password}
    User findByNameAndPassword(String name, String password);

    // 将自动生成SQL: select count(*) c from xx where name = #{name}
    int countByName(String name);

    // 将自动生成SQL: select 'cols参数指定的列' from xx where name = #{name} and password like '%#{password}%'
    List<User> findByNameAndPasswordLike(String name, String password, String... cols);

    // 将自动生成SQL: delete from xx where name = #{name} and version = #{version}
    int deleteByNameAndVersion(String name, Long version);

}

参数为空问题
有时候查询参数为空的时候，我们期望此条件不参与查询，比如Mapper接口为：
User findByNameAndPassword(String name, String password)
我们希望name为null或者空字符串时候，name这个条件不参与查询，在Mapper方法尾部加上Opt即可
User findByNameAndPasswordOpt(String name, String password)
此时name如果为null或者空字符串，那么sql语句就成为了：
select '全部列' from user where password = #{password}
Opt结尾的mapper方法自动就将空参数给移除掉了
指定列名
在进行findBy和内置基础的select方法查询时，如果不希望使用select '全部列' 的方式，那么可以在定义mapper接口的时候最后一个参数定义成动态参数或者数组，参数名为：cols，在调用的时候传入列名即可，比如：
public interface UserMapper {

    User findById(Long id, String... cols);

}

// 调用
User user = userMapper.findById(1L, "id", "name");
驼峰和下划线转换问题
mybatis有一个配置叫做：map-underscore-to-camel-case，表示下划线是否转驼峰，默认false，mybatis-pro也依赖此配置。
对于findBy countBy existBy deleteBy开头的方法，后续的条件如果是驼峰，那么就根据上述的属性表示是否转换。比如方法：
findByUserName(String userName);
如果map-underscore-to-camel-case = true，那么就会将驼峰userName转换成下划线user_name。这种最后sql类似这样：
select '全部列' from user where user_name = #{userName}
如果map-underscore-to-camel-case = false，那么userName就保持不变, sql类似这样：
select '全部列' from user where userName = #{userName}
全部功能
> 一个方法可以有多个and或者or拼接多个条件，如：findByNameLikeOrPasswordIsNotNullAndVersion(String name, String password, Long version)<br>> 效果：where name like '%#{name}%' or password is not null and version = #{version}<br>
> 事实上这种单个方法可以写得很复杂，但是这与设计此工具的初衷是背离的，如果比较复杂的单表查询，我还是推荐xml方式最为直观
关键字
示例
效果
and
findByNameAndPassword(String name, String password)
where name = #{name} and #{password}
or
findByNameOrPassword(String name, String password)
where name = #{name} or #{password}
count
countByName(String name)
select count(*) c from xx where name = #{name}
exist
existByName(String name)
查询结果大于等于1，那么返回true，否则返回false
delete
deleteByName(String name)
delete from x where name = #{name}
Between
findByAgeBetween(Integer start, Integer end )
where age between #{start} and #{end}
LT（LessThan）
findByAgeLT(Integer age)
where age < #{age}
LTE（LessThanEqual）
findByAgeLTE(Integer age)
where age <= #{age}
GT（GreaterThan）
findByAgeGT(Integer age)
where age > #{age}
GTE（GreaterThanEqual）
findByAgeLTE(Integer age)
where age >= #{age}
IsNull
findByNameIsNull
where name is null
IsNotNull
findByNameIsNotNull
where name is not null
IsBlank
findByNameIsBlank
where name is null or name = ''
IsNotBlank
findByNameIsNotBlank
where name is not null and name != ''
Like
findByNameAndPasswordLike(String name, String password)
where name = #{name} and password like CONCAT('%',#{password},'%')
NotLike
findByNameNotLike(String name)
where name not like CONCAT('%',#{name},'%')
StartWith
findByNameStartWith(String name)
where name like CONCAT(#{name},'%')
EndWith
findByNameEndWith(String name)
where name like CONCAT('%',#{name})
Not
findByNameNot(String name)
where name <> #{name}
In
findByNameIn(List&lt;String&gt; list)
where name in ('A', 'B', 'C')
NotIn
findByNameNotIn(List&lt;String&gt; list)
where name not in ('A', 'B', 'C')
OrderBy
findByNameOrderById(String name)
where name = #{name} order by id
Desc
findByNameOrderByIdDesc(String name)
where name = #{name} order by id desc
最佳实践
提供一些工作中总结出来的一些最佳实践，包括通用泛型Service，放在mybatis-pro-service模块里面
通用泛型Service
● 由于我们业务系统大多数的service都需要具备基础的crud功能，所以此框架提供了泛型Service能力
● 使用方法：
    ○ 你的Mapper接口继承BaseMapper接口,如下：
public interface UserMapper extends BaseMapper<User, Long> {}
    ○ 你的Service（比如UserService）继承BaseService<T, ID>，泛型参数分别是对应的实体类型和主键id类型，如下：
public interface UserService extends BaseService<User, Long> {}
    ○ 你的Service实现类（比如UserServiceImpl），需要继承AbstractServiceImpl，并且实现你你的UserService接口，如下：
@Service
public class UserServiceImpl extends AbstractServiceImpl<User, Long> implements UserService {}
    ○ 在数据库创逻辑删除备份表建表（可以不创建），关于逻辑删除见下方“逻辑删除说明”：
CREATE TABLE `logical_delete` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `table_name` varchar(100) NOT NULL,
    `data` json NOT NULL,
    `delete_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
    ■ 于是你的UserService就具备了如下能力（参数String... cols与BaseMapper作用相同）：
    ■ 接下来你就可以在你的Controller使用UserService了，如：
● 逻辑删除（备份方案）：
    ○ 通用Service内包含了删除方法，对于删除方法，我们有两种方式处理，一种是直接物理删除，一种是逻辑删除；
    ○ 私认为逻辑删除可能要稳当一些，出了问题好排查一点；
    ○ 于是对于框架的删除方法，我们做了两类，一类是物理删除，一类是逻辑删除，delete()为逻辑删除，deleteDanger()是物理删除
    ○ 对于逻辑删除的处理，有2种方案，一种是记录有一个字段是状态字段，对于删除的记录进行update操作，修改状态字段，把状态改为“删除”状态，另一种是删除的时候进行物理删除，然后把此数据移动到其他地方进行存储，由于前者仅仅是修改状态，有时候会破坏唯一索引（数据一直存在），加之任何查询都得带上类似state != 1这种状态行为，而后者就不存在这个问题
    ○ 这里的做法是：将所有删除的数据都放在同一个表里面，于是需要在数据库创建一张备份表用于存放被删除的数据，表名默认是：logical_delete，也可以在application.yml里面通过属性指定：mybatis.pro.logical-delete-table = xxx，当然如果你不使用此方案的逻辑删除，系统里全是物理删除，也可以不创建此表，使用配置mybatis.pro.enable-logical-delete关闭逻辑删除即可；

    ■ 关于将整个库的删除数据都放在同一个表是否合适：微服务时代，每个库的表并不多，另外对于绝大多数据业务系统删除操作都不是很频繁的发生。所以基本上是合适的。
● 逻辑删除（使用update代替delete方案）：
    ○ 虽然上述方案更可取，但是部分业务系统对于删除很敏感，业务方由于误操作需要频繁的让开发人员恢复数据，这就导致了使用update方式更为合理。目前框架是兼容此方案的，使用mybatis.pro.logical-delete-type=update开启此功能，使用mybatis.pro.logical-delete-column=xxx（默认是字段是status）来标记逻辑删除列的列名，使用mybatis.pro.logical-delete-in-activ = xxx的值表示逻辑删除状态值（默认是0），使用mybatis.pro.logical-delete-active=xx的值表示正常数据（也就是未被删除数据，默认是1），那么，对于基础方法selectById,selectByIds, selectAll和findBy开头的方法的sql结尾都会加上类似xxx = ${mybatis.pro.logical-delete-active}的条件。比如：
mybatis.pro.enable-logical-delete=true
mybatis.pro.logical-delete-type=update
mybatis.pro.logical-delete-column=status
mybatis.pro.logical-delete-active=1
mybatis.pro.logical-delete-in-active=0

selectById` => `select * from xxx where id = #{id} AND state = 1
selectAll` => `select * from xxx where state = 1

● 如果开启了逻辑删除，但是依然想对某些数据做物理删除，【待开发此功能】
Adaptor
● 1、请求参数：系统中大量存在根据id和id数组来进行查询的请求，于是将id和id数组进行封装，放在id包内；
● 2、javax中的参数校验不是很全面，将一些参数校验放在validator包中；
额外功能
● 1、对枚举类型的支持：在开发过程中，对于数据库的字典字段，一般我们在数据库使用tinyint这种类型，Java代码中一般使用枚举类型，我们希望将枚举类型的数字类型的值存入数据库，而不是枚举类型的名称，比如存在如下性别类型的枚举：
@Getter
@AllArgsConstructor
public enum  Gender implements EnumMarker {

    MALE(1, "男"), FEMALE(2, "女");

    private final Integer value;
    private final String desc;

}

我们希望存入数据库的是1或者2，而不是MALE和FEMALE，而在我们进行查询的时候自动将数字类型转换成枚举类型，mybatis的typehandler刚好支持此功能，于是mybatis-pro内置枚举转换typehandler完成此功能，并且，我希望把此功能做得更加通用，业务代码都实现EnumMarker使用相同的规约，于是定义了EnumMarker接口和EnumTypeHandler转换器，枚举类型实现EnumMarker有2个作用：1、实现此接口的枚举将自动自动转型，不实现此接口则无法享受此待遇；2、接口的两个get方法分别是获取value和desc的值；如下实体就会自动进行转型：
@Getter
@AllArgsConstructor
public enum  Gender implements EnumMarker {

    MALE(1, "男"), FEMALE(2, "女");

    private final Integer value;
    private final String desc;

}

@Data
@Table("user")
public class User {
    @Id
    private Long id;
    // 性别（1-男；2-女）
    private Gender gender;
}

// 调用:userMapper.insert(User user); // 自动将gender的值1或者2保存到数据
// 调用:User user = userMapper.selectById(Long id); // 自动将gender的值1或者2转换成Gender类型存入user


