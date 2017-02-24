package com.onelio.connectu.Apps.Tutorias;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.API.WebApi;
import com.onelio.connectu.Apps.Chat.ChatAdapter;
import com.onelio.connectu.Apps.Chat.ChatMessage;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TutoriaViewActivity extends AppCompatActivity {

    ProgressDialog dialog;
    private EditText messageET;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    String idPadre = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoria_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        setTitle(Common.cTitle);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        companionLabel.setText(getString(R.string.teacher));// Hard Coded

        if(!Common.isNewChat) {
            sendRead();
            requestData();
            dialog.show();
        } else {
            initControls();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void sendRead() {
        String json = "idTuto=" + Common.tutId;
        UAWebService.HttpWebPostRequest(TutoriaViewActivity.this, UAWebService.TUTORIAS_READ, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, final String body) {
                if (isSuccessful) {
                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jdata = new JSONObject(body);
                                Toast.makeText(getBaseContext(), jdata.getString("result"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(getBaseContext(), body, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void requestData() {
        UAWebService.HttpWebGetRequest(TutoriaViewActivity.this, UAWebService.TUTORIAS_VIEW + Common.tutId, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get new answer
                    idPadre = doc.select("input[name=idPadre").attr("value");
                    Common.jdata = new JSONObject();
                    try {
                        Common.jdata.put("idTuto", Common.tutId);
                        Common.jdata.put("idPadre", idPadre);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Get view
                    Elements user = doc.select("img.imgUsuarioG");
                    Elements texto = doc.select("div.row");
                    chatHistory = new ArrayList<ChatMessage>();

                    for(int c = 0; c < user.size(); c++) {
                        Elements type = texto.eq(c*2).select("div.row > div > div.bubble");
                        String textor = texto.eq(c*2).select("div.row > div > div").text();
                        if(type.size() != 0) {
                            ChatMessage msg = new ChatMessage();
                            msg.setId(c + 1);
                            msg.setMe(true);
                            msg.setMessage(textor);
                            msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatHistory.add(msg);
                        } else {
                            ChatMessage msg = new ChatMessage();
                            msg.setId(c + 1);
                            msg.setMe(false);
                            msg.setMessage(textor);
                            msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatHistory.add(msg);
                        }

                    }
                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            initControls();
                        }
                    });
                } else {
                    AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                    alert.setMessage(getString(R.string.error_defTitle) ,getString(R.string.error_connect));
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

    public void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText(getString(R.string.teacher));// Hard Coded
        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                dialog.show();
                if(Common.isNewChat) {
                    try {
                        Common.jdata.put("txtPregunta", messageText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        WebApi.mpost(Common.TUTORIAS_NT, Common.jdata, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Document doc = Jsoup.parse(response.body().string());
                                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Common.isNewChat = false;
                                            sendBtn.setEnabled(false);
                                            dialog.hide();
                                            Toast.makeText(getBaseContext(), "Sended!", Toast.LENGTH_SHORT).show();
                                            //TODO: Fix get TutoID
                                        }
                                    });
                                }
                                response.close();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Common.jdata.put("TextBoxPregunta", messageText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        WebApi.mpostc(Common.TUTORIAS_NP, Common.jdata, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.hide();
                                        }
                                    });
                                }
                                response.close();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");
                messageET.setEnabled(false);

                displayMessage(chatMessage);
            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        adapter = new ChatAdapter(TutoriaViewActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        if (chatHistory != null) {
            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);
                displayMessage(message);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
