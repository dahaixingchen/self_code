package com.feifei.cache;

/**
 * @ClassName: CacheLine
 * @Author chengfei
 * @Date 2020/11/15 9:47
 * @Description: TODO  证明缓存行是64Byte（字节），修改数据
 **/
public class CacheLine {
    private static class T{
        public volatile long x = 0L;
    }

    public static T[] arr = new T[2];

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
