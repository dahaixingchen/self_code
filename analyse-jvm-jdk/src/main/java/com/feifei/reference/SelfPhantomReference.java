package com.feifei.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * @ClassName: SelfPhantomReference
 * @Author chengfei
 * @Date 2020/11/15 20:03
 * @Description: TODO 虚引用，用来回收堆外内存的（程序中用到的操作系统的内存），在NIo中用到
 **/
public class SelfPhantomReference {

    public static void main(String[] args) {
        ReferenceQueue referenceQueue = new ReferenceQueue();
        PhantomReference<byte[]> phantomReference = new PhantomReference<byte[]>(new byte[1024*1024*10],referenceQueue);
    }
}
