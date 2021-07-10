package com.github.dreamroute.mybatis.pro.core.interceptor;

import java.util.List;

public interface LogicalDeleteMapper {


    void backup(List<LogicalDelete> list);

}
