package com.mj.gpsclient.Activity;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseStrategyHandler {

	public static final String TAG = "StrategyHandler";
	protected int CORE_POOL_SIZE = 2, MAX_POOL_SIZE = 4, KEEP_ALIVE_TIME = 1;
	protected int QUENE_SIZE = 15;
	protected BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
			QUENE_SIZE);
	protected ThreadPoolExecutor executor = null;

	protected BaseStrategyHandler() {
		executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue);
		// 策略：先丢弃任务队列中的第一个任务，然后把这个任务加进队列
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	protected static BaseStrategyHandler instance = null;

	public static synchronized BaseStrategyHandler get() {
		if (null == instance) {
			instance = new BaseStrategyHandler();
		}
		return instance;
	}

	public interface ICallBack<T> {
		public void onTaskStart();

		public void onTaskFinish(T params);

		public void onTaskError();
	}

}
