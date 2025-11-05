import java.net.*;

public class AdminAvisos {
    public static void main(String[] args) throws Exception {
        String mensagem = "{\"type\":\"notice\",\"message\":\"Atenção: a votação termina em 30 segundos!\"}";
        byte[] buf = mensagem.getBytes();
        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        int porta = 7000;

        DatagramPacket pacote = new DatagramPacket(buf, buf.length, grupo, porta);
        try (MulticastSocket socket = new MulticastSocket()) {
            socket.send(pacote);
            System.out.println("Aviso enviado via multicast UDP.");
        }
    }
}
