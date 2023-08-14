package com.lxf.migration.pojo;

import java.util.List;

public class TreeListNode {
    private Integer id;
    private Integer pID;
    private String value;
    private String title;

    public TreeListNode(Integer id, Integer pID, String value, String title) {
        this.id = id;
        this.pID = pID;
        this.value = value;
        this.title = title;
    }

    @Override
    public String toString() {
        return "TreeListNode{" +
                "id=" + id +
                ", pID=" + pID +
                ", value='" + value + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpID() {
        return pID;
    }

    public void setpID(Integer pID) {
        this.pID = pID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
