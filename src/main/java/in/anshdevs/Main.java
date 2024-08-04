package in.anshdevs;

public class Main {
    public static void main(String[] args) {
        Server nioServer = new Server();
        nioServer.startServer(8000);
    }
}