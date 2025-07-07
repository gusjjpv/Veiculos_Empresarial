package app;

import model.Veiculo;
import model.StatusVeiculo;
import java.util.Date;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        System.out.println("--- Início dos testes das funções ---");

        // Cadastrando a frota inicial.
        System.out.println("\n[Cadastrando a frota inicial]");
        Veiculo kwid = new Veiculo("DEF5678", "Kwid", "Renault", "2021", "Branco", StatusVeiculo.DISPONIVEL, 29500.0,
                new Date());
        Veiculo mobi = new Veiculo("GHI9012", "Mobi", "Fiat", "2023", "Vermelho", StatusVeiculo.DISPONIVEL, 9990.0,
                new Date());
        Veiculo.cadastrarVeiculo(kwid);
        Veiculo.cadastrarVeiculo(mobi);
        System.out.println("Frota inicial: " + Veiculo.listarVeiculos());

        // Um motorista retira um veículo viagem.
        System.out.println("\n[Retirada de veículo para viagem]");
        System.out.println("Veículos disponíveis: " + Veiculo.listarVeiculosDisponiveis());
        Veiculo veiculoParaViagem = Veiculo.buscarVeiculoPorPlaca("DEF5678");
        veiculoParaViagem.usarVeiculo();
        System.out.println("Kwid retirado. Status atual: " + veiculoParaViagem.getStatus());
        System.out.println("Disponíveis agora: " + Veiculo.listarVeiculosDisponiveis());

        // O veículo retorna e a quilometragem é atualizada.
        System.out.println("\n[Retorno de veículo e atualização de dados]");
        veiculoParaViagem.atualizarQuilometragem(30000.0);
        veiculoParaViagem.atualizarStatus(StatusVeiculo.DISPONIVEL);
        System.out.println("Kwid retornou. KM atual: " + veiculoParaViagem.getQuilometragemAtual() + ", Status: "
                + veiculoParaViagem.getStatus());

        // Verificação de manutenção e correção de dados.
        System.out.println("\n[Verificação de manutenção e correção de dados]");
        boolean precisaRevisao = veiculoParaViagem.verificarNecessidadeRevisao();
        System.out.println("O Kwid precisa de revisão agora? " + precisaRevisao);
        System.out.println("Corrigindo a cor do Kwid para 'Prata'...");
        veiculoParaViagem.setCor("Prata");
        Veiculo.atualizarVeiculo(veiculoParaViagem);
        System.out.println("Dados atualizados: " + Veiculo.buscarVeiculoPorPlaca("DEF5678"));

        System.out.println("\n--- Fim dos testes das funções ---");

        // Veículo removido.
        System.out.println("\n[Removendo um veículo da frota]");
        Veiculo.excluirVeiculo("GHI9012");
        System.out.println("Frota final do dia: " + Veiculo.listarVeiculos());

        // Date hoje = new Date(); // Data atual

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JANUARY, 15);
        Date dataUltimaRevisao = calendar.getTime();

        System.out.println("\nCriando um novo veículo...");
        Veiculo meuCarro = new Veiculo(
                "ABC1234",
                "Onix",
                "Chevrolet",
                "2022",
                "Preto",
                StatusVeiculo.DISPONIVEL,
                15000.5,
                dataUltimaRevisao);

        System.out.println("Veículo criado com sucesso!");

        // Usando os Getters para exibir os atributos
        System.out.println("\n--- Detalhes do Veículo (via Getters) ---");
        System.out.println("Placa: " + meuCarro.getPlaca());
        System.out.println("Modelo: " + meuCarro.getModelo());
        System.out.println("Marca: " + meuCarro.getMarca());
        System.out.println("Ano: " + meuCarro.getAno());
        System.out.println("Cor: " + meuCarro.getCor());
        System.out.println("Status: " + meuCarro.getStatus());
        System.out.println("Quilometragem Atual: " + meuCarro.getQuilometragemAtual() + " km");
        System.out.println("Última Data de Revisão: " + meuCarro.getUltimaDataDeRevisao());

        // Usando Setters para modificar alguns atributos
        System.out.println("\n--- Modificando o Veículo (via Setters) ---");
        meuCarro.setCor("Azul Metálico");
        meuCarro.setStatus(StatusVeiculo.EM_USO);
        meuCarro.setQuilometragemAtual(15250.7);

        System.out.println("Cor atualizada para: " + meuCarro.getCor());
        System.out.println("Status atualizado para: " + meuCarro.getStatus());
        System.out.println("Quilometragem atualizada para: " + meuCarro.getQuilometragemAtual() + " km");

        System.out.println("\n--- Fim do Teste ---");
    }
}