package main.java.com.devShow.Veiculos_Empresarial.model;

import java.util.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Veiculo {

    // private static List<Veiculo> frota = new ArrayList<>();

    private String placa;
    private String modelo;
    private String marca;
    private int ano;
    private String cor;
    private StatusVeiculo status;
    private double quilometragemAtual;
    private Date ultimaDataDeRevisao;
    private int id;

    // Construtor para criar novo veículo (sem ID, o ID será gerado pelo banco)
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

    // NOVO CONSTRUTOR: Para carregar veículo do banco (com ID)
    public Veiculo(int id, String placa, String modelo, String marca, int ano, String cor,
                   StatusVeiculo status, double quilometragemAtual, Date ultimaDataDeRevisao) {
        this.id = id; // Inicializa o ID
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean usarVeiculo() {
        if (this.status == StatusVeiculo.DISPONIVEL) {
            this.atualizarStatus(StatusVeiculo.EM_USO);
            System.out.println("Veículo " + this.placa + " agora está EM_USO.");
            return true;
        }
        System.err.println("Ação não permitida: Veículo " + this.placa + " não está disponível. Status atual: " + this.status);
        return false;
    }

    public void atualizarStatus(StatusVeiculo novoStatus) {
        this.setStatus(novoStatus);
    }

    public boolean atualizarQuilometragem(double novaKm) {
        if (novaKm >= this.quilometragemAtual) {
            this.setQuilometragemAtual(novaKm);
            return true;
        }
        System.err.println("Erro: A nova quilometragem (" + novaKm + ") não pode ser menor que a atual (" + this.quilometragemAtual + ").");
        return false;
    }

    public boolean verificarNecessidadeRevisao() {
        final double INTERVALO_KM_REVISAO = 10000.0;
        final long INTERVALO_MESES_REVISAO = 6;
        double kmUltimaRevisao = this.quilometragemAtual > INTERVALO_KM_REVISAO ? this.quilometragemAtual - INTERVALO_KM_REVISAO : 0;

        boolean precisaPorKm = (this.quilometragemAtual - kmUltimaRevisao) >= INTERVALO_KM_REVISAO;
        boolean precisaPorTempo = false;
        if (this.ultimaDataDeRevisao != null) {
            long mesesDesdeUltimaRevisao = ChronoUnit.MONTHS.between(
                this.ultimaDataDeRevisao.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now()
            );
            precisaPorTempo = mesesDesdeUltimaRevisao >= INTERVALO_MESES_REVISAO;
        } else {
            precisaPorTempo = true; 
        }
        return precisaPorKm || precisaPorTempo;
    }

    @Override
    public String toString() {
        return String.format("Veiculo{placa='%s', modelo='%s', status=%s}", placa, modelo, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(placa, veiculo.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }
}