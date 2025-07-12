package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;

/**
 * Service para operações relacionadas a Motoristas
 * ETAPA 2: Apenas validações básicas, sem persistência
 */
public class MotoristaService {

    /**
     * Valida os dados de um motorista antes de cadastrar
     */
    public void validarMotorista(Motorista motorista) {
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista não pode ser nulo");
        }
        
        if (motorista.getNome() == null || motorista.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (motorista.getUsername() == null || motorista.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (motorista.getSenha() == null || motorista.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (motorista.getCnh() == null || motorista.getCnh().trim().isEmpty()) {
            throw new IllegalArgumentException("CNH é obrigatória");
        }
        
        if (motorista.getSetor() == null || motorista.getSetor().trim().isEmpty()) {
            throw new IllegalArgumentException("Setor é obrigatório");
        }

        if (!validarFormatoCnh(motorista.getCnh())) {
            throw new IllegalArgumentException("CNH deve ter 11 dígitos");
        }
    }

    /**
     * Valida se uma CNH tem formato correto
     */
    public boolean validarFormatoCnh(String cnh) {
        if (cnh == null || cnh.trim().isEmpty()) {
            return false;
        }
        
        String cnhLimpa = cnh.replaceAll("[^0-9]", "");
        return cnhLimpa.length() == 11;
    }
}
