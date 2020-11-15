package com.fei.workcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/10/11.
 */
public class WorkCount {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("spark workcount app");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        String hdfsPath = "hdfs://192.168.52.100:8020";
        String inputPath = hdfsPath + "/input/WorkCount.txt";
        String outputPath = hdfsPath + "/output/"
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        System.out.println("input path : " + inputPath);
        System.out.println("output path : " + outputPath);
        JavaRDD<String> flieRDD = javaSparkContext.textFile(inputPath);
//        flieRDD.map(new MapFunction<String,String>())

        JavaPairRDD<String, Integer> wordRDD = flieRDD.flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        JavaPairRDD<Integer, String> sortWordRDD = wordRDD.mapToPair(tuple2 -> new Tuple2<>(tuple2._2(), tuple2._1()))
                .sortByKey(false);
        List<Tuple2<Integer, String>> top10List = sortWordRDD.take(10);
        for (Tuple2<Integer, String> tuple2 : top10List) {
            System.out.println(tuple2._2() + "\t" + tuple2._1());

        }

        javaSparkContext.parallelize(top10List).coalesce(1)
                .saveAsTextFile(outputPath);
        javaSparkContext.close();
    }
}
