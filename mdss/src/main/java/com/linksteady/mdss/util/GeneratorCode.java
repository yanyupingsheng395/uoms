package com.linksteady.mdss.util;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GeneratorCode {

    private static void shell() {
        List<String> warnings = new ArrayList<>();
        try {
            boolean overwrite = true;
            File configFile = ResourceUtils.getFile("classpath:config/mybatis-generator.xml");
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            ProgressCallback progressCallback = new VerboseProgressCallback();
            myBatisGenerator.generate(null);
            myBatisGenerator.generate(progressCallback);
        } catch (Exception e) {
            log.error("Shell Exception:", e);
        }
        for (String warning : warnings) {
            log.warn("Shell Warnings:", warning);
        }
    }

    public static void main(String[] args) {
        shell();
    }
}

