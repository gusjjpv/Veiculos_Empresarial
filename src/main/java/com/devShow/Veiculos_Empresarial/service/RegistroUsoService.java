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
    }


    public RegistroUso registrarSaida(Veiculo veiculo, Motorista motorista, String destino) {
    try {

        RegistroUso novoRegistro = new RegistroUso(veiculo, motorista, new Date(), veiculo.getQuilometragemAtual(), destino);

        registroUsoRepository.salvar(novoRegistro);

        System.out.println("SERVICE (RegistroUso): Saída registrada com sucesso. ID: " + novoRegistro.getId());
        
        return novoRegistro;

    } catch (Exception e) {
        System.err.println("❌ Erro no serviço ao registrar saída: " + e.getMessage());
        return null;
    }
}

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

    public List<RegistroUso> listarRegistrosAtivos() {
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getDataHoraRetorno() == null)
            .toList();
    }
    
    public List<RegistroUso> listarRegistrosFinalizados() {
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getDataHoraRetorno() != null)
            .toList();
    }
    
    public List<RegistroUso> buscarRegistrosPorMotorista(String cnhMotorista) {
        Motorista motorista = motoristaRepository.buscarPorCnh(cnhMotorista);
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista não encontrado");
        }
        
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getMotorista().getId() == motorista.getId())
            .toList();
    }

    public List<RegistroUso> buscarRegistrosPorVeiculo(String placaVeiculo) {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado");
        }
        
        return registroUsoRepository.listarTodos().stream()
            .filter(registro -> registro.getVeiculo().getId() == veiculo.getId())
            .toList();
    }

    // ==================== MÉTODOS AUXILIARES ====================

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

    private boolean motoristaEstaUsandoVeiculo(int idMotorista) {
        return registroUsoRepository.listarTodos().stream()
            .anyMatch(registro -> registro.getMotorista().getId() == idMotorista && 
                     registro.getDataHoraRetorno() == null);
    }

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
