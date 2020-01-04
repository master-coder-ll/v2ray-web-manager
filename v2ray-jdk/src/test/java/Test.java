import com.com.core.common.protocol.SecurityConfig;
import com.com.core.common.protocol.SecurityType;
import com.com.core.proxy.vmess.Account;
import com.jhl.v2ray.V2RayApiClient;
import com.v2ray.core.app.proxyman.command.AddUserOperation;
import com.v2ray.core.app.proxyman.command.AlterInboundRequest;
import com.v2ray.core.app.proxyman.command.RemoveInboundRequest;
import com.v2ray.core.app.proxyman.command.RemoveUserOperation;
import com.v2ray.core.common.protocol.User;
import com.v2ray.core.common.serial.TypedMessage;

public class Test {
    static String API_ADDRESS = "127.0.0.1";
    static int API_PORT = 10085;
    static String INBOUND_TAG = "proxy";
    static int LEVEL = 0;
    static String EMAIL = "123@gmail.com";
    static String UUID= "2601070b-ab53-4352-a290-1d44414581ee";
    static int ALTERID = 32;
   static V2RayApiClient client =  V2RayApiClient.getInstance(API_ADDRESS,API_PORT);

    public static void main(String[] args) throws Exception {
        System.out.println(client);
    }


}
