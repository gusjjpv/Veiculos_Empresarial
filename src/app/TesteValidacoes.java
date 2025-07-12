package app;

import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import main.java.com.devShow.Veiculos_Empresarial.service.VeiculoService;
import main.java.com.devShow.Veiculos_Empresarial.service.MotoristaService;

import java.util.Date;
import java.util.Calendar;

/**
 * Teste simples das validações dos Services - ETAPA 2
 */
public class TesteValidacoes {
    public static void main(String[] args) {
        System.out.println("=== TESTE DAS VALIDAÇÕES - ETAPA 2 ===\n");
        
        VeiculoService veiculoService = new VeiculoService();
        MotoristaService motoristaService = new MotoristaService();
        
        // 1. Teste de validações de Veículo
        System.out.println("1. TESTANDO VALIDAÇÕES DE VEÍCULO:");
        testarValidacoesVeiculo(veiculoService);
        
        // 2. Teste de validações de Motorista  
        System.out.println("\n2. TESTANDO VALIDAÇÕES DE MOTORISTA:");
        testarValidacoesMotorista(motoristaService);
        
        System.out.println("\n=== TESTES DE VALIDAÇÃO CONCLUÍDOS ===");
    }
    
    private static void testarValidacoesVeiculo(VeiculoService service) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JANUARY, 15);
        Date dataRevisao = calendar.getTime();
        
        // Teste válido
        try {
            Veiculo veiculoValido = new Veiculo("ABC1234", "Corolla", "Toyota", 2023, "Prata", 
                                              StatusVeiculo.DISPONIVEL, 15000.0, dataRevisao);
            service.validarVeiculo(veiculoValido);
            System.out.println("✓ Veículo válido passou na validação");
        } catch (Exception e) {
            System.out.println("✗ Erro inesperado: " + e.getMessage());
        }
        
        // Teste com placa vazia
        try {
            Veiculo veiculoInvalido = new Veiculo("", "Civic", "Honda", 2022, "Branco", 
                                                StatusVeiculo.DISPONIVEL, 22000.0, dataRevisao);
            service.validarVeiculo(veiculoInvalido);
            System.out.println("✗ Deveria ter falhado com placa vazia");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Rejeitou placa vazia: " + e.getMessage());
        }
        
        // Teste com ano inválido
        try {
            Veiculo veiculoAnoInvalido = new Veiculo("DEF5678", "Civic", "Honda", 1800, "Branco", 
                                                   StatusVeiculo.DISPONIVEL, 22000.0, dataRevisao);
            service.validarVeiculo(veiculoAnoInvalido);
            System.out.println("✗ Deveria ter falhado com ano inválido");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Rejeitou ano inválido: " + e.getMessage());
        }
    }
    
    private static void testarValidacoesMotorista(MotoristaService service) {
        // Teste válido
        try {
            Motorista motoristaValido = new Motorista("João Silva", "joao.silva", "senha123", 
                                                    "TI", "12345678901");
            service.validarMotorista(motoristaValido);
            System.out.println("✓ Motorista válido passou na validação");
        } catch (Exception e) {
            System.out.println("✗ Erro inesperado: " + e.getMessage());
        }
        
        // Teste com CNH inválida
        try {
            Motorista motoristaInvalido = new Motorista("Maria Santos", "maria.santos", "senha456", 
                                                       "RH", "123");
            service.validarMotorista(motoristaInvalido);
            System.out.println("✗ Deveria ter falhado com CNH inválida");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Rejeitou CNH inválida: " + e.getMessage());
        }
        
        // Teste validação individual de CNH
        System.out.println("✓ CNH '12345678901' é válida? " + service.validarFormatoCnh("12345678901"));
        System.out.println("✓ CNH '123' é válida? " + service.validarFormatoCnh("123"));
    }
}
