package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.*;
import main.java.com.devShow.Veiculos_Empresarial.repository.*;
import java.time.LocalDate;

/**
 * Service para operações relacionadas a Manutenções.
 * Contém a lógica de negócio para gerenciamento de manutenções da frota.
 */
public class ManutencaoService {
    
    private ManutencaoRepository manutencaoRepository;
    private VeiculoRepository veiculoRepository;
    
    public ManutencaoService() {
        this.manutencaoRepository = new ManutencaoRepository();
        this.veiculoRepository = new VeiculoRepository();
    }
    
    /**
     * Inicia uma nova manutenção para um veículo.
     * 
     * @param placaVeiculo Placa do veículo
     * @param descricaoServico Descrição do serviço a ser realizado
     * @param nomeOficina Nome da oficina (opcional)
     * @param dataSaidaPrevista Data prevista para saída (opcional)
     * @return true se iniciou com sucesso, false caso contrário
     */
    public boolean iniciarManutencao(String placaVeiculo, String descricaoServico, String nomeOficina, LocalDate dataSaidaPrevista) {
        try {
            // Validações
            validarDadosManutencao(placaVeiculo, descricaoServico);
            
            // Busca o veículo
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
            if (veiculo == null) {
                throw new IllegalArgumentException("Veículo com placa " + placaVeiculo + " não encontrado");
            }
            
            // Verifica se o veículo pode entrar em manutenção
            if (veiculo.getStatus() == StatusVeiculo.MANUTENCAO) {
                throw new IllegalStateException("Veículo já está em manutenção");
            }
            
            if (veiculo.getStatus() == StatusVeiculo.EM_USO) {
                throw new IllegalStateException("Veículo está em uso. Finalize o uso antes de iniciar manutenção");
            }
            
            // Cria a manutenção com o construtor existente
            Manutencao novaManutencao = new Manutencao(
                veiculo,
                descricaoServico,
                LocalDate.now(), // Data de entrada (hoje)
                dataSaidaPrevista,
                nomeOficina
            );
            
            // Salva a manutenção
            manutencaoRepository.salvar(novaManutencao);
            
            // Atualiza status do veículo para MANUTENCAO
            veiculo.setStatus(StatusVeiculo.MANUTENCAO);
            veiculoRepository.update(veiculo);
            
            System.out.println("✅ Manutenção iniciada com sucesso!");
            System.out.println("   Veículo: " + veiculo.getPlaca() + " (" + veiculo.getModelo() + ")");
            System.out.println("   Serviço: " + descricaoServico);
            if (nomeOficina != null && !nomeOficina.trim().isEmpty()) {
                System.out.println("   Oficina: " + nomeOficina);
            }
            if (dataSaidaPrevista != null) {
                System.out.println("   Previsão de saída: " + dataSaidaPrevista);
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar manutenção: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Finaliza uma manutenção.
     * 
     * @param placaVeiculo Placa do veículo em manutenção
     * @param custoReal Custo real da manutenção
     * @return true se finalizou com sucesso, false caso contrário
     */
    public boolean finalizarManutencao(String placaVeiculo, double custoReal) {
        try {
            // Busca o veículo
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
            if (veiculo == null) {
                throw new IllegalArgumentException("Veículo com placa " + placaVeiculo + " não encontrado");
            }
            
            // Verifica se está em manutenção
            if (veiculo.getStatus() != StatusVeiculo.MANUTENCAO) {
                throw new IllegalStateException("Veículo não está em manutenção");
            }
            
            // Valida o custo
            if (custoReal < 0) {
                throw new IllegalArgumentException("Custo real não pode ser negativo");
            }
            
            // Por enquanto, implementação simplificada
            // TODO: Buscar manutenção ativa específica do veículo e finalizar
            
            // Libera o veículo (volta para disponível)
            veiculo.setStatus(StatusVeiculo.DISPONIVEL);
            veiculoRepository.update(veiculo);
            
            System.out.println("✅ Manutenção finalizada com sucesso!");
            System.out.println("   Veículo: " + veiculo.getPlaca());
            System.out.println("   Custo: R$ " + String.format("%.2f", custoReal));
            System.out.println("   Status: Veículo disponível novamente");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao finalizar manutenção: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Valida se um veículo pode entrar em manutenção.
     * 
     * @param placaVeiculo Placa do veículo
     * @return true se pode entrar em manutenção, false caso contrário
     */
    public boolean podeEntrarEmManutencao(String placaVeiculo) {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        
        if (veiculo == null) {
            System.err.println("❌ Veículo não encontrado");
            return false;
        }
        
        if (veiculo.getStatus() == StatusVeiculo.MANUTENCAO) {
            System.err.println("❌ Veículo já está em manutenção");
            return false;
        }
        
        if (veiculo.getStatus() == StatusVeiculo.EM_USO) {
            System.err.println("❌ Veículo está em uso");
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica se um veículo está em manutenção.
     * 
     * @param placaVeiculo Placa do veículo
     * @return true se está em manutenção, false caso contrário
     */
    public boolean veiculoEstaEmManutencao(String placaVeiculo) {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        return veiculo != null && veiculo.getStatus() == StatusVeiculo.MANUTENCAO;
    }
    
    /**
     * Gera relatório simples de manutenções.
     * 
     * @return String com informações básicas
     */
    public String gerarRelatorioManutencoes() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("🔧 RELATÓRIO DE MANUTENÇÕES\n");
        relatorio.append("═══════════════════════════\n");
        
        // Por enquanto, implementação simplificada
        // TODO: Implementar quando ManutencaoRepository tiver métodos de listagem
        relatorio.append("⚠️ Funcionalidade em desenvolvimento\n");
        relatorio.append("Aguardando implementação completa do ManutencaoRepository\n");
        
        return relatorio.toString();
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Valida os dados de entrada para manutenção.
     */
    private void validarDadosManutencao(String placaVeiculo, String descricaoServico) {
        if (placaVeiculo == null || placaVeiculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa do veículo é obrigatória");
        }
        
        if (descricaoServico == null || descricaoServico.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do serviço é obrigatória");
        }
        
        if (descricaoServico.length() < 5) {
            throw new IllegalArgumentException("Descrição do serviço deve ter pelo menos 5 caracteres");
        }
    }
}
