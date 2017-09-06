package com.onelio.connectu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onelio.connectu.Containers.BubbleData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.List;

public class MessageRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context activity;
    private List<BubbleData> messagesItems;
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView txtMsg;
        RelativeLayout lmessage;
        public ViewHolder(View v) {
            super(v);
            lmessage = (RelativeLayout)v.findViewById(R.id.message);
            date = (TextView)v.findViewById(R.id.lblMsgFrom);
            txtMsg = (TextView)v.findViewById(R.id.txtMsg);
        }
    }

    public MessageRecycleAdapter(Activity activity, List<BubbleData> items) {
        this.activity = activity;
        if (items != null) {
            this.messagesItems = items;
        } else {
            this.messagesItems = new ArrayList<>();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        BubbleData m = messagesItems.get(position);
        if (m.isMe()) {
            return 0;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return messagesItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_message_right, parent, false);
            MessageRecycleAdapter.ViewHolder vh = new MessageRecycleAdapter.ViewHolder(v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_message_left, parent, false);
            MessageRecycleAdapter.ViewHolder vh = new MessageRecycleAdapter.ViewHolder(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ViewHolder vh = (ViewHolder)holder;
        BubbleData m = messagesItems.get(position);
        vh.txtMsg.setText(AppManager.removeLastChars(Html.fromHtml(m.getText()).toString(), 2));
        vh.date.setText("13:00"); //TODO change date

    }
}
