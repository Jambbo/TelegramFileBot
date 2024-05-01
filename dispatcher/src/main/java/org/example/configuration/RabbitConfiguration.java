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

    @Bean
    public Queue textMessageQueue(){
        return new Queue(TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMessageQueue(){
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMessageQueue(){
        return new Queue(DOC_MESSAGE_UPDATE);
    }
    //очередь в которую будут помещаться ответы от нод, адресуемые диспатчеру,
    // ну и дальше конечному пользователю в конкретный чат
    @Bean
    public Queue answerMessageQueue(){
        return new Queue(ANSWER_MESSAGE);
    }


}
