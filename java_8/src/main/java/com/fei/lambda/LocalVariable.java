package com.fei.lambda;

/**
 * @ClassName: LocalVariable
 * @Description: TODO
 * @Author chengfei
 * @Date 2020/1/7 14:07
 * @Version 1.0
 **/
public class LocalVariable {
    static String s = "dfa";

    public static void main(String[] args) {

        final int portNumber = 110;
        Runnable runnable = () -> System.out.println(s);
        runnable.run();
//        portNumber = 44;
    }
}
