package com.example.music_app.model;
import static com.example.music_app.utils.Constants.BASE_URL;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BaiHat implements Serializable {
    private int id;
    private String tenBaiHat;
    private String caSi;
    private String hinhAnh;
    private String link;

    // ====== GETTER ======
    public int getId() { return id; }
    public String getTenBaiHat() { return tenBaiHat; }
    public String getCaSi() { return caSi; }

    public String getHinhAnh() {
        if (hinhAnh != null && hinhAnh.startsWith("/")) {
            return "http://192.168.126.1:8080" + hinhAnh;
        }
        return hinhAnh;
    }

    public String getLink() {
        if (link != null && (link.startsWith("http://") || link.startsWith("https://"))) {
            return link;
        }
        if (link != null && link.startsWith("/")) {
            return BASE_URL + link;
        }
        return link;
    }

    // ✅ THÊM SETTER - BẮT BUỘC ĐỂ RETROFIT PARSE DỮ LIỆU
    public void setId(int id) { this.id = id; }
    public void setTenBaiHat(String tenBaiHat) { this.tenBaiHat = tenBaiHat; }
    public void setCaSi(String caSi) { this.caSi = caSi; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public void setLink(String link) { this.link = link; }

    @Override
    public String toString() {
        return "BaiHat{" +
                "id=" + id +
                ", tenBaiHat='" + tenBaiHat + '\'' +
                ", caSi='" + caSi + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}