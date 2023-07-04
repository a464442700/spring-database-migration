package test1;

public class Test {
    public static void main(String[] args) {
        SetUserImpl s=new SetUserImpl();
        User user=new User("1");
        s.setUser(user);
        s.setUserName("2");
        System.out.println(user.getName());
    }
}
