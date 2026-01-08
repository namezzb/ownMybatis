package cn.zzb.mybatis.test.loader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

/**
 * XML 解析详细示例
 */
public class XMLParseTest {

    // 示例 XML 内容
    private static final String XML_CONTENT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<configuration>\n" +
        "    <settings>\n" +
        "        <setting name=\"cacheEnabled\" value=\"true\"/>\n" +
        "        <setting name=\"lazyLoadingEnabled\" value=\"false\"/>\n" +
        "    </settings>\n" +
        "    <mappers>\n" +
        "        <mapper resource=\"mapper/User_Mapper.xml\"/>\n" +
        "        <mapper resource=\"mapper/Order_Mapper.xml\"/>\n" +
        "    </mappers>\n" +
        "</configuration>";

    @Test
    public void test1_BasicParsing() throws DocumentException {
        System.out.println("=== 1. 基础解析：读取 XML 文档 ===\n");

        // 1. 创建 SAXReader
        SAXReader saxReader = new SAXReader();
        System.out.println("1. 创建 SAXReader:");
        System.out.println("   SAXReader 是 dom4j 的 XML 解析器");

        // 2. 读取 XML 文档
        Document document = saxReader.read(new StringReader(XML_CONTENT));
        System.out.println("\n2. 读取 XML 文档:");
        System.out.println("   Document 对象代表整个 XML 文档");

        // 3. 获取根元素
        Element root = document.getRootElement();
        System.out.println("\n3. 获取根元素:");
        System.out.println("   根元素名称: " + root.getName());
        System.out.println("   根元素类型: " + root.getClass().getSimpleName());
    }

    @Test
    public void test2_GetChildElements() throws DocumentException {
        System.out.println("=== 2. 获取子元素 ===\n");

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new StringReader(XML_CONTENT));
        Element root = document.getRootElement();

        System.out.println("XML 结构:");
        System.out.println("<configuration>");
        System.out.println("    <settings>...</settings>");
        System.out.println("    <mappers>...</mappers>");
        System.out.println("</configuration>");

        // 方式 1：element() - 获取单个子元素
        System.out.println("\n方式 1: element(String name) - 获取单个子元素");
        Element settings = root.element("settings");
        System.out.println("   settings 元素: " + settings.getName());

        Element mappers = root.element("mappers");
        System.out.println("   mappers 元素: " + mappers.getName());

        // 方式 2：elements() - 获取所有子元素
        System.out.println("\n方式 2: elements() - 获取所有子元素");
        List<Element> allChildren = root.elements();
        System.out.println("   所有子元素数量: " + allChildren.size());
        for (Element child : allChildren) {
            System.out.println("   - " + child.getName());
        }

        // 方式 3：elements(String name) - 获取指定名称的所有子元素
        System.out.println("\n方式 3: elements(String name) - 获取指定名称的所有子元素");
        List<Element> settingList = settings.elements("setting");
        System.out.println("   <setting> 元素数量: " + settingList.size());
        for (Element setting : settingList) {
            System.out.println("   - " + setting.getName());
        }
    }

    @Test
    public void test3_GetAttributes() throws DocumentException {
        System.out.println("=== 3. 获取属性 ===\n");

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new StringReader(XML_CONTENT));
        Element root = document.getRootElement();

        Element settings = root.element("settings");
        List<Element> settingList = settings.elements("setting");

        System.out.println("XML 结构:");
        System.out.println("<setting name=\"cacheEnabled\" value=\"true\"/>");
        System.out.println("         ↑ 属性名          ↑ 属性名");
        System.out.println("         ↓ 属性值          ↓ 属性值");

        System.out.println("\n解析属性:");
        for (Element setting : settingList) {
            // attributeValue(String name) - 获取属性值
            String name = setting.attributeValue("name");
            String value = setting.attributeValue("value");

            System.out.println("   <setting>");
            System.out.println("      name 属性: " + name);
            System.out.println("      value 属性: " + value);
        }
    }

    @Test
    public void test4_GetText() throws DocumentException {
        System.out.println("=== 4. 获取元素文本内容 ===\n");

        String xmlWithText =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<mapper namespace=\"cn.zzb.mybatis.test.dao.IUserDao\">\n" +
            "    <select id=\"selectUserNameById\" resultType=\"String\">\n" +
            "        SELECT username FROM user WHERE id = #{id}\n" +
            "    </select>\n" +
            "</mapper>";

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new StringReader(xmlWithText));
        Element root = document.getRootElement();

        System.out.println("XML 结构:");
        System.out.println("<select id=\"selectUserNameById\">");
        System.out.println("    SELECT username FROM user WHERE id = #{id}  ← 文本内容");
        System.out.println("</select>");

        Element select = root.element("select");

        // getText() - 获取元素的文本内容
        String sql = select.getText();
        System.out.println("\n获取文本内容:");
        System.out.println("   SQL: " + sql.trim());

        // getTextTrim() - 获取文本内容并去除首尾空白
        String sqlTrim = select.getTextTrim();
        System.out.println("   SQL (trim): " + sqlTrim);
    }

    @Test
    public void test5_CompleteExample() throws DocumentException {
        System.out.println("=== 5. 完整示例：解析 Mapper XML ===\n");

        String mapperXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<mapper namespace=\"cn.zzb.mybatis.test.dao.IUserDao\">\n" +
            "    <select id=\"selectUserById\" parameterType=\"int\" resultType=\"User\">\n" +
            "        SELECT * FROM user WHERE id = #{id}\n" +
            "    </select>\n" +
            "    <select id=\"selectUserByName\" parameterType=\"String\" resultType=\"User\">\n" +
            "        SELECT * FROM user WHERE username = #{username}\n" +
            "    </select>\n" +
            "    <insert id=\"insertUser\" parameterType=\"User\">\n" +
            "        INSERT INTO user (username, age) VALUES (#{username}, #{age})\n" +
            "    </insert>\n" +
            "</mapper>";

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new StringReader(mapperXml));
        Element root = document.getRootElement();

        // 1. 获取 namespace
        String namespace = root.attributeValue("namespace");
        System.out.println("1. Namespace: " + namespace);

        // 2. 解析所有 select 元素
        System.out.println("\n2. 解析 SELECT 语句:");
        List<Element> selectNodes = root.elements("select");
        for (Element select : selectNodes) {
            String id = select.attributeValue("id");
            String parameterType = select.attributeValue("parameterType");
            String resultType = select.attributeValue("resultType");
            String sql = select.getTextTrim();

            System.out.println("   ---");
            System.out.println("   ID: " + id);
            System.out.println("   参数类型: " + parameterType);
            System.out.println("   返回类型: " + resultType);
            System.out.println("   SQL: " + sql);
        }

        // 3. 解析所有 insert 元素
        System.out.println("\n3. 解析 INSERT 语句:");
        List<Element> insertNodes = root.elements("insert");
        for (Element insert : insertNodes) {
            String id = insert.attributeValue("id");
            String parameterType = insert.attributeValue("parameterType");
            String sql = insert.getTextTrim();

            System.out.println("   ---");
            System.out.println("   ID: " + id);
            System.out.println("   参数类型: " + parameterType);
            System.out.println("   SQL: " + sql);
        }
    }
}
