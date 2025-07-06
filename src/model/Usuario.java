package model;

public class Usuario {
    private String nome;
    private String senha;
    private boolean ehAdm;

    public Usuario(String nome, String senha, boolean ehAdm){
        this.nome = nome;
        this.senha = senha;
        this.ehAdm = ehAdm;
    }

    public boolean autenticar(String senha){
        if(senha.equals(this.senha)){
            return true;
        }
        return false;
    }

    public String toString() {
        return "nome:" + this.nome;
    }
}

