package com.example.music_app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TheLoai implements Serializable {

    @SerializedName("id") // 🔥 thêm dòng này
    private int idTheLoai;

    @SerializedName("tenTheLoai")
    private String tenTheLoai;

    @SerializedName("hinhAnh")
    private String hinhAnh;

    public int getIdTheLoai() { return idTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }
    public String getHinhAnh() {
        if (hinhAnh != null && hinhAnh.startsWith("/")) {
            return "http://192.168.126.1:8080" + hinhAnh;
        }
        return hinhAnh;
    }

    @Override
    public String toString() {
        return "TheLoai{" +
                "idTheLoai=" + idTheLoai +
                ", tenTheLoai='" + tenTheLoai + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                '}';
    }
}
