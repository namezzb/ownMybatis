package cn.zzb.mybatis.builder.xml;

import cn.zzb.mybatis.builder.BaseBuilder;
import cn.zzb.mybatis.datasource.DataSourceFactory;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.mapping.Environment;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

/**
 * XML 配置文件解析器（mybatis-config.xml）
 * <p>
 * 核心职责：
 * 1. 解析 MyBatis 主配置文件 mybatis-config.xml
 * 2. 构建全局 Configuration 对象，填充各项配置信息
 * 3. 解析环境配置（数据源、事务管理器）
 * 4. 解析 Mapper 映射文件的引用，委托给 XMLMapperBuilder 处理
 * <p>
 * 设计模式：
 * - 建造者模式：逐步构建复杂的 Configuration 对象
 * - 模板方法模式：继承 BaseBuilder，复用公共解析逻辑
 * <p>
 * 解析流程：
 * 读取 XML 文件 → 解析 environments（数据源配置） →
 * 解析 mappers（Mapper 文件引用） → 返回 Configuration
 * <p>
 * 配置文件结构：
 * <pre>
 * &lt;configuration&gt;
 *   &lt;environments default="development"&gt;
 *     &lt;environment id="development"&gt;
 *       &lt;transactionManager type="JDBC"/&gt;
 *       &lt;dataSource type="POOLED"&gt;...&lt;/dataSource&gt;
 *     &lt;/environment&gt;
 *   &lt;/environments&gt;
 *   &lt;mappers&gt;
 *     &lt;mapper resource="mapper/UserMapper.xml"/&gt;
 *   &lt;/mappers&gt;
 * &lt;/configuration&gt;
 * </pre>
 *
 * @author zzb
 */
public class XMLConfigBuilder extends BaseBuilder {

    /** XML 文档的根元素（configuration 标签） */
    private Element root;

    /**
     * 构造函数
     * <p>
     * 初始化流程：
     * 1. 创建新的 Configuration 对象并传递给父类
     * 2. 使用 DOM4J 的 SAXReader 解析 XML 文件
     * 3. 获取根元素（configuration 标签）供后续解析使用
     *
     * @param reader XML 文件的 Reader 对象
     */
    public XMLConfigBuilder(Reader reader) {
        // 1. 调用父类初始化Configuration
        super(new Configuration());
        // 2. dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析配置文件的入口方法
     * <p>
     * 解析顺序（按 MyBatis 标准顺序）：
     * 1. environments：环境配置（数据源、事务管理器）
     * 2. mappers：Mapper 映射文件引用
     * <p>
     * 完整版本还应包括：
     * - properties：属性配置
     * - settings：全局设置
     * - typeAliases：类型别名
     * - typeHandlers：类型处理器
     * - plugins：插件
     * - objectFactory：对象工厂
     * - objectWrapperFactory：对象包装器工厂
     * <p>
     * 当前简化版本仅实现了核心的 environments 和 mappers 解析。
     *
     * @return 填充完成的 Configuration 对象
     * @throws RuntimeException 解析过程中发生异常
     */
    public Configuration parse() {
        try {
            // 环境
            environmentsElement(root.element("environments"));
            // 解析映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    /**
     * 解析环境配置（environments 标签）
     * <p>
     * XML 结构示例：
     * <pre>
     * &lt;environments default="development"&gt;
     *   &lt;environment id="development"&gt;
     *     &lt;transactionManager type="JDBC"&gt;
     *       &lt;property name="..." value="..."/&gt;
     *     &lt;/transactionManager&gt;
     *     &lt;dataSource type="POOLED"&gt;
     *       &lt;property name="driver" value="${driver}"/&gt;
     *       &lt;property name="url" value="${url}"/&gt;
     *       &lt;property name="username" value="${username}"/&gt;
     *       &lt;property name="password" value="${password}"/&gt;
     *     &lt;/dataSource&gt;
     *   &lt;/environment&gt;
     * &lt;/environments&gt;
     * </pre>
     * <p>
     * 解析流程：
     * 1. 获取 default 属性，确定使用哪个环境配置
     * 2. 遍历所有 environment 标签，找到匹配的环境
     * 3. 解析 transactionManager：通过类型别名创建事务工厂
     * 4. 解析 dataSource：创建数据源工厂并设置属性
     * 5. 构建 Environment 对象并设置到 Configuration
     * <p>
     * 支持的事务管理器类型：
     * - JDBC：使用 JDBC 的事务管理（commit/rollback）
     * <p>
     * 支持的数据源类型：
     * - POOLED：池化数据源（自实现连接池）
     * - UNPOOLED：非池化数据源（每次创建新连接）
     * - DRUID：Druid 数据源（第三方连接池）
     *
     * @param context environments 元素节点
     * @throws Exception 解析过程中可能抛出的异常
     */
    private void environmentsElement(Element context) throws Exception {
        String environment = context.attributeValue("default");

        List<Element> environmentList = context.elements("environment");
        for (Element e : environmentList) {
            String id = e.attributeValue("id");
            if (environment.equals(id)) {
                // 事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(e.element("transactionManager").attributeValue("type")).newInstance();

                // 数据源
                Element dataSourceElement = e.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
                List<Element> propertyList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element property : propertyList) {
                    props.setProperty(property.attributeValue("name"), property.attributeValue("value"));
                }
                dataSourceFactory.setProperties(props);
                DataSource dataSource = dataSourceFactory.getDataSource();

                // 构建环境
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);

                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }

    /**
     * 解析 Mapper 映射文件引用（mappers 标签）
     * <p>
     * XML 结构示例：
     * <pre>
     * &lt;mappers&gt;
     *   &lt;mapper resource="org/mybatis/builder/AuthorMapper.xml"/&gt;
     *   &lt;mapper resource="org/mybatis/builder/BlogMapper.xml"/&gt;
     *   &lt;mapper resource="org/mybatis/builder/PostMapper.xml"/&gt;
     * &lt;/mappers&gt;
     * </pre>
     * <p>
     * 解析流程：
     * 1. 遍历所有 mapper 标签
     * 2. 获取 resource 属性（Mapper.xml 文件路径）
     * 3. 加载 XML 文件为输入流
     * 4. 为每个 Mapper 文件创建 XMLMapperBuilder 解析器
     * 5. 调用 parse() 方法解析 Mapper 文件（SQL 语句、结果映射等）
     * <p>
     * 注意：
     * - 每个 Mapper 文件都会创建独立的 XMLMapperBuilder 实例
     * - XMLMapperBuilder 负责解析具体的 SQL 语句和映射配置
     * - 解析结果会注册到 Configuration 的 mappedStatements 中
     *
     * @param mappers mappers 元素节点
     * @throws Exception 解析过程中可能抛出的异常
     */
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element e : mapperList) {
            String resource = e.attributeValue("resource");
            InputStream inputStream = Resources.getResourceAsStream(resource);

            // 在for循环里每个mapper都重新new一个XMLMapperBuilder，来解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
            mapperParser.parse();
        }
    }

}