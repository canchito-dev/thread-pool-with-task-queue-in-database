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

import com.canchitodev.example.domain.GenericTaskEntity;
import com.canchitodev.example.service.GenericTaskService;
import com.canchitodev.example.threadpool.runnable.GenericTaskRunnable;
import com.canchitodev.example.utils.ApplicationContextProvider;
import com.canchitodev.example.utils.enums.BehaviorTaskStatus;

public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {
	
	private static final Logger logger = Logger.getLogger(DefaultThreadPoolExecutor.class);
	
	private GenericTaskService genericTaskService;
	private Object lock;
	private String lockOwner;

	/**
	 * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue, using the provided 
	 * ThreadFactory to create new threads when needed. At any point, at most nThreads threads will be active processing tasks. 
	 * If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is available. 
	 * If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to 
	 * execute subsequent tasks. The threads in the pool will exist until it is explicitly shutdown.
	 * @param corePoolSize		- the number of threads to keep in the pool, even if they are idle
	 * @param maximumPoolSize	- the maximum number of threads to allow in the pool
	 * @param keepAliveTime		- when the number of threads is greater than the core, this is the maximum time that excess idle 
	 * 							  threads will wait for new tasks before terminating
	 * @param unit				- the time unit for the keepAliveTime argument
	 * @param workQueue			- the queue to use for holding tasks before they are executed. This queue will hold only the Runnable 
	 * 							  tasks submitted by the execute method
	 * @param lockOwner			- the thread owner of the task
	 * @param threadFactory		- the factory to use when the executor creates a new thread
	 * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue} is null
	 **/
	public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			PriorityBlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String lockOwner) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.genericTaskService = ApplicationContextProvider.getApplicationContext().getBean(GenericTaskService.class);
		this.lock = new Object();
		this.lockOwner = lockOwner;
	}

	/** 
	 * Method invoked prior to executing the given Runnable in the given thread. This method is invoked by thread t that will execute 
	 * task r, and may be used to re-initialize ThreadLocals, or to perform logging. This implementation does nothing, but may be 
	 * customized in subclasses. Note: To properly nest multiple overridings, subclasses should generally invoke super.beforeExecute 
	 * at the end of this method.
	 * @param thread 	- the thread that will run task r
	 * @param runnable 	- the task that will be executed
	 * @see java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread, java.lang.Runnable)
	 **/
	public void beforeExecute(Thread thread, Runnable runnable) {
		super.beforeExecute(thread, runnable);
		synchronized (lock) {
			GenericTaskRunnable genericTaskRunnable = (GenericTaskRunnable) runnable;
			GenericTaskEntity genericTaskEntity = genericTaskRunnable.getTask();
			genericTaskEntity.setStatus(BehaviorTaskStatus.EXECUTING.getStatus());
			this.genericTaskService.setTaskStatus(genericTaskEntity.getStatus(), genericTaskEntity.getUuid(), this.lockOwner);
			logger.info("Executing " + runnable.toString() + " using thread " + thread.getName());
		}
	}
	
	/**
	 * Method invoked upon completion of execution of the given Runnable. This method is invoked by the thread that executed the task. 
	 * If non-null, the Throwable is the uncaught RuntimeException or Error that caused execution to terminate abruptly. This implementation 
	 * does nothing, but may be customized in subclasses.
	 * 
	 * If there is an error during the execution of the thread's runnable, the throwable is saved in an Activiti's variable called 'runnableError'
	 * 
	 * <strong>Note:</strong> To properly nest multiple overridings, subclasses should generally invoke super.afterExecute at the beginning 
	 * of this method.
	 * @param runnable 	- the runnable that has completed
	 * @param throwable - the exception that caused termination, or null if execution completed normally
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
	 **/
	public void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		synchronized (lock) {
			GenericTaskRunnable genericTaskRunnable = (GenericTaskRunnable) runnable;
			GenericTaskEntity genericTaskEntity = genericTaskRunnable.getTask();
			if (throwable != null) {
				logger.error("Error Executing " + throwable);
				genericTaskEntity.setStatus(BehaviorTaskStatus.ERROR.getStatus());
			} else {
				logger.info("Done Executing " + runnable.toString());
				genericTaskEntity.setStatus(BehaviorTaskStatus.DONE.getStatus());
			}
			this.genericTaskService.setTaskStatus(genericTaskEntity.getStatus(), genericTaskEntity.getUuid(), this.lockOwner);
			logger.info("Finished task '" + genericTaskEntity + "'");
		}
	}
}