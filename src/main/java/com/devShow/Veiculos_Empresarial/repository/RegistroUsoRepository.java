package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.RegistroUso;
import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import main.java.com.devShow.Veiculos_Empresarial.model.Usuario;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistroUsoRepository {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private VeiculoRepository veiculoRepository;
    private MotoristaRepository motoristaRepository;
    // private UsuarioRepository usuarioRepository; // Descomentar e injetar se tiver um UsuarioRepository separado

    public RegistroUsoRepository(VeiculoRepository veiculoRepository, MotoristaRepository motoristaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.motoristaRepository = motoristaRepository;
        // this.usuarioRepository = usuarioRepository; // Atribuir se injetado
    }

    public int salvar(RegistroUso registro) {
        String sql = "INSERT INTO registros_uso(veiculo_id, motorista_id, usuario_id, data_inicio, quilometragem_inicial, destino_ou_finalidade) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        int idGerado = -1;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            
            pstmt.setInt(1, registro.getVeiculo().getId()); 
            pstmt.setInt(2, registro.getMotorista().getId()); 
            pstmt.setInt(3, registro.getUsuario().getId()); 
            pstmt.setString(4, sdf.format(registro.getDataHoraSaida())); 
            pstmt.setDouble(5, registro.getKmSaida());
            pstmt.setString(6, registro.getDestinoOuFinalidade());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGerado = rs.getInt(1);
                        registro.setId(idGerado);
                        System.out.println("Registro de uso salvo com ID: " + idGerado);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar registro de uso: " + e.getMessage());
        }
        return idGerado;
    }

    public boolean atualizar(RegistroUso registro) {
        String sql = "UPDATE registros_uso SET data_fim = ?, quilometragem_final = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (registro.getDataHoraRetorno() != null) {
                pstmt.setString(1, sdf.format(registro.getDataHoraRetorno()));
            } else {
                pstmt.setNull(1, Types.VARCHAR); 
            }
            pstmt.setDouble(2, registro.getKmRetorno());
            pstmt.setInt(3, registro.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registro de uso ID " + registro.getId() + " atualizado com sucesso.");
            } else {
                System.out.println("Nenhum registro de uso encontrado com ID " + registro.getId() + " para atualização.");
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar registro de uso: " + e.getMessage());
            return false;
        }
    }

    public RegistroUso buscarPorId(int id, Connection conn) {
        String sql = "SELECT * FROM registros_uso WHERE id = ?";
        RegistroUso registro = null;

        // Se não foi passada uma conexão, cria uma nova
        if (conn == null) {
            try (Connection connection = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        registro = criarRegistroUsoDoResultSet(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar registro de uso por ID: " + e.getMessage());
            }
        } else {
            // Usa a conexão fornecida
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        registro = criarRegistroUsoDoResultSet(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar registro de uso por ID: " + e.getMessage());
            }
        }
        return registro;
    }

    private RegistroUso criarRegistroUsoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int veiculoId = rs.getInt("veiculo_id");
        int motoristaId = rs.getInt("motorista_id");
        
        double quilometragemInicial = rs.getDouble("quilometragem_inicial");
        double quilometragemFinal = rs.getDouble("quilometragem_final");
        String destinoOuFinalidade = rs.getString("destino_ou_finalidade");


        Date dataHoraSaida = null;
        Date dataHoraRetorno = null;
        try {
            if (rs.getString("data_inicio") != null) dataHoraSaida = sdf.parse(rs.getString("data_inicio"));
            if (rs.getString("data_fim") != null) dataHoraRetorno = sdf.parse(rs.getString("data_fim"));
        } catch (ParseException e) {
            System.err.println("Erro ao converter data do registo de uso ID " + id + ": " + e.getMessage());
        }

        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId, null);
        Motorista motorista = motoristaRepository.buscarPorId(motoristaId);
        Usuario usuario = (motorista != null) ? motorista.getUsuario() : null;

        if (veiculo != null && motorista != null && usuario != null) {
            return new RegistroUso(
                id,
                veiculo,
                motorista,
                usuario,
                dataHoraSaida,
                dataHoraRetorno, 
                quilometragemInicial,
                quilometragemFinal,
                destinoOuFinalidade
            );
        } else {
            // Diagnóstico detalhado do problema
            StringBuilder problemas = new StringBuilder();
            if (veiculo == null) problemas.append("Veículo ID ").append(veiculoId).append(" não encontrado; ");
            if (motorista == null) problemas.append("Motorista ID ").append(motoristaId).append(" não encontrado; ");
            if (usuario == null && motorista != null) problemas.append("Usuário do motorista ausente; ");
            
            System.err.println("⚠️ Registro órfão ID " + id + ": " + problemas.toString());
            return null;
        }
    }
    
    /**
     * Busca um registro por ID de forma mais robusta, incluindo informações básicas mesmo se dados relacionados estiverem ausentes
     */
    public RegistroUso buscarPorIdRobusto(int id) {
        String sql = "SELECT * FROM registros_uso WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Primeiro tenta o método normal
                    RegistroUso registro = criarRegistroUsoDoResultSet(rs);
                    if (registro != null) {
                        return registro;
                    }
                    
                    // Se falhou, cria um registro básico para permitir operações como exclusão
                    return criarRegistroBasico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar registro de uso por ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Cria um registro básico apenas com os dados essenciais, sem validar referências
     */
    private RegistroUso criarRegistroBasico(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int veiculoId = rs.getInt("veiculo_id");
        int motoristaId = rs.getInt("motorista_id");
        
        double quilometragemInicial = rs.getDouble("quilometragem_inicial");
        double quilometragemFinal = rs.getDouble("quilometragem_final");
        String destinoOuFinalidade = rs.getString("destino_ou_finalidade");

        Date dataHoraSaida = null;
        Date dataHoraRetorno = null;
        try {
            if (rs.getString("data_inicio") != null) dataHoraSaida = sdf.parse(rs.getString("data_inicio"));
            if (rs.getString("data_fim") != null) dataHoraRetorno = sdf.parse(rs.getString("data_fim"));
        } catch (ParseException e) {
            System.err.println("Erro ao converter data do registro básico ID " + id + ": " + e.getMessage());
        }

        // Cria objetos básicos apenas com IDs para permitir operações
        Veiculo veiculo = new Veiculo(veiculoId, "PLACA_AUSENTE", "MODELO_AUSENTE", "MARCA_AUSENTE", 2000, "COR_AUSENTE", 
                                     StatusVeiculo.DISPONIVEL, 0.0, null);
        
        Motorista motorista = new Motorista("MOTORISTA_AUSENTE", "usuario_ausente", "senha", "SETOR_AUSENTE", "CNH_AUSENTE");
        motorista.setId(motoristaId);
        
        Usuario usuario = new Usuario("USUARIO_AUSENTE", "usuario_ausente", "senha", false);
        usuario.setId(motoristaId);
        
        return new RegistroUso(
            id,
            veiculo,
            motorista,
            usuario,
            dataHoraSaida,
            dataHoraRetorno,
            quilometragemInicial,
            quilometragemFinal,
            destinoOuFinalidade
        );
    }

    public List<RegistroUso> listarTodos() {
        String sql = "SELECT * FROM registros_uso";
        List<RegistroUso> registros = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                RegistroUso registro = criarRegistroUsoDoResultSet(rs);
                if (registro != null) {
                    registros.add(registro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os registros de uso: " + e.getMessage());
        }
        return registros;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM registros_uso WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registro de uso ID " + id + " excluído com sucesso.");
            } else {
                System.out.println("Nenhum registro de uso encontrado com ID " + id + " para exclusão.");
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir registro de uso: " + e.getMessage());
            return false;
        }
    }

    public boolean existsByVeiculoId(int veiculoId) {
        String sql = "SELECT COUNT(*) FROM registros_uso WHERE veiculo_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, veiculoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência de registros de uso para veículo ID " + veiculoId + ": " + e.getMessage());
        }
        return false;
    }

    public boolean existsByMotoristaId(int motoristaId) {
        String sql = "SELECT COUNT(*) FROM registros_uso WHERE motorista_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, motoristaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência de registros de uso para motorista ID " + motoristaId + ": " + e.getMessage());
        }
        return false;
    }

    public boolean existsUnfinishedByVeiculoId(int veiculoId) {
        String sql = "SELECT COUNT(*) FROM registros_uso WHERE veiculo_id = ? AND data_fim IS NULL";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, veiculoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar registros de uso não finalizados para veículo ID " + veiculoId + ": " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Exclui um registro de uso pelo ID
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM registros_uso WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao excluir registro de uso: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca registros por ID do motorista
     */
    public List<RegistroUso> buscarPorMotoristaId(int motoristaId) {
        String sql = "SELECT * FROM registros_uso WHERE motorista_id = ?";
        List<RegistroUso> registros = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, motoristaId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistroUso registro = criarRegistroUsoDoResultSet(rs);
                    if (registro != null) {
                        registros.add(registro);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar registros por motorista ID " + motoristaId + ": " + e.getMessage());
        }
        return registros;
    }
    
    /**
     * Busca registros por ID do veículo
     */
    public List<RegistroUso> buscarPorVeiculoId(int veiculoId) {
        String sql = "SELECT * FROM registros_uso WHERE veiculo_id = ?";
        List<RegistroUso> registros = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, veiculoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistroUso registro = criarRegistroUsoDoResultSet(rs);
                    if (registro != null) {
                        registros.add(registro);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar registros por veículo ID " + veiculoId + ": " + e.getMessage());
        }
        return registros;
    }
}
