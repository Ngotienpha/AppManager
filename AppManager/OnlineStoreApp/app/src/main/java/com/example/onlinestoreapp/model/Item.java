package com.example.onlinestoreapp.model;

public class Item {
    int idsp;
    String name;
    int soluong;
    String image;

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getHinhanh() {
        return image;
    }

    public void setHinhanh(String hinhanh) {
        this.image = hinhanh;
    }

    public int getIdsp() {
        return idsp;
    }

    public void setIdsp(int idsp) {
        this.idsp = idsp;
    }

    public String getTensp() {
        return name;
    }

    public void setTensp(String tensp) {
        this.name = tensp;
    }
}
