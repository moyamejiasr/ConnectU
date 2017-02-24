package com.onelio.connectu.Database;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class RealmManager {

    private Realm realm;

    //Initialize Manager
    public RealmManager(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public void createOption(String name, String value) {
        realm.beginTransaction();
        Settings setting = realm.createObject(Settings.class, name); // Create a new object
        setting.setValue(value);
        realm.commitTransaction();
    }

    public boolean onOptionExist(String value) {
        Settings setting = realm.where(Settings.class).equalTo("name", value).findFirst();
        return setting != null;
    }

    public String getOption(String value) {
        Settings setting = realm.where(Settings.class).equalTo("name", value).findFirst();
        return setting.getValue();
    }

    public void deleteRealmInstance() {
        realm.close();
    }

    public void modifyOption(String name, String value) {
        realm.beginTransaction();
        RealmQuery<Settings> query = realm.where(Settings.class);
        query.equalTo("name", name);
        Settings result = query.findFirst();
        result.setValue(value);
        realm.copyToRealmOrUpdate(result);
        realm.commitTransaction();
    }

    public void deleteAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

}
