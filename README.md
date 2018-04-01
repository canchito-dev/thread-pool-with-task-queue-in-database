# thread-pool-with-task-queue-in-database
In this article, I will explain one posible method to solved some _"limitations"_ encountered during the integration and implementation of [Flowable](https://www.flowable.org/) BPM, when executing long-running tasks, by implementing the Signallable Flowable Behavior and a database table as a task queue. Similar behavior can be achieved using [Flowable](https://www.flowable.org/)'s send task and receive task instead of Signallable Flowable Behavior. However, this solution is not limited to be used together with [Flowable](https://www.flowable.org/). Due to its design, it can be used in conjunction with other applications that required executing long-running tasks asynchronically.

> Simulates multiple threadpools of workers that share a same database table with the pending tasks

## Download
Help us find bugs, add new features or simply just feel free to use it. Download **Bootstrap Sign In Web Componentr** from our [ GitHub](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database) site.

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

This solution is already implemented in [**CANCHITO-WORKFLOW-MANAGER (CWM)**](http://canchito-dev.com/projects/cwm) can be downloaded from [github](https://github.com/canchito-dev/canchito-workflow-manager). Remember that this is a work in progress project. If you only wish to implement the asynchronous service by itself, it can also be downloaded from [github](https://github.com/canchito-dev/thread-pool-with-task-queue-in-database).

## Introduction
This article demonstrates how to implement an asynchronous service invocation using a Signallable Flowable Behavior and a database table for storing all the to be executed tasks.

I start by giving a brief introduction to [Flowable](https://www.flowable.org/) and its _"limitations"_ when it comes to executing long-running tasks with its normal task behavior. Afterwards, I will show you how to implement an advanced usage pattern of [Flowable](https://www.flowable.org/)'s process engine based on internal (non-public) API, and based on Signallable Flowable Behavior. And finally, I will explain the idea behind the async job service.

> Similar behavior can be achieved using [Flowable](https://www.flowable.org/)'s send task and receive task instead of Signallable Flowable Behavior.

## What is Flowable?
[Flowable](https://www.flowable.org/) is a light-weight workflow and Business Process Management (BPM) Platform targeted at business people, developers and system admins. Its core is a super-fast and rock-solid BPMN 2 process engine for Java. It's open-source and distributed under the Apache license. [Flowable](https://www.flowable.org/) runs in any Java application, on a server, on a cluster or in the cloud. It integrates perfectly with Spring, it is extremely lightweight and based on simple concepts.

## Current _"limitation"_
In order to understand the main _"limitation"_, you need to understand how [Flowable](https://www.flowable.org/)'s asyc job executor works. Please read the following section from [Flowable](https://www.flowable.org/)'s user guide:

*   [3.9. Job Executor (from version 6.0.0 onwards)](http://www.flowable.org/docs/userguide/index.html#jobExecutorConfiguration)
*   [3.10. Job executor activation](http://www.flowable.org/docs/userguide/index.html#_job_executor_activation)
*   [18.1. Async Executor](http://www.flowable.org/docs/userguide/index.html#_async_executor)

And according to [Frederik Heremans](https://www.linkedin.com/in/frederikheremans/) (one of [Flowable](https://www.flowable.org/)'s main developers), in his reply to this [question](https://community.alfresco.com/thread/220468-modelling-an-async-user-wait-on-a-long-running-service-task) rised in [Flowable](https://www.flowable.org/)'s official forum, we also know that when executing long-running tasks the async job executor, bahaves as follow:

*   Executing a service-task (or any other task) keeps a transaction open until a wait-state/process-end/async-task is reached. If you have long-running operations, make sure your database doesn't time out
*   When a jobs is running for 5 minutes, the job aquisistion-thread assumes the async job executor that was running the job, has either died or has failed. The lock of the job is removed and the job will be executed by another thread in the executor-pool. This timeout-setting can be raised, if that is required
*   Long-running tasks modeled in the flowable-process always keep a transaction open and a async job executor thread occupied. Better practice is to use a queue-signal approach where the long-running operation is executed outside of [Flowable](https://www.flowable.org/) (queued to eg. camel using a service-task, providing the neccesary variables needed alongside). When the long-running task is completed, it should signal the execution, which has a recieve-task modeled in

Due to the requirements needed on **[CANCHITO-WORKFLOW-MANAGER](https://github.com/canchito-dev/canchito-workflow-manager)** [(CWM)](http://canchito-dev.com/projects/cwm), the regular way [Flowable](https://www.flowable.org/)'s async job executer behavior was a _"limitation"_. But they are not actual limitations. They are simply the way in which the engine should behave.

Following [Frederik Heremans](https://www.linkedin.com/in/frederikheremans/) reply, with **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager) it was decided to implement the queue-signal approach together with a database table, which will contain the tasks queue.

## Async Job Executor
Put in simple words, **[CANCHITO-WORKFLOW-MANAGER](https://github.com/canchito-dev/canchito-workflow-manager)** [(CWM)](http://canchito-dev.com/projects/cwm)'s async job executor are individual threads that are started once when the application is started. Each thread starts a thread pool that reuses a (configurable) fixed number of threads operating off database table called _CWM\_TASKS\_QUEUE_ and acting as a priority blocking list, using the provided ThreadFactory to create new threads when needed. At any point, at most _n_ threads will be active processing tasks.

![CANCHITO-DEV: Task Queue Service](http://canchito-dev.com/img/cwm/userguide/canchito_dev_task_queue_service.png)

Periodically, pending tasks are pulled from the database. The number of pending tasks that are pulled at once, depends on the number of available threads on **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager)'s async job executor for a specific task type. If additional tasks are submitted when all threads are active, they will reside in the database until a thread is available.

If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The threads in the pool will exist until it is explicitly shutdown.

Once a task is pulled by a thread, it is locked. By locking it, we can have several **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager) simultaneously running on different servers. Thus allowing us to have a fail-over, clustered system.

If two or more **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager)'s async job executor are started, they all will be raising for pulling the pending tasks. Consequently, only the first one that looks the task, will be the one that will execute it. The other async job executor will get an optimistic locking exception.

### Async Executor's Design
In order to understand the way long-running tasks are added to the queue, lets have a look at a very simple workflow as the one in the below image. As you can see, it is composed of a start event, a copy task (which is a service task), and an end event.

![CANCHITO-DEV: Copy Task sample workflow](http://canchito-dev.com/img/cwm/userguide/canchito_dev_copy_task_sample_workflow.png)

The copy task is a long-running service task, which needs to be processed by the async job executor. Long-runing tasks in **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager) extend _AbstractTaskCanchitoBehavior_, which at the same time extend from [Flowable](https://www.flowable.org/)'s _TaskActivityBehavior_ class.

The _TaskActivityBehavior_ parent class for all BPMN 2.0 task types such as ServiceTask, ScriptTask, UserTask, etc. When used on its own, it behaves just as a pass-through activity. This class provides two methods: `execute()`and `trigger()`. The class _AbstractTaskCanchitoBehavior_ provides a two methods: `submitTask()` and `checkSignal(DelegateExecution execution)`. These four methods are the pillars for creating a long-running task implementing the Signallable Flowable Behavior instead of adding two BPMN task (send task and receive task) in your process diagram.

![CANCHITO-DEV: CWM's Async Executor Design](http://canchito-dev.com/img/cwm/userguide/canchito_dev_async_executor_design.png)

The `execute(DelegateExecution execution)` method is invoked when the service task is entered. In our case, it is typically used for data valiation and preparation. There is no business logic here. For instance, it validates that all the needed information for the task to be correctly executed has valid values.

The `submitTask(DelegateExecution execution, JSONObject details, String beanId)` method submits an asynchronous task to the actual service. The submit action is actually storing the task in the database table by calling the `save()` method from the _GenericTaskService_ class. You can modify this method according to your database table structure.

After submitting the task and the method returns, the process engine will **not** continue execution. The _TaskActivityBehavior_ acts as a wait state. This means, that the process instances is put on hold, until a signal to continue is received.

Periodically, the _AcquireTaskThread_ in charge of these kind of service task (in this example, the copy task), read the task que database table. When it finds a new task, it acquires it, and locks it. Once locked, it calls the `run()` method. Here, you will find the business logic for the invoked service task. For our examples, it will problably call functions and method to copy a file from one location to another. When done, _AcquireTaskThread_ will call the `trigger()` method.

The `trigger(DelegateExecution execution, String signalName, Object signalData)` method is invoked as the process engine is being triggered by the callback. The `trigger()` method is responsible for leaving the service task activity and allowing the normal flow of the process instance. But before leaving the service task, the `checkSignal()` is called. It is in this method, were the execution of the service task's logic is analyzed and determined if it finished correctly or with errors.

By having a separate thread pool for executing long-running tasks, **[CANCHITO-WORKFLOW-MANAGER](http://canchito-dev.com/projects/cwm)** [(CWM)](https://github.com/canchito-dev/canchito-workflow-manager) has decoupled the process engine from the service implementation. From the point of view of [Flowable](https://www.flowable.org/)'s process engine, the _TaskActivityBehavior_ is a wait state: after the `execute()` method returns, the process engine will stop execution, makes the state of the execution to the database persistance and wait for the callback to occure.

As the long-running task implementation is not directly executed by [Flowable](https://www.flowable.org/)'s process engine and it does not participate in the process engine transaction, if there is an error in the service implementation, the failure will not cause the process engine to roll back.

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
A long-running service task is composed of two different classes: _Behavio_r class and _Runnable_ class. The behavior class, extends from _AbstractTaskCanchitoBehavior_, which at the same time extend from [Flowable](https://www.flowable.org/)'s _TaskActivityBehavior_ class. Let's have a look at how to implement it by creating a new class:

```java
@Service("task1")
@Scope("prototype")
public class Task1Behavior extends AbstractTaskCanchitoBehavior {

  private static final long serialVersionUID = -4740654158860004620L;

  @Override
  public void execute(DelegateExecution execution) {
    try {			
      this.submitTask(execution, new JSONObject(), "task1Runnable");
    } catch (Exception e) {
      this.throwException(execution, 
          "There was a problem when trying to execute task 'task1Runnable'"
      );
    }
  }

  @Override
  protected void validateParameters(DelegateExecution execution) throws IllegalArgumentException {
    // TODO Auto-generated method stub
  }
}
```

You can add additional logic before calling the `submitTask()` method if required. For our example, this would be enough. Next, create the _Runnable_ class. Remember, this is the class with all the business logic.

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

## Summary
In this post, I will explain one posible method to solved some _"limitations"_ encountered during the integration and implementation of [Flowable](https://www.flowable.org/) BPM, when executing long-running tasks, by implementing the Signallable Flowable Behavior and a database table as a task queue. Similar behavior can be achieved using [Flowable](https://www.flowable.org/)'s send task and receive task instead of Signallable Flowable Behavior. However, this solution is not limited to be used together with [Flowable](https://www.flowable.org/). Due to its design, it can be used in conjunction with other applications that required executing long-running tasks asynchronically.

In this post, I hope you got to learn about the following:

*   A brief introduction about [Flowable](https://www.flowable.org/) BPM.
*   The _"limitations_" that [Flowable](https://www.flowable.org/) has when it comes to executing long-running tasks.
*   A possible solution to solve this _"limitations._

Hope you enjoyed this post as much as I did writing it. Please free to leave your comments and feedback.