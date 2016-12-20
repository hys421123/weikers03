package com.team.witkers.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lenovo on 2016/12/20.
 */

public class SplashActivity extends AppCompatActivity{
    private boolean isFirstIn;
//    private   Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("first_pref",MODE_PRIVATE);
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        if (isFirstIn) {
            // start guideactivity1
//            intent = new Intent(this, GuideActivity1.class);
            setContentView(R.layout.activity_splash);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = getSharedPreferences("first_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstIn", false);
                    editor.commit();
                    try {
                        Thread.sleep(3000);

                        startMainAct();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            // start MainActivity
            startMainAct();
        }
    }//onCreate

    private void startMainAct(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}//SplashAct_cls
