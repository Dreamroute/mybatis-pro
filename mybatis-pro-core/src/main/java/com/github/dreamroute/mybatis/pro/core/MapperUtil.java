package com.github.dreamroute.mybatis.pro.core;

import com.github.dream.mybatis.pro.sdk.Mapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author w.dehai
 */
public class MapperUtil {

    private Resource resource;
    private Set<Class<?>> parentInters;
    private String entityCls;
    private String idCls;
    private String tableName;
    private String idName;

    public MapperUtil(Resource resource) {
        this.resource = resource;
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            Type[] genericInterfaces = mapper.getGenericInterfaces();
            ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
            Type[] args = pt.getActualTypeArguments();
            entityCls = args[0].getTypeName();
            idCls = args[1].getTypeName();
            tableName = ResourceUtil.getTableName(entityCls);
            try {
                Class<?> entity = ClassUtils.forName(entityCls, getClass().getClassLoader());
                idName = ClassUtil.getIdName(entity);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
    }

    public Resource parse() {
        Document doc = ResourceUtil.createDoc(resource);
        return processSelectMapper(doc);
    }

    private Resource processSelectMapper(Document doc) {
        Element select = doc.createElement(XmlLabel.SELECT);

        Text sql = doc.createTextNode("select * from " + tableName + " where " + idName + " = #{id}");
        select.appendChild(sql);

        Attr id = doc.createAttribute(XmlLabel.ID);
        id.setValue("selectById");
        select.setAttributeNode(id);

        Attr resultType = doc.createAttribute(XmlLabel.RESULT_TYPE);
        resultType.setValue(entityCls);
        select.setAttributeNode(resultType);

        doc.getElementsByTagName(XmlLabel.MAPPER).item(0).appendChild(select);

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doc.getDoctype().getPublicId());
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
            t.setOutputProperty("encoding", "UTF-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            t.transform(new DOMSource(doc), new StreamResult(bos));

            String xml = bos.toString("UTF-8");
            // 必须要将尖括号进行替换，否则要报错
            String replace = xml.replace("&gt;", ">").replace("&lt;", "<");
            return new ByteArrayResource(replace.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {

        }
        return null;
    }

}