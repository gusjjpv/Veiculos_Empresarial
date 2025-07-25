package main.java.com.devShow.Veiculos_Empresarial.model;

public class Usuario {
    private int id; 
    private String nome;
    private String username;
    private String senha;
    private boolean ehAdm;
    private boolean ativo;

    public Usuario(String nome, String username, String senha, boolean ehAdm){
        this.nome = nome;
        this.username = username;
        this.senha = senha;
        this.ehAdm = ehAdm;
        this.ativo = true;
    }

    // Construtor para carregar usuário do banco (com ID e 'ativo')
    public Usuario(int id, String nome, String username, String senha, boolean ehAdm, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.senha = senha;
        this.ehAdm = ehAdm;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getSenha(){
        return senha;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

      public boolean isAtivo() { 
        return ativo;
    }

    public boolean getEhAdm(){
        return ehAdm;
    }

    public void setEhAdm(boolean ehAdm){
        this.ehAdm = ehAdm;
    }

    public boolean autenticar(String senha){
        if(senha.equals(this.senha)){
            return true;
        }
        return false;
    }

    public String toString() {
        return "nome:" + this.nome + " username:" + this.username + " ehAdm: " + (ehAdm ? "sim":"nao") + " ativo: " + (ativo ? "sim":"nao");
    }
}

