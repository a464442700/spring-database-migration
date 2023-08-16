package parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test1 {
    public static void main(String[] args) {
        //初始化测试数据
        List<Integer> array = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        List<Thread> threads = new ArrayList<>();

        array.stream().forEach(i -> {
            Thread thread = new Thread(new RunnableTest(i));//步骤1
            threads.add(thread);
            thread.start();//步骤2
        });
        for (Thread thread : threads) {
            try {
                thread.join();//步骤3，保证所有线程执行完毕
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("所有线程执行完毕");
    }


}
