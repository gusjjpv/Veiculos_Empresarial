package model;

import java.time.LocalDate;

public class Manutencao {
    private LocalDate dataEntrada;
    private LocalDate dataSaidaPrevista;
    private LocalDate dataSaidaReal;
    private String descricaoServico;
    private double custoReal;
    private String nomeOficina; 
    private Veiculo veiculo;  
    
    // --- Construtor ---
    public Manutencao(Veiculo veiculo, String descricaoServico, LocalDate dataEntrada, LocalDate dataSaidaPrevista, String nomeOficina) {
        this.veiculo = veiculo;
        this.descricaoServico = descricaoServico;
        this.dataEntrada = dataEntrada;
        this.dataSaidaPrevista = dataSaidaPrevista;
        this.nomeOficina = nomeOficina;
        this.dataSaidaReal = null;
        this.custoReal = 0.0;
    }

    // --- Getters e Setters ---
    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataSaidaPrevista() {
        return dataSaidaPrevista;
    }

    public void setDataSaidaPrevista(LocalDate dataSaidaPrevista) {
        this.dataSaidaPrevista = dataSaidaPrevista;
    }

    public LocalDate getDataSaidaReal() {
        return dataSaidaReal;
    }

    public void setDataSaidaReal(LocalDate dataSaidaReal) {
        this.dataSaidaReal = dataSaidaReal;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public double getCustoReal() {
        return custoReal;
    }

    public void setCustoReal(double custoReal) {
        this.custoReal = custoReal;
    }

    public String getNomeOficina() {
        return nomeOficina;
    }

    public void setNomeOficina(String nomeOficina) {
        this.nomeOficina = nomeOficina;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }
}