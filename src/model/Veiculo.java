package model;

import java.util.Date;

public class Veiculo{
    
    private String placa;
    private String modelo;
    private String marca;
    private String ano;
    private String cor;
    private StatusVeiculo status;
    private double quilometragemAtual;
    private Date ultimaDataDeRevisao;


    public Veiculo(String placa, String modelo, String marca, String ano, String cor,
    StatusVeiculo status, double quilometragemAtual, Date ultimaDataDeRevisao) {
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.status = status;
        this.quilometragemAtual = quilometragemAtual;
        this.ultimaDataDeRevisao = ultimaDataDeRevisao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getAno() { // CORRIGIDO: Retorna String
        return ano;
    }

    public void setAno(String ano) { // CORRIGIDO: Recebe String
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public double getQuilometragemAtual() {
        return quilometragemAtual;
    }

    public void setQuilometragemAtual(double quilometragemAtual) {
        this.quilometragemAtual = quilometragemAtual;
    }

    public Date getUltimaDataDeRevisao() {
        return ultimaDataDeRevisao;
    }

    public void setUltimaDataDeRevisao(Date ultimaDataDeRevisao) {
        this.ultimaDataDeRevisao = ultimaDataDeRevisao;
    }

    // Implementacao dos outros Metodos e logica

}

