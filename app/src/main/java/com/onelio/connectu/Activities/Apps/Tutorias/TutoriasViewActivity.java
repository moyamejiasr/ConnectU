package com.onelio.connectu.Activities.Apps.Tutorias;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.TutoriasRequest;
import com.onelio.connectu.Adapters.MessageRecycleAdapter;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.BubbleData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutoriasViewActivity extends AppCompatActivity {

    String id;
    String title;
    String subjectID;
    String year;
    String author;
    String authorImg;
    boolean isHome = false;
    //__
    String authorId;
    TutoriasRequest request;

    List<BubbleData> bubbles;
    RecyclerView listMsg;
    MessageRecycleAdapter adapter;
    LinearLayoutManager manager;
    ProgressBar progress;

    boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_tutorias_view);

        //Get Data
        id = getIntent().getStringExtra(Common.TUTORIAS_STRING_ID);
        author = getIntent().getStringExtra(Common.TUTORIAS_STRING_AUTHOR);
        authorImg = getIntent().getStringExtra(Common.TUTORIAS_STRING_AUTHOR_IMG);
        title = getIntent().getStringExtra(Common.TUTORIAS_STRING_TITLE);
        year = getIntent().getStringExtra(Common.TUTORIAS_STRING_YEAR);
        subjectID = getIntent().getStringExtra(Common.TUTORIAS_STRING_SUBJECTID);
        isHome = getIntent().hasExtra(Common.TUTORIAS_BOOL_ISHOME);

        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
        }

        request = new TutoriasRequest(getBaseContext());

        progress = (ProgressBar) findViewById(R.id.tutorias_view_progressBar);
        TextView vtitle = (TextView) findViewById(R.id.titleD);
        TextView vauthor = (TextView) findViewById(R.id.authorD);
        vtitle.setText(title);
        vauthor.setText(AppManager.capAfterSpace(author));

        CircleImageView image = (CircleImageView) findViewById(R.id.toolbar_image);
        if (authorImg != null && !authorImg.isEmpty()) {
            Picasso.with(getBaseContext()).load(authorImg).placeholder(R.drawable.ic_placeholder).into(image);
        } else {
            Picasso.with(getBaseContext()).load(R.drawable.ic_placeholder).into(image);
        }

        listMsg = (RecyclerView) findViewById(R.id.tutoriasViewRecycler);
        initializeConnection();
    }

    private void initializeConnection() { //Used to initialize communication with the UACloud service that we are going to use
        if ( id != null && !id.isEmpty()) { //Empty or null means new one
            request.fetchTutoriaById(id, new TutoriasRequest.TutoriasCallback() {
                @Override
                public void onResult(final boolean onResult, final String message) { //Get Data
                    TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onResult) {
                                isLoading = false;
                                request.markTutoriaReadId(id, new TutoriasRequest.TutoriasCallback() {
                                    @Override
                                    public void onResult(boolean onResult, final String message) {
                                    }
                                }); //Mark Read
                                progress.setVisibility(View.GONE);
                                bubbles = request.getTutoriaChats();
                                setTeacherPicture();
                                putInitialMessages();
                            } else {
                                ErrorManager error = new ErrorManager(getBaseContext());
                                if (!error.handleError(message)) {
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        } else {
            request.requestNewTutoria(subjectID, author, new TutoriasRequest.TutoriasCallback() {
                @Override
                public void onResult(final boolean onResult, final String message) { //Create New One
                    TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onResult) {
                                isLoading = false;
                                authorId = message;
                            } else {
                                ErrorManager error = new ErrorManager(getBaseContext());
                                if (!error.handleError(message)) {
                                    Toast.makeText(getBaseContext(), getString(R.string.error_unkown_response_format), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    public void addBubble(String text) {
        Date date = new Date();
        BubbleData bubble = new BubbleData();
        bubble.setAuthor("Yo");
        bubble.setDate(" " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
        bubble.setImage("");
        bubble.setText(text + "bb"); //bb is added to fit the -2 chars at the end of the adapter
        bubble.setMe(true);
        if (bubbles == null) {
            bubbles = new ArrayList<>();
        }
        bubbles.add(bubble);
        putInitialMessages();
    }

    public void sendMessage(final View v) {
        AppManager.hideKeyboard(this);
        final EditText text = (EditText) findViewById(R.id.messageEdit);
        if (text.getText().toString().isEmpty())
            return;
        if (!isLoading) {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage(getString(R.string.alert_loading));
            progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            text.setEnabled(false);
            v.setEnabled(false);
            //Bugfix newline being ignored because Windows like screen
            final String str = text.getText().toString().replace("\\n", "\\r\\n");
            if (id != null && !id.isEmpty()) {
                request.answerTutoria(id, str, new TutoriasRequest.TutoriasCallback() {
                    @Override
                    public void onResult(final boolean onResult, final String message) {
                        TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                if (onResult) {
                                    addBubble(str);
                                    text.setText("");
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                } else {
                                    text.setEnabled(true);
                                    v.setEnabled(true);
                                    ErrorManager error = new ErrorManager(getBaseContext());
                                    if (!error.handleError(message)) {
                                        Toast.makeText(getBaseContext(), getString(R.string.not_available), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                request.createTutoria(subjectID, authorId, title, str, new TutoriasRequest.TutoriasCallback() {
                    @Override
                    public void onResult(final boolean onResult, final String message) {
                        TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                if (onResult) {
                                    addBubble(str);
                                    text.setText("");
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                } else {
                                    text.setEnabled(true);
                                    v.setEnabled(true);
                                    ErrorManager error = new ErrorManager(getBaseContext());
                                    if (!error.handleError(message)) {
                                        Toast.makeText(getBaseContext(), getString(R.string.not_available), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void setTeacherPicture() {
        if (authorImg != null && !authorImg.isEmpty())
            return; //In that case image has been actually set, so return
        for (BubbleData bubble : bubbles) {
            if (!bubble.isMe()) {
                CircleImageView image = (CircleImageView) findViewById(R.id.toolbar_image);
                Picasso.with(getBaseContext()).load(bubble.getImage()).placeholder(R.drawable.ic_placeholder).into(image);
            }
        }
    }

    private void putInitialMessages() {
        adapter = new MessageRecycleAdapter(this, bubbles);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        listMsg.setLayoutManager(manager);
        listMsg.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tutorias_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int sid = item.getItemId();
        if (sid == R.id.action_markRead) {
            //Do Read
            if (!id.isEmpty()) {
                request.markTutoriaReadId(id, new TutoriasRequest.TutoriasCallback() {
                    @Override
                    public void onResult(boolean onResult, final String message) {
                        TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorManager error = new ErrorManager(getBaseContext());
                                if (!error.handleError(message)) {
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            return super.onOptionsItemSelected(item);
        } else if (sid == R.id.action_markUnread) {
            //Do Read
            if (!id.isEmpty()) {
                request.markTutoriaUnreadId(id, new TutoriasRequest.TutoriasCallback() {
                    @Override
                    public void onResult(boolean onResult, final String message) {
                        TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorManager error = new ErrorManager(getBaseContext());
                                if (!error.handleError(message)) {
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            return super.onOptionsItemSelected(item);
        } else {
            super.onBackPressed();
            if (!isHome) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isHome) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}
