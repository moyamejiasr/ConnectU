package com.onelio.connectu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.onelio.connectu.API.TutoriasRequest;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasViewActivity;
import com.onelio.connectu.Adapters.TutoriasAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Containers.TutoriaData;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class TutoriasMenuFragment extends Fragment {

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout emptyView;
    private AVLoadingIndicatorView progress;

    private App app;
    private TutoriasRequest request;
    private List<TutoriaData> tutorias;
    private List<SubjectData> subjects;
    private int additions;

    public TutoriasMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_tutorias, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.tutoriasRecycler);
        emptyView = (LinearLayout) view.findViewById(R.id.tutorias_blank);
        recyclerView.setLayoutManager(mLayoutManager);
        progress = (AVLoadingIndicatorView) view.findViewById(R.id.tutorias_progress);
        request = new TutoriasRequest(getContext());
        app = (App) getActivity().getApplication();
        subjects = app.academicYears.get(request.getYear()).getSubjectsData();
        additions = 0;
        loadTutorias();
    }

    private void loadTutorias() {
        progress.show();
        tutorias = new ArrayList<>();
        for (final SubjectData subject : subjects) {
            request.fetchPendingTutoriasFrom(subject.getId(), new TutoriasRequest.TutoriasCallback() {
                @Override
                public void onResult(final boolean onResult, final String message) {
                    if(getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onResult) {
                                List<TutoriaData> stutorias = request.parseTutorias(true, message, subject);
                                for (TutoriaData tutoria : stutorias) {
                                    tutorias.add(tutoria);
                                }
                            } else {
                                ErrorManager error = new ErrorManager(getContext());
                                error.handleError(message);
                            }
                            additions++;
                            if (additions == subjects.size()) { //Ended!
                                progress.hide();
                                mAdapter = new TutoriasAdapter(getContext(), onClick, tutorias);
                                recyclerView.setAdapter(mAdapter);
                                if (tutorias.size() == 0) {
                                    recyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                                else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            });
        }
        if (subjects.isEmpty()) { //In case of no subjects still for this year
            progress.hide();
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    TutoriasAdapter.OnItemClickListener onClick = new TutoriasAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item) {
            Intent intent = new Intent(getActivity(), TutoriasViewActivity.class);
            intent.putExtra(Common.TUTORIAS_STRING_ID, tutorias.get(item).getId());
            intent.putExtra(Common.TUTORIAS_STRING_AUTHOR, tutorias.get(item).getTeacherName());
            intent.putExtra(Common.TUTORIAS_STRING_TITLE, tutorias.get(item).getTitle());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

}
