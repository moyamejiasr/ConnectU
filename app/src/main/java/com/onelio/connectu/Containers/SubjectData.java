package com.onelio.connectu.Containers;

import java.util.ArrayList;
import java.util.List;

public class SubjectData {

    private String id;
    private String name;
    private List<TeacherData> teachers;

    public SubjectData() {
        id = "";
        name = "";
        teachers = new ArrayList<>();
    }

    public List<TeacherData> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherData> teachers) {
        this.teachers = teachers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
