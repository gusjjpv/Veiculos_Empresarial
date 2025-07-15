package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.StatusVeiculo;

import java.sql.*;

public class VeiculoRepository {
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
                java.util.Date utilDate = veiculo.getUltimaDataDeRevisao();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                pstmt.setDate(8, sqlDate);
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.executeUpdate();
            System.out.println("Veículo com placa '" + veiculo.getPlaca() + "' salvo com sucesso!");

        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                System.err.println("Erro: A placa '" + veiculo.getPlaca() + "' já está cadastrada.");
            } else {
                System.err.println("Erro ao salvar veículo: " + e.getMessage());
            }
        }
    }
    public Veiculo buscarPorId(int id) {
        String sql = "SELECT * FROM veiculos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return criarVeiculoDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículo por ID: " + e.getMessage());
        }
        return null;
    }

    private Veiculo criarVeiculoDoResultSet(ResultSet rs) throws SQLException {
        String placa = rs.getString("placa");
        String modelo = rs.getString("modelo");
        String marca = rs.getString("marca");
        int ano = rs.getInt("ano");
        String cor = rs.getString("cor");
        StatusVeiculo status = StatusVeiculo.valueOf(rs.getString("status"));
        double quilometragemAtual = rs.getDouble("quilometragem_Atual");
        Date ultimaDataDeRevisao = rs.getDate("ultima_data_revisao");
        Veiculo veiculo = new Veiculo(placa, modelo, marca, ano, cor, status, quilometragemAtual, ultimaDataDeRevisao);
        veiculo.setId(rs.getInt("id"));
        return veiculo;
    }
}