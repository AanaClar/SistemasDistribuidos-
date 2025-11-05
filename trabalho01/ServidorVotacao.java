import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorVotacao {
    private static final int TCP_PORT = 6000;
    private static final Map<Integer, String> candidatos = new HashMap<>();
    private static final Map<Integer, Integer> votos = new ConcurrentHashMap<>();
    private static volatile boolean votacaoAberta = true;

    public static void main(String[] args) throws Exception {
        // Lista de candidatos
        candidatos.put(1, "Clara");
        candidatos.put(2, "Matheus");
        candidatos.put(3, "Rafael");

        for (Integer id : candidatos.keySet()) {
            votos.put(id, 0);
        }

        // tempo de votação (60s)
        new Thread(() -> {
            try {
                Thread.sleep(60000);
                votacaoAberta = false;
                System.out.println("\n>>> VOTAÇÃO ENCERRADA <<<");
                mostrarResultados();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        ServerSocket servidor = new ServerSocket(TCP_PORT);
        System.out.println("Servidor de votação iniciado na porta " + TCP_PORT);

        while (true) {
            Socket cliente = servidor.accept();
            new Thread(new AtendeCliente(cliente)).start();
        }
    }

    private static void mostrarResultados() {
        int total = votos.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\n=== RESULTADOS FINAIS ===");
        int vencedorId = -1, maior = -1;
        for (Integer id : candidatos.keySet()) {
            int v = votos.get(id);
            double pct = total == 0 ? 0 : (v * 100.0 / total);
            System.out.printf("%d - %s: %d votos (%.2f%%)%n", id, candidatos.get(id), v, pct);
            if (v > maior) {
                maior = v;
                vencedorId = id;
            }
        }
        if (vencedorId != -1)
            System.out.println("VENCEDOR: " + candidatos.get(vencedorId));
        else
            System.out.println("Sem votos registrados.");
    }

    // Thread que atende cada cliente TCP
    private static class AtendeCliente implements Runnable {
        private Socket socket;

        AtendeCliente(Socket s) { this.socket = s; }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String linha;
                while ((linha = in.readLine()) != null) {
                    if (linha.contains("\"type\":\"login\"")) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("{\"type\":\"candidates\",\"items\":[");
                        boolean first = true;
                        for (Integer id : candidatos.keySet()) {
                            if (!first) sb.append(",");
                            sb.append("{\"id\":").append(id)
                              .append(",\"name\":\"").append(candidatos.get(id)).append("\"}");
                            first = false;
                        }
                        sb.append("]}");
                        out.println(sb.toString());
                    }
                    else if (linha.contains("\"type\":\"vote\"")) {
                        if (!votacaoAberta) {
                            out.println("{\"type\":\"voteResult\",\"status\":\"closed\"}");
                        } else {
                            int idx = linha.indexOf("\"candidateId\"");
                            if (idx == -1) {
                                out.println("{\"type\":\"voteResult\",\"status\":\"error\",\"msg\":\"candidateId faltando\"}");
                            } else {
                                // pega só os dígitos depois de "candidateId":
                                int colon = linha.indexOf(":", idx);
                                int start = colon + 1;

                                // pular espaços
                                while (start < linha.length() && Character.isWhitespace(linha.charAt(start))) {
                                    start++;
                                }

                                int end = start;
                                // pegar só números
                                while (end < linha.length() && Character.isDigit(linha.charAt(end))) {
                                    end++;
                                }

                                String numStr = linha.substring(start, end);
                                int candidateId;

                                try {
                                    candidateId = Integer.parseInt(numStr);
                                } catch (NumberFormatException e) {
                                    out.println("{\"type\":\"voteResult\",\"status\":\"error\",\"msg\":\"candidateId inválido\"}");
                                    continue;
                                }

                                if (votos.containsKey(candidateId)) {
                                    votos.computeIfPresent(candidateId, (k, v) -> v + 1);
                                    out.println("{\"type\":\"voteResult\",\"status\":\"ok\"}");
                                    System.out.println("Voto recebido para: " + candidatos.get(candidateId));
                                } else {
                                    out.println("{\"type\":\"voteResult\",\"status\":\"error\",\"msg\":\"id inexistente\"}");
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
