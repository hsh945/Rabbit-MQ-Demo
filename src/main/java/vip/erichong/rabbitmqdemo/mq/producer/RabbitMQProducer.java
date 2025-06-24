package vip.erichong.rabbitmqdemo.mq.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import vip.erichong.rabbitmqdemo.mq.config.RabbitMQConfig;

import java.time.LocalDateTime;

/**
 * @author eric
 */
@Service
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // 发送延迟消息
    public void sendDelayedMessage(String message, int delaySeconds) {
        System.out.println("[" + LocalDateTime.now() + "] 发送延迟消息: " + message + ", 延迟: " + delaySeconds + "秒");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WORK_EXCHANGE,
                RabbitMQConfig.WORK_ROUTING_KEY,
                message,
                msg -> {
                    msg.getMessageProperties().setExpiration(String.valueOf(delaySeconds * 1000));
                    return msg;
                }
        );
    }
}