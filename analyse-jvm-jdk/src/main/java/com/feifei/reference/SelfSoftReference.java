package com.feifei.reference;

import java.lang.ref.SoftReference;

/**
 * @ClassName: SelfSoftReference
 * @Author chengfei
 * @Date 2020/11/15 17:34
 * @Description: TODO 软引用，它就特点：gc发生后，它开辟的内存不会被回收，当程序再次申请内存
 *                TODO，堆内存 不够的时候，自动就会回收，用软引用开辟的内存，
 *                TODO  它的一般用来做缓存
 **/
public class SelfSoftReference {
    public static void main(String[] args) {
        SoftReference<byte[]> m = new SoftReference<>(new byte[1024 * 1024 * 10]);
        long l = 10L;
        byte[] longs = new byte[1024*1024*2];
        System.out.println(m.get());
        System.gc();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(m.get());
        byte[] bytes = new byte[1024 * 1024 * 10];
        System.out.println(m.get());
    }
}
