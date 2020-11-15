package com.feifei;

import java.sql.SQLOutput;

/**
 * @ClassName: 匿名内部类Test
 * @Author chengfei
 * @Date 2020/11/15 12:46
 * @Description: TODO 匿名内部类可以看做是成员内部类的简洁写法
 **/
public class 匿名内部类Test extends Test implements TestInte {
    public static void main(String[] args) {
        Test test = new Test() {
            //匿名内部内
            int i = 10;
            String str = s;
            public void test(){
                System.out.println(str);
            }
        };
        test.test();
    }
}
