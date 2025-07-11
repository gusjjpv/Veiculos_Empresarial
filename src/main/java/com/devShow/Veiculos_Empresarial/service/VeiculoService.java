package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import java.util.Calendar;

/**
 * Service para operações relacionadas a Veículos
 * ETAPA 2: Apenas validações básicas, sem persistência
 */
public class VeiculoService {

    /**
     * Valida os dados de um veículo antes de cadastrar
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
     * Valida se uma quilometragem nova é válida
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
