package com.lxf.migration.pojo;

public class ServiceStatus {
    private  String title;
    private String color;

    public ServiceStatus(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ServiceStatus{" +
                "title='" + title + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
