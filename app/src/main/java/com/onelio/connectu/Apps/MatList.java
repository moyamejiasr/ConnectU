package com.onelio.connectu.Apps;

/**
 * Created by Onelio on 06/12/2016.
 */
public class MatList {
    private boolean folder;
    private boolean clean; //if folder
    private String codasi; //if folder
    private String fileid; //if file
    private String ftype; //if file
    private String name; //files and folders have diff classes
    private String description;
    private String signame;
    private String date;
    private String imgmaker;
    private String namemaker;

    public boolean isFolder() {
        return folder;
    }
    public void setisFolder(boolean text) {
        this.folder = text;
    }

    public boolean getisFolderClean() {
        return clean;
    }
    public void setisFolderClean(boolean text) {
        this.clean = text;
    }

    public String getCodasi() {
        return codasi;
    }
    public void setCodasi(String text) {
        this.codasi = text;
    }

    public String getFileid() {
        return fileid;
    }
    public void setFileid(String text) {
        this.fileid = text;
    }

    public String getFiletype() {
        return ftype;
    }
    public void setFiletype(String text) {
        this.ftype = text;
    }

    public String getName() {
        return name;
    }
    public void setName(String text) {
        this.name = text;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String text) {
        this.description = text;
    }

    public String getSigname() {
        return signame;
    }
    public void setSigname(String text) {
        this.signame = text;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String text) {
        this.date = text;
    }

    public String getImgmaker() {
        return imgmaker;
    }
    public void setImgmaker(String text) {
        this.imgmaker = text;
    }

    public String getNamemaker() {
        return namemaker;
    }
    public void setNamemaker(String text) {
        this.namemaker = text;
    }

}