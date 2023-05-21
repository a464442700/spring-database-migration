package com.lxf.migration.pojo;

public class Package {
    private String owner;
    private String name;
    private String type;
    private String line;
    private String text;

    @Override
    public String toString() {
        return "Package{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", line='" + line + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Package(String owner, String name, String type, String line, String text) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.line = line;
        this.text = text;
    }
}
