package parallel;



public class RunnableTest implements Runnable {
    public RunnableTest(Integer n) {
        this.n = n;
    }
    private Integer n;

    @Override
    public void run() {

        try {
           Integer time=100*(11-this.n);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println( n +"*2="+n*2);

    }
}
