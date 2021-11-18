package com.github.dreamroute.mybatis.pro.autoconfiguration;

import com.github.dreamroute.mybatis.pro.base.typehandler.EnumTypeHandler;
import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import com.github.dreamroute.mybatis.pro.core.interceptor.LogicalDeleteInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.dreamroute.mybatis.pro.core.consts.ToLineThreadLocal.TO_LINE;
import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.processMyBatisPro;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.ObjectUtils.isEmpty;

/**
 *
 * @author w.dehai
 */
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureBefore({MybatisAutoConfiguration.class})
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties({MybatisProperties.class, MyBatisProProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
public class MyBatisProAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisProAutoConfiguration.class);
    private final MybatisProperties properties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;

    @Autowired
    private ApplicationContext context;
    @Value("${mybatis.configuration.map-underscore-to-camel-case:false}")
    private boolean toLine;

    public MyBatisProAutoConfiguration(
            MybatisProperties properties,
            ResourceLoader resourceLoader,
            ObjectProvider<Interceptor[]> interceptorsProvider,
            ObjectProvider<TypeHandler[]> typeHandlersProvider,
            ObjectProvider<LanguageDriver[]> languageDriversProvider,
            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {

        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, MyBatisProProperties props) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }

        // 如果逻辑删除开启，这里将逻辑删除插件加入到插件列表
        if (props.isEnableLogicalDelete()) {
            properties.getConfiguration().addInterceptor(new LogicalDeleteInterceptor(props));
        }
        // 如果枚举处理器开启，那么加入到configuratin中
        if (props.isEnableEnumTypeHandler()) {
            properties.getConfiguration().getTypeHandlerRegistry().register(new EnumTypeHandler<>());
        }

        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }

        Resource[] resources = this.properties.resolveMapperLocations();

        // -- mybatis-pro begin.
        logger.info("织入mybatis-pro开始 ......");
        StopWatch watch = new StopWatch();
        watch.start();

        TO_LINE.set(toLine);

        Set<String> mapperPackages = getMapperPackages();
        if (!isEmpty(resources) || !isEmpty(mapperPackages)) {
            Resource[] rs = processMyBatisPro(resources, mapperPackages);
            factory.setMapperLocations(rs);
        }

        TO_LINE.remove();

        logger.info("织入mybatis-pro结束 ......");
        watch.stop();
        logger.info("织入mybatis-pro耗时: {}", watch.getTotalTimeSeconds());
        // -- mybatis-pro end.

        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
            // Need to mybatis-spring 2.0.2+
            factory.setScriptingLanguageDrivers(this.languageDrivers);
            if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
                defaultLanguageDriver = this.languageDrivers[0].getClass();
            }
        }
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            // Need to mybatis-spring 2.0.2+
            factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }

        return factory.getObject();
    }

    private void applyConfiguration(SqlSessionFactoryBean factory) {
        org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    /**
     * 获取mapper接口的包路径集合
     */
    private Set<String> getMapperPackages() {
        Set<String> mapperPackages = new HashSet<>();
        Map<String, Object> mapperScan = context.getBeansWithAnnotation(MapperScan.class);
        if (!mapperScan.isEmpty()) {
            for (Object scan : mapperScan.values()) {
                Class<?> scanCls = scan.getClass();
                MapperScan ms = AnnotationUtils.findAnnotation(scanCls, MapperScan.class);
                String[] value = ms != null ? ms.value() : new String[0];
                String[] basePackages = ms != null ? ms.basePackages() : new String[0];
                Class<?>[] basePackageClasses = ms != null ? ms.basePackageClasses() : new Class<?>[0];

                mapperPackages.addAll(asList(value));
                mapperPackages.addAll(asList(basePackages));
                mapperPackages.addAll(stream(basePackageClasses).map(cls -> cls.getPackage().getName()).collect(toSet()));
            }

            logger.info("MyBatis-Pro检测出Mapper路径包括: {}", mapperPackages);
        }
        return mapperPackages;
    }

}
