package com.github.dreamroute.mybatis.pro.core;

import org.junit.jupiter.api.Test;

class SqlUtilTest {

    @Test
    void createSqlTest() {
        String name = "NameBetweenAndPassWordLessThanOrVersionAndAgeAndEmailOrAddrLessThanEqualAnd" +
                "NameGreaterThanAndNameGreaterThanEqualOrNameIsNullAndAgeIsNotNullOrNameLikeAndAgeNotLikeAndEmailStartingWithOrEmailEndingWithAndNameNot" +
                "AndNameInAndNameNotInAndNameTrueAndNameFalse";
        System.err.println(SqlUtil.createSql(name));
    }

}
