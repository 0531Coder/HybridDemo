package com.sheyuan.hybriddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by moutain on 17-10-17 13:18.
 */

public class RxjavaFlowable extends AppCompatActivity {
    /**
     * flowable是采用响应式拉取的方式来解决订阅者处理事件的能力和被订阅者发送事件之间的关系，
     * 订阅者根据自己对事件的处理能力响应式的向被订阅者拉取一定数量的事件，从而解决了被订阅者事件发送太快导致容器中事件大量积累的情况（flowable中容器的size为128）
     * BUFFER_SIZE = Math.max(1, Integer.getInteger("rx2.buffer-size", 128));
     * 如果容器中的事件数量总数大于128，则会抛出MissingBackpressureException
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        flowableSyncExample();
//        flowableAsyncExample();
//        flowableStrategyExample();
        otherFlowableExample();
    }

    private void otherFlowableExample() {
        /**
         * 如果一个Flowable不是自己定义的，这个时候为了避免BackpressureException的产生我们也可以使用背压的策略
         * inerval是将一个数字每隔固定时间+1后发送.
         * 下面的逻辑不做处理肯定会异常，所以加一个BUFFSIZE的策略
         */
        Flowable.interval(1, TimeUnit.MICROSECONDS).onBackpressureBuffer().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Long aLong) {
                try {
                    Thread.sleep(1000);
                    Log.e("log",aLong+"");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("log","onError:"+t);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void flowableStrategyExample() {
        /**
         *  重点介绍flowable的除BackpressureStrategy.ERROR之外的其他策略
         *  1.扩大flowable的BUFFSIZE,这样的策略下flowable和Observable没有什么区别了，但是这样会和Observable一样要注意OOM的问题
         *（被观察者一直在发送事件，但是观察者没有消费事件，虽然容器的大小增大了但是由于观察者没有拉取消费事件，所以还是会导致事件在内存中的大量积累）。
         * BackpressureStrategy.LATEST:只保留最新的事件。
         * BackpressureStrategy.DROP:丢弃超出128的事件。
         */
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    e.onNext(i);
                    Log.e("log", i + "");
                }

            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {
                Log.e("log", "onError:" + t);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void flowableAsyncExample() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 129; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.e("log", "onSubscribe");
//                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                Log.e("log", "onNext:" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("log", "onError:" + t);
            }

            @Override
            public void onComplete() {
                Log.e("log", "onComplete");
            }
        });
    }

    private void flowableSyncExample() {
        //同步状态下，如果订阅者没有调用request()方法来拉取事件,这样就会导致被订阅者一直等待订阅者拉取事件处理，但是如果这段逻辑运行在主线程中会导致ANR
        //所以flowable会抛出MissingBackpressureException
        Flowable<Integer> upsteam = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                Log.e("log", "emit1");
                e.onNext(1);
                Log.e("log", "emit2");
                e.onNext(2);
                Log.e("log", "emit3");
                e.onNext(3);
                Log.e("log", "emit4");
                e.onNext(4);
                Log.e("log", "emit5");
                e.onNext(5);
                Log.e("log", "onComplete");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR);
        Subscriber<Integer> downsteam = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.e("log", "onSubscribe");
//                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                Log.e("log", "onNext:" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("log", "onError:" + t);
            }

            @Override
            public void onComplete() {
                Log.e("log", "onComplete");
            }
        };

        upsteam.subscribe(downsteam);
    }
}
