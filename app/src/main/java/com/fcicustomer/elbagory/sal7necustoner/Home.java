package com.fcicustomer.elbagory.sal7necustoner;

import android.Manifest;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.fcicustomer.elbagory.sal7necustoner.Fragments.DashbordFragment;
import com.fcicustomer.elbagory.sal7necustoner.Fragments.HomeFragment;
import com.fcicustomer.elbagory.sal7necustoner.Fragments.SettingFragment;
import com.fcicustomer.elbagory.sal7necustoner.NetworkUtils.NetworkUtils;
import com.roughike.bottombar.BottomBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.roughike.bottombar.OnTabSelectListener;
import com.sdsmdg.tastytoast.TastyToast;

public class Home extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EnableRuntimePermission();

       // getSupportActionBar().setTitle("الصفحه الشخصيه");

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
               switch (tabId)
               {
                   case R.id.tab_home:
                       if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                           changeFragment(new HomeFragment());
                          // getSupportActionBar().setTitle("الصفحه الشخصيه");
                       }
                       else
                       {
                           TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                   TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                       }
                       break;
                   case R.id.tab_dashbord:
                       if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                           changeFragment(new DashbordFragment());
                       }
                       else
                       {
                           TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                   TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                       }
                       break;
                       case R.id.tab_setting:
                           if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {

                               changeFragment(new SettingFragment());
                              // getSupportActionBar().setTitle("الاعدادات");
                           }
                           else
                           {
                               TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                                       TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                           }
                           break;
               }
            }
        });


    }

    private void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentContainer, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    private void EnableRuntimePermission(){


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE))
        {


        } else {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CALL_PHONE}, RequestPermissionCode);

        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {


        } else {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);

        }
    }




}


