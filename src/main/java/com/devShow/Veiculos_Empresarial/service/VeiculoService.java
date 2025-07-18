package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;
import main.java.com.devShow.Veiculos_Empresarial.repository.MotoristaRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.RegistroUsoRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.VeiculoRepository;
import java.util.Calendar;
import java.util.List;

/**
 * Service para operações relacionadas a Veículos.
 * Contém toda a lógica de negócio para gerenciamento da frota empresarial.
 */
public class VeiculoService {
    
    private VeiculoRepository veiculoRepository;
    private RegistroUsoRepository registroUsoRepository;
    
    public VeiculoService() {
        this.veiculoRepository = new VeiculoRepository();
        this.registroUsoRepository = new RegistroUsoRepository(new VeiculoRepository(), new MotoristaRepository());
    }
    
    /**
     * Cadastra um novo veículo na frota.
     * 
     * @param placa Placa do veículo
     * @param modelo Modelo do veículo
     * @param marca Marca do veículo
     * @param ano Ano do veículo
     * @param cor Cor do veículo
     * @param quilometragemInicial Quilometragem inicial
     * @return true se cadastrou com sucesso, false caso contrário
     */
    public boolean cadastrarVeiculo(String placa, String modelo, String marca, int ano, String cor, double quilometragemInicial) {
        try {
            // Cria novo veículo
            Veiculo novoVeiculo = new Veiculo(placa, modelo, marca, ano, cor, StatusVeiculo.DISPONIVEL, quilometragemInicial, null);
            
            // Valida antes de salvar
            validarVeiculo(novoVeiculo);
            
            // Verifica se placa já existe
            Veiculo veiculoExistente = veiculoRepository.buscarVeiculoPorPlaca(placa);
            if (veiculoExistente != null) {
                System.err.println("❌ Erro: Placa '" + placa + "' já está cadastrada");
                return false;
            }
            
            // Salva no banco
            veiculoRepository.salvar(novoVeiculo);
            
            System.out.println("✅ Veículo cadastrado com sucesso!");
            System.out.println("   Placa: " + placa);
            System.out.println("   Modelo: " + modelo + " " + marca);
            System.out.println("   Ano: " + ano);
            System.out.println("   Status: DISPONÍVEL");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar veículo: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca um veículo por placa.
     * 
     * @param placa Placa do veículo
     * @return Veiculo encontrado ou null se não existir
     */
    public Veiculo buscarVeiculoPorPlaca(String placa) {
        return veiculoRepository.buscarVeiculoPorPlaca(placa);
    }
    
    /**
     * Busca um veículo por ID.
     * 
     * @param id ID do veículo
     * @return Veiculo encontrado ou null se não existir
     */
    public Veiculo buscarVeiculoPorId(int id) {
        return veiculoRepository.buscarPorId(id);
    }
    
    /**
     * Lista todos os veículos da frota.
     * 
     * @return Lista de todos os veículos
     */
    public List<Veiculo> listarTodosVeiculos() {
        try {
            return veiculoRepository.findAll();
        } catch (Exception e) {
            System.err.println("Erro ao listar todos os veículos: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Lista veículos por status.
     * 
     * @param status Status desejado
     * @return Lista de veículos com o status especificado
     */
    public List<Veiculo> listarVeiculosPorStatus(StatusVeiculo status) {
        try {
            if (status == StatusVeiculo.DISPONIVEL) {
                return veiculoRepository.findAvailable();
            } else {
                // Para outros status, filtra da lista completa
                return veiculoRepository.findAll().stream()
                    .filter(v -> v.getStatus() == status)
                    .toList();
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar veículos por status " + status + ": " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Lista apenas veículos disponíveis.
     * 
     * @return Lista de veículos disponíveis
     */
    public List<Veiculo> listarVeiculosDisponiveis() {
        return listarVeiculosPorStatus(StatusVeiculo.DISPONIVEL);
    }
    
    /**
     * Lista veículos em uso.
     * 
     * @return Lista de veículos em uso
     */
    public List<Veiculo> listarVeiculosEmUso() {
        return listarVeiculosPorStatus(StatusVeiculo.EM_USO);
    }
    
    /**
     * Lista veículos em manutenção.
     * 
     * @return Lista de veículos em manutenção
     */
    public List<Veiculo> listarVeiculosEmManutencao() {
        return listarVeiculosPorStatus(StatusVeiculo.MANUTENCAO);
    }


    public boolean atualizarDadosBasicos(String placaParaBuscar, String novoModelo, String novaMarca, int novoAno, String novaCor){
        Veiculo veiculoExistente = veiculoRepository.buscarVeiculoPorPlaca(placaParaBuscar);
        if(veiculoExistente == null){
            System.err.println("SERVIÇO: Erro! Veículo com placa " + placaParaBuscar + " não encontrado.");
            return false;
        }

        veiculoExistente.setModelo(novoModelo);
        veiculoExistente.setMarca(novaMarca);
        veiculoExistente.setAno(novoAno);
        veiculoExistente.setCor(novaCor);
        veiculoRepository.atualizar(veiculoExistente);
        return true;
    }

    
    /**
     * Atualiza o status de um veículo.
     * 
     * @param placa Placa do veículo
     * @param novoStatus Novo status
     * @return true se atualizou com sucesso, false caso contrário
     */
    public boolean atualizarStatusVeiculo(String placa, StatusVeiculo novoStatus) {
        try {
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placa);
            if (veiculo == null) {
                System.err.println("❌ Veículo com placa '" + placa + "' não encontrado");
                return false;
            }
            
            StatusVeiculo statusAnterior = veiculo.getStatus();
            veiculo.setStatus(novoStatus);
            
            boolean atualizado = veiculoRepository.atualizar(veiculo);
            
            if (atualizado) {
                System.out.println("✅ Status do veículo atualizado!");
                System.out.println("   Placa: " + placa);
                System.out.println("   Status anterior: " + statusAnterior);
                System.out.println("   Novo status: " + novoStatus);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar status do veículo: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Atualiza a quilometragem de um veículo.
     * 
     * @param placa Placa do veículo
     * @param novaQuilometragem Nova quilometragem
     * @return true se atualizou com sucesso, false caso contrário
     */
    public boolean atualizarQuilometragem(String placa, double novaQuilometragem) {
        try {
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placa);
            if (veiculo == null) {
                System.err.println("❌ Veículo com placa '" + placa + "' não encontrado");
                return false;
            }
            
            // Valida a nova quilometragem
            validarNovaQuilometragem(veiculo.getQuilometragemAtual(), novaQuilometragem);
            
            double quilometragemAnterior = veiculo.getQuilometragemAtual();
            veiculo.setQuilometragemAtual(novaQuilometragem);
            
            boolean atualizado = veiculoRepository.atualizar(veiculo);
            
            if (atualizado) {
                double diferenca = novaQuilometragem - quilometragemAnterior;
                System.out.println("✅ Quilometragem atualizada!");
                System.out.println("   Placa: " + placa);
                System.out.println("   KM anterior: " + quilometragemAnterior);
                System.out.println("   KM atual: " + novaQuilometragem);
                System.out.println("   Diferença: +" + diferenca + " km");
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar quilometragem: " + e.getMessage());
        }
        
        return false;
    }


    public boolean excluirVeiculo(String placa){
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placa);
        if(veiculo == null){
            System.err.println("ERRO: Veiculo com placa "+ placa + " nao encontrado");
            return false;
        }

        boolean temRegistrosDeUso = registroUsoRepository.existsByMotoristaId(veiculo.getId());
        if(temRegistrosDeUso){
            System.err.println("ERRO: Veiculo " + placa + " nao pode ser excluido, pois existe registros de uso associados");
            return false;
        }
        //     boolean temManutencoes = manutencaoRepository.existsByVeiculoId(veiculo.getId());
        // if (temManutencoes) {
        //     System.err.println("ERRO: Veículo " + placa + " não pode ser excluído, pois possui manutenções associadas.");
        //     return false;
        // }
        veiculoRepository.delete(veiculo.getId());
        return true;
    }

    
    /**
     * Gera estatísticas da frota.
     * 
     * @return String com estatísticas formatadas
     */
    public String gerarEstatisticasFrota() {
        try {
            List<Veiculo> todosVeiculos = veiculoRepository.findAll();
            List<Veiculo> disponiveis = veiculoRepository.findAvailable();
            List<Veiculo> emUso = todosVeiculos.stream()
                .filter(v -> v.getStatus() == StatusVeiculo.EM_USO)
                .toList();
            List<Veiculo> emManutencao = todosVeiculos.stream()
                .filter(v -> v.getStatus() == StatusVeiculo.MANUTENCAO)
                .toList();
            
            StringBuilder stats = new StringBuilder();
            stats.append("🚗 ESTATÍSTICAS DA FROTA\n");
            stats.append("═══════════════════════\n\n");
            
            // Estatísticas gerais
            stats.append("📊 RESUMO GERAL:\n");
            stats.append("├─ Total de veículos: ").append(todosVeiculos.size()).append("\n");
            stats.append("├─ Disponíveis: ").append(disponiveis.size()).append("\n");
            stats.append("├─ Em uso: ").append(emUso.size()).append("\n");
            stats.append("└─ Em manutenção: ").append(emManutencao.size()).append("\n\n");
            
            // Percentuais
            if (todosVeiculos.size() > 0) {
                double percDisponivel = (disponiveis.size() * 100.0) / todosVeiculos.size();
                double percEmUso = (emUso.size() * 100.0) / todosVeiculos.size();
                double percManutencao = (emManutencao.size() * 100.0) / todosVeiculos.size();
                
                stats.append("📈 PERCENTUAIS:\n");
                stats.append(String.format("├─ Disponibilidade: %.1f%%\n", percDisponivel));
                stats.append(String.format("├─ Taxa de uso: %.1f%%\n", percEmUso));
                stats.append(String.format("└─ Em manutenção: %.1f%%\n\n", percManutencao));
            }
            
            // Estatísticas por marca
            var marcasCount = todosVeiculos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Veiculo::getMarca,
                    java.util.stream.Collectors.counting()
                ));
            
            if (!marcasCount.isEmpty()) {
                stats.append("🏭 POR MARCA:\n");
                marcasCount.entrySet().stream()
                    .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> stats.append(String.format("├─ %s: %d veículos\n", 
                        entry.getKey(), entry.getValue())));
                stats.append("\n");
            }
            
            // Quilometragem média
            if (!todosVeiculos.isEmpty()) {
                double kmMedia = todosVeiculos.stream()
                    .mapToDouble(Veiculo::getQuilometragemAtual)
                    .average()
                    .orElse(0.0);
                
                double kmTotal = todosVeiculos.stream()
                    .mapToDouble(Veiculo::getQuilometragemAtual)
                    .sum();
                
                stats.append("📏 QUILOMETRAGEM:\n");
                stats.append(String.format("├─ Média por veículo: %.1f km\n", kmMedia));
                stats.append(String.format("└─ Total da frota: %.1f km\n\n", kmTotal));
            }
            
            // Status da frota
            stats.append("🚦 STATUS DA FROTA:\n");
            if (todosVeiculos.isEmpty()) {
                stats.append("└─ Nenhum veículo cadastrado\n");
            } else if (disponiveis.size() == todosVeiculos.size()) {
                stats.append("└─ ✅ Toda a frota está disponível!\n");
            } else if (emUso.size() > disponiveis.size()) {
                stats.append("└─ 🔥 Alta demanda - mais veículos em uso que disponíveis\n");
            } else if (emManutencao.size() > todosVeiculos.size() * 0.3) {
                stats.append("└─ ⚠️ Muitos veículos em manutenção (>30%)\n");
            } else {
                stats.append("└─ ✅ Frota em bom estado operacional\n");
            }
            
            return stats.toString();
            
        } catch (Exception e) {
            return "❌ Erro ao gerar estatísticas: " + e.getMessage();
        }
    }

    /**
     * Valida os dados de um veículo antes de cadastrar.
     */
    public void validarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não pode ser nulo");
        }
        
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("Placa é obrigatória");
        }
        
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo é obrigatório");
        }
        
        if (veiculo.getMarca() == null || veiculo.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("Marca é obrigatória");
        }
        
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        if (veiculo.getAno() < 1900 || veiculo.getAno() > anoAtual + 1) {
            throw new IllegalArgumentException("Ano deve estar entre 1900 e " + (anoAtual + 1));
        }
        
        if (veiculo.getQuilometragemAtual() < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser negativa");
        }
    }

    /**
     * Valida se uma quilometragem nova é válida.
     */
    public void validarNovaQuilometragem(double quilometragemAtual, double novaQuilometragem) {
        if (novaQuilometragem < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser negativa");
        }
        
        if (novaQuilometragem < quilometragemAtual) {
            throw new IllegalArgumentException("Nova quilometragem não pode ser menor que a atual");
        }
    }
}
