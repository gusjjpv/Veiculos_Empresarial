package main.java.com.devShow.Veiculos_Empresarial.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Manutencao {
    private static List<Manutencao> historicoGeral = new ArrayList<>();

    private LocalDate dataEntrada;
    private LocalDate dataSaidaPrevista;
    private LocalDate dataSaidaReal;
    private String descricaoServico;
    private double custoReal;
    private String nomeOficina;
    private Veiculo veiculo;

    public Manutencao(Veiculo veiculo, String descricaoServico, LocalDate dataEntrada, LocalDate dataSaidaPrevista,
            String nomeOficina) {
        this.veiculo = veiculo;
        this.descricaoServico = descricaoServico;
        this.dataEntrada = dataEntrada;
        this.dataSaidaPrevista = dataSaidaPrevista;
        this.nomeOficina = nomeOficina;
        this.dataSaidaReal = null;
        this.custoReal = 0.0;
    }

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

    public static void iniciarManutencao(Veiculo veiculo, String descricao, LocalDate dataEntrada,
            LocalDate dataPrevista, String oficina) {
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            System.out.println("ERRO: Veículo " + veiculo.getPlaca() + " não está disponível para manutenção.");
            return;
        }
        Manutencao novaManutencao = new Manutencao(veiculo, descricao, dataEntrada, dataPrevista, oficina);
        historicoGeral.add(novaManutencao);

        veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);

        System.out.println("Manutenção iniciada para o veículo " + veiculo.getPlaca());
    }

    public static void concluirManutencao(Veiculo veiculo, LocalDate dataSaidaReal, double custo) {
        Manutencao manutencaoAberta = historicoGeral.stream()
                .filter(m -> m.getVeiculo().equals(veiculo) && m.getDataSaidaReal() == null)
                .findFirst()
                .orElse(null);

        if (manutencaoAberta != null) {
            manutencaoAberta.setDataSaidaReal(dataSaidaReal);
            manutencaoAberta.setCustoReal(custo);
            manutencaoAberta.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
            System.out.println("Manutenção concluída para o veículo " + veiculo.getPlaca());
        } else {
            System.out.println("ERRO: Nenhuma manutenção em aberto encontrada para o veículo " + veiculo.getPlaca());
        }
    }

    public static List<Manutencao> listarManutencoes() {
        return historicoGeral;
    }

    public static void excluirManutecao(Manutencao manutencao) {
        if (historicoGeral.remove(manutencao)) {
            System.out.println("Registro de manutenção removido com sucesso.");
        } else {
            System.out.println("ERRO: Registro de manutenção não encontrado.");
        }
    }
}