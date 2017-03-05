package com.onelio.connectu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ListView;

import com.onelio.connectu.Apps.Chat.MessagesListAdapter;
import com.onelio.connectu.Apps.Chat.Msg;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

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
    }
}
