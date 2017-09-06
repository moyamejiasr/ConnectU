package com.onelio.connectu.Activities.Apps.Tutorias;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutoriasViewActivity extends AppCompatActivity {

    String id;
    String title;
    String subjectID;
    String year;
    String author;
    boolean isHome = false;
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
        setContentView(R.layout.activity_tutorias_view);

        //Get Data
        id = getIntent().getStringExtra(Common.TUTORIAS_STRING_ID);
        author = getIntent().getStringExtra(Common.TUTORIAS_STRING_AUTHOR);
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

        listMsg = (RecyclerView) findViewById(R.id.tutoriasViewRecycler);
        initializeConnection();
    }

    private void initializeConnection() { //Used to initialize communication with the UACloud service that we are going to use
        if (!id.isEmpty()) { //Empty or null means new one
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
            request.requestNewTutoria(new TutoriasRequest.TutoriasCallback() {
                @Override
                public void onResult(final boolean onResult, final String message) {
                    TutoriasViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onResult) {
                                isLoading = false;
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

    private void setTeacherPicture() {
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
