# Quick Start
## 1.SpringBoot接入
* 启动类上需要添加@AutoRisiroTask注解表示开启框架，同时扫描指定路径的包
```java
@AutoRisiroTask
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```
* 编写配置文件
```yml
redis:
  url: redis://my-redis.orb.local:6379
tasks:
  configs:
    - consumerBeanName: org.example.SumTaskHandler#0
      taskName: org.example.SumTaskHandler#0
      dataType: org.example.SumTask
      runningTimeout: 10000
      retryTimes: 3
      workerNum: 2
      type: REDIS
    - consumerBeanName: org.example.Sum2TaskHandler#0
      taskName: org.example.Sum2TaskHandler#0
      dataType: org.example.SumTask2
      runningTimeout: 15000
      retryTimes: 3
      workerNum: 1
      type: LOCAL
```
* 编写任务生产者
```java
RedisTaskSubmitter redisTaskSubmitter = new RedisTaskSubmitter();
SumTask sumTask = new SumTask(5, 6);
for (int i = 0; i < 10; i++) {
    redisTaskSubmitter
            .newTask(sumTask, "org.example.SumTaskHandler#0")
            .context("key1", "val1")
            .asyncSubmit()
            .subscribe();
}
```
* 编写任务消费者
```java
@Component("sumTask")
public class SumTaskHandler implements TaskRunnableHandler<SumTask> {

    @Override
    public void handler(SumTask taskRequest, WorkerContext workerContext) {
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Object val = workerContext.getContext().get("key1");
        System.out.println("sumTask收到回调：" + taskRequest.getA() + ":" + taskRequest.getB() + ":" + val.toString() + ":" + workerContext.getTaskId());
    }
}
```
运行结果：
```shell
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-0-1:PENDING
worker-redis-org.example.SumTaskHandler#0-0-1:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-1-2:PENDING
worker-redis-org.example.SumTaskHandler#0-1-2:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-0-1:PENDING
worker-redis-org.example.SumTaskHandler#0-0-1:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-1-2:PENDING
worker-redis-org.example.SumTaskHandler#0-1-2:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-0-1:PENDING
worker-redis-org.example.SumTaskHandler#0-0-1:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-1-2:PENDING
worker-redis-org.example.SumTaskHandler#0-1-2:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-0-1:PENDING
worker-redis-org.example.SumTaskHandler#0-0-1:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-1-2:PENDING
worker-redis-org.example.SumTaskHandler#0-1-2:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-0-1:PENDING
worker-redis-org.example.SumTaskHandler#0-0-1:COMPLATE
sumTask收到回调：5:6:val1:null
worker-redis-org.example.SumTaskHandler#0-1-2:PENDING
worker-redis-org.example.SumTaskHandler#0-1-2:COMPLATE
```