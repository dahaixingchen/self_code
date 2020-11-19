package com.feifei.reference;

import java.lang.ref.WeakReference;

/**
 * @ClassName: SelfWarkReference
 * @Author chengfei
 * @Date 2020/11/15 19:43
 * @Description: TODO 弱引用，只要gc，就会被回收，threadLocal就是用这个实现的
 **/
public class SelfWeakReference {
    public static void main(String[] args) {
        WeakReference<byte[]> weakReference = new WeakReference<>(new byte[1024*1024*10]);

        System.out.println(weakReference.get());
        System.gc();
        System.out.println(weakReference.get());

        ThreadLocal<M> mThreadLocal = new ThreadLocal<>();
        mThreadLocal.set(new M());
        mThreadLocal.remove();

    }
}
