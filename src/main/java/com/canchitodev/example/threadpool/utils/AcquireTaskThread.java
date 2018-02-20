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

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.canchitodev.example.threadpool.runnable.GenericTaskRunnable;
import com.canchitodev.example.domain.GenericTaskEntity;
import com.canchitodev.example.service.GenericTaskService;
import com.canchitodev.example.threadpool.service.TaskQueue;
import com.canchitodev.example.utils.ApplicationContextProvider;

/**
 * AcquireTaskThread is in charge of acquiring the pending tasks and assign it them to its respective thread-pool
 * for its execution
 **/
public class AcquireTaskThread extends Thread {
	
	private static final Logger logger = Logger.getLogger(AcquireTaskThread.class);
	
	private String serverTenantId;
	private String lockOwner;
	private TaskQueue taskQueue;
	private ThreadPoolExecutor threadPoolExecutor;
	private boolean shutDown;
	
	private GenericTaskService genericTaskService;

	public AcquireTaskThread(String serverTenantId, String lockOwner, TaskQueue taskQueue) {
		this.serverTenantId = serverTenantId;
		this.shutDown = false;
		this.lockOwner = lockOwner;
		this.taskQueue = taskQueue;
		this.threadPoolExecutor = ExecutorServiceUtils.createDefaultExecutorService(
				this.taskQueue.getPoolName(), 
				this.taskQueue.getCorePoolSize(),
				this.taskQueue.getMaximumPoolSize(), 
				this.taskQueue.getKeepAliveTimeInMillis(),
				this.lockOwner
		);
		this.genericTaskService = ApplicationContextProvider.getApplicationContext().getBean(GenericTaskService.class);
	}
	
	public void run() {
		logger.info("Executing thread pool executor '" + this.taskQueue.getPoolName() + "'");
		
		while(!this.shutDown) {
			Page<GenericTaskEntity> tasks = null; 
			
			int maxTaskRequests = (int) (this.taskQueue.getMaximumPoolSize() - this.threadPoolExecutor.getActiveCount());
			maxTaskRequests = (int) (maxTaskRequests > this.taskQueue.getMaxTasksPerAcquisition() 
					? this.taskQueue.getMaxTasksPerAcquisition() 
					: maxTaskRequests);
			
			logger.info("Thread pool executor '" + this.taskQueue.getPoolName() + "' summary: \n" 
					+ "Server's tenant Id: " + this.serverTenantId + "\n " 
					+ "Maximum number or allow tasks to acquire for this cycle: " + maxTaskRequests + "\n " 
					+ "Number of threads that are actively executing tasks: " + this.threadPoolExecutor.getActiveCount() + "\n " 
					+ "Total number of tasks that have completed execution: " + this.threadPoolExecutor.getCompletedTaskCount() + "\n "
					+ "Total number of tasks that have ever been scheduled for execution: " + this.threadPoolExecutor.getTaskCount() + "\n ");
			try {
				tasks = this.genericTaskService.findByBeanIdAndTenantIdAndLockOwnerIsNullOrderByPriorityDesc(
						this.taskQueue.getRunnableName(), 
						this.serverTenantId, 
						new PageRequest(0, maxTaskRequests)
				);
			} catch (ObjectOptimisticLockingFailureException | StaleStateException e) {
				logger.error("Thread pool executor '" + this.taskQueue.getPoolName() 
						+ "' tried to acquire new tasks but there was an error");
			}
			
			if(tasks != null) {
				for (GenericTaskEntity task : tasks) {
					task.setLockOwner(this.lockOwner);
					try {
						this.genericTaskService.save(task);
						logger.info("Thread pool executor '" + this.taskQueue.getPoolName() + "' is the owner of task '" + task.toString() + "' - Owner: " + this.lockOwner);
						
						GenericTaskRunnable runnable = ApplicationContextProvider.getApplicationContext().getBean(GenericTaskRunnable.class);
						runnable.setTask(task);
						logger.info("Submitting tasks '" + task.toString() + "' to thread pool executor '" + this.taskQueue.getPoolName() + "' for its execution");
						try {
							this.threadPoolExecutor.execute(runnable);
						} catch (RejectedExecutionException e) {
							if(logger.isDebugEnabled()) {
								logger.warn("Task '" + task.toString() + "'. \n"  
										+ "	Exception class name: " + e.getClass().getSimpleName() + ". \n"
										+ "	Exception cause: " + e.getCause() + ". \n" 
										+ "	Exception message: " + e.getMessage() + ". \n");
							}
						}
					} catch (ObjectOptimisticLockingFailureException | StaleStateException e) {
						if(logger.isDebugEnabled()) {
							logger.warn("Thread pool executor '" + this.taskQueue.getPoolName() 
									+ "' tried to become the owner '" + this.lockOwner + "' of task '"
									+ task.toString() + "' but there was an error");
							
							logger.warn("Optimistic locking exception during tasks acquisition. If you have multiple service executors running against the same database, "
									+ "this exception means that this thread tried to acquire a task, which already was acquired by another service executor acquisition thread."
									+ "This is expected behavior in a clustered environment. "
									+ "You can ignore this message if you indeed have multiple service executor acquisition threads running against the same database. \n"  
									+ "	Exception class name: " + e.getClass().getSimpleName() + ". \n"
									+ "	Exception cause: " + e.getCause() + ". \n" 
									+ "	Exception message: " + e.getMessage() + ". \n");
						}
					}
				}
			}
			
			try {
				logger.info("Thread pool executor '" + this.taskQueue.getPoolName() + "' is waiting " + this.taskQueue.getAcquireWaitTimeInMillis() + " millis before acquiring new tasks");
				Thread.sleep(this.taskQueue.getAcquireWaitTimeInMillis());
			} catch (InterruptedException e) {
				logger.warn("Acquire-" + this.taskQueue.getPoolName() + " thread was interrupted or unexpectedly shutdown."
						+ "Thread pool executor '" + this.taskQueue.getPoolName() + "' will be shutdown shortly.");
				this.shutDown = true;
			}
		}
		
		if(!this.shutDown) {
			logger.info("Shutting down thread pool executor '" + this.taskQueue.getPoolName() + "'");
			this.threadPoolExecutor.shutdown();
		}
	}

	public String getLockOwner() {
		return lockOwner;
	}

	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	public void setTaskQueue(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public Boolean getShutDown() {
		return shutDown;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	@Override
	public String toString() {
		return "AcquireTaskThread [serverTenantId=" + serverTenantId + ", lockOwner=" + lockOwner + ", taskQueue="
				+ taskQueue + ", threadPoolExecutor=" + threadPoolExecutor + ", shutDown=" + shutDown
				+ ", genericTaskService=" + genericTaskService + "]";
	}
}