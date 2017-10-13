package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by moutain on 17-10-13 16:43.
 */

public class RxjavaBackPressure extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressureExample();
    }

    private void backPressureExample() {
        //backpressure(背压)的作用是减轻事件发射堆积而产生的内存溢出，例如
        Observable.zip(Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                for (int i=0;;i++){
                    e.onNext(i+"");
                }
            }
        }), Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

            }
        }), new BiFunction<String, String, String>() {
            @Override
            public String apply(@NonNull String s, @NonNull String s2) throws Exception {
                return null;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

            }
        });
    }
}
