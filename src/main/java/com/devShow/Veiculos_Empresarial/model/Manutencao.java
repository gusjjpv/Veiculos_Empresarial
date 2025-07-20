package main.java.com.devShow.Veiculos_Empresarial.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Manutencao {

    // --- Atributos ---
    private int id;
    private Veiculo veiculo;
    private String descricaoServico;
    private String nomeOficina;
    private Date dataEntrada;
    private Date dataSaidaPrevista;
    private Date dataSaidaReal;
    private double custoReal;

    public Manutencao(Veiculo veiculo, String descricaoServico, String nomeOficina, Date dataEntrada, Date dataSaidaPrevista) {
        this.veiculo = veiculo;
        this.descricaoServico = descricaoServico;
        this.nomeOficina = nomeOficina;
        this.dataEntrada = dataEntrada;
        this.dataSaidaPrevista = dataSaidaPrevista;
        this.dataSaidaReal = null;
        this.custoReal = 0.0;
    }

    public Manutencao(int id, Veiculo veiculo, String descricaoServico, String nomeOficina, Date dataEntrada, Date dataSaidaPrevista, Date dataSaidaReal, double custoReal) {
        this.id = id;
        this.veiculo = veiculo;
        this.descricaoServico = descricaoServico;
        this.nomeOficina = nomeOficina;
        this.dataEntrada = dataEntrada;
        this.dataSaidaPrevista = dataSaidaPrevista;
        this.dataSaidaReal = dataSaidaReal;
        this.custoReal = custoReal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public String getNomeOficina() {
        return nomeOficina;
    }

    public void setNomeOficina(String nomeOficina) {
        this.nomeOficina = nomeOficina;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Date getDataSaidaPrevista() {
        return dataSaidaPrevista;
    }

    public void setDataSaidaPrevista(Date dataSaidaPrevista) {
        this.dataSaidaPrevista = dataSaidaPrevista;
    }

    public Date getDataSaidaReal() {
        return dataSaidaReal;
    }

    public void setDataSaidaReal(Date dataSaidaReal) {
        this.dataSaidaReal = dataSaidaReal;
    }

    public double getCustoReal() {
        return custoReal;
    }

    public void setCustoReal(double custoReal) {
        this.custoReal = custoReal;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String entradaFormatada = (dataEntrada != null) ? sdf.format(dataEntrada) : "N/A";
        String saidaPrevistaFormatada = (dataSaidaPrevista != null) ? sdf.format(dataSaidaPrevista) : "N/A";
        String saidaRealFormatada = (dataSaidaReal != null) ? sdf.format(dataSaidaReal) : "Pendente";

        return String.format(
            "Manutencao [ID: %d, Veículo: %s, Descrição: '%s', Oficina: '%s', Entrada: %s, Prev. Saída: %s, Saída Real: %s, Custo: R$%.2f]",
            id,
            (veiculo != null ? veiculo.getPlaca() : "N/A"),
            descricaoServico,
            nomeOficina,
            entradaFormatada,
            saidaPrevistaFormatada,
            saidaRealFormatada,
            custoReal
        );
    }
}