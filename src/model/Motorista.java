package model;

public class Motorista extends Usuario{
    private String setor;
    private String cnh;

    public Motorista(String nome, String senha,String setor, String cnh){
        super(nome, senha, false);
        this.setor = setor;
        this.cnh = cnh;
    }

    @Override
    public String toString(){
        return super.toString() + " setor:" + this.setor + " cnh:" + this.cnh;
    }

}
