package com.fcicustomer.elbagory.sal7necustoner.Models;

public class Worker {

    public Worker() {
    }

    private String name;
    private String Section;
    private String phone;
    private String id;
    private long rate;
    private boolean IScheked;
    private String img;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public boolean isIScheked() {
        return IScheked;
    }

    public void setIScheked(boolean IScheked) {
        this.IScheked = IScheked;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }




}
