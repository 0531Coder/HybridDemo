package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by moutain on 17-10-12 15:42.
 * Rxjava的基本使用
 */

public class RxjavaBaseUse extends AppCompatActivity {
    private Disposable mdisposable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        这个是最基础的写法和下面的链式写法只有形式的区别
//        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
//                e.onNext("第一波");
//                e.onNext("第二波");
//                e.onNext("第三波");
//                e.onNext("第四波");
//            }
//        });
//        io.reactivex.Observer<String> observer = new io.reactivex.Observer<String>() {
//
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                Log.e("onSubscribe","onSubscribe");
//            }
//
//            @Override
//            public void onNext(@NonNull String s) {
//                Log.e("onNext",s);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                Log.e("onError","onError");
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e("onComplete","onComplete");
//            }
//        };
//
//        observable.subscribe(observer);


//        rxjava引以为傲的链式写法
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
//                ObserverEmitter是一个事件发射器，可以发射onNext onComplete onError 三种事件，分别对应的就是订阅者的onNext onComplete onError三个函数，有一定的发射规则：
//                1.被订阅者可以发送无数多个onNext事件，订阅者也可以接收无数多个onNext事件
//                2.当被订阅者发出onComplete事件后，还可以继续发送下面的事件，但是当订阅者接收并执行完onComplete事件后，不再接收被订阅者发出的事件
//                3.被订阅者可以不发送onComplete和onError事件
//                4.onComplete和onError事件互斥并且唯一，不可发送多个onComplete,不可发送多个onError,不可两个都发送。
                e.onNext("第一波");
                Log.e("log", "第一波已经发送");
                e.onNext("第二波");
                Log.e("log", "第二波已经发送");
                e.onNext("第三波");
                Log.e("log", "第三波已经发送");
                mdisposable.dispose();
                e.onNext("第四波");
                Log.e("log", "第四波已经发送");
                e.onComplete();
                e.onNext("第五波");
                Log.e("log", "第五波已经发送");
//                e.onError(new Exception("error"));

            }
        }).subscribe(new Observer<String>() {
            //subscribe()有多个重载方法
            //public final Disposable subscribe() {}  订阅者不接受任何的事件
//            public final Disposable subscribe(Consumer<? super T> onNext) {}　订阅者只接受onNext事件
//            public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}　订阅者只接受onNext和onError事件
//            public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}　订阅者只接受onNext和onError，onComplete事件
//            public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {}　订阅者接收全部事件
//            public final void subscribe(Observer<? super T> observer) {}　订阅者接收全部事件

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                //Disposable是一个事件接收的切断器，可以在某一时间将监听者的事件切断但是不会影响事件发送者对事件的发送,会在线程调度中使用到
                // 使用方法就是在被观察者的订阅方法中调用Disposable.dispose();
                mdisposable = d;
                Log.e("log", "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.e("log", s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("log", "onError");
            }

            @Override
            public void onComplete() {
                Log.e("log", "onComplete");
            }
        });


    }
}
