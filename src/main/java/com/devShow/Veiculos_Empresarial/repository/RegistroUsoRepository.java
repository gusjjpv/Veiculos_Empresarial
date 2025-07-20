package main.java.com.devShow.Veiculos_Empresarial.repository;

import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.RegistroUso;
import main.java.com.devShow.Veiculos_Empresarial.model.Veiculo;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import main.java.com.devShow.Veiculos_Empresarial.model.Usuario; // Necessário para o atributo Usuario em RegistroUso

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // Para setar NULL em campos INTEGER

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

    /**
     * Salva um novo registro de uso no banco de dados.
     * O ID do registro será gerado automaticamente pelo banco e setado no objeto.
     * Assume que veiculo.getId(), motorista.getId() e registro.getUsuario().getId() já estão preenchidos
     * nos objetos Veiculo, Motorista e Usuario antes de chamar este método.
     * @param registro O objeto RegistroUso a ser salvo.
     * @return O ID gerado para o registro, ou -1 em caso de falha.
     */
    public int salvar(RegistroUso registro) {
        // SQL alinhado com DatabaseConnection
        String sql = "INSERT INTO registros_uso(veiculo_id, motorista_id, usuario_id, data_inicio, quilometragem_inicial, destino_ou_finalidade) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        int idGerado = -1;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            
            pstmt.setInt(1, registro.getVeiculo().getId()); 
            pstmt.setInt(2, registro.getMotorista().getId()); 
            pstmt.setInt(3, registro.getUsuario().getId()); 
            pstmt.setLong(4, registro.getDataHoraSaida().getTime()); 
            pstmt.setDouble(5, registro.getKmSaida()); // kmSaida no modelo -> quilometragem_inicial no DB
            pstmt.setString(6, registro.getDestinoOuFinalidade()); // destinoOuFinalidade no modelo -> destino_ou_finalidade no DB

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGerado = rs.getInt(1);
                        registro.setId(idGerado); // Seta o ID gerado no objeto RegistroUso
                        System.out.println("Registro de uso salvo com ID: " + idGerado);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar registro de uso: " + e.getMessage());
        }
        return idGerado;
    }

    /**
     * Atualiza um registro de uso existente no banco de dados.
     * Usado principalmente para finalizar um registro, preenchendo data_fim e quilometragem_final.
     * @param registro O objeto RegistroUso com os dados atualizados (especialmente ID, dataHoraRetorno e kmRetorno).
     * @return true se o registro foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizar(RegistroUso registro) {
        // SQL alinhado com DatabaseConnection: usa data_fim e quilometragem_final
        String sql = "UPDATE registros_uso SET data_fim = ?, quilometragem_final = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define data_fim: se for null no objeto, seta SQL NULL (INTEGER para timestamp)
            if (registro.getDataHoraRetorno() != null) { // dataHoraRetorno no modelo -> data_fim no DB
                pstmt.setLong(1, registro.getDataHoraRetorno().getTime());
            } else {
                pstmt.setNull(1, Types.INTEGER); // Tipo INTEGER para timestamp
            }
            // Define quilometragem_final
            pstmt.setDouble(2, registro.getKmRetorno()); // kmRetorno no modelo -> quilometragem_final no DB
            // Define o ID para a cláusula WHERE
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

    /**
     * Busca um registro de uso pelo seu ID.
     * @param id O ID do registro de uso a ser buscado.
     * @return O objeto RegistroUso encontrado, ou null se não existir.
     */
    public RegistroUso buscarPorId(int id, Connection conn) {
        // SQL alinhado com DatabaseConnection: seleciona todas as colunas de registros_uso
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

    /**
     * Método auxiliar para criar um objeto RegistroUso a partir de um ResultSet.
     * Centraliza a lógica de criação dos objetos RegistroUso a partir dos dados do banco.
     * @param rs O ResultSet contendo os dados do registro de uso.
     * @return O objeto RegistroUso criado, ou null em caso de erro.
     */
    private RegistroUso criarRegistroUsoDoResultSet(ResultSet rs) throws SQLException {
        // Lê todos os dados do ResultSet primeiro
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

        // Busca os objetos completos usando seus repositórios (por ID)
        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId, null);
        Motorista motorista = motoristaRepository.buscarPorId(motoristaId);
        // O objeto Usuario completo virá dentro do objeto Motorista
        Usuario usuario = (motorista != null) ? motorista.getUsuario() : null;

        // Converte timestamps (INTEGER) do banco de dados de volta para objetos Date
        Date dataHoraSaida = new Date(dataInicio);
        Date dataHoraRetorno = (dataFimLong != null) ? new Date(dataFimLong) : null;

        // Adiciona o registro apenas se os objetos relacionados foram encontrados
        if (veiculo != null && motorista != null && usuario != null) {
            return new RegistroUso(
                id,                    // ID do registro
                veiculo,              // Objeto Veiculo completo
                motorista,            // Objeto Motorista completo
                usuario,              // Objeto Usuario completo
                dataHoraSaida,        // data_inicio do DB -> dataHoraSaida no modelo
                dataHoraRetorno,      // data_fim do DB -> dataHoraRetorno no modelo
                quilometragemInicial, // quilometragem_inicial do DB -> kmSaida no modelo
                quilometragemFinal,   // quilometragem_final do DB -> kmRetorno no modelo
                destinoOuFinalidade   // destino_ou_finalidade do DB -> destinoOuFinalidade no modelo
            );
        } else {
            System.err.println("Aviso: Registro de uso com ID " + id + " não pôde ser totalmente carregado devido a Veículo, Motorista ou Usuário ausente.");
            return null;
        }
    }

    /**
     * Lista todos os registros de uso no banco de dados.
     * @return Uma lista de todos os objetos RegistroUso.
     */
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

    /**
     * Exclui um registro de uso do banco de dados pelo seu ID.
     * @param id O ID do registro de uso a ser excluído.
     * @return true se o registro foi excluído com sucesso, false caso contrário.
     */
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

    /**
     * Verifica se existem registros de uso associados a um ID de veículo.
     * Usado para validações na camada de serviço (ex: não excluir veículo com registros).
     * @param veiculoId O ID do veículo.
     * @return true se existirem registros de uso para o ID do veículo, false caso contrário.
     */
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

    /**
     * Verifica se existem registros de uso associados a um ID de motorista.
     * Usado para validações na camada de serviço (ex: não excluir motorista com registros).
     * @param motoristaId O ID do motorista.
     * @return true se existirem registros de uso para o ID do motorista, false caso contrário.
     */
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

    /**
     * Verifica se existe um registro de uso em andamento (não finalizado) para um ID de veículo.
     * Usado para evitar iniciar um novo uso se o veículo já estiver em um registro aberto.
     * @param veiculoId O ID do veículo.
     * @return true se existir um registro de uso não finalizado para o ID do veículo, false caso contrário.
     */
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
