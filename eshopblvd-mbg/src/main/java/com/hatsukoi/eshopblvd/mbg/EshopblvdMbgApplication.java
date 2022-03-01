package com.hatsukoi.eshopblvd.mbg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用MyBatisGenerator生成逆向工程
 */
public class EshopblvdMbgApplication {
    public static void main(String[] args) throws IOException, XMLParserException, SQLException, InterruptedException, InvalidConfigurationException {
        // 告警信息
        List<String> warnings = new ArrayList<>();
        // 当生成的代码重复时，覆盖原代码
        boolean overwrite = true;
        // 解析读取MybatisGenerator配置文件
        InputStream inputStream = EshopblvdMbgApplication.class.getResourceAsStream("/generatorConfig.xml");
        ConfigurationParser configurationParser = new ConfigurationParser(warnings);
        Configuration configuration = configurationParser.parseConfiguration(inputStream);
        inputStream.close();

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        // 创建 MBG
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
        // 执行生成代码
        myBatisGenerator.generate(null);
        // 输出警告信息
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }
}
