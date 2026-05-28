import org.mindrot.jbcrypt.BCrypt;
public class TestHash {
    public static void main(String[] args) {
        System.out.println(BCrypt.checkpw("admin123", "$2a$12$hBSqUTqZvkJ3v3JYO.jLVOqp/gT2R1vR0.x9kpZ7M8N4uY1wQ6CJa"));
        System.out.println(BCrypt.checkpw("user123", "$2a$12$XtFVRh5y0wNrOpJeREfbLuT.rq3bV9KJ6e7Y4jNk1sP2ZmB8eGuOi"));
    }
}
