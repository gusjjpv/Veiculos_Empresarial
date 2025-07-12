package main.java.com.devShow.Veiculos_Empresarial.service;
import main.java.com.devShow.Veiculos_Empresarial.repository.MotoristaRepository;
import main.java.com.devShow.Veiculos_Empresarial.model.Motorista;

import java.util.List;

public class MotoristaService {
    private MotoristaRepository motoristaRepository = new MotoristaRepository();

    public void cadastrarMotorista(String nome, String userName, String senha, String setor, String cnh){
        if(motoristaRepository.buscarPorCnh(cnh) != null){
            System.err.println("ERRO: CNH JA CADASTRADA");
            return;
        }

        Motorista novoMotorista = new Motorista(nome, userName, senha, setor, cnh);
        motoristaRepository.salvar(novoMotorista);
    }

    public List<Motorista> listarTodosMotoristas() {
        List<Motorista> listaMotoristas = motoristaRepository.listarTodos();
        return listaMotoristas;
    }

    public void atualizarDadosDeMotorista(String cnhDoMotorista, String novoNome, String novoSetor, String novoUsername, String novaSenha){
        Motorista motoristaParaAtualizar = motoristaRepository.buscarPorCnh(cnhDoMotorista);

        if(motoristaParaAtualizar == null){
            System.err.println("ERRO: Motorista com CNH:" + cnhDoMotorista + " nao encontrado. Atualizacao falhou");
            return;
        }

        System.out.println("Dados antigos: " + motoristaParaAtualizar);
        motoristaParaAtualizar.setNome(novoNome);
        motoristaParaAtualizar.setUsername(novoUsername);
        motoristaParaAtualizar.setSetor(novoSetor);
        motoristaParaAtualizar.setSenha(novaSenha);
        System.out.println("Dados novos: " + motoristaParaAtualizar);

        motoristaRepository.atualizar(motoristaParaAtualizar);
    }
}
