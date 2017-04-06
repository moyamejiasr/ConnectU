package com.onelio.connectu.Apps.Tutorias;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.API.WebApi;
import com.onelio.connectu.Apps.Chat.MessagesListAdapter;
import com.onelio.connectu.Apps.Chat.Msg;
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
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TutoriaViewActivity extends AppCompatActivity {

    ProgressDialog dialog;
    private EditText messageET;
    private GridView messagesContainer;
    private ImageButton sendBtn;
    private MessagesListAdapter adapter;
    ArrayList<Msg> listMessages;
    private ArrayList<Msg> chatHistory;
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

        if(!Common.isNewChat) {
            sendRead();
            requestData();
            dialog.show();
        } else {
            initControls();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutoria_view, menu);
        menu.getItem(0).setIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id != R.id.icon) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendRead() {
        String json = "idTuto=" + Common.tutId;
        UAWebService.HttpWebPostRequest(TutoriaViewActivity.this, UAWebService.TUTORIAS_READ, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, final String body) {
                if (!isSuccessful) {
                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseCrash.log(body);
                            FirebaseCrash.report(new Exception("Failed to make tutoria " + Common.tutId + " readed"));
                            Toast.makeText(getBaseContext(), "Error while making this tutoria as readed!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Common.needMainReload = true;
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
                    chatHistory = new ArrayList<>();

                    for(int c = 0; c < user.size(); c++) {
                        Elements type = texto.eq(c*2).select("div.row > div > div.bubble");
                        String textor = texto.eq(c*2).select("div.row > div > div").text();
                        if(type.size() != 0) {
                            Msg msg = new Msg(true, textor);
                            chatHistory.add(msg);
                        } else {
                            Msg msg = new Msg(false, textor);
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
        messagesContainer = (GridView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);

        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                dialog.show();
                if(Common.isNewChat) {
                    messageET.setEnabled(false);
                    try {
                        Common.jdata.put("txtPregunta", messageText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        WebApi.mpost(Common.TUTORIAS_NT, Common.jdata, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                alert.setMessage(getString(R.string.app_name), getString(R.string.error_connect));
                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                    @Override
                                    public void onClick(boolean isPositive) {
                                    }
                                });
                                alert.show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String text = response.body().string();
                                if (response.isSuccessful()) {
                                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.hide();
                                            try {
                                                JSONObject result = new JSONObject(text);
                                                if (result.getInt("resultado") == -1) {
                                                    AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                                    alert.setMessage(getString(R.string.app_name), result.getString("result"));
                                                    alert.setIcon(R.mipmap.ic_launcher);
                                                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                        @Override
                                                        public void onClick(boolean isPositive) {
                                                        }
                                                    });
                                                    alert.show();
                                                } else {
                                                    Common.isNewChat = false;
                                                    sendBtn.setEnabled(true); //Who cares...
                                                    Common.tutId = String.valueOf(result.getInt("resultado"));
                                                    if (Common.jdata.has("idTuto") ) {
                                                        Common.jdata.remove("idTuto");
                                                    }
                                                    Common.jdata.put("idTuto", Common.tutId);
                                                    Msg chatMessage = new Msg(true, messageText);
                                                    messageET.setText("");
                                                    displayMessage(chatMessage);
                                                    Toast.makeText(getBaseContext(), result.getString("result"), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                                alert.setMessage(getString(R.string.app_name), getString(R.string.error_format));
                                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                    @Override
                                                    public void onClick(boolean isPositive) {
                                                    }
                                                });
                                                alert.show();
                                            }
                                        }
                                    });
                                } else {
                                    AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                    alert.setMessage(getString(R.string.app_name), getString(R.string.error_connect));
                                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                        @Override
                                        public void onClick(boolean isPositive) {
                                        }
                                    });
                                    alert.show();
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
                                AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                alert.setMessage(getString(R.string.app_name), getString(R.string.error_connect));
                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                    @Override
                                    public void onClick(boolean isPositive) {
                                    }
                                });
                                alert.show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String text = response.body().string();
                                if (response.isSuccessful()) {
                                    TutoriaViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.hide();
                                            try {
                                                JSONObject result = new JSONObject(text);
                                                if (result.getInt("resultado") == -1) {
                                                    AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                                    alert.setMessage(getString(R.string.app_name), result.getString("result"));
                                                    alert.setIcon(R.mipmap.ic_launcher);
                                                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                        @Override
                                                        public void onClick(boolean isPositive) {
                                                        }
                                                    });
                                                    alert.show();
                                                } else {
                                                    Msg chatMessage = new Msg(true, messageText);
                                                    messageET.setText("");
                                                    displayMessage(chatMessage);
                                                    Toast.makeText(getBaseContext(), result.getString("result"), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                                alert.setMessage(getString(R.string.app_name), getString(R.string.error_format));
                                                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                                    @Override
                                                    public void onClick(boolean isPositive) {
                                                    }
                                                });
                                                alert.show();
                                            }
                                        }
                                    });
                                } else {
                                    AlertManager alert = new AlertManager(TutoriaViewActivity.this);
                                    alert.setMessage(getString(R.string.app_name), getString(R.string.error_connect));
                                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                        @Override
                                        public void onClick(boolean isPositive) {
                                        }
                                    });
                                    alert.show();
                                }
                                response.close();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void displayMessage(Msg message) {
        listMessages.add(message);
        messagesContainer.deferNotifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(listMessages.size()-1);
    }

    private void loadDummyHistory(){
        listMessages = new ArrayList<Msg>();
        adapter = new MessagesListAdapter(this, listMessages);
        messagesContainer.setAdapter(adapter);

        if (chatHistory != null) {
            for (int i = 0; i < chatHistory.size(); i++) {
                Msg message = chatHistory.get(i);
                displayMessage(message);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
