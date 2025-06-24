package vip.erichong.rabbitmqdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.erichong.rabbitmqdemo.mq.producer.RabbitMQProducer;

import java.util.Optional;

/**
 * @author eric
 */
@RestController
@RequestMapping("/message")
public class TestController {
    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @GetMapping("/pushDelayMessage")
    public void pushDelayMessage(@RequestParam(value = "message", required = false) String message) {
        String msg = Optional.ofNullable(message).orElse("delay message");

        // 延迟 20 秒后，由 MessageConsumer.handleDeadLetterMessage 消费消息
        rabbitMQProducer.sendDelayedMessage(msg, 20);
    }
}
