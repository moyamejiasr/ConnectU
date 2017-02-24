package com.onelio.connectu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Database.RealmManager;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.*;

public class LoginActivity extends AppCompatActivity {

    //Variables
    boolean isBackPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("UACloud Login");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Button btn = (Button)findViewById(R.id.btn_login);
        final CheckBox checkAccept = (CheckBox)findViewById(R.id.checkAccept);
        final EditText user = (EditText)findViewById(R.id.username);
        user.setSelected(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAccept.isChecked()) {
                    onButtonClick();
                } else {
                    AlertManager alert = new AlertManager(LoginActivity.this);
                    alert.setMessage(getString(R.string.app_name), "Error, Tienes que aceptar las condiciones de uso para poder conectarte.");
                    alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                        }
                    });
                    alert.show();
                }
            }
        });

        checkAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    AlertManager alert = new AlertManager(LoginActivity.this);
                    alert.setCancelable(false);
                    alert.setMessage(getString(R.string.app_name), Html.fromHtml(getString(R.string.disclaimer)));
                    alert.setPositiveButton("Acepto", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                            checkAccept.setChecked(true);
                        }
                    });
                    alert.setNegativeButton("No Acepto", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                            checkAccept.setChecked(false);
                        }
                    });
                    alert.show();
                }
            }
        });

    }

    String getJData() {
        String json = "fail";
        try {
            json = "username=" + URLEncoder.encode(Common.loginUsername, "UTF-8") +
                    "&password=" + Common.loginPassword +
                    "&lt=" + Common.lt +
                    "&execution=" + Common.execution +
                    "&_eventId=submit&submit=";
        } catch (UnsupportedEncodingException e) {
            AlertManager manager = new AlertManager(LoginActivity.this);
            manager.setMessage(getResources().getString(R.string.error_defTitle), getResources().getString(R.string.error_unknown));
            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    DeviceManager.appClose();
                }
            });
            manager.show();
        }
        return json;
    }

    public void onButtonClick() {
        final EditText user = (EditText)findViewById(R.id.username);
        final EditText pass = (EditText)findViewById(R.id.password);
        Common.loginUsername = user.getText().toString() + "@alu.ua.es";
        Common.loginPassword = pass.getText().toString();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        UAWebService.HttpWebPostRequest(LoginActivity.this, UAWebService.LOGIN_DOMAIN + Common.loginURL, getJData(), new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                dialog.cancel();
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get User Data
                    int error = 0;
                    String err_message = "Error, Alert not added";
                    try {
                        error = doc.select("div.contenido-caja-texto > div.row").get(0).children().size(); //Get Count of alerts
                        err_message = doc.select("div.contenido-caja-texto > div.row").get(0).children().text(); //Get text inside
                    } catch(Exception ex){}; //In case of 0, prevent exception
                    final String errms = err_message; //Need to be final to return at the end
                    if (error == 0) {
                        //Save data to database
                        RealmManager realm = new RealmManager(getBaseContext());
                        realm.modifyOption("username", Common.loginUsername);
                        realm.modifyOption("pass", Common.loginPassword);
                        realm.deleteRealmInstance();
                        Intent intent = new Intent(getApplication(), LauncherActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast errort = Toast.makeText(getApplicationContext(), errms, Toast.LENGTH_SHORT);
                                errort.show();
                            }
                        });
                    }
                } else {
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            this.finishAffinity();
        } else {
            isBackPressedOnce = true;
            Toast.makeText(getBaseContext(),getResources().getString(R.string.again_exit),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
