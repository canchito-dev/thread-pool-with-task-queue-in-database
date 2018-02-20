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
package com.canchitodev.example.threadpool.service;

public class TaskQueue {
	
	private String runnableName = "notInitRunnableName";
	private String poolName = "notInitPoolName";
	private int corePoolSize = 1;
	private int maximumPoolSize = 1;
	private long keepAliveTimeInMillis = 1000;
	private long acquireWaitTimeInMillis = 1000;
	private long maxTasksPerAcquisition = 1000;

	public TaskQueue() {}

	public TaskQueue(String runnableName, String poolName, int corePoolSize, int maximumPoolSize, 
			long keepAliveTimeInMillis, long acquireWaitTimeInMillis, long maxTasksPerAcquisition) {
		this.runnableName = runnableName;
		this.poolName = poolName;
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTimeInMillis = keepAliveTimeInMillis;
		this.acquireWaitTimeInMillis = acquireWaitTimeInMillis;
		this.maxTasksPerAcquisition = maxTasksPerAcquisition;
	}

	public String getRunnableName() {
		return runnableName;
	}

	public void setRunnableName(String runnableName) {
		this.runnableName = runnableName;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public long getKeepAliveTimeInMillis() {
		return keepAliveTimeInMillis;
	}

	public void setKeepAliveTimeInMillis(long keepAliveTimeInMillis) {
		this.keepAliveTimeInMillis = keepAliveTimeInMillis;
	}

	public long getAcquireWaitTimeInMillis() {
		return acquireWaitTimeInMillis;
	}

	public void setAcquireWaitTimeInMillis(long acquireWaitTimeInMillis) {
		this.acquireWaitTimeInMillis = acquireWaitTimeInMillis;
	}

	public long getMaxTasksPerAcquisition() {
		return maxTasksPerAcquisition;
	}

	public void setMaxTasksPerAcquisition(long maxTasksPerAcquisition) {
		this.maxTasksPerAcquisition = maxTasksPerAcquisition;
	}

	@Override
	public String toString() {
		return "TaskQueue [runnableName=" + runnableName + ", poolName=" + poolName + ", corePoolSize=" + corePoolSize
				+ ", maximumPoolSize=" + maximumPoolSize + ", keepAliveTimeInMillis=" + keepAliveTimeInMillis
				+ ", acquireWaitTimeInMillis=" + acquireWaitTimeInMillis + ", maxTasksPerAcquisition="
				+ maxTasksPerAcquisition + "]";
	}
}