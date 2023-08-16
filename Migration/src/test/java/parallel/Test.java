package parallel;

import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        int[] array = IntStream.rangeClosed(1, 10).toArray();
        for (int i : array) {
            RunnableTest runnableTest = new RunnableTest(i);
            runnableTest.run();
        }
    }


}
