package com.example.minimybatis.session;

import com.example.minimybatis.builder.AnnotationMapperBuilder;
import com.example.minimybatis.builder.XMLMapperBuilder;
import com.example.minimybatis.mapping.MappedStatement;

import java.io.InputStream;
import java.util.List;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Configuration config) {
        return new SqlSessionFactory(config);
    }

    public SqlSessionFactory build(InputStream xmlInputStream, String namespace) {
        try {
            Configuration configuration = new Configuration();

            XMLMapperBuilder xmlBuilder = new XMLMapperBuilder(xmlInputStream, namespace);
            List<MappedStatement> mappedStatements = xmlBuilder.parseMappedStatements();

            for (MappedStatement mappedStatement : mappedStatements) {
                configuration.addMappedStatement(mappedStatement);
            }
            return new SqlSessionFactory(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse XML mapper", e);
        }
    }

    public SqlSessionFactory build(Class<?> mapperClass) {
        Configuration config = new Configuration();
        AnnotationMapperBuilder annotationMapperBuilder = new AnnotationMapperBuilder(mapperClass);
        List<MappedStatement> mappedStatements = annotationMapperBuilder.parseMappedStatements();

        for (MappedStatement mappedStatement : mappedStatements) {
            config.addMappedStatement(mappedStatement);
        }
        return new SqlSessionFactory(config);
    }

}
