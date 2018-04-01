# thread-pool-with-task-queue-in-database
Simulates multiple threadpools of workers that share a same database table with the pending to be executed tasks resided. This is the solution implemented in the project [**CANCHITO-WORKFLOW-MANAGER (CWM)**](http://canchito-dev.com/projects/cwm) for solving some _"limitations"_ encountered during the integration and implementation of [Flowable](https://www.flowable.org/) BPM, when executing long-running tasks. 

## Download
Help us find bugs, add new features or simply just feel free to use it. Download **thread-pool-with-task-queue-in-database** from our [ GitHub](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database) site.

## License
The MIT License (MIT)  

Copyright (c) 2018, canchito-dev  

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:  

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.  

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Contribute Code
If you would like to become an active contributor to this project please follow theses simple steps:

1.  Fork it
2.  Create your feature branch
3.  Commit your changes
4.  Push to the branch
5.  Create new Pull Request

This solution is already implemented in [**CANCHITO-WORKFLOW-MANAGER (CWM)**](http://canchito-dev.com/projects/cwm) which can be downloaded from [github](https://github.com/canchito-dev/canchito-workflow-manager). Remember that [**CANCHITO-WORKFLOW-MANAGER (CWM)**](http://canchito-dev.com/projects/cwm) is a work in progress project. If you only wish to implement the asynchronous service by itself, it can also be downloaded from [github](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database).

## Async Job Executor
Put in simple words, [**thread-pool-with-task-queue-in-database**](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database)'s async job executor are individual threads that are started once when the application is started. Each thread starts a thread pool that reuses a (configurable) fixed number of threads operating off database table called _CWM\_TASKS\_QUEUE_ and acting as a priority blocking list, using the provided ThreadFactory to create new threads when needed. At any point, at most _n_ threads will be active processing tasks.

![CANCHITO-DEV: Task Queue Service](http://canchito-dev.com/img/cwm/userguide/canchito_dev_task_queue_service.png)

Periodically, pending tasks are pulled from the database. The number of pending tasks that are pulled at once, depends on the number of available threads for a specific task type. If additional tasks are submitted when all threads are active, they will reside in the database until a thread is available.

![CANCHITO-DEV: CWM's Async Executor Design](http://canchito-dev.com/img/thread-pool-with-task-queue-in-database/canchito_dev_async_executor_design.png)

If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The threads in the pool will exist until it is explicitly shutdown.

Once a task is pulled by a thread, it is locked. By locking it, we can have several async job executors simultaneously running on different servers. Thus allowing us to have a fail-over, clustered system.

If two or more async job executor are started, they all will be raising for pulling the pending tasks. Consequently, only the first one that looks the task, will be the one that will execute it. The other async job executor will get an optimistic locking exception.

### Async Executor's Configuration
The async job executor configuration is done by modifying two XML files (found under `src/main/resources/`):

*   `task-queue-beans.xml`: specified how each **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager)'s async job executor, dedicated to process a specific task is configured
*   `task-runnable-beans.xml`: here you will find the _Runnable_ classes that are used by each [CWM](https://github.com/canchito-dev/canchito-workflow-manager)'s async job executor to execute the task

As you can see, each queue used by the **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager)'s async job executor needs to have some configuration. Let's describe those parameters found in `task-queue-beans.xml` file.

*   `runnableName`: the id of the runnable that and instance is initialized and afterward executed. This is the id which relates to the information found in `task-runnable-beans.xml`.
*   `poolName`: the name of the thread pool
*   `corePoolSize`: the number of threads to keep in the pool, even if they are idle
*   `maximumPoolSize`: the maximum number of threads to allow in the pool
*   `keepAliveTimeInMillis`: when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating
*   `acquireWaitTimeInMillis`: millis to wait before new tasks are pulled from the database
*   `maxTasksPerAcquisition`: maximum tasks that can be pulled from the database

For instance `task-queue-beans.xml`:

```xml
<bean id="task1Queue" class="com.canchitodev.cwm.threadpool.service.TaskQueue" scope="prototype">
  <property name="runnableName" value="task1Runnable" />
  <property name="poolName" value="task1Queue" />
  <property name="corePoolSize" value="2" />
  <property name="maximumPoolSize" value="5" />
  <property name="keepAliveTimeInMillis" value="300000"/>
  <property name="acquireWaitTimeInMillis" value="5000"/>
  <property name="maxTasksPerAcquisition" value="2"/>
</bean>
```

For instance `task-runnable-beans.xml`:

```xml
<bean id="task1Runnable" class="com.canchitodev.cwm.tasks.runnable.Task1Runnable" scope="prototype"></bean>
```

### Creating a long-running Service Task
A long-running task is composed of a _Runnable_ class, which implements the _TaskRunnable_ class, so that it can override the `execute()` method. It is in this last method, where the business logic behind the long-running tasks is found.

```java
public class Task1Runnable implements TaskRunnable {
  
  private static final Logger logger = Logger.getLogger(Task1Runnable.class);
  
  private GenericTaskEntity task;

  public Task1Runnable() {}
  
  public Task1Runnable(GenericTaskEntity task) {
    this.task = task;
  }

  public GenericTaskEntity getTask() {
    return task;
  }

  public void setTask(GenericTaskEntity task) {
    this.task = task;
  }

  @Override
  public void execute() {
    try {
      logger.info("Executing task " + task.toString());
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      logger.info("Done executing task " + task.toString());
    }
  }
}
```

That's it. Now just add the task queue in the `task-queue-beans.xml` and the _Runnable_ class to the respective `task-runnable-beans.xml` file so that _TaskQueueService_ can start its respective _AcquireTaskThread_.

## Let's test it!!
In this [github](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database) project, we have included a jUnit class for testing the async job executor. Just remember to comment the `@Ignore` annotation. If you rather see it in action together with [Flowable](https://www.flowable.org/), you can have a look at **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager).