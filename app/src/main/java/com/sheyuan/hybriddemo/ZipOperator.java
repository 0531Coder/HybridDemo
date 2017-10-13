package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by moutain on 17-10-13 12:35.
 */

public class ZipOperator extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        zipExample();
        zipUse();
    }

    private void zipUse() {
        //zip操作符的应用场景
        /**
         * 当一个页面的内容又两个或者多个接口返回的数据构成时可以使用zip
         */
        Observable.zip(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                //发起第一个接口的请求
                Thread.sleep(2000);
                e.onNext("请求第一个接口");
                Log.e("log","请求第一个接口");
            }
        }).subscribeOn(Schedulers.io()), Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Thread.sleep(8000);
                e.onNext("请求第二个接口");
                Log.e("log","请求第二个接口");
            }
        }).subscribeOn(Schedulers.io()), new BiFunction<String, String, String>() {
            @Override
            public String apply(@NonNull String s, @NonNull String s2) throws Exception {
                return "结果展示";
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.e("log",s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void zipExample() {
        //zip操作符是将多个Observable发送的事件合并成一个事件并按照原Observable的顺序进行发送,发送事件的个数与事件最少的Observable的个数相同
        //创建两个Observable进行演示
        /**
         * 注意：
         * 1.zip操作符是按照是发送事件最少的Observable来确定发送个数的
         *      如果两个Observable在同一个线程中定义，因为在同一个线程中存在代码执行的先后顺序，所以定义在前面的Observable先执行并且发送事件。
         *      由于其它的Observable还没有定义，并不知道zip要处理的事件的个数，所以事件的发送规则和未使用zip操作符发送规则一致。
         *      定义在前面的Observable先将所有事件发送完，后面的Observable再发送事件，发送的事件个数和事件个数少的Observable一致
         * 2.如果想让两个Observable同时工作，就给它们指定不同的线程，它们会同时发送然后经过zip操作符合并发送给观察者，如果在Observable中发送了onComplete事件，
         *      那么事件较多的Observable多出来的事件就不会发送了，如果Observable没有发送onComplete事件那么事件个数较多的Observable还会继续发送事件但是zip操作符不再接收处理。
         * 3.两个Observable中有一个发送onError事件，那么最终的订阅者都会回调onError方法
         */
        Observable<String> observableA = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Log.e("log","Observable1的线程名称为:"+Thread.currentThread().getId());
                Log.e("log", "发送1");
                e.onNext("1");
//                Thread.sleep(1000);
                Log.e("log", "发送2");
                e.onNext("2");
//                e.onError(new Throwable("网络请求1连接失败"));
//                Thread.sleep(1000);
                Log.e("log", "发送3");
                e.onNext("3");
//                Thread.sleep(1000);
//                Thread.sleep(1000);
                e.onNext("4");
                Log.e("log", "发送4");
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observableB = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Log.e("log","Observable2的线程名称为:"+Thread.currentThread().getId());
                Log.e("log", "发送A");
                e.onNext("A");
//                Thread.sleep(1000);
                Log.e("log", "发送B");
                e.onNext("B");
//                Thread.sleep(1000);
                e.onError(new Throwable("网络请求2连接失败"));
                Log.e("log", "发送C");
                e.onNext("C");
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observableA, observableB, new BiFunction<String, String, String>() {
            @Override
            public String apply(@NonNull String o, @NonNull String o2) throws Exception {
                return o + o2;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.e("log", s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("log","显示失败"+e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
