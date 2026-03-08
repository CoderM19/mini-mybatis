package com.example.minimybatis.builder;

import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.mapping.ParameterMapping;
import com.example.minimybatis.parser.SqlParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLMapperBuilder {

    private final InputStream inputStream;

    private final String namespace;

    public XMLMapperBuilder(InputStream inputStream, String namespace) {
        this.inputStream = inputStream;
        this.namespace = namespace;
    }

    public List<MappedStatement> parseMappedStatements() throws Exception {
      List<MappedStatement> mappedStatements = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(inputStream);

        NodeList selectNodes = document.getElementsByTagName("select");
        for (int i = 0; i < selectNodes.getLength(); i++) {
            Element element = (Element) selectNodes.item(i);
            MappedStatement mappedStatement = parseElement(element, "select");
            if (mappedStatement != null){
                mappedStatements.add(mappedStatement);
            }
        }

        NodeList insertNodes = document.getElementsByTagName("insert");
        for (int i = 0; i < insertNodes.getLength(); i++) {
            Element element = (Element) insertNodes.item(i);
            MappedStatement mappedStatement = parseElement(element, "insert");
            if (mappedStatement != null){
                mappedStatements.add(mappedStatement);
            }
        }

        NodeList updateNodes = document.getElementsByTagName("update");
        for (int i = 0; i < updateNodes.getLength(); i++) {
            Element element = (Element) updateNodes.item(i);
            MappedStatement mappedStatement = parseElement(element, "update");
            if (mappedStatement != null){
                mappedStatements.add(mappedStatement);
            }
        }

        NodeList deleteNodes = document.getElementsByTagName("delete");
        for (int i = 0; i < deleteNodes.getLength(); i++) {
            Element element = (Element) deleteNodes.item(i);
            MappedStatement mappedStatement = parseElement(element, "delete");
            if (mappedStatement != null){
                mappedStatements.add(mappedStatement);
            }
        }
        return mappedStatements;
    }


    private MappedStatement parseElement(Element element, String type) {
        try {
            MappedStatement ms = new MappedStatement();
            String id = element.getAttribute("id");
            ms.setId(namespace + "." + id);
            String resultType = element.getAttribute("resultType");
            if (resultType != null && !resultType.isEmpty()) {
                ms.setResultType(Class.forName(resultType));
            }
            String parameterType = element.getAttribute("parameterType");
            if (parameterType != null && !parameterType.isEmpty()) {
                List<Class<?>> parameterTypes = new ArrayList<>();
                parameterTypes.add(Class.forName(parameterType));
                ms.setParameterTypes(parameterTypes);
            }
            String sql = getTextContent(element);
            ms.setSql(sql);
            SqlParser sqlParser = new SqlParser();
            SqlParser.ParsedSql parsedSql = sqlParser.parse(sql);

            List<ParameterMapping> parameterMappings = new ArrayList<>();
            for (String paramName : parsedSql.getParameterNames()) {
                ParameterMapping parameterMapping = new ParameterMapping();
                parameterMapping.setName(paramName);
                parameterMappings.add(parameterMapping);
            }
            ms.setParameterMappings(parameterMappings);
            return ms;
        } catch (Exception e) {
            return null;
        }
    }

    private String getTextContent(Element element) {
        StringBuilder sb = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                String text = node.getTextContent().trim();
                if (!text.isEmpty()) {
                    sb.append(text).append(" ");
                }
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                sb.append(getTextContent((Element) node)).append(" ");
            }
        }
        return sb.toString().trim();
    }

}
