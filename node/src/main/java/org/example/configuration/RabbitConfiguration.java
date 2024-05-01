package org.example.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

import static org.example.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {
    //бин, который будет преобразовывать апдейты в json и передавать их в RabbitMQ
    //и при получении этих апдейтов обратно в приложение оно будет их преобразовывать соотвественно в Java object
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
