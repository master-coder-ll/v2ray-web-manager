import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhl.admin.VO.UserVO;
import com.jhl.admin.model.User;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

public class TestByMain {
    public static void main(String[] args) throws JsonProcessingException, ClassNotFoundException {
        User user = new User();
        user.setId(2);
        user.setRemark("2121");
        UserVO userVO = user.toVO(UserVO.class);
        User toModel = userVO.toModel(User.class);
        System.out.println(toModel.getRemark());
    }

    @Test
    public void testTimeZone() {

        System.out.println(TimeZone.getDefault());

        System.out.println(+System.currentTimeMillis());

        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long time = instance.getTime().getTime();
        System.out.println("UTC:" + time);
    }

}
