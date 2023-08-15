package com.lxf.migration.pojo;

import java.util.List;

public class TreeListNode {
    private Integer id;
    private Integer pId;
    private String value;
    private String title;

    public TreeListNode(Integer id, Integer pId, String value, String title) {
        this.id = id;
        this.pId = pId;
        this.value = value;
        this.title = title;
    }

    @Override
    public String toString() {
        return "TreeListNode{" +
                "id=" + id +
                ", pId=" + pId +
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

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
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
