package parallel;

import java.util.List;

public class Test5 {
    public static void main(String[] args) {
        List<Integer> nodes = List.of(1, 2, 3, 4, 5);
        nodes.parallelStream().forEach(
             node ->{
                 try {
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 System.out.println("第一组"+node);}

        );
        nodes.parallelStream().forEach(
                node ->{System.out.println("第二组"+node);}

        );
    }
}
