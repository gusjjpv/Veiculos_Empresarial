package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.repository.MotoristaRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.RegistroUsoRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.VeiculoRepository;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import java.util.List;

/**
 * Service para operações relacionadas a Motoristas.
 * Contém a lógica de negócio para gerenciamento de motoristas.
 */
public class MotoristaService {
    
    private MotoristaRepository motoristaRepository;
    private RegistroUsoRepository registroUsoRepository;
    private VeiculoRepository veiculoRepository; // Necessário para o construtor do RegistroUsoRepository

    // Crie um construtor para inicializar as dependências
    public MotoristaService() {
        this.motoristaRepository = new MotoristaRepository();
        this.veiculoRepository = new VeiculoRepository();
        
        // Agora, inicialize o RegistroUsoRepository passando suas dependências
        this.registroUsoRepository = new RegistroUsoRepository(this.veiculoRepository, this.motoristaRepository);
    }

    /**
     * Cadastra um novo motorista no sistema.
     * Valida dados e verifica se a CNH já existe.
     * 
     * @param nome Nome completo do motorista
     * @param userName Nome de usuário único
     * @param senha Senha do motorista
     * @param setor Setor onde trabalha
     * @param cnh CNH do motorista
     * @return true se cadastrou com sucesso, false caso contrário
     */
    public boolean cadastrarMotorista(String nome, String userName, String senha, String setor, String cnh) {
        try {
            // Validações de entrada
            validarDadosMotorista(nome, userName, senha, setor, cnh);
            
            // Verifica se a CNH já existe
            if (motoristaRepository.buscarPorCnh(cnh) != null) {
                System.err.println("❌ ERRO: CNH " + cnh + " já está cadastrada");
                return false;
            }
            
            // Cria novo motorista
            Motorista novoMotorista = new Motorista(nome, userName, senha, setor, cnh);
            motoristaRepository.salvar(novoMotorista);
            
            System.out.println("✅ Motorista cadastrado com sucesso!");
            System.out.println("   Nome: " + nome);
            System.out.println("   CNH: " + cnh);
            System.out.println("   Setor: " + setor);
            System.out.println("   Username: " + userName);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar motorista: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Atualiza dados de um motorista existente.
     * 
     * @param cnhDoMotorista CNH do motorista a ser atualizado
     * @param novoNome Novo nome
     * @param novoSetor Novo setor
     * @param novoUsername Novo username
     * @param novaSenha Nova senha
     * @return true se atualizou com sucesso, false caso contrário
     */
    public boolean atualizarDadosDeMotorista(String cnhDoMotorista, String novoNome, String novoSetor, String novoUsername, String novaSenha) {
        try {
            Motorista motoristaParaAtualizar = motoristaRepository.buscarPorCnh(cnhDoMotorista);

            if (motoristaParaAtualizar == null) {
                System.err.println("❌ ERRO: Motorista com CNH " + cnhDoMotorista + " não encontrado. Atualização falhou");
                return false;
            }

            // Validações dos novos dados
            validarDadosMotorista(novoNome, novoUsername, novaSenha, novoSetor, cnhDoMotorista);

            System.out.println("Dados antigos: " + motoristaParaAtualizar);
            
            motoristaParaAtualizar.setNome(novoNome);
            motoristaParaAtualizar.setUsername(novoUsername);
            motoristaParaAtualizar.setSetor(novoSetor);
            motoristaParaAtualizar.setSenha(novaSenha);
            
            System.out.println("Dados novos: " + motoristaParaAtualizar);

            motoristaRepository.atualizar(motoristaParaAtualizar);
            
            System.out.println("✅ Motorista atualizado com sucesso!");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar motorista: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca um motorista por CNH.
     * 
     * @param cnh CNH do motorista
     * @return Motorista encontrado ou null se não existir
     */
    public Motorista buscarMotorista(String cnh) {
        if (cnh == null || cnh.trim().isEmpty()) {
            System.err.println("❌ CNH é obrigatória para busca");
            return null;
        }
        
        Motorista motorista = motoristaRepository.buscarPorCnh(cnh);
        
        if (motorista == null) {
            System.err.println("❌ Motorista com CNH " + cnh + " não encontrado");
        }
        
        return motorista;
    }

    /**
     * Busca um motorista por ID.
     * 
     * @param id ID do motorista
     * @return Motorista encontrado ou null se não existir
     */
    public Motorista buscarMotoristaPorId(int id) {
        return motoristaRepository.buscarPorId(id);
    }

    /**
     * Lista todos os motoristas cadastrados.
     * 
     * @return Lista de todos os motoristas
     */
    public List<Motorista> listarTodosMotoristas() {
        List<Motorista> listaMotoristas = motoristaRepository.listarTodos();
        
        if (listaMotoristas.isEmpty()) {
            System.out.println("ℹ️ Nenhum motorista cadastrado no sistema");
        } else {
            System.out.println("📋 Total de motoristas: " + listaMotoristas.size());
        }
        
        return listaMotoristas;
    }
    
    public void excluirMotorista(String cnh) {
        Motorista motorista = motoristaRepository.buscarPorCnh(cnh);
        if (motorista == null) {
            System.err.println("ERRO: Motorista com CNH " + cnh + " não encontrado.");
            return;
        }

        boolean temRegistros = registroUsoRepository.existsByMotoristaId(motorista.getId());

        if (temRegistros) {
            System.err.println("ERRO: O motorista '" + motorista.getNome() + "' não pode ser excluído, pois possui registros de uso associados.");
            return;
        }

        System.out.println("SERVICE: Motorista pode ser excluído. Solicitando remoção ao repositório...");
        motoristaRepository.remover(cnh);
    }
    
    /**
     * Verifica se um motorista está ativo.
     * 
     * @param cnh CNH do motorista
     * @return true se está ativo, false caso contrário
     */
    public boolean motoristaEstaAtivo(String cnh) {
        Motorista motorista = buscarMotorista(cnh);
        return motorista != null && motorista.isAtivo();
    }
    
    /**
     * Valida as credenciais de login de um motorista.
     * 
     * @param userName Username do motorista
     * @param senha Senha do motorista
     * @return Motorista se credenciais válidas, null caso contrário
     */
    public Motorista validarLoginMotorista(String userName, String senha) {
        try {
            if (userName == null || userName.trim().isEmpty()) {
                throw new IllegalArgumentException("Username é obrigatório");
            }
            
            if (senha == null || senha.trim().isEmpty()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            
            // Busca por todos os motoristas e verifica credenciais
            List<Motorista> motoristas = motoristaRepository.listarTodos();
            
            for (Motorista motorista : motoristas) {
                if (userName.equals(motorista.getUsername()) && senha.equals(motorista.getSenha())) {
                    if (!motorista.isAtivo()) {
                        System.err.println("❌ Motorista está desativado");
                        return null;
                    }
                    
                    System.out.println("✅ Login de motorista realizado com sucesso!");
                    System.out.println("   Bem-vindo, " + motorista.getNome() + "!");
                    System.out.println("   CNH: " + motorista.getCnh());
                    System.out.println("   Setor: " + motorista.getSetor());
                    
                    return motorista;
                }
            }
            
            System.err.println("❌ Credenciais inválidas");
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Erro no login do motorista: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Gera estatísticas de motoristas.
     * 
     * @return String com estatísticas formatadas
     */
    public String gerarEstatisticasMotoristas() {
        List<Motorista> todosMotoristas = motoristaRepository.listarTodos();
        
        long motoristasAtivos = todosMotoristas.stream().filter(Motorista::isAtivo).count();
        long motoristasInativos = todosMotoristas.size() - motoristasAtivos;
        
        // Conta motoristas por setor
        var motoristasPorSetor = todosMotoristas.stream()
            .filter(Motorista::isAtivo)
            .collect(java.util.stream.Collectors.groupingBy(
                Motorista::getSetor,
                java.util.stream.Collectors.counting()
            ));
        
        StringBuilder stats = new StringBuilder();
        stats.append("🚛 ESTATÍSTICAS DE MOTORISTAS\n");
        stats.append("════════════════════════════\n");
        stats.append("Total de motoristas: ").append(todosMotoristas.size()).append("\n");
        stats.append("Motoristas ativos: ").append(motoristasAtivos).append("\n");
        stats.append("Motoristas inativos: ").append(motoristasInativos).append("\n");
        stats.append("\n");
        stats.append("Por setor:\n");
        
        if (motoristasPorSetor.isEmpty()) {
            stats.append("  Nenhum motorista ativo cadastrado\n");
        } else {
            motoristasPorSetor.forEach((setor, quantidade) -> 
                stats.append("  ").append(setor).append(": ").append(quantidade).append("\n")
            );
        }
        
        return stats.toString();
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Valida os dados de um motorista.
     */
    private void validarDadosMotorista(String nome, String userName, String senha, String setor, String cnh) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (userName.length() < 3) {
            throw new IllegalArgumentException("Username deve ter pelo menos 3 caracteres");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres");
        }
        
        if (setor == null || setor.trim().isEmpty()) {
            throw new IllegalArgumentException("Setor é obrigatório");
        }
        
        if (cnh == null || cnh.trim().isEmpty()) {
            throw new IllegalArgumentException("CNH é obrigatória");
        }
        
        if (cnh.length() != 11) {
            throw new IllegalArgumentException("CNH deve ter 11 dígitos");
        }
        
        // Verifica se CNH contém apenas números
        if (!cnh.matches("\\d{11}")) {
            throw new IllegalArgumentException("CNH deve conter apenas números");
        }
    }
}
