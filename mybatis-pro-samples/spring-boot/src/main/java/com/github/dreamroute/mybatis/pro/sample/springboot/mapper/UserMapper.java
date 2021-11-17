package com.github.dreamroute.mybatis.pro.sample.springboot.mapper;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.service.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User, Long> {

    List<User> findByName(String name);

    User findByPassword(String password, String... cols);

    User findByNameAndPassword(String name, String password);

    List<User> findByNameAndPasswordLike(String name, String password);

    List<User> findByVersionOrderByIdDesc(Long version);

    List<User> findByNameLikeAndVersionOrPasswordOrderById(String name, Long version, String password);

    List<User> findByIdIn(List<Long> list);

    List<User> findByIdNotIn(List<Long> list);

    List<User> findByVersionLT(Long version);

    List<User> findByVersionLTE(Long version);

    List<User> findByVersionGT(Long version);

    List<User> findByVersionGTE(Long version);

    List<User> findByNameIsNull();

    List<User> findByNameIsNotNull();

    List<User> findByNameIsBlank();

    List<User> findByNameIsNotBlank();

    List<User> findByNameLike(String name);

    List<User> findByNameNotLike(String name);

    List<User> findByNameStartWith(String name);

    List<User> findByNameEndWith(String name);

    List<User> findByNameNot(String name);

    List<User> findByNameIn(List<String> list, String... cols);

    List<User> findByNameNotIn(List<String> names);

    List<User> findByNameOrderByVersion(String name);

    List<User> findByNameOrderByVersionDesc(String name);

    int countByName(String name);

    int countByNameAndPhoneNo(String name, String phoneNo);

    int countByNameAndPhoneNoOpt(String name, String phoneNo);

    int deleteByNameOrPassword(String name, String password);

    int deleteByNameOrPasswordOpt(String name, String password);

    boolean existByNameOrPassword(String name, String password);

    @Select("select * from smart_user where name = #{name} and password = #{password}")
    List<User> selectByNameAndPasssword(String name, String password);

    List<User> selectAllPage(User pageRequest);

    List<User> findByNameAndPasswordOpt(String name, String password);

    List<User> findByNameOrderByVersionDescOpt(String name);

    /**
     * 对于Opt结尾的方法，在生成sql的时候使用了<if test obj != null and obj != '' />这种针对字符串的风格，这里测试一下非字符串类型的查询，看是否可以兼容
     */
    List<User> findByVersionOpt(Long version);

    List<User> findByIdLTE(long id, String... cols);

    FindByIdLTDto findByIdLT(Long id);

    @Data
    class FindByIdLTDto {
        private Long id;
        private String name;
    }
}
