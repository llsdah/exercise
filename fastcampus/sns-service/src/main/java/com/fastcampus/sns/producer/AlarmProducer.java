package com.fastcampus.sns.producer;


import com.fastcampus.sns.model.event.AlarmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmProducer {

    @Value("${spring.kafka.topic.alarm}")
    private String topic;

    private final KafkaTemplate<Integer, Object> kafkaTemplate;

    public void send(AlarmEvent event) {
        kafkaTemplate.send(topic, event.getReceiverUserId(), event);
        log.info("send kafka send event is {}",event.toString());
    }
}
