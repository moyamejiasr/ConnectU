package com.onelio.connectu.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.onelio.connectu.R;

public class FavoritesMenuFragment extends Fragment {

  public FavoritesMenuFragment() {
    //  Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //  Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_menu_favorites, container, false);
  }
}
