package com.feifei;

/**
 * @ClassName: CpuReorder
 * @Author chengfei
 * @Date 2020/11/15 12:39
 * @Description: TODO 多线程时候如果CPU都是按次序执行，那只可能出现x =0,y = 1或是x = 1,y = 0，绝对不会出现x = 0,y=0,或是x = 1,y =1
 **/
public class CpuReorder {

    static int a = 0;
    static int b = 0;
    static int x = 0;
    static int y = 0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for (;;){
            i++ ;
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    a = 1;
                    x = b;
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    b = 1;
                    y = a;
                }
            });
            thread1.start();thread2.start();
            thread1.join();thread2.join();
            String result = "第"+ i +"次（" + x + "," + y + "）";
            if (x == 0 && y == 0){
                System.out.println(result);
                break;
            }
        }
    }
}
