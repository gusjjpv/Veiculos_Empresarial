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

       public void setUsuarioId(int usuarioId) { // Setter para o ID da FK 'usuario_id'
        this.usuarioId = usuarioId;
    }

       public boolean isAtivoMotorista() { // Getter para o atributo 'ativoMotorista'
        return ativoMotorista;
    }

    public void setAtivoMotorista(boolean ativoMotorista) { // Setter para o atributo 'ativoMotorista'
        this.ativoMotorista = ativoMotorista;
    }

    /**
     * Retorna o objeto Usuario associado a este motorista.
     * Como Motorista herda de Usuario, retorna uma referência para si mesmo.
     * @return O objeto Usuario (this).
     */
    public Usuario getUsuario() {
        return this;
    }

    @Override
    public String toString() {
        return "Motorista{" +
               "id=" + id + // ID do motorista
               ", setor='" + setor + '\'' +
               ", cnh='" + cnh + '\'' +
               ", usuarioId=" + usuarioId + // ID do usuário associado
               ", ativoMotorista=" + ativoMotorista + // Ativo do motorista
               ", " + super.toString() + // Inclui o toString da superclasse (Usuario)
               '}';
    }
}
