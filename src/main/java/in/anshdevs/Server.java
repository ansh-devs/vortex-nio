package in.anshdevs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;


public class Server {
    HashSet<SocketChannel> clients=new HashSet<>();
    public void startServer(int port) {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()){
            var selector = Selector.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind(new InetSocketAddress(port));
            // selector will fire whenever there is incoming conn. in serverSocket
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                if(selector.select() == 0){
                    continue;
                }
                for(var key : selector.selectedKeys()){
                    if (key.isAcceptable()){
                        if (key.channel() instanceof ServerSocketChannel ch){
                            var client = ch.accept();
                            Socket socket = client.socket();
                            System.out.printf("[INFO]: connected %s::%d \n",
                                    socket.getInetAddress().getHostAddress(),
                                    socket.getPort()
                            );
                            client.configureBlocking(false);
                            client.register(selector,SelectionKey.OP_READ);
                            clients.add(client);
                        }
                    }
                }
                selector.selectedKeys().clear();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            for (SocketChannel client : clients) {
                try{
                    client.close();
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
