package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.Manutencao;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.repository.ManutencaoRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.VeiculoRepository;

import java.util.Date;
import java.util.List;

public class ManutencaoService {

    private ManutencaoRepository manutencaoRepository = new ManutencaoRepository();
    private VeiculoRepository veiculoRepository = new VeiculoRepository();

    public boolean iniciarManutencao(String placaVeiculo, String descricaoServico, String nomeOficina, Date dataSaidaPrevista) {
        try {
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
            if (veiculo == null) {
                throw new Exception("Veículo com placa " + placaVeiculo + " não encontrado.");
            }

            if (veiculo.getStatus() == StatusVeiculo.EM_USO) {
                throw new Exception("Veículo está em uso e não pode entrar em manutenção.");
            }
            if (veiculo.getStatus() == StatusVeiculo.MANUTENCAO) {
                throw new Exception("Veículo já está em manutenção.");
            }

            Manutencao novaManutencao = new Manutencao(veiculo, descricaoServico, nomeOficina, new Date(), dataSaidaPrevista);

            manutencaoRepository.salvar(novaManutencao);

            veiculo.setStatus(StatusVeiculo.MANUTENCAO);
            veiculoRepository.atualizar(veiculo);

            System.out.println("✅ Manutenção iniciada com sucesso para o veículo " + placaVeiculo);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar manutenção: " + e.getMessage());
            return false;
        }
    }

    public boolean concluirManutencao(String placaVeiculo, Date dataSaidaReal, double custoReal) {
    try {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        if (veiculo == null) {
            throw new Exception("Veículo com placa " + placaVeiculo + " não encontrado.");
        }

        if (veiculo.getStatus() != StatusVeiculo.MANUTENCAO) {
            throw new Exception("O veículo " + placaVeiculo + " não está atualmente em manutenção.");
        }
        Manutencao manutencao = manutencaoRepository.buscarManutencaoAtivaPorVeiculoId(veiculo.getId());
        if (manutencao == null) {
            throw new Exception("Nenhuma manutenção em andamento encontrada para o veículo " + placaVeiculo);
        }

        manutencao.setDataSaidaReal(dataSaidaReal);
        manutencao.setCustoReal(custoReal);

        manutencaoRepository.atualizar(manutencao);

        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        veiculoRepository.atualizar(veiculo);

        System.out.println("Manutenção finalizada com sucesso. Veículo " + placaVeiculo + " está disponível.");
        return true;

    } catch (Exception e) {
        System.err.println("Erro ao concluir manutenção: " + e.getMessage());
        return false;
    }
}

    public List<Manutencao> listarTodas() {
        return manutencaoRepository.listarTodos();
    }
}