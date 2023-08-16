package parallel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test4 {

    public static void main(String[] args) {
        List<Integer> array = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        array.parallelStream().forEach(i ->
                {
                    RunnableTest runnableTest = new RunnableTest(i);
                    runnableTest.run();
                }
        );
        System.out.println("所有线程执行完毕");
    }
}
