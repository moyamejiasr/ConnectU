package com.onelio.connectu.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.LoginRequest;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Helpers.UpdaterHelper;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.onelio.connectu.Services.UAUpdate;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LauncherActivity extends AppCompatActivity {
    //Account
    private App app;
    private ConstraintLayout layout; //CLayout for notifications
    private RelativeLayout rlayout; //Layout for progressbar
    private CircleImageView profile; //Inside rlayout
    private TextView ltext; //Inside rlayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_launcher);
        registerReceiver(receiver, new IntentFilter(UAUpdate.NOTIFICATION));
        //__________________________________________________________
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //Bugfix illegal access in +24 versions
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        //__________________________________________________________
        //Set Snackbar content
        layout = (ConstraintLayout)findViewById(R.id.launcher_layout);
        rlayout = (RelativeLayout) findViewById(R.id.launcher_playout);
        profile = (CircleImageView) findViewById(R.id.launcher_cimage);
        ltext = (TextView) findViewById(R.id.launcher_ltext);
        //Initialize app networking
        FirebaseCrash.log("01-Launched");
        app = (App) LauncherActivity.this.getApplication();
        app.initializeNetworking();
        app.isAppRunning = true;

        sessionCreate();
    }

    //Request user first login
    void sessionCreate() {
        FirebaseCrash.log("02-Creating login");
        final LoginRequest login = new LoginRequest(this);
        login.createSession(new LoginRequest.LoginCallback() {
            @Override
            public void onLoginResult(boolean onResult, String message) {
                if (onResult) {
                    LauncherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (app.loadUser()) {
                                //Continue to login
                                login.loginAccount(app.account.getEmail(), app.account.getPassword(), onUserLogin);
                            } else {
                                //Do first login
                                FirebaseCrash.log("03-Requesting first login");
                                requestLogin();
                            }
                        }
                    });
                } else {
                    //Error login in
                    ErrorManager error = new ErrorManager(layout);
                    if (!error.handleError(message)) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                        FirebaseCrash.report(new Exception("**Exception making session: " + message));
                    }
                }
            }
        });
    }

    //Normal Login completed!
    LoginRequest.LoginCallback onUserLogin = new LoginRequest.LoginCallback() {
        @Override
        public void onLoginResult(final boolean onResult, final String message) {
            LauncherActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onResult) {
                        //Continue to Main management
                        doMainWork();
                    } else {
                        //Something went wrong
                        ErrorManager error = new ErrorManager(layout);
                        if (!error.handleError(message)) {
                            AlertManager alert = new AlertManager(LauncherActivity.this);
                            alert.setMessage(getString(R.string.error_unexpected), message);
                            alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                    LauncherActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestLogin();
                                        }
                                    });
                                }
                            });
                            alert.show();
                        }
                    }
                }
            });
        }
    };

    //Request user login first time
    void requestLogin() {
        //Login user manually
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        View imageView = findViewById(R.id.launcher_logo);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(imageView, getString(R.string.activity_login_image_trans));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1);
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }

    //After user logged for the first login
    boolean isFirstResume = true;
    @Override
    public void onResume(){
        try {
            registerReceiver(receiver, new IntentFilter(UAUpdate.NOTIFICATION));
        } catch(RuntimeException e) {
            e.printStackTrace(); //Prevent crash, no method
        }
        super.onResume();
        //Success login??
        if(app.account.isLogged()) {
            if (isFirstResume) {
                //Continue to Main management
                isFirstResume = false;
                doMainWork();
            }
        }
    }

    @Override
    public void onStop() {
        try {
        unregisterReceiver(receiver);
        } catch(RuntimeException e) {
            e.printStackTrace(); //Prevent crash, no method
        }
        super.onStop();
    }

    void doMainWork() {
        FirebaseCrash.log("05-Login completed");
        //Start loading data
        if (UpdaterHelper.isFirstLauncher(app.lastUpdateTime)) { //Update first time data
            Picasso.with(getBaseContext())
                    .load(app.account.getPictureURL())
                    .error(R.drawable.ic_placeholder)
                    .into(profile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            rlayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            rlayout.setVisibility(View.VISIBLE);
                        }
                    });
            //Normal Update
            Intent intent = new Intent(this, UAUpdate.class);
            intent.putExtra(Common.INTENT_KEY_UPDATE_TYPE, Common.UAUPDATE_TYPE_NORMAL);
            startService(intent);
        } else {
            //Everything is OK
            completeAccess();
        }

    }

    //Funny Box Update Progress
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = (String)bundle.get(Common.INTENT_KEY_ERROR);
            UAUpdate.YearDataLoc num = (UAUpdate.YearDataLoc)bundle.get(Common.INTENT_KEY_LOC);
            if (bundle != null) {
                int resultCode = bundle.getInt(Common.INTENT_KEY_RESULT);
                if (resultCode == RESULT_FIRST_USER) {
                    //Completed
                    ltext.setText(getString(R.string.completed));
                    profile.setImageResource(R.drawable.ic_accept_green);
                    completeAccess();
                } else if (resultCode == RESULT_OK) {
                    //Continue
                    switch(num) {
                        case YEARS:
                            ltext.setText(getString(R.string.loading1));
                            break;
                        case C_DOSENCIA:
                            ltext.setText(getString(R.string.loading2));
                            break;
                        case C_EVALUACION:
                            ltext.setText(getString(R.string.loading3));
                            break;
                        case C_EXAMENES:
                            ltext.setText(getString(R.string.loading4));
                            break;
                        case C_FESTIVOS:
                            ltext.setText(getString(R.string.loading5));
                            break;
                    }
                }else {
                    //Failed
                    unregisterReceiver(receiver);
                    profile.setImageResource(R.drawable.ic_cancel_red);
                    ltext.setText(getString(R.string.app_name_error));
                    ErrorManager error = new ErrorManager(LauncherActivity.this);
                    if (!error.handleError(message)) {
                        FirebaseCrash.log("Falied doing normal update in " + num.name());
                        FirebaseCrash.log(message);
                        FirebaseCrash.report(new Exception("Normal Update Error"));
                        Toast.makeText(getBaseContext(), getString(R.string.error_problem_service) + " " + num.name(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    void completeAccess() {
        try {
        unregisterReceiver(receiver);
        } catch(RuntimeException e) {
            e.printStackTrace(); //Prevent crash, no method
        }
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){ //Prevent going back error
        //super.onBackPressed();
    }

}
