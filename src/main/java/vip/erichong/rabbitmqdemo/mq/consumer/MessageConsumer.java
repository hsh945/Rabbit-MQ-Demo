package vip.erichong.rabbitmqdemo.mq.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import vip.erichong.rabbitmqdemo.mq.config.RabbitMQConfig;

import java.time.LocalDateTime;

/**
 * @author eric
 */
@Component
public class MessageConsumer {
    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUE)
    public void handleDeadLetterMessage(Message message) {
        System.out.println("[" + LocalDateTime.now() + "] 收到延迟消息: " + new String(message.getBody()));
        // 处理延迟后的业务逻辑
    }
}
