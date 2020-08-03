package com.github.dreamroute.mybatis.pro.core.consts;

/**
 * 方法命名的所有关键字，与JPA保持同步，去掉了JPA中个别有歧义以及雷同的用法的关键字
 * 参考：https://docs.spring.io/spring-data/jpa/docs/2.3.2.RELEASE/reference/html/#
 *
 * @author w.dehai
 */
public class KeyWord {

    private KeyWord() {}

    // … where x.lastname = ?1 and x.firstname = ?2
    public static final String AND = "And";
    // … where x.lastname = ?1 or x.firstname = ?2
    public static final String OR = "Or";

    // … where x.startDate between ?1 and ?2
    public static final String BETWEEN = "Between";

    // … where x.age < ?1
    public static final String LESS_THAN = "LessThan";
    // … where x.age <= ?1
    public static final String LESS_THAN_EQUAL = "LessThanEqual";

    // … where x.age > ?1
    public static final String GREATER_THAN = "GreaterThan";
    // … where x.age >= ?1
    public static final String GREATER_THAN_EQUAL = "GreaterThanEqual";

    // … where x.startDate > ?1
    public static final String AFTER = "After";
    //… where x.startDate < ?1
    public static final String BEFORE = "Before";

    //… where x.age is null
    public static final String IS_NULL = "IsNull";

    //… where x.age not null
    public static final String IS_NOT_NULL = "IsNotNull";

    // … where x.firstname like ?1
    public static final String LIKE = "Like";
    // … where x.firstname not like ?1
    public static final String NOT_LIKE = "NotLike";

    // … where x.firstname like ?1%
    public static final String STARTING_WITH = "StartingWith";

    // … where x.firstname like %?1
    public static final String ENDING_WITH = "EndingWith";

    // … where x.firstname like ?1
    public static final String CONTAINING = "Containing";

    //… where x.lastname <> ?1
    public static final String NOT = "Not";

    //… where x.age in ?1
    public static final String IN = "In";
    // … where x.age not in ?1
    public static final String NOT_IN = "NotIn";

    // … where x.active = true
    public static final String TRUE = "True";
    // … where x.active = false
    public static final String FALSE = "False";

    // ... 需要与order by配合
    public static final String DESC = "Desc";
    // … where x.age = ?1 order by x.lastname( desc), 可以配合DESC常量
    public static final String ORDER_BY = "OrderBy";

    // … where UPPER(x.firstame) = UPPER(?1)
    public static final String IGNORE_CASE = "IgnoreCase";

}
