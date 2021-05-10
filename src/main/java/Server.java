import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created on 2021/5/10.
 *
 * @author wbk
 * @email 3207264942@qq.com
 */
public class Server {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(2000)) {
            System.out.println("服务器准备就绪~");
            System.out.println("服务器信息:" + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();
                SocketHandler socketHandler = new SocketHandler(socket);
                socketHandler.start();
            }
        } catch (IOException e) {
            System.out.println("通信错误:" + e.getMessage());
        }
    }

    private static class SocketHandler extends Thread {

        private final Socket socket;

        SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接:" + socket.getInetAddress() + ":" + socket.getPort());

                try (BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintStream serverPrintStream = new PrintStream(socket.getOutputStream())) {
                    while (true) {
                        String s = serverReader.readLine();
                        if ("bye".equalsIgnoreCase(s)) {
                            break;
                        }
                        System.out.println("服务器收到客户端[" +
                                socket.getInetAddress() + ":" + socket.getPort() + "]发送来的信息:" + s);
                        System.out.println("服务器回送消息给客户端[" + socket.getInetAddress() + ":" + socket.getPort() + "]:" + s);
                        serverPrintStream.println("服务器回送:" + s);
                    }
                } catch (IOException e) {
                    System.out.println("通信出错:" + e.getMessage());
                }
        }
    }

}
