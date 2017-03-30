package com.onelio.connectu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.onelio.connectu.Apps.Chat.MessagesListAdapter;
import com.onelio.connectu.Apps.Chat.Msg;

import java.io.File;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    public static String AbsolutegetMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GridView listMsg = (GridView)findViewById(R.id.chatBox);
        ArrayList<Msg> listMessages;
        MessagesListAdapter adapter;
        listMessages = new ArrayList<Msg>();

        listMessages.add(new Msg(true, "message mio"));
        listMessages.add(new Msg(false, "message mio"));
        adapter = new MessagesListAdapter(this, listMessages);
        listMsg.setAdapter(adapter);

        listMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/file.pdf");

                if(fileWithinMyDir.exists()) {
                    intentShareFile.setType(AbsolutegetMimeType(fileWithinMyDir.toString()));
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileWithinMyDir));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            }
        });

    }
}
