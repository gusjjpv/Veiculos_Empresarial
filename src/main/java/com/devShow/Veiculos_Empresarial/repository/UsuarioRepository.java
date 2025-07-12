package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.model.Usuario;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRepository {
    // salvar usuario no banco
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, user_name, senha, tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getUsername());
            pstmt.setString(3, usuario.getSenha());

            String tipo = usuario.getEhAdm() ? "ADMIN" : "FUNCIONARIO";
            pstmt.setString(4, tipo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                System.err.println("Erro: O nome de usuário '" + usuario.getUsername() + "' já existe.");
            } else {
                System.err.println("Erro ao salvar usuário: " + e.getMessage());
            }
        }
    }

    // busca por nome
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE user_name = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nomeDb = rs.getString("nome");
                    String usernameDb = rs.getString("user_name");
                    String senhaDb = rs.getString("senha");
                    String tipoDb = rs.getString("tipo");

                    boolean ehAdmin = tipoDb.equals("ADMIN");

                    usuario = new Usuario(nomeDb, usernameDb, senhaDb, ehAdmin);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return usuario;
    }

}
