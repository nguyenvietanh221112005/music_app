package com.example.musicapp.model;

public class song {


    private String tenBaiHat;
    private String tenCaSi;
    private Boolean yeuThich;

    public song(String tenBaiHat, String tenCaSi, Boolean yeuThich) {
        this.tenBaiHat = tenBaiHat;
        this.tenCaSi = tenCaSi;
        this.yeuThich = yeuThich;
    }
    public String getTenBaiHat() {
        return tenBaiHat;
    }
    public String getTenCaSi() {
        return tenCaSi;
    }
    public boolean isYeuThich() {
        return yeuThich;
    }
    public void setYeuThich(Boolean yeuThich) {
        this.yeuThich = yeuThich;
    }
}
