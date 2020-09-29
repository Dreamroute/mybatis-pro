package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.annotations.Type;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author w.dehai
 */
public class DocumentUtil {

    private DocumentUtil() {}

    /**
     * 将Document转换成Resource
     */
    public static Resource createResourceFromDocument(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, document.getDoctype().getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
            transformer.setOutputProperty("encoding", "UTF-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(bos));

            String xml = bos.toString("UTF-8");
            // 必须要将尖括号进行替换，否则要报错
            String replace = xml.replace("&gt;", ">").replace("&lt;", "<");
            return new ByteArrayResource(replace.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new MyBatisProException("Document转成Resource失败", e);
        }
    }

    /**
     * 将Resource转换成Document
     */
    public static Document createDocumentFromResource(Resource resource) {
        try {
            // 改写resource，加入xxxBy方法的标签
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            // 此处需要设置EntityResolver，以便从classpath寻找dtd文件进行解析，否则会从网路下载，很慢并且可能会connect timeout
            builder.setEntityResolver(new XMLMapperEntityResolver());
            return builder.parse(resource.getInputStream());
        } catch (Exception e) {
            throw new MyBatisProException("创建Document失败", e);
        }
    }

    /**
     * 给Document填充sql节点
     *
     * @param document mapper文档
     * @param tagName 标签
     * @param id id
     * @param resultType 返回类型
     * @param sql sql语句
     * @param type 主键是否自增
     */
    public static void fillSqlNode(Document document, MapperLabel tagName, String id, String resultType, String sql, Type type, String idName) {
        Element statement = document.createElement(tagName.getCode());

        Text sqlNode = document.createTextNode(sql);
        statement.appendChild(sqlNode);

        Attr idAttr = document.createAttribute(MapperLabel.ID.getCode());
        idAttr.setValue(id);
        statement.setAttributeNode(idAttr);

        if (!StringUtils.isEmpty(resultType)) {
            Attr resultTypeAttr = document.createAttribute(MapperLabel.RESULT_TYPE.getCode());
            resultTypeAttr.setValue(resultType);
            statement.setAttributeNode(resultTypeAttr);
        }

        if (tagName == MapperLabel.INSERT && type == Type.IDENTITY) {
            Attr useGeneratedKeysAttr = document.createAttribute(MapperLabel.USE_GENERATED_KEYS.getCode());
            useGeneratedKeysAttr.setValue("true");
            statement.setAttributeNode(useGeneratedKeysAttr);

            Attr keyPropertyAttr = document.createAttribute(MapperLabel.KEY_PROPERTY.getCode());
            keyPropertyAttr.setValue(idName);
            statement.setAttributeNode(keyPropertyAttr);
        }

        document.getElementsByTagName(MapperLabel.MAPPER.getCode()).item(0).appendChild(statement);
    }

}
