package com.sheyuan.hybriddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private Disposable mdisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,RxjavaFlowable.class);
        startActivity(intent);
    }
}
