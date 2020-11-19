package com.feifei.serializable;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @ClassName: Main
 * @Author chengfei
 * @Date 2020/11/19 12:43
 * @Description: TODO
 **/

public class Main {

    /**
     * @Description: 带序列化写入对象
      * @param args
     * @return void
     * @date: 2020/11/19 12:57
     */
    public static void main(String[] args) throws IOException {
        Uesr uesr = new Uesr();
        uesr.setId(9547);
        uesr.setName("程飞1254");
        uesr.setDec("支撑组成员");
        uesr.setCompany("漫微科技");
        File file = new File("E:\\tmp\\test.txt");
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
        stream.writeObject(uesr);
        stream.flush();
        stream.close();
    }

    @Test
    public void test1() throws IOException, ClassNotFoundException {
        File file = new File("E:\\tmp\\test.txt");
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
        Object o = stream.readObject();
        System.out.println(o);
    }

    /**
     * @Description: 不用序列化不能写入对象到，
     * java.io.NotSerializableException: com.feifei.serializable.Uesr
      * @param
     * @return void
     * @date: 2020/11/19 12:56
     */
    @Test
    public void test2() throws IOException {
        Uesr uesr = new Uesr();
        uesr.setId(9547);
        uesr.setName("程飞");
        uesr.setDec("支撑组成员");
        uesr.setCompany("漫微科技");
        File file = new File("E:\\tmp\\test1.txt");
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
        stream.writeObject(uesr);
        stream.flush();
        stream.close();
    }
}
