import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhl.admin.ManagerApplication;
import com.jhl.admin.cron.AppCron;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManagerApplication.class})// 指定启动类
public class Test {
@Autowired
AppCron appCron;
    @org.junit.Test
    public void test() {
        //appCron.createStatTimer();
    }



    public static void main(String[] args) throws JsonProcessingException {
        String[] split = "asdsad".split(",");
        System.out.println(Arrays.toString(split));
    }
}
