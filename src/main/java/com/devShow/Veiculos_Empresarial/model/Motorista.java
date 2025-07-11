package main.java.com.devShow.Veiculos_Empresarial.model;

public class Motorista extends Usuario {
    private String setor;
    private String cnh;

    public Motorista(String nome, String username, String senha, String setor, String cnh) {
        super(nome, username, senha, false);
        this.setor = setor;
        this.cnh = cnh;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getCnh() {
        return cnh;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    public void cadastrarMotorista() {
    }

    public void excluirMotorista() {
    }

    public void listarTodosMotoristas() {
    }

    public void atualizarDadosDeMotorista() {
        // TODO: Implementar lógica de atualização
    }

    public void buscarMotorista() {
    }

    @Override
    public String toString() {
        return super.toString() + " setor:" + this.setor + " cnh:" + this.cnh;
    }
}
