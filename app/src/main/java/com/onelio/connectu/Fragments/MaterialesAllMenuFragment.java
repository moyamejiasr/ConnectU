package com.onelio.connectu.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.MaterialesRequest;
import com.onelio.connectu.Activities.Apps.Materiales.MaterialesActivity;
import com.onelio.connectu.Activities.Apps.Materiales.MaterialesDialog;
import com.onelio.connectu.Adapters.MaterialesAdapter;
import com.onelio.connectu.Containers.MaterialData;
import com.onelio.connectu.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class MaterialesAllMenuFragment extends Fragment {

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout emptyView;
    private AVLoadingIndicatorView progress;
    private TextView location;

    private MaterialesRequest request;
    private List<MaterialData> files;

    public MaterialesAllMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_materiales, container, false);
    }

    @Override
    public void onDestroy() {
        MaterialesActivity.mOnBackPressed = null;
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.materialesRecycler);
        emptyView = (LinearLayout) view.findViewById(R.id.materiales_blank);
        recyclerView.setLayoutManager(mLayoutManager);
        progress = (AVLoadingIndicatorView) view.findViewById(R.id.material_progress);
        location = (TextView) view.findViewById(R.id.materialesLoc);
        request = new MaterialesRequest(getContext());
        location.setText(getString(R.string.title_folder_unseen));
        files = new ArrayList<>();

        loadUnseen();
    }

    private void loadUnseen() {
        progress.show();
        files.clear();
        mAdapter = new MaterialesAdapter(getContext(), onClick, files);
        recyclerView.setAdapter(mAdapter);
        request.loadPendingMateriales(new MaterialesRequest.MaterialsCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.hide();
                        if (onResult) {
                            files = request.getMateriales();
                            mAdapter = new MaterialesAdapter(getContext(), onClick, files);
                            recyclerView.setAdapter(mAdapter);
                            if (files == null || files.size() == 0) {
                                if (!request.isSessionTerminated(message)) {
                                    recyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    //Session terminated -Retry once login
                                    Toast.makeText(getContext(), getString(R.string.error_materiales_session_ended), Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                //Everything OK
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        });
    }

    MaterialesAdapter.OnItemClickListener onClick = new MaterialesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item) {
            MaterialData file = files.get(item);
            if (!file.isFolder()) {
                //Show dialog file properties
                MaterialesDialog dialog = new MaterialesDialog(getActivity(), files.get(item));
                dialog.show();
            }
        }
    };

}
