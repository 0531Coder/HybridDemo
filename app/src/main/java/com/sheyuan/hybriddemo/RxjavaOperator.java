package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by moutain on 17-10-12 17:50.
 * Rxjava的操作符使用
 */

public class RxjavaOperator extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        example();
        mapOperator();
//        flatMapOperator();
        //使用flatmap解决嵌套问题
//        flatExample();
        //注意map和flatmap之间的区别
        /*
        1.map返回的是结果集，flatmap返回的是包含结果集的Observable
        2.map被订阅时每传递处理一个事件执行一次订阅者的onNext()方法。
          而flatmap()用于一对多或者多对多的被订阅时，先把所有的数据汇总到一个Observable再进行一一分发(调用订阅者的onNext())。
        3.map只能单一转换，单一指的是只能一对一进行转换，指一个对象可以转化为另一个对象但是不能转换成对象数组（map返回结果集不能直接使用from/just再次进行事件分发，一旦转换成对象数组的话，再处理集合/数组的结果时需要利用for一一遍历取出。而使用RxJava就是为了剔除这样的嵌套结构，使得整体的逻辑性更强。）
          flatmap既可以单一转换也可以一对多 多对多转换，flatmap要求返回Observable，因此可以再内部进行from/just的再次事件分发，一一取出单一对象（转换对象的能力不同）
        */
    }

    private void flatExample() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.e("log", "开始请求注册接口，当前线程名称：" + Thread.currentThread().getName());
                Thread.sleep(2000);
                e.onNext("注册成功");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e("log", "请求注册接口结束，当前线程名称：" + Thread.currentThread().getName());
                        Log.e("log", s);
                    }
                }).observeOn(Schedulers.io()).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        Log.e("log", "开始请求登录接口，当前线程名称：" + Thread.currentThread().getName());
                        Thread.sleep(2000);
                        e.onNext("登录成功");
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("log", "请求登录接口结束，当前线程名称：" + Thread.currentThread().getName());
                Log.e("log", s);
            }
        });
    }

    private void flatMapOperator() {
        //flatmap可以将Observable拆分成多个发送事件的Observables然后将他们的事件合并后放在一个单独的Observable中。
        // 注意：flatmap不能保证事件发送的顺序（不一定和被订阅者的事件发送顺序相同）如果有发送顺序与被订阅者的发送顺序相同的需求以使用concatmap

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
                e.onNext(6);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add(integer + "");
                }
                return Observable.fromIterable(list).delay(3000, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("log", s);
            }
        });

    }

    private void mapOperator() {
        //map 转换的操作符 主要是类型或者内容的转换 比方说将String转换成Integer或者是内容的变化。
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("傻狗1");
                Log.e("log", "发送傻狗1");
                e.onNext("傻狗2");
                Log.e("log", "发送傻狗2");
                e.onNext("傻狗3");
                Log.e("log", "发送傻狗3");
                e.onNext("傻狗4");
                Log.e("log", "发送傻狗4");
                e.onNext("傻狗5");
                Log.e("log", "发送傻狗5");
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(@NonNull String s) throws Exception {
                //这儿执行转换逻辑并且返回。
                return 88888 + s;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("log", s);
            }
        });

    }

    private void example() {
        //嵌套循环 场景预设 操作符可以比较清晰的解决复杂的逻辑
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Thread.sleep(5000);
                e.onNext("请求结果-token");
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.e("log", s);
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        Thread.sleep(5000);
                        e.onNext("请求结果-login");
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.e("log", s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
