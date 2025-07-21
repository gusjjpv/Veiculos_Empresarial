package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class VeiculoRepository {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void salvar(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (placa, modelo, marca, ano, cor, quilometragem, status, ultima_data_revisao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, veiculo.getPlaca());
            pstmt.setString(2, veiculo.getModelo());
            pstmt.setString(3, veiculo.getMarca());
            pstmt.setInt(4, veiculo.getAno());
            pstmt.setString(5, veiculo.getCor());
            pstmt.setDouble(6, veiculo.getQuilometragemAtual());
            pstmt.setString(7, veiculo.getStatus().name());

            if (veiculo.getUltimaDataDeRevisao() != null) {
                pstmt.setString(8, sdf.format(veiculo.getUltimaDataDeRevisao()));
            } else {
                pstmt.setNull(8, Types.VARCHAR);
            }

            pstmt.executeUpdate();

             try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    veiculo.setId(rs.getInt(1));
                }
            }
            System.out.println("Veículo com placa '" + veiculo.getPlaca() + "' salvo com sucesso!");

        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                System.err.println("Erro: A placa '" + veiculo.getPlaca() + "' já está cadastrada.");
            } else {
                System.err.println("Erro ao salvar veículo: " + e.getMessage());
            }
        }
    }
    public Veiculo buscarPorId(int id, Connection conn) {
        String sql = "SELECT * FROM veiculos WHERE id = ?";
        
        if (conn == null) {
            try (Connection connection = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return criarVeiculoDoResultSet(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao procurar veículo por ID: " + e.getMessage());
            }
        } else {
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return criarVeiculoDoResultSet(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao procurar veículo por ID: " + e.getMessage());
            }
        }
        return null;
    }


      public Veiculo buscarVeiculoPorPlaca(String placa) {
        String sql = "SELECT * FROM veiculos WHERE placa = ?;";
        Veiculo veiculo = null;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, placa);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // CONSERTADO AQUI: Chamando o método auxiliar para criar o objeto
                    veiculo = criarVeiculoDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículo por placa: " + e.getMessage());
        }
        return veiculo;
    }


    private Veiculo criarVeiculoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id"); 
        String placa = rs.getString("placa");
        String modelo = rs.getString("modelo");
        String marca = rs.getString("marca");
        int ano = rs.getInt("ano"); 
        String cor = rs.getString("cor");
        double quilometragemAtual = rs.getDouble("quilometragem"); 
        StatusVeiculo status = StatusVeiculo.valueOf(rs.getString("status"));
        
        Date ultimaDataDeRevisao = null;
        try {
            java.sql.Date sqlDate = rs.getDate("ultima_data_revisao");
            if (sqlDate != null) {
                ultimaDataDeRevisao = new Date(sqlDate.getTime());
            }
        } catch (SQLException e) {
            try {
                long timestamp = rs.getLong("ultima_data_revisao");
                if (!rs.wasNull()) {
                    ultimaDataDeRevisao = new Date(timestamp);
                }
            } catch (SQLException e2) {
                ultimaDataDeRevisao = null;
            }
        }

        return new Veiculo(id, placa, modelo, marca, ano, cor, status, quilometragemAtual, ultimaDataDeRevisao);
    }

    public boolean atualizar(Veiculo veiculo) {
        String sql = "UPDATE veiculos SET modelo = ?, marca = ?, ano = ?, cor = ?, quilometragem = ?, status = ?, ultima_data_revisao = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, veiculo.getModelo());
            pstmt.setString(2, veiculo.getMarca());
            pstmt.setInt(3, veiculo.getAno());
            pstmt.setString(4, veiculo.getCor());
            pstmt.setDouble(5, veiculo.getQuilometragemAtual());
            pstmt.setString(6, veiculo.getStatus().name());

            if (veiculo.getUltimaDataDeRevisao() != null) {
                pstmt.setString(7, sdf.format(veiculo.getUltimaDataDeRevisao()));
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }
            pstmt.setInt(8, veiculo.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar veículo: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM veiculos WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar veículo: " + e.getMessage());
            return false;
        }
    }

    public List<Veiculo> findAll() {
        String sql = "SELECT * FROM veiculos;";
        List<Veiculo> veiculos = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                veiculos.add(criarVeiculoDoResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar veículos: " + e.getMessage());
        }
        return veiculos;
    }

    public List<Veiculo> listarVeiculosDisponiveis() {
        String sql = "SELECT * FROM veiculos WHERE status = ?;";
        List<Veiculo> veiculos = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, StatusVeiculo.DISPONIVEL.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(criarVeiculoDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar veículos disponíveis: " + e.getMessage());
        }
        return veiculos;
    }
}
