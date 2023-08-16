package parallel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test2 {

    public static void main(String[] args) {
        //步骤1，初始化线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<Integer> array = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        array.stream().forEach(i -> {
            //步骤2，执行任务
            executorService.submit(new RunnableTest(i));
        });
        //步骤4，关闭线程池
        executorService.shutdown();

        //步骤5，保证任务执行完毕再进行后续操作

        while (!executorService.isTerminated()) {//循环是要判断所有的线程是否关闭
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        System.out.println("所有线程执行完毕，线程池已关闭");


    }
}
