import com.alibaba.fastjson.JSON;
import com.jhl.admin.AdminApplication;
import com.jhl.admin.VO.UserVO;
import com.jhl.admin.model.Message;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.MessageRepository;
import com.jhl.admin.repository.UserRepository;
import com.jhl.admin.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AdminApplication.class})// 指定启动类
public class UserMessageTest {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    User user =null;

    @Before
    public  void testAddUser(){


        UserVO userVO = userService.getOne(User.builder().email(("test@test.com")).build());
        if (userVO != null){
             user = userVO.toModel(User.class);
            return;
        }
        user = User.builder().email("test@test.com").password("1").role("vip").build();
        userService.create(user);
    }


    @Test
    public  void testAddMessage(){
        Message message = new Message();
        message.setMessageContent("test1");
        message.setUser(user);
        messageRepository.save(message);
        Message message2 = new Message();
        message2.setMessageContent("test22");
        message2.setUser(user);
        messageRepository.save(message2);

    }

    @Test
    public  void testGetMessage(){
        Message message = new Message();
        message.setMessageContent("test1");
        message.setUser(user);
        messageRepository.save(message);
        Integer id = message.getId();
        Message message1 = messageRepository.findById(id).orElse(null);
        System.out.println( JSON.toJSONString(message1));

    }

    @Test
    public void testDeleteMessage()  {
    Message message = new Message();
        message.setMessageContent("test1");
        message.setUser(user);
        messageRepository.save(message);

        messageRepository.deleteById(message.getId());
    }

    @Test
    public  void testDeleteUser(){
        userRepository.deleteById(user.getId());
    }
}
