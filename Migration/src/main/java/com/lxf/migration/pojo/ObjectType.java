package com.lxf.migration.pojo;

public class ObjectType {
    private  String type;
    private String Name;

    public ObjectType(String type, String name) {

        this.type = type;
        Name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
