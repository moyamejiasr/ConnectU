package com.onelio.connectu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.BackgroundService.UAService;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        TextView tname = (TextView)findViewById(R.id.tname);
        tname.setText(Common.name);
        TextView temail = (TextView)findViewById(R.id.temail);
        temail.setText("(" + Common.loginUsername + ")");
        CircularImageView view = (CircularImageView)findViewById(R.id.imageView2);
        Picasso.with(getBaseContext())
                .load(Common.src)
                .into(view);
    }

    public void onlogout(View v) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
        adb.setTitle("Account Manager");
        adb.setMessage(getResources().getString(R.string.rlogout));
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                realm.close();
                UAService.active = false;
                Common.isNotifOn = false;
                Intent intent = new Intent(getApplication(), LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } });


        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            } });
        AccountActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adb.show();
            }
        });
    }
}
