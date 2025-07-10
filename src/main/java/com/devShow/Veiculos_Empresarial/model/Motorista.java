package main.java.com.devShow.Veiculos_Empresarial.model;

public class Motorista extends Usuario{
    private String setor;
    private String cnh;

    public Motorista(String nome, String senha,String setor, String cnh){
        super(nome, senha, false);
        this.setor = setor;
        this.cnh = cnh;
    }

    public void cadastrarMotorista(){
    }

    public void excluirMotorista(){
    }

    public void listarTodosMotoristas(){
    }

    public void atualizarDadosDeMotorista(){
    }

    public void atualizarDadoDeMotorista(){
    }

    public void buscarMotorista(){
    }

    @Override
    public String toString(){
        return super.toString() + " setor:" + this.setor + " cnh:" + this.cnh;
    }
}
