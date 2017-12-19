package com.onelio.connectu.Activities.Apps.Materiales;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.MaterialesRequest;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.MaterialData;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class MaterialesDialog extends Dialog {

    private MaterialData data;
    private Activity activity;
    private MaterialesRequest request;

    private boolean isDownloading = false;

    private CardView progress;

    public MaterialesDialog(Activity activity, MaterialData data) {
        super(activity);
        this.activity = activity;
        this.data = data;
        request = new MaterialesRequest(getContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_dialog_materiales);
        //Set content
        TextView date = (TextView) findViewById(R.id.dateD);
        TextView type = (TextView) findViewById(R.id.typeD);
        TextView title = (TextView) findViewById(R.id.titleD);
        TextView text = (TextView) findViewById(R.id.textD);
        TextView subtitle = (TextView) findViewById(R.id.subtitleD);
        TextView autor = (TextView) findViewById(R.id.autorD);
        CircleImageView autorPic = (CircleImageView) findViewById(R.id.autorpD);
        LinearLayout lExit = (LinearLayout) findViewById(R.id.llExitD);
        TextView cancel = (TextView) findViewById(R.id.onCancel);
        LinearLayout open = (LinearLayout) findViewById(R.id.action_open);
        LinearLayout download = (LinearLayout) findViewById(R.id.action_download);
        LinearLayout share = (LinearLayout) findViewById(R.id.action_share);
        progress = (CardView) findViewById(R.id.materialesLoader);
        open.setOnClickListener(onOpen);
        download.setOnClickListener(onDownload);
        share.setOnClickListener(onShare);

        if (data.getFileName().contains(".")) {
            title.setText(AppManager.before(data.getFileName(), "."));
        } else {
            title.setText(data.getFileName());
        }
        subtitle.setText(AppManager.capFirstLetter(data.getSubjectName()));
        type.setText(data.getTypeName() + " " + data.getType().toUpperCase());
        date.setText(AppManager.before(data.getDate(), " "));
        text.setText(data.getFileDescription());
        autor.setText(AppManager.capAfterSpace(data.getPublisherName()));

        //Bugfix vectors drawable bug <API 19 BY IMPREZA233
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Picasso.with(getContext()).load(data.getPublisherPicture()).placeholder(R.drawable.ic_placeholder).into(autorPic);
        }

        lExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDownloading)
                    MaterialesDialog.super.onBackPressed();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancellDownload();
            }
        });

    }

    public void onCancellDownload() {
        isDownloading = false;
        progress.setVisibility(View.GONE);
    }

    private View.OnClickListener onOpen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Prevent more executions
            if (isDownloading)
                return;
            else
                isDownloading = true;

            if (AppManager.isStoragePermissionGranted(activity)) {
                final File file = new File(AppManager.getFileSaveLoc(Common.MATERIALES_TEMP_FILE), Common.MATERIALES_TEMP_FILE + "." + data.getType());
                progress.setVisibility(View.VISIBLE);
                request.downloadFile(data, file, new MaterialesRequest.MaterialsCallback() {
                    @Override
                    public void onResult(final boolean onResult, final String message) {
                        if(activity == null)
                            return;
                        //At this point, false equals cancelled
                        if (!isDownloading)
                            return;
                        else
                            isDownloading = false;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                if (onResult) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), AppManager.getMimeType(file.getParent() + "/" + file.getName()));
                                    try {
                                        activity.startActivity(intent);
                                    } catch (ActivityNotFoundException ex) {
                                        if(activity == null)
                                            return;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertManager alert = new AlertManager(getContext());
                                                alert.setMessage(getContext().getString(R.string.app_name_error), getContext().getString(R.string.error_find_open));
                                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                    @Override
                                                    public void onClick(boolean isPositive) {
                                                    }
                                                });
                                                alert.show();
                                            }
                                        });
                                    }
                                } else {
                                    ErrorManager error = new ErrorManager(getContext());
                                    if (!error.handleError(message)) {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                isDownloading = false;
            }
        }
    };

    private View.OnClickListener onDownload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Prevent more executions
            if (isDownloading)
                return;
            else
                isDownloading = true;

            if (AppManager.isStoragePermissionGranted(activity)) {
                final File file = new File(AppManager.getFileSaveLoc(data.getSubjectName()), data.getFileName());
                progress.setVisibility(View.VISIBLE);
                request.downloadFile(data, file, new MaterialesRequest.MaterialsCallback() {
                    @Override
                    public void onResult(final boolean onResult, final String message) {
                        if(activity == null)
                            return;
                        //At this point, false equals cancelled
                        if (!isDownloading)
                            return;
                        else
                            isDownloading = false;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                if (onResult) {
                                    Toast.makeText(getContext(), getContext().getString(R.string.materiales_saved_at) + data.getSubjectName(), Toast.LENGTH_LONG).show();
                                } else {
                                    ErrorManager error = new ErrorManager(getContext());
                                    if (!error.handleError(message)) {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                isDownloading = false;
            }
        }
    };

    private View.OnClickListener onShare = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Prevent more executions
            if (isDownloading)
                return;
            else
                isDownloading = true;

            if (AppManager.isStoragePermissionGranted(activity)) {
                final File file = new File(AppManager.getFileSaveLoc(Common.MATERIALES_TEMP_FILE), Common.MATERIALES_TEMP_FILE + "." + data.getType());
                progress.setVisibility(View.VISIBLE);
                request.downloadFile(data, file, new MaterialesRequest.MaterialsCallback() {
                    @Override
                    public void onResult(final boolean onResult, final String message) {
                        if(activity == null)
                            return;
                        //At this point, false equals cancelled
                        if (!isDownloading)
                            return;
                        else
                            isDownloading = false;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                if (onResult) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType(AppManager.getMimeType(file.getParent() + "/" + file.getName()));
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
                                    intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.materiales_sharing_you));
                                    intent.putExtra(Intent.EXTRA_TEXT, data.getFileName());
                                    try {
                                        activity.startActivity(Intent.createChooser(intent, getContext().getString(R.string.materiales_share_button)));
                                    } catch (ActivityNotFoundException ex) {
                                        if(activity == null)
                                            return;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertManager alert = new AlertManager(getContext());
                                                alert.setMessage(getContext().getString(R.string.app_name_error), getContext().getString(R.string.error_find_open));
                                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                    @Override
                                                    public void onClick(boolean isPositive) {
                                                    }
                                                });
                                                alert.show();
                                            }
                                        });
                                    }
                                } else {
                                    ErrorManager error = new ErrorManager(getContext());
                                    if (!error.handleError(message)) {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                isDownloading = false;
            }
        }
    };



}