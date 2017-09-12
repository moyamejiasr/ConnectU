package com.onelio.connectu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.HomeRequest;
import com.onelio.connectu.API.HorarioRequest;
import com.onelio.connectu.Activities.Apps.Anuncios.AnunciosActivity;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionActivity;
import com.onelio.connectu.Activities.Apps.Horario.HorarioDialog;
import com.onelio.connectu.Activities.Apps.Materiales.MaterialesActivity;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasActivity;
import com.onelio.connectu.Adapters.NotificationsMiniAdapter;
import com.onelio.connectu.Adapters.ReminderAdapter;
import com.onelio.connectu.Adapters.TimeLineAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Helpers.AnimTransHelper;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class HomeMenuFragment extends Fragment {

    App app;
    TextView day;
    TextView month;

    //Swipe
    private SwipeRefreshLayout swiper;

    //RecyclerView -> Notifications Mini
    private RecyclerView notiMrecyclerView;
    private RecyclerView.Adapter notiMAdapter;
    private LinearLayoutManager notiMLayoutManager;
    private LinearLayout notiMLayout;

    //RecyclerView -> Reminder
    private RecyclerView remrecyclerView;
    private RecyclerView.Adapter remAdapter;
    private LinearLayoutManager remLayoutManager;
    private LinearLayout remLLayout;

    //RecyclerView -> Calendar
    private RecyclerView calRecyclerView;
    private TimeLineAdapter calTimeLineAdapter;
    private LinearLayoutManager calTimeLineLayoutManager;
    private LinearLayout calemptyView;

    //Fragments -> Notifications
    private LinearLayout notiLLayout;

    public HomeMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();

        day = (TextView) view.findViewById(R.id.dayD);
        month = (TextView) view.findViewById(R.id.monthD);

        //Date
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime()).toUpperCase();
        day.setText(TimeParserHelper.parseTime(cal.get(Calendar.DAY_OF_MONTH)));
        month.setText(month_name);

        swiper = (SwipeRefreshLayout) view.findViewById(R.id.homeSwipe);
        swiper.setOnRefreshListener(onRefresh);

        //Mini-Notifications
        notiMLayoutManager = new LinearLayoutManager(getContext());
        notiMLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        notiMrecyclerView = (RecyclerView) view.findViewById(R.id.notificationsMiniRecycler);
        notiMLayout = (LinearLayout) view.findViewById(R.id.notiMiniLayout);
        notiMrecyclerView.setLayoutManager(notiMLayoutManager);
        loadMiniNotifications();

        //Reminder
        remLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        remLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        remrecyclerView = (RecyclerView) view.findViewById(R.id.reminderRecycler);
        remrecyclerView.setLayoutManager(remLayoutManager);
        remLLayout = (LinearLayout) view.findViewById(R.id.reminderLayout);
        loadReminders();

        //Calendar
        calTimeLineLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        calTimeLineLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        calRecyclerView = (RecyclerView) view.findViewById(R.id.timelineRecycler);
        calemptyView = (LinearLayout) view.findViewById(R.id.calendar_blank);
        calRecyclerView.setLayoutManager(calTimeLineLayoutManager);
        calRecyclerView.setHasFixedSize(true);
        loadCalendar();

        //Notifications
        notiLLayout = (LinearLayout) view.findViewById(R.id.fragments_container);
        loadNotifications();

    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            HomeRequest request = new HomeRequest(getContext());
            request.getAlerts(new HomeRequest.HomeCallback() {
                @Override
                public void onHomeResult(final boolean onSuccess, final String message) {
                    if(getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swiper.setRefreshing(false);
                            if (onSuccess) {
                                loadMiniNotifications();
                                loadNotifications();
                            } else {
                                ErrorManager error = new ErrorManager(getContext());
                                if (!error.handleError(message)) {
                                    FirebaseCrash.log("Failed Updating swipe at home");
                                    FirebaseCrash.report(new Exception(message));
                                    Toast.makeText(getContext(), getString(R.string.error_unkown_response_format), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        }
    };

    private void loadMiniNotifications() {
        List<String> notificationsMini = new ArrayList<>();
        JSONObject jsonObj = app.notifications;
        Iterator<String> keys = jsonObj.keys();
        while( keys.hasNext() ){
            String key = keys.next(); // Get key in json object
            if (!key.equals("count")) {
                notificationsMini.add(key);
            }
        }
        notiMAdapter = new NotificationsMiniAdapter(getContext(), onNotificationMiniClick, notificationsMini);
        notiMrecyclerView.setAdapter(notiMAdapter);
        if (notificationsMini.size() == 0) {
            notiMLayout.setVisibility(View.GONE);
        }
        else {
            notiMLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadReminders() {
        HorarioRequest request = new HorarioRequest(getContext());
        List<CalendarEvent> events = request.getDateEvent(new Date(), Common.HORARIO_FILTER_EXAMENES + Common.HORARIO_FILTER_EVALUACION);
        remAdapter = new ReminderAdapter(getContext(), onReminderClick, events);
        remrecyclerView.setAdapter(remAdapter);
        if (events.isEmpty()) {
            remLLayout.setVisibility(View.GONE);
        }
        else {
            remLLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadCalendar() {
        HorarioRequest request = new HorarioRequest(getContext());
        List<CalendarEvent> events = request.getDateEvent(new Date(), Common.HORARIO_FILTER_DOCENCIA);
        calTimeLineAdapter = new TimeLineAdapter(getContext(), onTimeLineClick, events);
        calRecyclerView.setAdapter(calTimeLineAdapter);
        if (events.isEmpty()) {
            calRecyclerView.setVisibility(View.GONE);
            calemptyView.setVisibility(View.VISIBLE);
        }
        else {
            calRecyclerView.setVisibility(View.VISIBLE);
            calemptyView.setVisibility(View.GONE);
            calRecyclerView.scrollToPosition(ObjectHelper.getActualLocFromCalendarList(events));
        }
    }

    private void loadNotifications() {
        JSONObject jsonObj = app.notifications;
        Iterator<String> keys = jsonObj.keys();
        LinearLayout linear = (LinearLayout)getActivity().findViewById(R.id.fragments_view);
        if (linear == null)
            return; //Fix error caused by a bad refresh cycle that deletes the linearlayout
        linear.removeAllViewsInLayout(); //Clear before any use
        int i = 1;
        while( keys.hasNext() ){
            String key = keys.next(); // Get key in json object
            if (!key.equals("count")) {
                //Set Extra
                NotificationListFragment intent = new NotificationListFragment();
                Bundle b = new Bundle();
                b.putString(Common.HOME_EXTRA_TYPE, key);
                intent.setArguments(b);
                //Create View and add it
                RelativeLayout fragment = new RelativeLayout(getActivity());
                fragment.setId(i);
                getFragmentManager().beginTransaction().add(fragment.getId(), intent, null).commitAllowingStateLoss(); //commitAllowingStateLoss insteaf of commit to fix a bug Exception java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                linear.addView(fragment);
                i++;
            }
        }
        if (i<2) { //1 or 0 Means really 0
            notiLLayout.setVisibility(View.GONE);
        } else {
            notiLLayout.setVisibility(View.VISIBLE);
        }
    }

    NotificationsMiniAdapter.OnItemClickListener onNotificationMiniClick = new NotificationsMiniAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, String id, View v) {
            if (id.equals("MATDOCENTE")) {
                Intent intent = new Intent(getActivity(), MaterialesActivity.class);
                startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), v));
            }
            if (id.equals("UATUTORIAS")) {
                Intent intent = new Intent(getActivity(), TutoriasActivity.class);
                startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), v));
            }
            if (id.equals("ANUNCIOS")) {
                Intent intent = new Intent(getActivity(), AnunciosActivity.class);
                startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), v));
            }
            if (id.equals("UAEVALUACION")) {
                Intent intent = new Intent(getActivity(), EvaluacionActivity.class);
                startActivity(intent, AnimTransHelper.circleSlideUp(getContext(), v));
            }
        }
    };

    ReminderAdapter.OnItemClickListener onReminderClick = new ReminderAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, CalendarEvent event) {
            HorarioDialog dialog = new HorarioDialog(getActivity(), event);
            dialog.show();
        }
    };

    TimeLineAdapter.OnItemClickListener onTimeLineClick = new TimeLineAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, CalendarEvent event) {
            HorarioDialog dialog = new HorarioDialog(getActivity(), event);
            dialog.show();
        }
    };

}