package com.feifei.reference;

import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;

/**
 * @ClassName: NormalReference
 * @Author chengfei
 * @Date 2020/11/15 17:47
 * @Description: TODO 当一个对象指向null的时候，他对应的存储空间就会被回收
 **/
public class NormalReference {
    public static void main(String[] args) throws IOException {
        //
        M m = new M();
//        System.out.println(ClassLayout.parseInstance(m).toPrintable());
        m = null;

        System.gc();
        System.in.read();
    }
}
