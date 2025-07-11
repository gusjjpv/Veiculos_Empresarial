package main.java.com.devShow.Veiculos_Empresarial.repository;
 import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class MotoristaRepository {
    //salvar motorista no banco
    public void salvar(Motorista motorista) {
        String sqlUsuario = "INSERT INTO usuarios (nome, user_name, senha, tipo) VALUES (?, ?, ?, ?)";
        String sqlMotorista = "INSERT INTO motoristas (setor, cnh, usuario_id) VALUES (?, ?, ?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        long usuarioId = -1;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                pstmtUsuario.setString(1, motorista.getNome());
                pstmtUsuario.setString(2, motorista.getUsername());
                pstmtUsuario.setString(3, motorista.getSenha());
                pstmtUsuario.setString(4, "FUNCIONARIO");

                int affectedRows = pstmtUsuario.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmtUsuario.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            usuarioId = generatedKeys.getLong(1);
                        }
                    }
                }
            }

            if (usuarioId == -1) {
                throw new SQLException("Falha ao criar o usuário, ID não obtido.");
            }

            try (PreparedStatement pstmtMotorista = conn.prepareStatement(sqlMotorista)) {
                pstmtMotorista.setString(1, motorista.getSetor());
                pstmtMotorista.setString(2, motorista.getCnh());
                pstmtMotorista.setLong(3, usuarioId);
                pstmtMotorista.executeUpdate();
            }

            conn.commit();
            System.out.println(
                    "Motorista '" + motorista.getNome() + "' salvo com sucesso (ID do usuário: " + usuarioId + ")");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar motorista, revertendo a transação: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro crítico ao reverter a transação: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException finalEx) {
                System.err.println("Erro ao restaurar auto-commit: " + finalEx.getMessage());
            }
        }
    }
}
