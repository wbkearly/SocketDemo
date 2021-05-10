### Socket Demo

#### 基础知识

Socket - 客户端套接字

    getInetAddress
        返回套接字连接到的地址

    getPort
        返回此套接字连接到的远程端口号

    getLocalAddress
        获取套接字绑定到的本地地址

    getLocalPort
        返回此套接字绑定到的本地端口号


ServerSocket - 服务器套接字。 

服务器套接字等待网络请求。 
它根据该请求执行一些操作，然后可能将结果返回给请求者。

    getInetAddress
        返回此服务器套接字的本地地址

    getLocalPort
        返回此套接字正在侦听的端口号

#### Demo 客户端

```java
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
```

#### Demo 服务器端

```java
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
```