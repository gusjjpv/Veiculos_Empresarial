package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.*;
import main.java.com.devShow.Veiculos_Empresarial.repository.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Service para gerenciar registros de uso de veículos.
 * Contém toda a lógica de negócio para controle de uso da frota empresarial.
 */
public class RegistroUsoService {
    
    private RegistroUsoRepository registroUsoRepository;
    private VeiculoRepository veiculoRepository;
    private MotoristaRepository motoristaRepository;
    
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
            // Busca o registro de forma robusta (funciona mesmo com dados órfãos)
            RegistroUso registro = registroUsoRepository.buscarPorIdRobusto(idRegistro);
            if (registro == null) {
                System.err.println("❌ Registro com ID " + idRegistro + " não encontrado");
                return false;
            }
            
            // Verifica se o registro já foi finalizado
            if (registro.getDataHoraRetorno() != null) {
                System.err.println("❌ Registro já foi finalizado anteriormente");
                return false;
            }
            
            // Valida a quilometragem final
            if (quilometragemFinal < registro.getKmSaida()) {
                System.err.println("❌ Quilometragem final (" + quilometragemFinal + ") não pode ser menor que a inicial (" + registro.getKmSaida() + ")");
                return false;
            }
            
            // Atualiza o registro
            registro.setDataHoraRetorno(new Date());
            registro.setKmRetorno(quilometragemFinal);
            
            boolean atualizouRegistro = registroUsoRepository.atualizar(registro);
            
            if (atualizouRegistro) {
                // Atualiza o veículo (se ainda existir)
                try {
                    Veiculo veiculo = registro.getVeiculo();
                    if (veiculo != null && !veiculo.getPlaca().equals("PLACA_AUSENTE")) {
                        // Busca o veículo real do banco
                        Veiculo veiculoReal = veiculoRepository.buscarPorId(veiculo.getId(), null);
                        if (veiculoReal != null) {
                            veiculoReal.setQuilometragemAtual(quilometragemFinal);
                            veiculoReal.setStatus(StatusVeiculo.DISPONIVEL);
                            veiculoRepository.atualizar(veiculoReal);
                            
                            double kmRodados = quilometragemFinal - registro.getKmSaida();
                            
                            System.out.println("✅ Uso do veículo finalizado com sucesso!");
                            System.out.println("   Registro ID: " + idRegistro);
                            System.out.println("   Veículo: " + veiculoReal.getPlaca());
                            System.out.println("   KM rodados: " + kmRodados + " km");
                            System.out.println("   Duração: " + calcularDuracaoUso(registro));
                        } else {
                            System.out.println("✅ Registro finalizado! (Veículo não encontrado no sistema)");
                        }
                    } else {
                        System.out.println("✅ Registro órfão finalizado com sucesso!");
                    }
                } catch (Exception e) {
                    System.out.println("✅ Registro finalizado! (Erro ao atualizar veículo: " + e.getMessage() + ")");
                }
                
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
            System.err.println("❌ Motorista não encontrado com CNH: " + cnhMotorista);
            return new ArrayList<>();
        }
        
        return registroUsoRepository.buscarPorMotoristaId(motorista.getId());
    }

    public List<RegistroUso> buscarRegistrosPorVeiculo(String placaVeiculo) {
        Veiculo veiculo = veiculoRepository.buscarVeiculoPorPlaca(placaVeiculo);
        if (veiculo == null) {
            System.err.println("❌ Veículo não encontrado com placa: " + placaVeiculo);
            return new ArrayList<>();
        }
        
        return registroUsoRepository.buscarPorVeiculoId(veiculo.getId());
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
    
    /**
     * Lista todos os registros de uso (finalizados e ativos)
     */
    public List<RegistroUso> listarTodosRegistros() {
        return registroUsoRepository.listarTodos();
    }
    
    /**
     * Exclui um registro de uso pelo ID
     */
    public boolean excluirRegistro(int idRegistro) {
        try {
            RegistroUso registro = registroUsoRepository.buscarPorId(idRegistro, null);
            if (registro == null) {
                System.err.println("❌ Registro com ID " + idRegistro + " não encontrado.");
                return false;
            }
            
            boolean sucesso = registroUsoRepository.excluir(idRegistro);
            
            if (sucesso) {
                System.out.println("✅ Registro de viagem ID " + idRegistro + " excluído com sucesso.");
                
                // Se o registro estava ativo, libera o veículo
                if (registro.getDataHoraRetorno() == null) {
                    Veiculo veiculo = veiculoRepository.buscarPorId(registro.getVeiculo().getId(), null);
                    if (veiculo != null) {
                        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
                        veiculoRepository.atualizar(veiculo);
                        System.out.println("🚗 Veículo " + veiculo.getPlaca() + " liberado (status: DISPONÍVEL).");
                    }
                }
            } else {
                System.err.println("❌ Erro ao excluir registro de viagem ID " + idRegistro + ".");
            }
            
            return sucesso;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao excluir registro: " + e.getMessage());
            return false;
        }
    }
}
