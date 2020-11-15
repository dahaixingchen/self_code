package com.fei.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * @Author: chengfei
 * @Date : 2019-10-18 15:38
 * @Description:
 */
public class MyKafkaConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092,node02:9092,node03:9092");


    }
}
