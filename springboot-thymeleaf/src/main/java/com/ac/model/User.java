package com.ac.model;

/**
 * @author anchao
 * @date 2020/2/24 16:32
 */
public class User {
    private Integer id;
    private String name;
    private String address;
    //..get..set


    public User(Integer id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public User(){

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
