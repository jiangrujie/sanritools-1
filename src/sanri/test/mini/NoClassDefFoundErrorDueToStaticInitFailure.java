package sanri.test.mini;

import java.util.ArrayList;
import java.util.List;

/**
 * Java program to demonstrate how failure of static initialization subsequently cause
 * java.lang.NoClassDefFoundError in Java.
 * @author Javin Paul
 */
public class NoClassDefFoundErrorDueToStaticInitFailure {

    public static void main(String args[]){

//        List<User> users = new ArrayList<User>(2);
//
//        for(int i=0; i<2; i++){
//            try{
//            	users.add(new User(String.valueOf(i))); //will throw NoClassDefFoundError
//            }catch(Throwable t){
//                t.printStackTrace();
//            }
//        }         
    	new User("1");
    }
}

class User{
    private static String USER_ID = getUserId();

    public User(String id){
        User.USER_ID = id;
    }
    private static String getUserId() {
        throw new RuntimeException("UserId Not found");
    }     
}