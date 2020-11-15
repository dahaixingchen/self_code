package com.fei.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: chengfei
 * @Date : 2019-10-18 14:13
 * @Description: 写入待分区号的数据
 */
public class MyKafkaProducer {
    private static String topic = "test";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092,node02:9092,node03:9092");
        properties.put("acks", "all");
        properties.put("retries", "10");
        properties.put("enable.idempotence", true); //开启幂等性功能，保证消息的仅仅被处理一次
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.fei.common.MyInterceptors");//自定义拦截器

        ArrayList<String> dataList = new ArrayList<String>();
        dataList.add("64600196936,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196936,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196936,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196936,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196936,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196937,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64605696937,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");
        dataList.add("64600196937,沪ET8058,65.0,2019-10-12 17:47:55,1570873675000,121.64478,29.961416,1,334,,,,true,0.0,\n");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);
        for (String s : dataList) {
            String[] strs = s.split(",");
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, strs[0], s);
//w            MyInterceptors myInterceptors = new MyInterceptors();
//            myInterceptors.onSend()
            Future<RecordMetadata> future = kafkaProducer.send(record);
            RecordMetadata metadata = future.get();
            System.out.println(metadata.partition());

        }

        kafkaProducer.close();
    }
}
