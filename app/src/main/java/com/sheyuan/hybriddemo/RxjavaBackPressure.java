package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        //backpressure(背压)的作用是减轻事件发射堆积而产生的内存溢出。
        // 例如 如果其中一个Observable发送事件特别快, 而另一个Observable发送事件特别慢, 那就可能出现这种情况,
        // 发得快的Observable已经发送了1000个事件了, 而发的慢的Observable才发一个出来,
        // 组合了一个之后第一个Observable还剩999个事件, 这些事件需要继续等待第二个Observable发送事件出来组合, 那么这么多的事件是放在哪里的呢?
        // 总有一个地方保存吧? 没错, Zip给我们的每个Observable都弄了一个容器 , 用来保存这些事件。
        //zip中容器的实现是按照队列的形式来保存事件，先进先出。
        //如果一直向容器中添加事件，会导致内存消耗陡升,这个时候就使用backpressure来控制事件发送的流量。
        //但是Rxjava2.0中并没有backpressure的概念，flowable有backpressure的概念。如果Observable控制流量的话有两种方式，一个是取样发送事件（sample()）
        //一个是延时发送事件（线程睡眠）。一般是延时发送事件，取样发送会造成事件丢失。这儿要考虑线程问题（同步 异步），同步状态下，延时取容器中的事件就会同时实现事件的延时发送，异步状态下要分别控制事件的存取。
        Observable.zip(Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i + "");
                }
            }
        }).subscribeOn(Schedulers.io()), Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("你好");
            }
        }).subscribeOn(Schedulers.io()), new BiFunction<String, String, String>() {
            @Override
            public String apply(@NonNull String s, @NonNull String s2) throws Exception {
                return s + s2;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("log", s);
            }
        });
    }
}
