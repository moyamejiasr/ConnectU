package com.onelio.connectu.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.Activities.Apps.Anuncios.AnunciosActivity;
import com.onelio.connectu.Activities.Apps.Anuncios.AnunciosViewActivity;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionActivity;
import com.onelio.connectu.Activities.Apps.Materiales.MaterialesActivity;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasActivity;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasViewActivity;
import com.onelio.connectu.Adapters.NotificationsAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Helpers.AnimTransHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;


public class NotificationListFragment extends Fragment {

    App app;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();
        final String stype = getArguments().getString(Common.HOME_EXTRA_TYPE);

        TextView type = (TextView) view.findViewById(R.id.typeD);
        type.setText(stype);

        //Set List
        JSONArray jsonObj = new JSONArray();
        try {
            jsonObj = app.notifications.getJSONObject(stype).getJSONArray("notifications");
            ListView list = (ListView) view.findViewById(R.id.notificationsList);
            final NotificationsAdapter adapter = new NotificationsAdapter(getContext(), jsonObj);
            list.setAdapter(adapter);
            final JSONArray finalJsonObj = jsonObj;
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (stype.equals("MATDOCENTE")) {
                        Intent intent = new Intent(getActivity(), MaterialesActivity.class);
                        startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                    }
                    if (stype.equals("UATUTORIAS")) {
                        try {
                            Intent intent = new Intent(getActivity(), TutoriasViewActivity.class);
                            intent.putExtra(Common.TUTORIAS_STRING_ID, AppManager.after(finalJsonObj.getJSONObject(i).getString("url"), "/"));
                            intent.putExtra(Common.TUTORIAS_STRING_AUTHOR, finalJsonObj.getJSONObject(i).getString("title"));
                            intent.putExtra(Common.TUTORIAS_STRING_TITLE, finalJsonObj.getJSONObject(i).getString("text"));
                            intent.putExtra(Common.TUTORIAS_BOOL_ISHOME, true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                finalJsonObj.remove(i);
                            }
                            adapter.notifyDataSetChanged();
                            countNotifications(finalJsonObj);
                            startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                        } catch (JSONException e) {
                            Intent intent = new Intent(getActivity(), TutoriasActivity.class);
                            startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                        }
                    }
                    if (stype.equals("ANUNCIOS")) {
                        try {
                            Intent intent = new Intent(getActivity(), AnunciosViewActivity.class);
                            intent.putExtra("JDATA", finalJsonObj.getJSONObject(i).getString("id"));
                            intent.putExtra("LOAD", true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                finalJsonObj.remove(i);
                            }
                            adapter.notifyDataSetChanged();
                            countNotifications(finalJsonObj);
                            startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                        } catch (JSONException e) {
                            Intent intent = new Intent(getActivity(), AnunciosActivity.class);
                            startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                        }
                    }
                    if (stype.equals("UAEVALUACION")) {
                        Intent intent = new Intent(getActivity(), EvaluacionActivity.class);
                        startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), view));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.log("Exception getting notifications in main");
            FirebaseCrash.log(app.notifications.toString());
            FirebaseCrash.report(e);
        }

        TextView count = (TextView) view.findViewById(R.id.countD);
        count.setText(getContext().getString(R.string.notifi_more_title_have) + " " + jsonObj.length() + " " + getContext().getString(R.string.notifi_more_title_more_notifications));

    }

    private void countNotifications(JSONArray data) {
        TextView count = (TextView) getActivity().findViewById(R.id.countD);
        count.setText(getContext().getString(R.string.notifi_more_title_have) + " " + data.length() + " " + getContext().getString(R.string.notifi_more_title_more_notifications));
    }

}
