package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.RegistroUso;
import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import main.java.com.devShow.Veiculos_Empresarial.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistroUsoRepository {

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
            pstmt.setLong(4, registro.getDataHoraSaida().getTime()); 
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
                pstmt.setLong(1, registro.getDataHoraRetorno().getTime());
            } else {
                pstmt.setNull(1, Types.INTEGER);
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
        return registro;
    }

    private RegistroUso criarRegistroUsoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int veiculoId = rs.getInt("veiculo_id");
        int motoristaId = rs.getInt("motorista_id");
        long dataInicio = rs.getLong("data_inicio");
        
        // Trata data_fim que pode ser NULL
        Long dataFimLong = null;
        if (rs.getObject("data_fim") != null) {
            dataFimLong = rs.getLong("data_fim");
        }
        
        double quilometragemInicial = rs.getDouble("quilometragem_inicial");
        double quilometragemFinal = rs.getDouble("quilometragem_final");
        String destinoOuFinalidade = rs.getString("destino_ou_finalidade");

        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId, null);
        Motorista motorista = motoristaRepository.buscarPorId(motoristaId);
        Usuario usuario = (motorista != null) ? motorista.getUsuario() : null;
        Date dataHoraSaida = new Date(dataInicio);
        Date dataHoraRetorno = (dataFimLong != null) ? new Date(dataFimLong) : null;
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
            System.err.println("Aviso: Registro de uso com ID " + id + " não pôde ser totalmente carregado devido a Veículo, Motorista ou Usuário ausente.");
            return null;
        }
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
}
