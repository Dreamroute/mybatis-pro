package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

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

    public static void createSelectMethod(Document document, String tagName, String id, String resultType, String sql) {
        Element select = document.createElement(tagName);

        Text sqlNode = document.createTextNode(sql);
        select.appendChild(sqlNode);

        Attr idAttr = document.createAttribute(MapperLabel.ID.getCode());
        idAttr.setValue(id);
        select.setAttributeNode(idAttr);

        Attr resultTypeAttr = document.createAttribute(MapperLabel.RESULT_TYPE.getCode());
        resultTypeAttr.setValue(resultType);
        select.setAttributeNode(resultTypeAttr);

        document.getElementsByTagName(MapperLabel.MAPPER.getCode()).item(0).appendChild(select);
    }

}
