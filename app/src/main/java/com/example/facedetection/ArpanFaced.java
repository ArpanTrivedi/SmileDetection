package com.example.facedetection;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ArpanFaced extends Application {

    public static final String RESULT_TEXT="RESULT_TEXT";
    public static final String RESULT_D="RESULT_D";


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
