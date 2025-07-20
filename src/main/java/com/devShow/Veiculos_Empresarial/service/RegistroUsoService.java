package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.*;
import main.java.com.devShow.Veiculos_Empresarial.repository.*;
import java.util.Date;
import java.util.List;

/**
 * Service para gerenciar registros de uso de veículos.
 * Contém toda a lógica de negócio para controle de uso da frota empresarial.
 */
public class RegistroUsoService {
    
    private RegistroUsoRepository registroUsoRepository;
    private VeiculoRepository veiculoRepository;
    private MotoristaRepository motoristaRepository;
    private VeiculoService veiculoService;
    
    public RegistroUsoService() {
        this.veiculoRepository = new VeiculoRepository();
        this.motoristaRepository = new MotoristaRepository();
        this.registroUsoRepository = new RegistroUsoRepository(veiculoRepository, motoristaRepository);
        this.veiculoService = new VeiculoService();
    }
    
    /**
     * Inicia um novo registro de uso de veículo.
     * Valida disponibilidade do veículo e dados do motorista.
     * 
     * @param placaVeiculo Placa do veículo a ser usado
     * @param cnhMotorista CNH do motorista
     * @param destinoOuFinalidade Destino ou finalidade do uso
     * @return ID do registro criado ou -1 se falhou
     */
    public int iniciarUsoVeiculo(String placaVeiculo, String cnhMotorista, String destinoOuFinalidade) {
        try {
            // Validações de entrada
            validarDadosInicioUso(placaVeiculo, cnhMotorista, destinoOuFinalidade);
            
            // Busca veículo e motorista
            Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
            Motorista motorista = motoristaRepository.buscarPorCnh(cnhMotorista);
            
            if (veiculo == null) {
                throw new IllegalArgumentException("Veículo com placa " + placaVeiculo + " não encontrado");
            }
            
            if (motorista == null) {
                throw new IllegalArgumentException("Motorista com CNH " + cnhMotorista + " não encontrado");
            }
            
            // Verifica se o veículo está disponível
            if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
                throw new IllegalStateException("Veículo não está disponível. Status atual: " + veiculo.getStatus());
            }
            
            // Verifica se o motorista não está usando outro veículo
            if (motoristaEstaUsandoVeiculo(motorista.getId())) {
                throw new IllegalStateException("Motorista já está usando outro veículo");
            }
            
            // Cria o registro de uso
            RegistroUso novoRegistro = new RegistroUso(
                veiculo,
                motorista,
                new Date(), // Data/hora atual de saída
                veiculo.getQuilometragemAtual(), // KM atual do veículo
                destinoOuFinalidade
            );
            
            // Salva o registro
            int idRegistro = registroUsoRepository.salvar(novoRegistro);
            
            if (idRegistro > 0) {
                // Atualiza status do veículo para EM_USO
                veiculo.setStatus(StatusVeiculo.EM_USO);
                veiculoRepository.atualizar(veiculo);
                
                System.out.println("✅ Uso do veículo iniciado com sucesso!");
                System.out.println("   Registro ID: " + idRegistro);
                System.out.println("   Veículo: " + veiculo.getPlaca() + " (" + veiculo.getModelo() + ")");
                System.out.println("   Motorista: " + motorista.getNome());
                System.out.println("   KM inicial: " + veiculo.getQuilometragemAtual());
                
                return idRegistro;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar uso do veículo: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Finaliza um registro de uso de veículo.
     * Atualiza quilometragem final e libera o veículo.
     * 
     * @param idRegistro ID do registro a ser finalizado
     * @param quilometragemFinal Quilometragem do veículo na devolução
     * @return true se finalizou com sucesso, false caso contrário
     */
    public boolean finalizarUsoVeiculo(int idRegistro, double quilometragemFinal) {
        try {
            // Busca o registro
            RegistroUso registro = registroUsoRepository.buscarPorId(idRegistro, null);
            if (registro == null) {
                throw new IllegalArgumentException("Registro com ID " + idRegistro + " não encontrado");
            }
            
            // Verifica se o registro já foi finalizado
            if (registro.getDataHoraRetorno() != null) {
                throw new IllegalStateException("Registro já foi finalizado anteriormente");
            }
            
            // Valida a quilometragem final
            veiculoService.validarNovaQuilometragem(registro.getKmSaida(), quilometragemFinal);
            
            // Atualiza o registro
            registro.setDataHoraRetorno(new Date());
            registro.setKmRetorno(quilometragemFinal);
            
            boolean atualizouRegistro = registroUsoRepository.atualizar(registro);
            
            if (atualizouRegistro) {
                // Atualiza o veículo
                Veiculo veiculo = registro.getVeiculo();
                veiculo.setQuilometragemAtual(quilometragemFinal);
                veiculo.setStatus(StatusVeiculo.DISPONIVEL);
                veiculoRepository.atualizar(veiculo);
                
                double kmRodados = quilometragemFinal - registro.getKmSaida();
                
                System.out.println("✅ Uso do veículo finalizado com sucesso!");
                System.out.println("   Registro ID: " + idRegistro);
                System.out.println("   Veículo: " + veiculo.getPlaca());
                System.out.println("   KM rodados: " + kmRodados + " km");
                System.out.println("   Duração: " + calcularDuracaoUso(registro));
                
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao finalizar uso do veículo: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Lista todos os registros de uso ativos (ainda não finalizados).
     * 
     * @return Lista de registros ativos
     */
    public List<RegistroUso> listarRegistrosAtivos() {
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getDataHoraRetorno() == null)
            .toList();
    }
    
    /**
     * Lista todos os registros de uso finalizados.
     * 
     * @return Lista de registros finalizados
     */
    public List<RegistroUso> listarRegistrosFinalizados() {
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getDataHoraRetorno() != null)
            .toList();
    }
    
    /**
     * Busca registros de uso por motorista.
     * 
     * @param cnhMotorista CNH do motorista
     * @return Lista de registros do motorista
     */
    public List<RegistroUso> buscarRegistrosPorMotorista(String cnhMotorista) {
        Motorista motorista = motoristaRepository.buscarPorCnh(cnhMotorista);
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista não encontrado");
        }
        
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getMotorista().getId() == motorista.getId())
            .toList();
    }
    
    /**
     * Busca registros de uso por veículo.
     * 
     * @param placaVeiculo Placa do veículo
     * @return Lista de registros do veículo
     */
    public List<RegistroUso> buscarRegistrosPorVeiculo(String placaVeiculo) {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado");
        }
        
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getVeiculo().getId() == veiculo.getId())
            .toList();
    }
    
    /**
     * Calcula estatísticas de uso da frota.
     * 
     * @return String com estatísticas formatadas
     */
    public String gerarEstatisticasUso() {
        List<RegistroUso> todosRegistros = registroUsoRepository.listarTodos();
        List<RegistroUso> registrosFinalizados = todosRegistros.stream()
            .filter(registro -> registro.getDataHoraRetorno() != null)
            .toList();
        
        int totalRegistros = todosRegistros.size();
        int registrosAtivos = totalRegistros - registrosFinalizados.size();
        
        double totalKmRodados = registrosFinalizados.stream()
            .mapToDouble(RegistroUso::calcularKmRodados)
            .sum();
        
        double mediaKmPorViagem = registrosFinalizados.isEmpty() ? 0 : 
            totalKmRodados / registrosFinalizados.size();
        
        StringBuilder stats = new StringBuilder();
        stats.append("📊 ESTATÍSTICAS DE USO DA FROTA\n");
        stats.append("═══════════════════════════════\n");
        stats.append("Total de registros: ").append(totalRegistros).append("\n");
        stats.append("Registros ativos: ").append(registrosAtivos).append("\n");
        stats.append("Registros finalizados: ").append(registrosFinalizados.size()).append("\n");
        stats.append("Total km rodados: ").append(String.format("%.1f", totalKmRodados)).append(" km\n");
        stats.append("Média km por viagem: ").append(String.format("%.1f", mediaKmPorViagem)).append(" km\n");
        
        return stats.toString();
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Valida os dados de entrada para início de uso.
     */
    private void validarDadosInicioUso(String placaVeiculo, String cnhMotorista, String destinoOuFinalidade) {
        if (placaVeiculo == null || placaVeiculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa do veículo é obrigatória");
        }
        
        if (cnhMotorista == null || cnhMotorista.trim().isEmpty()) {
            throw new IllegalArgumentException("CNH do motorista é obrigatória");
        }
        
        if (destinoOuFinalidade == null || destinoOuFinalidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Destino ou finalidade é obrigatório");
        }
    }
    
    /**
     * Verifica se um motorista já está usando algum veículo.
     */
    private boolean motoristaEstaUsandoVeiculo(int idMotorista) {
        return registroUsoRepository.listarTodos().stream()
            .anyMatch(registro -> registro.getMotorista().getId() == idMotorista && 
                     registro.getDataHoraRetorno() == null);
    }
    
    /**
     * Calcula a duração de um uso de veículo.
     */
    private String calcularDuracaoUso(RegistroUso registro) {
        if (registro.getDataHoraRetorno() == null) {
            return "Em andamento";
        }
        
        long duracaoMs = registro.getDataHoraRetorno().getTime() - 
                        registro.getDataHoraSaida().getTime();
        
        long segundos = duracaoMs / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        
        if (horas > 0) {
            return horas + "h " + (minutos % 60) + "min";
        } else if (minutos > 0) {
            return minutos + "min " + (segundos % 60) + "s";
        } else {
            return segundos + "s";
        }
    }
}
