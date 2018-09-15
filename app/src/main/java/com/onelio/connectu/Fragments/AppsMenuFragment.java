package com.onelio.connectu.Fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.onelio.connectu.Activities.Apps.Anuncios.AnunciosActivity;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionActivity;
import com.onelio.connectu.Activities.Apps.Horario.ScheduleActivity;
import com.onelio.connectu.Activities.Apps.Materiales.MaterialesActivity;
import com.onelio.connectu.Activities.Apps.Notas.NotasActivity;
import com.onelio.connectu.Activities.Apps.Profesores.ProfesoresActivity;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasActivity;
import com.onelio.connectu.Activities.Apps.WebView.WebActivity;
import com.onelio.connectu.Activities.Apps.WebView.WebApps;
import com.onelio.connectu.Activities.Apps.Webmail.WebmailActivity;
import com.onelio.connectu.Adapters.AppsAdapter;
import com.onelio.connectu.Common;
import com.onelio.connectu.Helpers.AnimTransHelper;
import com.onelio.connectu.Helpers.SpacesItemDecoration;
import com.onelio.connectu.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppsMenuFragment extends Fragment {

  private RecyclerView recyclerView;
  private RecyclerView.Adapter mAdapter;
  private RecyclerView.LayoutManager mLayoutManager;

  public AppsMenuFragment() {
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
    return inflater.inflate(R.layout.fragment_menu_apps, container, false);
  }

  List<String> input;

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    recyclerView = (RecyclerView) getActivity().findViewById(R.id.appsRecycler);
    recyclerView.setHasFixedSize(true);

    mLayoutManager = new GridLayoutManager(getContext(), 3);
    recyclerView.setLayoutManager(mLayoutManager);
    input = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.app_names)));
    mAdapter = new AppsAdapter(getContext(), onClick, input);
    recyclerView.setAdapter(mAdapter);
    recyclerView.addItemDecoration(new SpacesItemDecoration(getContext()));
  }

  AppsAdapter.OnItemClickListener onClick =
      new AppsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, MotionEvent event, View v) {
          switch (item) {
            case 0: // Anuncios
              Intent intentAnuncios = new Intent(getActivity(), AnunciosActivity.class);
              startActivity(intentAnuncios, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 1: // Materiales
              Intent intentMateriales = new Intent(getActivity(), MaterialesActivity.class);
              startActivity(intentMateriales, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 2: // Profesores
              Intent intentProfesores = new Intent(getActivity(), ProfesoresActivity.class);
              startActivity(intentProfesores, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 3: // Tutorias
              Intent intentTutorias = new Intent(getActivity(), TutoriasActivity.class);
              startActivity(intentTutorias, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 4: // Moodle
              Intent intentMoodle = new Intent(getActivity(), WebActivity.class);
              intentMoodle.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#ff9a05"));
              intentMoodle.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_moodle));
              intentMoodle.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.Moodle);
              intentMoodle.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentMoodle, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 5: // Horario
              Intent intentHorario = new Intent(getActivity(), ScheduleActivity.class);
              startActivity(intentHorario, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 6: // Evaluacion
              Intent intentEvaluacion = new Intent(getActivity(), EvaluacionActivity.class);
              startActivity(intentEvaluacion, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 7: // Notas
              Intent intentNotas = new Intent(getActivity(), NotasActivity.class);
              startActivity(intentNotas, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 8: // Expediente
              Intent intentex = new Intent(getActivity(), WebActivity.class);
              intentex.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#07aaf4"));
              intentex.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_expediente));
              intentex.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.Expediente);
              intentex.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentex, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 9: // UAProject
              Intent intentUAProject = new Intent(getActivity(), WebActivity.class);
              intentUAProject.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#607d8b"));
              intentUAProject.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_uaproject));
              intentUAProject.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.UAProject);
              intentUAProject.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentUAProject, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 10: // OtrosServicios
              Intent intentOtrosServicios = new Intent(getActivity(), WebActivity.class);
              intentOtrosServicios.putExtra(
                  Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#07aaf4"));
              intentOtrosServicios.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_otrosservicios));
              intentOtrosServicios.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.OtrosServicios);
              intentOtrosServicios.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentOtrosServicios, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 11: // Sigua
              Intent intentSigua = new Intent(getActivity(), WebActivity.class);
              intentSigua.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#e91e63"));
              intentSigua.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_sigua));
              intentSigua.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.Sigua);
              intentSigua.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentSigua, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 12: // Webmail
              Intent intentmail = new Intent(getActivity(), WebmailActivity.class);
              intentmail.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#607d8b"));
              intentmail.putExtra(
                  Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_email));
              intentmail.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.Webmail);
              intentmail.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, true);
              startActivity(intentmail, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 13: // UACloud
              Intent intentua = new Intent(getActivity(), WebActivity.class);
              intentua.putExtra(Common.WEBVIEW_EXTRA_COLOR, Color.parseColor("#07aaf4"));
              intentua.putExtra(Common.WEBVIEW_EXTRA_NAME, getString(R.string.title_webapp_ua));
              intentua.putExtra(Common.WEBVIEW_EXTRA_URL, WebApps.UACloud);
              intentua.putExtra(Common.WEBVIEW_EXTRA_NLOGIN, false);
              startActivity(intentua, AnimTransHelper.circleSlideUp(getContext(), v));
              break;
            case 14: // Contacto Beta
              String version = "unknown";
              try {
                PackageInfo pInfo =
                    getActivity()
                        .getPackageManager()
                        .getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
              } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
              }
              Intent emailIntent =
                  new Intent(
                      Intent.ACTION_SENDTO, Uri.fromParts("mailto", "dgrripoll@gmail.com", null));
              emailIntent.putExtra(
                  Intent.EXTRA_SUBJECT, "Contacto Beta ConnectU Version " + version);
              emailIntent.putExtra(
                  Intent.EXTRA_TEXT,
                  "Escribe aquí tu mensaje... Aceptamos reportes y sugerencias así como comentarios de todo tipo");
              startActivity(Intent.createChooser(emailIntent, "Send email..."));
              break;
          }
        }
      };
}
