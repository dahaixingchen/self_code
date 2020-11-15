package com.feifei;

/**
 * @ClassName: Test
 * @Author chengfei
 * @Date 2020/11/15 12:46
 * @Description: TODO
 **/
public class Test {
    public String s = "men and woman";
    public void test(){
        System.out.println("我是方法");
        ThreadLocal<Object> objectThreadLocal = new ThreadLocal<>();
        objectThreadLocal.set("dfasf");
        Object o = objectThreadLocal.get();
    }
}

