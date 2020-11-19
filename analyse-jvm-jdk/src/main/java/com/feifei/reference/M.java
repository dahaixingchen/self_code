package com.feifei.reference;

/**
 * @ClassName: M
 * @Author chengfei
 * @Date 2020/11/15 17:45
 * @Description: TODO
 **/
public class M {
    int i;
    @Override
    protected void finalize(){

        System.out.println("垃圾回收器已经回收，finalize");
    }
}
