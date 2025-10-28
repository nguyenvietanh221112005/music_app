package com.example.music_app.model;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaiHat implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("tenBaiHat")
    private String tenBaiHat;

    @SerializedName("caSi")
    private String caSi;

    @SerializedName("hinhAnh")
    private String hinhAnh;

    @SerializedName("link")
    private String link;

    @SerializedName("luotThich")
    private int luotThich;


    public int getId() { return id; }
    public String getTenBaiHat() { return tenBaiHat; }
    public String getCaSi() { return caSi; }

    // Xử lý URL giống như TheLoai
    public String getHinhAnh() {
        if (hinhAnh != null && hinhAnh.startsWith("/")) {
            return "http://192.168.1.162:8080" + hinhAnh;
        }
        return hinhAnh;
    }

    public String getLink() { return link; }

    public int getLuotThich() { return luotThich; }


    public void setId(int id) { this.id = id; }
    public void setTenBaiHat(String tenBaiHat) { this.tenBaiHat = tenBaiHat; }
    public void setCaSi(String caSi) { this.caSi = caSi; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public void setLink(String link) { this.link = link; }
    public void setLuotThich(int luotThich) { this.luotThich = luotThich; }


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