import java.net.*;

public class OuvinteAvisos implements Runnable {
    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(7000)) {
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            byte[] buf = new byte[1024];
            System.out.println("[Ouvindo avisos multicast...]");
            while (true) {
                DatagramPacket pacote = new DatagramPacket(buf, buf.length);
                socket.receive(pacote);
                String msg = new String(pacote.getData(), 0, pacote.getLength());
                System.out.println("[AVISO ADMIN] " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
