package com.onelio.connectu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsAdapter extends BaseAdapter {

  private Context context;
  private JSONArray rowData;

  public NotificationsAdapter(Context context, JSONArray items) {
    this.context = context;
    this.rowData = items;
  }
  /*private view holder class*/
  private class ViewHolder {
    TextView title;
    TextView subtitle;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.view_notifications_list, null);
      holder = new ViewHolder();
      holder.title = (TextView) convertView.findViewById(R.id.titleD);
      holder.subtitle = (TextView) convertView.findViewById(R.id.subtitleD);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    String title = context.getString(R.string.error_notificatin_null_title);
    String subtitle = context.getString(R.string.error_notificatin_null);
    try {
      JSONObject jdata = rowData.getJSONObject(position);
      String type = jdata.getString("type");
      if (type.equals("MATDOCENTE")) {
        title = AppManager.capFirstLetter(AppManager.after(jdata.getString("text"), ") "));
        subtitle =
            context.getString(R.string.notifi_more_title_have)
                + jdata.getString("count")
                + " "
                + context.getString(R.string.notifi_more_title_more_files);
      } else if (type.equals("UATUTORIAS")) {
        title = jdata.getString("text");
        subtitle = AppManager.capAfterSpace(jdata.getString("title"));
      } else {
        title = jdata.getString("title");
        subtitle = jdata.getString("text");
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    holder.title.setText(title);
    holder.subtitle.setText(subtitle);
    return convertView;
  }

  @Override
  public int getCount() {
    return rowData.length();
  }

  @Override
  public Object getItem(int position) {
    try {
      return rowData.get(position);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }
}
