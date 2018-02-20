/**
 * This content is released under the MIT License (MIT)
 *
 * Copyright (c) 2018, canchito-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author 		José Carlos Mendoza Prego
 * @copyright	Copyright (c) 2018, canchito-dev (http://www.canchito-dev.com)
 * @license		http://opensource.org/licenses/MIT	MIT License
 * @link		https://github.com/canchito-dev/thread-pool-with-task-queue-in-database
 **/
package com.canchitodev.example.threadpool.utils;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ExecutorServiceUtils {

	private static String DEFAULT_POOL_NAME = "RUNNABLE";
	private static int DEFAULT_CORE_POOL_SIZE = 10;
	private static int DEFAULT_MAXIMUM_POOL_SIZE = 10;
	private static long DEFAULT_KEEP_ALIVE_TIME = 0L;
	private static String DEFAULT_LOCK_OWNER = "OWNER";
	
	private static final Logger logger = Logger.getLogger(ExecutorServiceUtils.class);
	
	private static PriorityBlockingQueue<Runnable> getWorkQueue() {
		PriorityBlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<Runnable>();
		return workQueue;
	}
	
	/**
     * Returns a new thread configured with the default settings.
     * @param poolName			- name of the thread pool
     * @return A new thread configured with the default settings.
     **/
	private static ThreadFactory createDefaultThreadFactory(String poolName) {
		return new DefaultThreadFactory(poolName);
	}
	
	/**
     * Returns a new thread pool configured with the default settings.
     * @return A new thread pool configured with the default settings.
     **/
    public static ThreadPoolExecutor createDefaultExecutorService() {
		logger.info("Creating executor service '" + DEFAULT_POOL_NAME 
				+ "' with corePoolSize '" + DEFAULT_CORE_POOL_SIZE 
				+ "', maxPoolSize '" + DEFAULT_MAXIMUM_POOL_SIZE 
				+ "', keepAliveTime '" + DEFAULT_KEEP_ALIVE_TIME 
				+ "' and lockOwner '" + DEFAULT_LOCK_OWNER);
        ThreadFactory threadFactory = ExecutorServiceUtils.createDefaultThreadFactory("runnable");
        return new DefaultThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, 
        		TimeUnit.MILLISECONDS, getWorkQueue(), threadFactory, DEFAULT_LOCK_OWNER);
    }
    
    /**
     * Returns a new thread pool configured with the default settings.
     * @param poolName			- name of the thread pool
	 * @param corePoolSize		- the number of threads to keep in the pool, even if they are idle
	 * @param keepAliveTime		- when the number of threads is greater than the core, this is the maximum time that excess idle 
	 * 							  threads will wait for new tasks before terminating
	 * @param lockOwner			- the id of the owner of the tasks executed by the thread pool
     * @return A new thread pool configured with the default settings.
     **/
    public static ThreadPoolExecutor createDefaultExecutorService(String poolName, int corePoolSize, long keepAliveTime, String lockOwner) {
		logger.info("Creating executor service '" + poolName 
				+ "' with corePoolSize '" + corePoolSize 
				+ "', maxPoolSize '" + corePoolSize 
				+ "', keepAliveTime '" + keepAliveTime 
				+ "' and lockOwner '" + lockOwner);
        ThreadFactory threadFactory = ExecutorServiceUtils.createDefaultThreadFactory(poolName);
        return new DefaultThreadPoolExecutor(corePoolSize, corePoolSize, keepAliveTime, 
        		TimeUnit.MILLISECONDS, getWorkQueue(), threadFactory, lockOwner);
    }
    
    /**
     * Returns a new thread pool configured with the default settings.
     * @param poolName			- name of the thread pool
	 * @param corePoolSize		- the number of threads to keep in the pool, even if they are idle
	 * @param maximumPoolSize	- the maximum number of threads to create
	 * @param keepAliveTime		- when the number of threads is greater than the core, this is the maximum time that excess idle 
	 * 							  threads will wait for new tasks before terminating
	 * @param lockOwner			- the id of the owner of the tasks executed by the thread pool
     * @return A new thread pool configured with the default settings.
     **/
    public static ThreadPoolExecutor createDefaultExecutorService(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, String lockOwner) {
		logger.info("Creating executor service '" + poolName 
				+ "' with corePoolSize '" + corePoolSize 
				+ "', maxPoolSize '" + maximumPoolSize 
				+ "', keepAliveTime '" + keepAliveTime 
				+ "' and lockOwner '" + lockOwner);
        ThreadFactory threadFactory = ExecutorServiceUtils.createDefaultThreadFactory(poolName);
        return new DefaultThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, 
        		TimeUnit.MILLISECONDS, getWorkQueue(), threadFactory, lockOwner);
    }
}