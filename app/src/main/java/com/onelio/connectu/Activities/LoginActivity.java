package com.onelio.connectu.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.LoginRequest;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.DatabaseManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.Managers.NotificationManager;
import com.onelio.connectu.Managers.SnackManager;
import com.onelio.connectu.R;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    ImageButton viewButton;
    ProgressBar progress;
    boolean userch = false;
    boolean passch = false;
    boolean displayedPass = false;
    boolean isAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.btn_login);
        viewButton = (ImageButton) findViewById(R.id.showpass);
        progress = (ProgressBar)findViewById(R.id.login_progressBar);

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curpos = password.getSelectionStart();
                if(!displayedPass) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                password.setSelection(curpos);
                displayedPass = !displayedPass;
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userch = true;
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passch = true;
            }
        });
    }

    public void onLoginClick(View view){
        AppManager.hideKeyboard(this);
        if (userch && passch && isAvailable) {
            AlertManager alert = new AlertManager(LoginActivity.this);
            alert.setCancelable(false);
            alert.setMessage(getString(R.string.app_name), Html.fromHtml(getString(R.string.disclaimer)));
            alert.setPositiveButton(getString(R.string.accept), new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    progress.setVisibility(View.VISIBLE);
                    loginButton.setBackgroundColor(Color.parseColor("#757575"));
                    isAvailable = false;
                    LoginRequest login = new LoginRequest(LoginActivity.this);
                    final String user = username.getText().toString() + "@alu.ua.es";
                    final String pass = password.getText().toString();
                    login.loginAccount(user, pass, new LoginRequest.LoginCallback() {
                        @Override
                        public void onLoginResult(final boolean onResult, final String message) {
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (onResult) {
                                        //Login success -> Save Data
                                        FirebaseCrash.log("04-User_" + user + " logged successfully");
                                        DatabaseManager database = new DatabaseManager(getBaseContext());
                                        database.putBoolean(Common.PREFERENCE_BOOLEAN_ISLOGGED, true);
                                        database.putString(Common.PREFERENCE_STRING_EMAIL, user);
                                        database.putString(Common.PREFERENCE_STRING_PASSWORD, pass);
                                        database.putInt(Common.PREFERENCE_INT_RECTIME, Common.INT_REC_TIME);
                                        //Initialize background task alarm to future updates
                                        App.isFirstLaunch = true;
                                        NotificationManager manager = new NotificationManager(getBaseContext());
                                        if (!manager.isAlarmOn()) {
                                            manager.setRecurrentService();
                                        }
                                        //Continue with login
                                        LoginActivity.super.onBackPressed();
                                    } else {
                                        //Error, couldn't loggin
                                        FirebaseCrash.log("04-User_" + user + " login failed");
                                        progress.setVisibility(View.GONE);
                                        isAvailable = true;
                                        loginButton.setBackgroundColor(Color.parseColor("#212121"));
                                        //Usual problems handler
                                        View v = findViewById(R.id.login_constr_layout);
                                        ErrorManager error = new ErrorManager(v);
                                        if (!error.handleError(message)) {
                                            SnackManager snack = new SnackManager(v);
                                            snack.setMessage(message, Snackbar.LENGTH_SHORT);
                                            snack.setIcon(R.drawable.ic_no_encryption_black_24dp);
                                            snack.show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
            alert.setNegativeButton(getString(R.string.deny), new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                }
            });
            alert.show();
        }
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
    }
}
