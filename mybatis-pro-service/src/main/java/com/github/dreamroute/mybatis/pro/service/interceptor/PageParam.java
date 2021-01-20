package com.github.dreamroute.mybatis.pro.service.interceptor;

public class PageParam {
    private Integer defaultPage;
    //    默认每页显示条数
    private Integer defaultPageSize;
    //    是否启用分页功能
    private Boolean defaultUseFlag;
    //    是否检测当前页码的合法性（大于最大页码或小于最小页码都不合法）
    private Boolean defaultCheckFlag;
    //当前sql查询的总记录数，回填
    private Integer totle;
    //    当前sql查询实现分页后的总页数，回填
    private Integer totlePage;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(Integer defaultPage) {
        this.defaultPage = defaultPage;
    }

    public Integer getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public Boolean isDefaultUseFlag() {
        return defaultUseFlag;
    }

    public void setDefaultUseFlag(Boolean defaultUseFlag) {
        this.defaultUseFlag = defaultUseFlag;
    }

    public Boolean isDefaultCheckFlag() {
        return defaultCheckFlag;
    }

    public void setDefaultCheckFlag(Boolean defaultCheckFlag) {
        this.defaultCheckFlag = defaultCheckFlag;
    }

    public Integer getTotle() {
        return totle;
    }

    public void setTotle(Integer totle) {
        this.totle = totle;
    }

    public Integer getTotlePage() {
        return totlePage;
    }

    public void setTotlePage(Integer totlePage) {
        this.totlePage = totlePage;
    }
}