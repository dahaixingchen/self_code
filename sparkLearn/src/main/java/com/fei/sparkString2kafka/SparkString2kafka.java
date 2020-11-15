package com.fei.sparkString2kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * Author: 程飞
 * Created by Administrator on 2019/10/12.
 * Description:
 */
public class SparkString2kafka {
    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[2]")
                .setAppName("sparkString-kafka");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        sc.setLogLevel("WARN");
        JavaStreamingContext streamingContext =
                new JavaStreamingContext(sc, Durations.seconds(10L));


        HashMap<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "192.168.100.177:9092,192.168.100.11:9092,192.168.100.204:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "streaming-kafka");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("vdp_source");

        JavaInputDStream<ConsumerRecord<String, String>> kafkaStream =
                KafkaUtils.createDirectStream(streamingContext, LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics,
                                kafkaParams));


//        kafkaStream.print();
        JavaDStream<String> dStream = kafkaStream.flatMap(x -> Arrays.asList(x.value().toString())
                .iterator());

        dStream.print();

        streamingContext.start();
        streamingContext.awaitTermination();
        streamingContext.close();
    }


}
