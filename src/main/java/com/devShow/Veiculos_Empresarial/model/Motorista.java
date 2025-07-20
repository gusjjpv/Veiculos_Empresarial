package main.java.com.devShow.Veiculos_Empresarial.model;

public class Motorista extends Usuario {
    private String setor;
    private String cnh;
    private int usuarioId; 
    private boolean ativoMotorista;
    private int id;  
    
    public Motorista(String nome, String username, String senha, String setor, String cnh) {
        super(nome, username, senha, false);
        this.setor = setor;
        this.cnh = cnh;
        this.ativoMotorista = true;
    }

    public Motorista(int id, String nome, String username, String senha, boolean ehAdm, boolean ativoUsuario,
                     String setor, String cnh, int usuarioId, boolean ativoMotorista) {
        super(id, nome, username, senha, ehAdm, ativoUsuario);
        this.id = id; 
        this.setor = setor;
        this.cnh = cnh;
        this.usuarioId = usuarioId;
        this.ativoMotorista = ativoMotorista;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

       public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

       public boolean isAtivoMotorista() {
        return ativoMotorista;
    }

    public void setAtivoMotorista(boolean ativoMotorista) {
        this.ativoMotorista = ativoMotorista;
    }

    public Usuario getUsuario() {
        return this;
    }

    @Override
    public String toString() {
        return "Motorista{" +
               "id=" + id +
               ", setor='" + setor + '\'' +
               ", cnh='" + cnh + '\'' +
               ", usuarioId=" + usuarioId +
               ", ativoMotorista=" + ativoMotorista +
               ", " + super.toString() +
               '}';
    }
}
