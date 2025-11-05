import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteEleitor {
    public static void main(String[] args) throws Exception {
        new Thread(new OuvinteAvisos()).start();

        Socket socket = new Socket("localhost", 6000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        // login
        out.println("{\"type\":\"login\",\"user\":\"eleitor\"}");
        String resposta = in.readLine();
        System.out.println("Candidatos disponíveis: " + resposta);

        System.out.print("Digite o ID do candidato (1=Clara, 2=Matheus, 3=Rafael): ");
        int id = Integer.parseInt(sc.nextLine());
        out.println("{\"type\":\"vote\",\"candidateId\":" + id + ",\"user\":\"eleitor\"}");

        String respostaVoto = in.readLine();
        System.out.println("Resposta do servidor: " + respostaVoto);

        socket.close();
        sc.close();
    }
}
