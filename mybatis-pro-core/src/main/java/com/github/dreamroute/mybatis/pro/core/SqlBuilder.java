package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author w.dehai
 */
public class SqlBuilder {

    private Document document;
    private MapperLabel tagName;
    private String id;
    private String resultType;
    private String sql;

    public SqlBuilder document(Document document) {
        this.document = document;
        return this;
    }

    public SqlBuilder tagName(MapperLabel tagName) {
        this.tagName = tagName;
        return this;
    }

    public SqlBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SqlBuilder resultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public SqlBuilder sql(String sql) {
        this.sql = sql;
        return this;
    }

    public Document build() {
        Element select = document.createElement(tagName.getCode());

        Text sqlNode = document.createTextNode(sql);
        select.appendChild(sqlNode);

        Attr idAttr = document.createAttribute(MapperLabel.ID.getCode());
        idAttr.setValue(id);
        select.setAttributeNode(idAttr);

        Attr resultTypeAttr = document.createAttribute(MapperLabel.RESULT_TYPE.getCode());
        resultTypeAttr.setValue(resultType);
        select.setAttributeNode(resultTypeAttr);

        document.getElementsByTagName(MapperLabel.MAPPER.getCode()).item(0).appendChild(select);
        return document;
    }

}