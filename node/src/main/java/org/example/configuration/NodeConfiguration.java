package org.example.configuration;

import org.checkerframework.checker.units.qual.C;
import org.example.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//этот класс нужен для того чтобы подключить наш CryptoTool из common-utils
// в качестве спрингового бина!
@Configuration
public class NodeConfiguration  {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool cryptoTool(){
        return new CryptoTool(salt);
    }

}
