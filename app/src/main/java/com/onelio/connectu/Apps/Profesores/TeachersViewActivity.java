package com.onelio.connectu.Apps.Profesores;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Apps.Tutorias.TutoriaViewActivity;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.BlurTransform;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class TeachersViewActivity extends AppCompatActivity {

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_view);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        CircularImageView img = (CircularImageView)findViewById(R.id.img);
        TextView textView = (TextView)findViewById(R.id.name) ;
        TextView textView1 = (TextView)findViewById(R.id.signature) ;
        TextView textView2 = (TextView)findViewById(R.id.email) ;
        TextView textView3 = (TextView)findViewById(R.id.html) ;
        ImageView imgb = (ImageView)findViewById(R.id.imgb);
        try {
            textView.setText(Common.teacher.getString("name"));
            textView1.setText(Common.teacher.getString("signature"));
            textView2.setText(Common.teacher.getString("email"));
            textView2.setMovementMethod(LinkMovementMethod.getInstance());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView3.setText(Html.fromHtml(Common.teacher.getString("html").replace("&nbsp", " "), Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView3.setText(Html.fromHtml(Common.teacher.getString("html").replace("nbsp", " ")));
            }
            Picasso.with(getBaseContext()).load(Common.teacher.getString("img")).into(img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Picasso.with(getBaseContext()).load(Common.teacher.getString("img")).transform(new BlurTransform(TeachersViewActivity.this)).into(imgb);
            } else {
                Picasso.with(getBaseContext()).load(Common.teacher.getString("img")).into(imgb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onEmailClick(View view) {
        Intent emailIntent = null;
        try {
            emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", Common.teacher.getString("email"), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mensaje de " + Common.name + " alumno de " + Common.teacher.getString("signature"));
        } catch (JSONException e) {
            emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "error@error.error", null));
        }
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje...");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void onSendTutoria(View view) {

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //Start connection
        requestConn();
    }

    public void requestNewTutoria() {
        UAWebService.HttpWebGetRequest(TeachersViewActivity.this, UAWebService.TUTORIAS_G_MAKE, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    //Ready to send Tutoria
                    TeachersViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                            AlertDialog.Builder builder = new AlertDialog.Builder(TeachersViewActivity.this);
                            builder.setTitle(getString(R.string.ititle));

                            final EditText input = new EditText(TeachersViewActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Common.jdata = new JSONObject();
                                    try {
                                        Common.jdata.put("ddlCurso", Common.teacher.getString("date"));
                                        Common.jdata.put("ddlAsignatura", Common.teacher.getString("signature_id"));
                                        Common.jdata.put("ddlDestinatario", Common.teacher.getString("id"));
                                        Common.jdata.put("ckDestinatarios", "1");
                                        Common.jdata.put("ckDestinatarios1", "false");
                                        Common.jdata.put("txtAsunto", input.getText().toString());
                                        Common.cTitle = input.getText().toString();
                                        Common.isNewChat = true;
                                        startActivity(new Intent(TeachersViewActivity.this, TutoriaViewActivity.class));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                } else {
                    AlertManager alert = new AlertManager(TeachersViewActivity.this);
                    alert.setMessage(getResources().getString(R.string.error_defTitle), getResources().getString(R.string.error_connect));
                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                            DeviceManager.appClose();
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    public void requestConn() {
        UAWebService.HttpWebGetRequest(TeachersViewActivity.this, UAWebService.TUTORIAS, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    requestNewTutoria();
                } else {
                    AlertManager alert = new AlertManager(TeachersViewActivity.this);
                    alert.setMessage(getResources().getString(R.string.error_defTitle), getResources().getString(R.string.error_connect));
                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                            DeviceManager.appClose();
                        }
                    });
                    alert.show();
                }
            }
        });
    }

}
