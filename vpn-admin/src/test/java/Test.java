import com.jhl.admin.AdminApplication;
import com.jhl.admin.cron.AppCron;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AdminApplication.class})// 指定启动类
public class Test {
@Autowired
AppCron appCron;
    @org.junit.Test
    public void test() throws ClassNotFoundException {

    }




}
