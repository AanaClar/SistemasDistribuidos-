process.stdout.setEncoding('utf8');
const axios = require('axios');
const readline = require('readline-sync');

const BASE_URL = "http://localhost:8080/pecas";

async function main() {
    console.log("--- Cliente de Peças (Node.js) ---");
    
    while (true) {
        console.log("\n1 - Adicionar | 2 - Listar | 3 - Atualizar | 4 - Deletar | 5 - Sair");
        const opcao = readline.question("Opção: ");

        try {
            if (opcao === '1') {
                const nome = readline.question("Nome da peça: ");
                const codigo = readline.question("Código da peça: ");
                const quantidade = readline.questionInt("Quantidade: ");
                
                const res = await axios.post(BASE_URL, { nome, codigo, quantidade });
                console.log("Sucesso:", res.data);

            } else if (opcao === '2') {
                const res = await axios.get(BASE_URL);
                console.log("Lista de Peças:");
                console.table(res.data);

            } else if (opcao === '3') {
                const codigo = readline.question("Código da peça a ser ATUALIZADA: ");
                const nome = readline.question("Novo nome: ");
                const quantidade = readline.questionInt("Nova quantidade: ");
                
                const res = await axios.put(`${BASE_URL}/${codigo}`, { nome, codigo, quantidade });
                console.log("Sucesso:", res.data);

            } else if (opcao === '4') {
                const codigo = readline.question("Código da peça a ser DELETADA: ");
                const res = await axios.delete(`${BASE_URL}/${codigo}`);
                console.log("Sucesso:", res.data);

            } else if (opcao === '5') {
                console.log("Saindo...");
                break;
            } else {
                console.log("Opção inválida!");
            }
        } catch (err) {
            // Trata erros como 404 (Não encontrado) ou 500 (Erro no servidor)
            if (err.response) {
                console.error(`Erro do Servidor (${err.response.status}): ${err.response.data}`);
            } else {
                console.error("Erro ao conectar com o servidor. O Java está rodando?");
            }
        }
    }
}

main();