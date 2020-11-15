package com.feifei.sync;

import org.openjdk.jol.info.ClassLayout;

/**
 * @ClassName: T01_Sync1
 * @Author chengfei
 * @Date 2020/11/12 11:08
 * @Description: TODO
 **/
public class T01_Sync1 {
    public static void main(String[] args) {
        char[] chars = new char[6];
         char[] id1 = new char[61];
        User user = new User();
        String str = "ssssyyypggdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssfsadddddddddddddd";
//        Object o = new Object();
//        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        System.out.println(ClassLayout.parseInstance(chars).toPrintable());
        System.out.println(ClassLayout.parseInstance(user).toPrintable());
//        System.out.println(ClassLayout.parseInstance(str).toPrintable());
    }
    static class User{
//        private int id = 12;
//        private String name = "sdfasfsdfdsfsdfdasfds";

        private int id;
        private char[] id1 = new char[61];
    }
}
