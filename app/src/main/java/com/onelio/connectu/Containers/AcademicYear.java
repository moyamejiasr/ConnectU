package com.onelio.connectu.Containers;

import java.util.ArrayList;
import java.util.List;

public class AcademicYear {

  private String year;
  private boolean isSelected;
  private List<SubjectData> subjectsData;

  public AcademicYear() {
    year = "";
    isSelected = false;
    subjectsData = new ArrayList<>();
  }

  public List<SubjectData> getSubjectsData() {
    return subjectsData;
  }

  public void setSubjectsData(List<SubjectData> subjectsData) {
    this.subjectsData = subjectsData;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}
