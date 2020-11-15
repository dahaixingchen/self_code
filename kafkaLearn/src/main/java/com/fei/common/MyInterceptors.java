package com.fei.common;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * @Author: chengfei
 * @Date : 2019-10-18 14:37
 * @Description:
 */
public class MyInterceptors implements ProducerInterceptor {
    public ProducerRecord onSend(ProducerRecord pr) {
        System.out.println("partition" + pr.partition() + "-topic" + pr.topic() +
                "-key" + pr.key() + "-headers" + pr.headers());
        return pr;
    }

    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    public void close() {

    }

    public void configure(Map<String, ?> map) {

    }
}
