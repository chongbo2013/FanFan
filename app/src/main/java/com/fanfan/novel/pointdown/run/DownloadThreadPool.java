/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanfan.novel.pointdown.run;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadThreadPool {

    private int corePoolSize = 3;                        //核心线程池的数量，同时能执行的线程数量，默认3个
    private static final int MAX_POOL_SIZE = 5;          //最大线程池的数量
    private static final int KEEP_ALIVE_TIME = 1;        //存活的时间
    private static final TimeUnit UNIT = TimeUnit.HOURS; //时间单位
    private ThreadPoolExecutor executor;               //线程池执行器

    public ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            synchronized (DownloadThreadPool.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(corePoolSize, MAX_POOL_SIZE, KEEP_ALIVE_TIME, UNIT, //
                            new SynchronousQueue<Runnable>(),   //无限容量的缓冲队列
                            Executors.defaultThreadFactory(),        //线程创建工厂
                            new ThreadPoolExecutor.AbortPolicy());   //继续超出上限的策略，阻止
                }
            }
        }
        return executor;
    }

}
