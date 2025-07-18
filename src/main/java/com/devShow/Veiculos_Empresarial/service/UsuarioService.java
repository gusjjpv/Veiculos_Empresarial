package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.repository.UsuarioRepository;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class UsuarioService {
    MotoristaService motortistaService = new MotoristaService();
    UsuarioRepository usuarioRepository = new UsuarioRepository();
    VeiculoService veiculoService = new VeiculoService();
    ManutencaoService manutencaoService = new ManutencaoService();

    public void cadastrarUsuario(String nome, String username, String senha, boolean ehAdm){
        Usuario usuarioExistente = usuarioRepository.buscarPorUsername(username);
        if(usuarioExistente != null){
            System.err.println("ERRO no Cadastro: usuario " + username + " ja existe");
            return;
        }

        Usuario novoUsuario = new Usuario(nome, username, senha, ehAdm);
        usuarioRepository.salvar(novoUsuario);
    }


    public Usuario autenticar(String username, String senha){
        Usuario usuarioEncontrado = usuarioRepository.buscarPorUsername(username);
        if(usuarioEncontrado == null){
            System.out.println("Falha na autenticação: Usuário '" + username + "' não existe.");
            return null;
        }

        if(usuarioEncontrado.getSenha().equals(senha)){
            System.out.println("Autenticacao bem-sucedida:" + username);
            return usuarioEncontrado;
        }else{
            System.out.println("Falha na autenticacao: senha incorreta.");
            return null;
        }
    }

    //metodos de motorista
    public void cadastrarMotorista(Usuario admin, String nome, String username, String senha, String setor, String cnh){
        if(!admin.getEhAdm()){
            return;
        }
        motortistaService.cadastrarMotorista(nome, username, senha, setor,cnh);
    }


    public void editarMotorista(Usuario admin, String novoNome, String novoUsername, String novaSenha, String novoSetor, String cnh){
        if(!admin.getEhAdm()){
            return;
        }

        motortistaService.atualizarDadosDeMotorista(cnh, novoNome, novoSetor, novoUsername, novaSenha); 
    }


    public void listarMotoristas(Usuario admin){
        if(!admin.getEhAdm()){
            return;
        }
        List<Motorista> listaMotoristas = motortistaService.listarTodosMotoristas();
        for(Motorista m: listaMotoristas){
            System.out.println(m);
        }
    }


    public void excluirMotorista(Usuario admin, String cnhParaRemover){
        if(!admin.getEhAdm()){
            return;
        }

        motortistaService.excluirMotorista(cnhParaRemover);
    }

    //metodos para veiculos
    public boolean adicionarVeiculos(Usuario admin, String placa, String modelo, String marca, int ano, String cor, double quilometragem){
        if(!admin.getEhAdm()){
            return false;
        }
        veiculoService.cadastrarVeiculo(placa, modelo, marca, ano, cor, quilometragem);
        return true;
    }


    public void editarVeiculo(Usuario admin,String placaParaEditar, String novoModelo, String novaMarca, int novoAno, String novaCor){
        if(!admin.getEhAdm()){
            return;
        }
        veiculoService.atualizarDadosBasicos(placaParaEditar, novoModelo, novaMarca, novoAno, novaCor);
    }


    public void removerVeiculo(Usuario admin, String placaParaRemover){
        if(!admin.getEhAdm()){
            return;
        }

        veiculoService.excluirVeiculo(placaParaRemover);
    }

    //metodos de manutencao
    public List<Manutencao> listarManutencao(Usuario admin){
        if(!admin.getEhAdm()){
            return new ArrayList<>();
        }

        return manutencaoService.listarTodas();
    }

    
    public void iniciarManutencao(Usuario admin, String placaVeiculo, String descricaoServico, String nomeOficina, Date dataSaidaPrevista) {
        if (admin == null || !admin.getEhAdm()) {
            System.err.println("ACESSO NEGADO: Apenas administradores podem iniciar manutenções.");
            return;
        }

        manutencaoService.iniciarManutencao(placaVeiculo, descricaoServico, nomeOficina, dataSaidaPrevista);
    }


    public void concluirManutencao(Usuario admin, String placa, double custoReal){
        if(!admin.getEhAdm()){
            System.err.println("ACESSO NEGADO: Apenas administradores podem iniciar manutenções.");
            return;
        }

        Date horaSaida = new Date();

        manutencaoService.concluirManutencao(placa, horaSaida, custoReal);
    }

    //metodos de registroUso



}