package com.onelio.connectu.Containers;

public class MaterialData {

    private boolean isFolder;
    private boolean isAvailableFolder;
    private String id;
    private String type;
    private String typeName;
    private String fileName;
    private String fileDescription;
    private String subjectId;
    private String subjectName;
    private String date;
    private String publisherName;
    private String publisherPicture;

    public MaterialData() {
        isFolder = false;
        isAvailableFolder = false;
        id = "";
        type = "";
        typeName = "";
        fileName = "";
        fileDescription = "";
        subjectId = "";
        subjectName =  "";
        date = "";
        publisherName = "";
        publisherPicture = "";
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isAvailableFolder() {
        return isAvailableFolder;
    }

    public void setAvailableFolder(boolean availableFolder) {
        isAvailableFolder = availableFolder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherPicture() {
        return publisherPicture;
    }

    public void setPublisherPicture(String publisherPicture) {
        this.publisherPicture = publisherPicture;
    }
}
