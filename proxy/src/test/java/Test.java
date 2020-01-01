import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    public static void main(String[] args) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes("你好啊".getBytes());
        System.out.println(buffer.toString(Charset.defaultCharset()));
    }
    public static void main2(String[] args) {
        int len ="/ws2/5000/".length();
        String string="sad/ws2/5000/dsadasdsadasd sada";
        int i = string.indexOf("/ws2/5000/");

        System.out.println(string.replaceAll("/ws2/5000/","/ws2/5000/".substring(0,len-5)));
        }
}
