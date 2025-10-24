package com.example.music_app.model;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaiHat implements Serializable {
    private int id;
    private String tenBaiHat;
    private String caSi;
    private String hinhAnh;
    private String link;

    public int getId() { return id; }
    public String getTenBaiHat() { return tenBaiHat; }
    public String getCaSi() { return caSi; }

    // Xử lý URL giống như TheLoai
    public String getHinhAnh() {
        if (hinhAnh != null && hinhAnh.startsWith("/")) {
            return "http://192.168.126.1:8080" + hinhAnh;
        }
        return hinhAnh;
    }

    public String getLink() { return link; }
    @Override
    public String toString() {
        return "BaiHat{" +
                "id=" + id +
                ", tenBaiHat='" + tenBaiHat + '\'' +
                ", caSi='" + caSi + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                '}';
    }
}