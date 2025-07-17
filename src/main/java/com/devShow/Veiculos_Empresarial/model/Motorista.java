package main.java.com.devShow.Veiculos_Empresarial.model;

public class Motorista extends Usuario {
    private String setor;
    private String cnh;
    private int usuarioId; 
    private boolean ativoMotorista;
    private int id;  
    // esses novos campos sao para compatibilidade com o banco de dados
    // 'usuarioId' é a FK para 'usuarios.id' e 'ativoMotorista' é o campo correspondente na tabela 'motoristas'


    public Motorista(String nome, String username, String senha, String setor, String cnh) {
        super(nome, username, senha, false);
        this.setor = setor;
        this.cnh = cnh;
        this.ativoMotorista = true; // Por padrão, o motorista está ativo
    }

    // NOVO CONSTRUTOR: Para carregar motorista do banco (com IDs e 'ativoMotorista')
    // Recebe os dados completos do motorista, incluindo o ID da tabela 'motoristas' e 'usuario_id'
    public Motorista(int id, String nome, String username, String senha, boolean ehAdm, boolean ativoUsuario, // Dados do Usuario
                     String setor, String cnh, int usuarioId, boolean ativoMotorista) { // Dados do Motorista
        super(id, nome, username, senha, ehAdm, ativoUsuario); // Chama o construtor da superclasse Usuario com seu ID e ativo
        this.id = id; // ID da tabela 'motoristas'
        this.setor = setor;
        this.cnh = cnh;
        this.usuarioId = usuarioId; // FK para 'usuarios.id'
        this.ativoMotorista = ativoMotorista; // Ativo da tabela 'motoristas'
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
