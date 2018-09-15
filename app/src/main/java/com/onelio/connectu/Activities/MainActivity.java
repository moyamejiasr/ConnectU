package com.onelio.connectu.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;
import com.onelio.connectu.Adapters.ViewPagerAdapter;
import com.onelio.connectu.Fragments.AppsMenuFragment;
import com.onelio.connectu.Fragments.FavoritesMenuFragment;
import com.onelio.connectu.Fragments.HomeMenuFragment;
import com.onelio.connectu.Fragments.PreferenceMenuFragment;
import com.onelio.connectu.R;

public class MainActivity extends AppCompatActivity {

  private TabLayout tabLayout;
  private ViewPager viewPager;

  private boolean hasBackBeenPressed = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    overridePendingTransition(R.anim.normal_fade_in, R.anim.normal_fade_out);
    setContentView(R.layout.activity_main);

    // SetView
    viewPager = (ViewPager) findViewById(R.id.viewpager);
    tabLayout = (TabLayout) findViewById(R.id.tabs);
    setupViewPager(viewPager);
  }

  @Override
  public void onBackPressed() {
    if (!hasBackBeenPressed) {
      hasBackBeenPressed = true;
      Toast.makeText(getBaseContext(), getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();
    } else {
      super.onBackPressed();
    }
  }

  private void setupViewPager(ViewPager viewPager) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    adapter.addFrag(new HomeMenuFragment(), "Home");
    adapter.addFrag(new AppsMenuFragment(), "Apps");
    adapter.addFrag(new FavoritesMenuFragment(), "Favs");
    adapter.addFrag(new PreferenceMenuFragment(), "Settings");
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
    setupTabIcons();
  }

  private void setupTabIcons() {
    tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
    tabLayout.getTabAt(1).setIcon(R.drawable.ic_apps_black_24dp);
    tabLayout.getTabAt(2).setIcon(R.drawable.ic_fav_black_24dp);
    tabLayout.getTabAt(3).setIcon(R.drawable.ic_view_black_25dp);
  }
}
