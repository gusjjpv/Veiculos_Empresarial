package main.java.com.devShow.Veiculos_Empresarial.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Veiculo {

    private static List<Veiculo> frota = new ArrayList<>();

    private String placa;
    private String modelo;
    private String marca;
    private int ano;
    private String cor;
    private StatusVeiculo status;
    private double quilometragemAtual;
    private Date ultimaDataDeRevisao;

    public Veiculo(String placa, String modelo, String marca, int ano, String cor,
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
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

    public void usarVeiculo() {
        this.setStatus(StatusVeiculo.EM_USO);
    }

    public void atualizarStatus(StatusVeiculo novoStatus) {
        this.setStatus(novoStatus);
    }

    public void atualizarQuilometragem(double novaKm) {
        if (novaKm >= this.quilometragemAtual) {
            this.setQuilometragemAtual(novaKm);
        }
    }

    public boolean verificarNecessidadeRevisao() {
        return this.quilometragemAtual % 10000 == 0;
    }

    public static void cadastrarVeiculo(Veiculo veiculo) {
        frota.add(veiculo);
    }

    public static void atualizarVeiculo(Veiculo veiculo) {
        for (int i = 0; i < frota.size(); i++) {
            if (frota.get(i).getPlaca().equals(veiculo.getPlaca())) {
                frota.set(i, veiculo);
                return;
            }
        }
    }

    public static void excluirVeiculo(String placa) {
        frota.removeIf(v -> v.getPlaca().equals(placa));
    }

    public static List<Veiculo> listarVeiculos() {
        return new ArrayList<>(frota);
    }

    public static List<Veiculo> listarVeiculosDisponiveis() {
        return frota.stream()
                .filter(v -> v.getStatus() == StatusVeiculo.DISPONIVEL)
                .collect(Collectors.toList());
    }

    public static Veiculo buscarVeiculoPorPlaca(String placa) {
        for (Veiculo v : frota) {
            if (v.getPlaca().equals(placa)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Veiculo{placa='%s', modelo='%s', marca='%s', ano=%d, cor='%s', status=%s, km=%.1f}", 
                           placa, modelo, marca, ano, cor, status, quilometragemAtual);
    }
}