import requests

BASE_URL = "http://localhost:8080/pecas"

def ler_inteiro(mensagem):
    while True:
        try:
            return int(input(mensagem))
        except ValueError:
            print("Erro: Por favor, digite um número válido.")

def adicionar_peca():
    nome = input("Digite o nome da peça: ")
    codigo = input("Digite o código da peça: ")
    quantidade = ler_inteiro("Digite a quantidade de peças: ")

    json_data = {
        "nome": nome,
        "codigo": codigo,
        "quantidade": quantidade
    }

    try:
        response = requests.post(BASE_URL, json=json_data)
        print("Resposta:", response.text)
    except Exception as e:
        print("Erro ao conectar com o servidor:", e)

def listar_pecas():
    try:
        response = requests.get(BASE_URL)
        print("Resposta:", response.text)
    except Exception as e:
        print("Erro ao conectar com o servidor:", e)

def atualizar_peca():
    codigo = input("Digite o código da peça a ser atualizada: ")
    nome = input("Digite o novo nome da peça: ")
    quantidade = ler_inteiro("Digite a nova quantidade de peças: ")

    json_data = {
        "nome": nome,
        "codigo": codigo,
        "quantidade": quantidade
    }

    try:
        response = requests.put(f"{BASE_URL}/{codigo}", json=json_data)
        print("Resposta:", response.text)
    except Exception as e:
        print("Erro ao conectar com o servidor:", e)

def deletar_peca():
    codigo = input("Digite o código da peça a ser deletada: ")
    try:
        response = requests.delete(f"{BASE_URL}/{codigo}")
        print("Resposta:", response.text)
    except Exception as e:
        print("Erro ao conectar com o servidor:", e)

def main():
    while True:
        print("\n--- Sistema de Peças ---")
        print("1 - Adicionar Peça")
        print("2 - Listar Peças")
        print("3 - Atualizar Peça")
        print("4 - Deletar Peça")
        print("5 - Sair")
        opcao = input("Opção: ")

        if opcao == "1":
            adicionar_peca()
        elif opcao == "2":
            listar_pecas()
        elif opcao == "3":
            atualizar_peca()
        elif opcao == "4":
            deletar_peca()
        elif opcao == "5":
            print("Saindo...")
            break
        else:
            print("Opção inválida!")

if __name__ == "__main__":
    main()