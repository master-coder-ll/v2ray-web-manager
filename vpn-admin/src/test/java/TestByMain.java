import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhl.admin.VO.UserVO;
import com.jhl.admin.model.User;
import com.test.test.testModel;

public class TestByMain {
    public static void main(String[] args) throws JsonProcessingException, ClassNotFoundException {
        User user= new User();
        user.setId(2);
        user.setRemark("2121");
        UserVO userVO = user.toVO(UserVO.class);
        User toModel = userVO.toModel(User.class);
        System.out.println(toModel.getRemark());
    }
}
