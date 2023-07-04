package test1;

public class SetUserImpl {
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserName(String name) {
      //  this.user.setName(name);
        this.user=new User(name);
    }
}
