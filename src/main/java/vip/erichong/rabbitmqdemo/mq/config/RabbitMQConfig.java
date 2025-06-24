package vip.erichong.rabbitmqdemo.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eric
 */
@Configuration
public class RabbitMQConfig {

    // 死信队列相关配置
    public static final String DEAD_LETTER_EXCHANGE = "dlx.exchange";
    public static final String DEAD_LETTER_QUEUE = "dlx.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "dlx.key";

    // 业务队列相关配置
    public static final String WORK_EXCHANGE = "work.exchange";
    public static final String WORK_QUEUE = "work.queue";
    public static final String WORK_ROUTING_KEY = "work.key";

    // 配置死信交换器
    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // 配置死信队列
    @Bean
    Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    // 绑定死信队列到死信交换器
    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    // 配置业务交换器
    @Bean
    DirectExchange workExchange() {
        return new DirectExchange(WORK_EXCHANGE);
    }

    // 配置业务队列，并设置死信交换器
    @Bean
    Queue workQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        return new Queue(WORK_QUEUE, true, false, false, args);
    }

    // 绑定业务队列到业务交换器
    @Bean
    Binding workBinding() {
        return BindingBuilder.bind(workQueue())
                .to(workExchange())
                .with(WORK_ROUTING_KEY);
    }

    // 配置消息转换器
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 配置RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}