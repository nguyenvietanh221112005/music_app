// TheLoai.java
package com.example.music_app.model;

import java.io.Serializable;

public class TheLoai implements Serializable {
    private int idTheLoai;
    private String tenTheLoai;
    private String hinhAnh;

    public int getIdTheLoai() { return idTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }
    public String getHinhAnh() {
        // Kiểm tra và xử lý URL nếu cần
        if (hinhAnh != null && hinhAnh.startsWith("/")) {
            return "http://192.168.126.1:8080" + hinhAnh;
        }
        return hinhAnh;
    }

    // Thêm toString để debug
    @Override
    public String toString() {
        return "TheLoai{" +
                "idTheLoai=" + idTheLoai +
                ", tenTheLoai='" + tenTheLoai + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                '}';
    }
}
