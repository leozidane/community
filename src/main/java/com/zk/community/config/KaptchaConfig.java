package com.zk.community.config;


import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * springboot没有自动配置的包需要手段设置配置类，注入到spring容器中
 *
 */
@Configuration
public class KaptchaConfig {

    /**
     * Producer是一个接口
     * 其两个方法一个能返回生成的随机字符串，一个是能根据返回的随机字符串生成图片
     * @return
     */
    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        //设置参数
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789QWERTYUIOPASDFGHJKLZXCVBNM");
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        //DefaultKaptcha是Producer一个实现类
        DefaultKaptcha kaptcha = new DefaultKaptcha();

        //config是生成验证码所需要的配置参数，构造参数为一个properties对象
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
