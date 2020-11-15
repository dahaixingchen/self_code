package com.feifei.cache;

/**
 * @ClassName: CacheLineSuper
 * @Author chengfei
 * @Date 2020/11/15 11:02
 * @Description: TODO 利用缓存行的原理，填充成完成的缓存行能优化运行速度
 **/
public class CacheLineSuper {
    static class Apadding{
        static volatile long l1,l2,l3 ,l4,l5,l6,l7;
    }
    static class T extends Apadding{
        volatile long x = 0L;
    }

    static T[] arr = new T[2];

    static {
        arr[0] = new T();
        arr[1] = new T();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (long i = 0; i < 100000000; i++) {
                arr[0].x = i;
            }
        });

        Thread thread2 = new Thread(() -> {
            for (long i = 0; i < 100000000; i++) {
                arr[1].x = i;
            }
        });

        long l = System.nanoTime(); //它返回的是毫微秒

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        System.out.println((System.nanoTime() - l) /1000000);

    }
}
