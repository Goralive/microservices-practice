package com.microservices.notification;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    @Getter
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Getter
    @Value("${rabbitmq.queue.notification}")
    private String notificationQueue;

    @Getter
    @Value("${rabbitmq.routing-keys.internal-notification}")
    private String internalNotificationRoutingKeys;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(this.notificationQueue);
    }

    @Bean
    public Binding internalToNotificationQueueBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(internalTopicExchange())
                .with(this.internalNotificationRoutingKeys);
    }

}
