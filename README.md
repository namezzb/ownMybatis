# Own MyBatis

> 🎯 一个从零开始手写的 MyBatis 框架，用于深入理解 MyBatis 核心原理

## 📖 项目简介

本项目是一个简化版的 MyBatis ORM 框架实现，通过手写核心功能来深入理解 MyBatis 的设计思想和实现原理。项目采用渐进式开发，逐步实现了 MyBatis 的核心特性。

**学习目标：**
- 理解 ORM 框架的设计思想
- 掌握动态代理、反射、建造者等设计模式
- 深入理解 MyBatis 的 SQL 解析和执行流程
- 学习连接池、事务管理等数据库编程技术

## ✨ 已实现功能

### 核心功能模块

#### 1. 配置解析与管理
- ✅ XML 配置文件解析（mybatis-config.xml）
- ✅ Mapper XML 文件解析
- ✅ 统一的 Configuration 配置类管理
- ✅ 类型别名注册与解析（TypeAliasRegistry）
- ✅ 类型处理器注册（TypeHandlerRegistry）

#### 2. SQL 解析与处理
- ✅ XML 语句构建器（XMLStatementBuilder）
- ✅ SQL 源码抽象（SqlSource）
  - 静态 SQL 源码（StaticSqlSource）
  - 原始 SQL 源码（RawSqlSource）
- ✅ 参数映射解析（ParameterMapping）
- ✅ 动态 SQL 节点支持（SqlNode）
  - 静态文本节点（StaticTextSqlNode）
  - 混合节点（MixedSqlNode）
- ✅ 通用 Token 解析器（GenericTokenParser）
- ✅ `#{}` 占位符解析与参数化

#### 3. Mapper 接口代理
- ✅ Mapper 接口动态代理（MapperProxy）
- ✅ Mapper 注册器（MapperRegistry）
- ✅ 方法签名解析与缓存

#### 4. SQL 执行引擎
- ✅ SqlSession 会话管理
- ✅ Executor 执行器抽象
- ✅ StatementHandler 语句处理器
  - PreparedStatementHandler
  - SimpleStatementHandler
- ✅ ResultSetHandler 结果集处理器

#### 5. 数据源管理
- ✅ 数据源工厂抽象（DataSourceFactory）
- ✅ UNPOOLED 非池化数据源
- ✅ POOLED 池化数据源（自实现连接池）
- ✅ Druid 数据源集成

#### 6. 事务管理
- ✅ 事务抽象接口（Transaction）
- ✅ JDBC 事务管理（JdbcTransaction）
- ✅ 事务工厂（TransactionFactory）

#### 7. 反射工具包
- ✅ Reflector 反射器（类元信息缓存）
- ✅ MetaObject 元对象（对象实例反射操作）
- ✅ MetaClass 元类（Class 类型反射操作）
- ✅ ObjectWrapper 对象包装器
  - BeanWrapper（JavaBean 包装）
  - MapWrapper（Map 包装）
  - CollectionWrapper（集合包装）
- ✅ PropertyTokenizer 属性表达式解析器
- ✅ 支持复杂属性路径（如 `user.address.city`）

#### 8. 语言驱动
- ✅ LanguageDriver 语言驱动抽象
- ✅ XMLLanguageDriver XML 语言驱动
- ✅ XMLScriptBuilder 脚本构建器
- ✅ DynamicContext 动态上下文

## 🏗️ 项目架构

```
own-mybatis
├── binding          # Mapper 接口绑定与代理
├── builder          # 构建器（XML 解析、SQL 构建）
│   └── xml         # XML 配置解析
├── datasource       # 数据源实现
│   ├── pooled      # 池化数据源
│   ├── unpooled    # 非池化数据源
│   └── druid       # Druid 数据源
├── executor         # SQL 执行器
│   ├── statement   # 语句处理器
│   └── resultset   # 结果集处理器
├── io               # 资源加载工具
├── mapping          # SQL 映射相关
├── parsing          # 通用解析工具
├── reflection       # 反射工具包
│   ├── factory     # 对象工厂
│   ├── invoker     # 方法/字段调用器
│   ├── property    # 属性解析
│   └── wrapper     # 对象包装器
├── scripting        # 脚本语言支持
│   ├── defaults    # 默认实现
│   └── xmltags     # XML 标签处理
├── session          # 会话管理
│   └── defaults    # 默认实现
├── transaction      # 事务管理
│   └── jdbc        # JDBC 事务
└── type             # 类型系统
```

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.x
- MySQL 5.7+

### 配置文件

#### 1. mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test"/>
                <property name="username" value="root"/>
                <property name="password" value="password"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```

#### 2. UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.zzb.mybatis.test.dao.IUserDao">
    <select id="queryUserInfoById"
            parameterType="java.lang.Long"
            resultType="cn.zzb.mybatis.test.po.User">
        SELECT id, userId, userName, userHead
        FROM user
        WHERE id = #{id}
    </select>
</mapper>
```

### 使用示例

```java
// 1. 加载配置文件
Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");

// 2. 构建 SqlSessionFactory
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

// 3. 获取 SqlSession
SqlSession sqlSession = sqlSessionFactory.openSession();

// 4. 获取 Mapper 代理对象
IUserDao userDao = sqlSession.getMapper(IUserDao.class);

// 5. 执行查询
User user = userDao.queryUserInfoById(1L);
System.out.println(user);

// 6. 关闭会话
sqlSession.close();
```

## 📚 核心设计模式

### 1. 建造者模式（Builder Pattern）
- `SqlSessionFactoryBuilder`：构建 SqlSessionFactory
- `XMLConfigBuilder`：构建 Configuration
- `MappedStatement.Builder`：构建 MappedStatement

### 2. 工厂模式（Factory Pattern）
- `SqlSessionFactory`：创建 SqlSession
- `DataSourceFactory`：创建数据源
- `TransactionFactory`：创建事务
- `ObjectFactory`：创建对象实例

### 3. 代理模式（Proxy Pattern）
- `MapperProxy`：Mapper 接口动态代理
- `PooledConnection`：连接池代理

### 4. 策略模式（Strategy Pattern）
- `LanguageDriver`：不同的 SQL 语言驱动
- `StatementHandler`：不同的语句处理策略

### 5. 组合模式（Composite Pattern）
- `SqlNode`：SQL 节点树形结构
- `MixedSqlNode`：组合多个 SQL 节点

### 6. 模板方法模式（Template Method Pattern）
- `BaseBuilder`：提供通用的构建方法

## 🔍 核心流程解析

### SQL 执行流程

```
1. 用户调用 Mapper 方法
   ↓
2. MapperProxy 拦截方法调用
   ↓
3. 从 Configuration 获取 MappedStatement
   ↓
4. SqlSource.getBoundSql(参数) 生成 BoundSql
   ↓
5. StatementHandler 创建 PreparedStatement
   ↓
6. ParameterHandler 设置参数
   ↓
7. 执行 SQL 查询
   ↓
8. ResultSetHandler 处理结果集
   ↓
9. 返回结果对象
```

### SQL 解析流程

```
1. XMLConfigBuilder 解析 mybatis-config.xml
   ↓
2. XMLMapperBuilder 解析 Mapper.xml
   ↓
3. XMLStatementBuilder 解析 <select> 等标签
   ↓
4. LanguageDriver.createSqlSource()
   ↓
5. XMLScriptBuilder 构建 SqlNode 树
   ↓
6. SqlSourceBuilder 解析 #{} 占位符
   ↓
7. 生成 StaticSqlSource（包含 SQL 和参数映射）
```

## 🛠️ 技术栈

- **核心框架**：Java 8
- **构建工具**：Maven
- **数据库驱动**：MySQL Connector
- **XML 解析**：DOM4J
- **连接池**：自实现 + Druid
- **日志框架**：SLF4J + Logback
- **工具库**：Hutool、Lombok
- **表达式引擎**：OGNL
- **JSON 处理**：Fastjson
- **测试框架**：JUnit 4

## 📖 学习资源

### 推荐阅读

1. **MyBatis 官方文档**：https://mybatis.org/mybatis-3/zh/index.html
2. **《MyBatis 技术内幕》** - 徐郡明
3. **《MyBatis 从入门到精通》** - 刘增辉

### 相关博客

- [MyBatis 源码分析系列](https://bugstack.cn)
- [手写 MyBatis 教程](https://github.com/fuzhengwei/CodeDesignTutorials)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目仅用于学习交流，请勿用于商业用途。

---

⭐ 如果这个项目对你有帮助，欢迎 Star！