package com.onelio.connectu.Containers;

public class AccountData {
  //  User login information.
  public String Execution;
  public String Event;
  public String Email;
  public String Password;

  //  Profile info.
  public boolean isLogged = false;
  public String Name;
  public String PictureURL;

  //  Profile settings.
  public int NotificationTime; //  Must not be lower than 10 minutes.
  public boolean fastmode = false;
}
