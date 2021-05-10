import java.io.*;
import java.net.*;

/**
 * Created on 2021/5/10.
 *
 * @author wbk
 * @email 3207264942@qq.com
 */
public class Client {

    public static void main(String[] args) {
        try(Socket socket = new Socket()) {
            try {
                socket.setSoTimeout(3000);
            } catch (SocketException e) {
                System.out.println("基础协议出错, 程序终止:" + e.getMessage());
                return;
            }
            try {
                socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 2000), 3000);
            } catch (IOException e) {
                System.out.println("客户端连接服务器失败:" + e.getMessage());
                return;
            }
            System.out.println("客户端已连接服务器~");
            System.out.println("客户端信息:" + socket.getLocalAddress() + ":" + socket.getLocalPort());
            System.out.println("服务器信息:" + socket.getInetAddress() + ":" + socket.getPort());

            communicate(socket);
        } catch (IOException e) {
            System.out.println("自动关闭Socket出错:" + e.getMessage());
        }
        System.out.println("退出连接...");
    }

    private static void communicate(Socket socket)  {

        try ( // 获取键盘输入流
              BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
              // 客户端输出流
              PrintStream clientPrintStream = new PrintStream(socket.getOutputStream());
              BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (true) {
                // 客户端向服务器发送一行从键盘输入的数据
                String str = keyboardReader.readLine();
                clientPrintStream.println(str);
                // 客户端读取一条从服务器获取的数据
                String s = clientReader.readLine();
                if ("bye".equals(s)) {
                    break;
                }
                System.out.println("客户端:" + socket.getLocalAddress() + ":" + socket.getLocalPort() +
                        "从服务器接收到消息:" + s);
            }
        } catch (IOException e) {
            System.out.println("通信出错:" + e.getMessage());
        }
    }
}
