package com.zizoy.treasurehouse.model;

/**
 * @author falcon
 * @Description: 城市
 */
public class CityModel {
    private int id; //城市ID
    private String name; // 城市名
    private String sortLetters; // 显示数据拼音的首字母

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}