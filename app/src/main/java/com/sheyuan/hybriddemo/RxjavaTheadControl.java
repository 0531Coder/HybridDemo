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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by moutain on 17-10-12 15:47.
 * Rxjava的线程调度
 */

public class RxjavaTheadControl extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //验证观察者是在和被观察者同一线程消费事件
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Observable.create(new ObservableOnSubscribe<String>() {
//                    //被观察者在哪个线程发出事件，观察者在同一线程处理消费事件,联想handler消息机制
//                    @Override
//                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
//                        e.onNext("第一波");
//                        Log.e("log","Thead name:"+Thread.currentThread().getId());
//                    }
//                }).subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull String s) {
//                        Log.e("log",s);
//                        Log.e("log","Thead name:"+Thread.currentThread().getId());
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//            }
//        }).start();


        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Log.e("log", Thread.currentThread().getName());
                e.onNext("第一波");
                Log.e("log", "第一波已经发出");
                Log.e("log", Thread.currentThread().getName());
                e.onNext("第二波");
                Log.e("log", "第二波已经发出");
                Log.e("log", Thread.currentThread().getName());
                e.onNext("第三波");
                Log.e("log", "第三波已经发出");
                Log.e("log", Thread.currentThread().getName());
                e.onNext("第四波");
                Log.e("log", "第四波已经发出");
            }
        });
        Consumer<String> consumer = new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                Log.e("log", s);
                Log.e("log", Thread.currentThread().getName());
            }
        };
        //指定被观察者和观察者的执行线程
        //注意：被观察者的线程如果指定了多次，只会第一次指定的有效。观察者的线程如果指定了多次就每执行一次对应的观察者所在的线程就会切换一次
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
//        举例说明多次指定线程的时候线程的切换过程，这样每次调用observe就会切换一次线程，doOnNext()方法只是在Subscribe观察者回调方法执行之前执行，并无其他的作用(常用作网络请求结果的缓存和数据显示之前的比对)。
//        如果不调用doOnNext()。因为每调用observeOn都会切换一次线程，所以订阅者的回调方法只会在最后指定的线程中执行，则无法体现线程切换效果。
//        doOnNext()方法会造成线程的阻塞，例如缓存，只有等待数据缓存完才能执行数据的显示，肯定是不符合预期的。可以使用非阻塞I/O操作来解决这个问题(https://github.com/yuxingxin/RxJava-Essentials-CN/blob/master/chapter7/nonblocking_io_operations.md)。
        observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).doOnNext(consumer).observeOn(Schedulers.io()).doOnNext(consumer).subscribe(consumer);
    }
}
