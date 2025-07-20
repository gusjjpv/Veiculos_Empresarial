package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.repository.MotoristaRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.RegistroUsoRepository;
import main.java.com.devShow.Veiculos_Empresarial.repository.VeiculoRepository;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;
import main.java.com.devShow.Veiculos_Empresarial.model.RegistroUso;
import main.java.com.devShow.Veiculos_Empresarial.model.Usuario;

import java.util.List;

public class MotoristaService {
    
    private MotoristaRepository motoristaRepository;
    private RegistroUsoRepository registroUsoRepository;
    private VeiculoRepository veiculoRepository;
    private VeiculoService veiculoService;

    public MotoristaService() {
        this.motoristaRepository = new MotoristaRepository();
        this.veiculoRepository = new VeiculoRepository();
        this.registroUsoRepository = new RegistroUsoRepository(this.veiculoRepository, this.motoristaRepository);
        this.veiculoService = new VeiculoService();
    }

    public boolean cadastrarMotorista(String nome, String userName, String senha, String setor, String cnh) {
        try {
            validarDadosMotorista(nome, userName, senha, setor, cnh);
            
            if (motoristaRepository.buscarPorCnh(cnh) != null) {
                System.err.println("❌ ERRO: CNH " + cnh + " já está cadastrada");
                return false;
            }
            
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

    public boolean atualizarDadosDeMotorista(String cnhDoMotorista, String novoNome, String novoSetor, String novoUsername, String novaSenha) {
        try {
            Motorista motoristaParaAtualizar = motoristaRepository.buscarPorCnh(cnhDoMotorista);

            if (motoristaParaAtualizar == null) {
                System.err.println("❌ ERRO: Motorista com CNH " + cnhDoMotorista + " não encontrado. Atualização falhou");
                return false;
            }

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


    public boolean iniciarViagem(Motorista motoristaUsuario, String placa, String destino) {
        if (motoristaUsuario == null) {
            System.out.println("❌ Erro: Usuário não é um motorista válido!");
            return false;
        }
        RegistroUso novoRegistro = veiculoService.usarVeiculo(placa, motoristaUsuario, destino);

        return novoRegistro != null;
    }

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

    public Motorista buscarMotoristaPorId(int id) {
        return motoristaRepository.buscarPorId(id);
    }

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

    public boolean motoristaEstaAtivo(String cnh) {
        Motorista motorista = buscarMotorista(cnh);
        return motorista != null && motorista.isAtivo();
    }

    public Motorista validarLoginMotorista(String userName, String senha) {
        try {
            if (userName == null || userName.trim().isEmpty()) {
                throw new IllegalArgumentException("Username é obrigatório");
            }
            
            if (senha == null || senha.trim().isEmpty()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            
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
    // ==================== MÉTODOS AUXILIARES ====================

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

        if (!cnh.matches("\\d{11}")) {
            throw new IllegalArgumentException("CNH deve conter apenas números");
        }
    }
}
