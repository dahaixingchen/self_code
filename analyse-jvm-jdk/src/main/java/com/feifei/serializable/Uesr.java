package com.feifei.serializable;

import java.io.Serializable;

/**
 * @ClassName: Uesr
 * @Author chengfei
 * @Date 2020/11/19 11:22
 * @Description: TODO 什么是序列化，
 **/
public class Uesr implements Serializable{
    private static final long serialversionUID = 1010;

    private Integer id ;
    private String name ;
    private String dec ;
    private String company ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
