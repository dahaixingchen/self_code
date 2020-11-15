package com.feifei.reference;

import java.lang.ref.SoftReference;

/**
 * @ClassName: SelfSoftReference
 * @Author chengfei
 * @Date 2020/11/15 17:34
 * @Description: TODO
 **/
public class SelfSoftReference {
    public static void main(String[] args) throws InterruptedException {
        SoftReference<byte[]> m = new SoftReference<>(new byte[1024*1024*20]);

        System.out.println(m.get());
        System.gc();

        Thread.sleep(500);
        byte[] bytes = new byte[1024 * 1024 * 15];
        System.out.println(m.get());
    }
}
