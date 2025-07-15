package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.Manutencao;
import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoRepository {
    public void salvar(Manutencao manutencao) {
        String sql = "INSERT INTO manutencoes (veiculo_id, descricao_servico, data_inicio, data_saida_prevista, nome_oficina) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (manutencao.getVeiculo() == null || manutencao.getVeiculo().getId() == 0) {
                System.err.println("Erro: A manutenção deve estar associada a um veículo com ID válido.");
                return;
            }

            pstmt.setInt(1, manutencao.getVeiculo().getId());
            pstmt.setString(2, manutencao.getDescricaoServico());
            
            // Converte LocalDate para java.sql.Date
            pstmt.setDate(3, java.sql.Date.valueOf(manutencao.getDataEntrada()));
            
            if (manutencao.getDataSaidaPrevista() != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(manutencao.getDataSaidaPrevista()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setString(5, manutencao.getNomeOficina());

            pstmt.executeUpdate();
            System.out.println("Manutenção para o veículo '" + manutencao.getVeiculo().getPlaca() + "' guardada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao guardar manutenção: " + e.getMessage());
        }
    }

    public void atualizar(Manutencao manutencao) {
        String sql = "UPDATE manutencoes SET data_saida_real = ?, custo_real = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (manutencao.getDataSaidaReal() != null) {
                pstmt.setDate(1, java.sql.Date.valueOf(manutencao.getDataSaidaReal()));
            } else {
                pstmt.setNull(1, Types.DATE);
            }
            
            pstmt.setDouble(2, manutencao.getCustoReal());
            pstmt.setInt(3, manutencao.getId());

            pstmt.executeUpdate();
            System.out.println("Manutenção ID " + manutencao.getId() + " atualizada com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar manutenção: " + e.getMessage());
        }
    }

    public List<Manutencao> listarPorVeiculo(int veiculoId) {
        String sql = "SELECT * FROM manutencoes WHERE veiculo_id = ?";
        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, veiculoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    manutencoes.add(criarManutencaoDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar manutenções por veículo: " + e.getMessage());
        }
        return manutencoes;
    }

    public List<Manutencao> listarTodas() {
        String sql = "SELECT * FROM manutencoes";
        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                manutencoes.add(criarManutencaoDoResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar manutenções: " + e.getMessage());
        }
        return manutencoes;
    }

    private Manutencao criarManutencaoDoResultSet(ResultSet rs) throws SQLException {
        // Assume que VeiculoRepository tem um método buscarPorId(int id)
        VeiculoRepository veiculoRepo = new VeiculoRepository(); 
        Veiculo veiculo = veiculoRepo.buscarPorId(rs.getInt("veiculo_id"));

        // Converte de java.sql.Date (vindo da BD) para LocalDate
        Date sqlDataEntrada = rs.getDate("data_inicio");
        LocalDate dataEntrada = (sqlDataEntrada != null) ? sqlDataEntrada.toLocalDate() : null;
        
        Date sqlDataSaidaPrevista = rs.getDate("data_saida_prevista");
        LocalDate dataSaidaPrevista = (sqlDataSaidaPrevista != null) ? sqlDataSaidaPrevista.toLocalDate() : null;

        Date sqlDataSaidaReal = rs.getDate("data_saida_real");
        LocalDate dataSaidaReal = (sqlDataSaidaReal != null) ? sqlDataSaidaReal.toLocalDate() : null;

        Manutencao manutencao = new Manutencao(
                veiculo,
                rs.getString("descricao_servico"),
                dataEntrada,
                dataSaidaPrevista,
                rs.getString("nome_oficina")
        );
        
        manutencao.setId(rs.getInt("id"));
        manutencao.setDataSaidaReal(dataSaidaReal);
        manutencao.setCustoReal(rs.getDouble("custo_real"));

        return manutencao;
    }
}
