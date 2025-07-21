package main.java.com.devShow.Veiculos_Empresarial.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroUso {
    // Atributos
    private int id;
    private Veiculo veiculo;
    private Motorista motorista;
    private Date dataHoraSaida;
    private Date dataHoraRetorno;
    private double kmSaida;
    private double kmRetorno;
    private String destinoOuFinalidade;

    public RegistroUso(Veiculo veiculo, Motorista motorista, Date dataHoraSaida, 
                      double kmSaida, String destinoOuFinalidade) {
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataHoraSaida = dataHoraSaida;
        this.kmSaida = kmSaida;
        this.destinoOuFinalidade = destinoOuFinalidade;
        this.dataHoraRetorno = null;
        this.kmRetorno = 0.0;
    }

    // Construtor para reconstruir um objeto do banco de dados (incluindo todos os atributos)
    public RegistroUso(int id, Veiculo veiculo, Motorista motorista, Date dataHoraSaida, 
                      Date dataHoraRetorno, double kmSaida, double kmRetorno, 
                      String destinoOuFinalidade) {
        this.id = id;
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataHoraSaida = dataHoraSaida;
        this.dataHoraRetorno = dataHoraRetorno;
        this.kmSaida = kmSaida;
        this.kmRetorno = kmRetorno;
        this.destinoOuFinalidade = destinoOuFinalidade;
    }

    // Construtor para compatibilidade com RegistroUsoRepository (com Usuario explícito)
    public RegistroUso(int id, Veiculo veiculo, Motorista motorista, Usuario usuario, Date dataHoraSaida, 
                      Date dataHoraRetorno, double kmSaida, double kmRetorno, 
                      String destinoOuFinalidade) {
        this.id = id;
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataHoraSaida = dataHoraSaida;
        this.dataHoraRetorno = dataHoraRetorno;
        this.kmSaida = kmSaida;
        this.kmRetorno = kmRetorno;
        this.destinoOuFinalidade = destinoOuFinalidade;
    }

    public int getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public Date getDataHoraSaida() {
        return dataHoraSaida;
    }

    public Date getDataHoraRetorno() {
        return dataHoraRetorno;
    }

    public double getKmSaida() {
        return kmSaida;
    }

    public double getKmRetorno() {
        return kmRetorno;
    }

    public String getDestinoOuFinalidade() {
        return destinoOuFinalidade;
    }

    public Usuario getUsuario() {
        return (motorista != null) ? motorista.getUsuario() : null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public void setDataHoraSaida(Date dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public void setDataHoraRetorno(Date dataHoraRetorno) {
        this.dataHoraRetorno = dataHoraRetorno;
    }

    public void setKmSaida(double kmSaida) {
        this.kmSaida = kmSaida;
    }

    public void setKmRetorno(double kmRetorno) {
        this.kmRetorno = kmRetorno;
    }

    public void setDestinoOuFinalidade(String destinoOuFinalidade) {
        this.destinoOuFinalidade = destinoOuFinalidade;
    }


    public double calcularKmRodados() {
        if (kmRetorno >= kmSaida && dataHoraRetorno != null) {
            return kmRetorno - kmSaida;
        }
        return 0.0;
    }


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        String dataSaidaFormatada = (getDataHoraSaida() != null) ? sdf.format(getDataHoraSaida()) : "N/A";
        String dataRetornoFormatada = (getDataHoraRetorno() != null) ? sdf.format(getDataHoraRetorno()) : "Em andamento";
        
        return String.format("RegistroUso{id=%d, veiculo='%s', motorista='%s', saida=%s, retorno=%s, kmSaida=%.1f, kmRetorno=%.1f, destino='%s', kmRodados=%.1f}",
                           id, 
                           veiculo != null ? veiculo.getPlaca() : "null",
                           motorista != null ? motorista.getNome() : "null",
                           dataSaidaFormatada,
                           dataRetornoFormatada,
                           kmSaida,
                           kmRetorno,
                           destinoOuFinalidade,
                           calcularKmRodados());
    }
}