package main.java.com.devShow.Veiculos_Empresarial.service;

import main.java.com.devShow.Veiculos_Empresarial.repository.UsuarioRepository;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.List;

public class UsuarioService {
    MotoristaService motortistaService = new MotoristaService();
    UsuarioRepository usuarioRepository = new UsuarioRepository();


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
    public void adicionarVeiculos(Usuario admin){
        if(!admin.getEhAdm()){
            return;
        }
    }


    public void editarVeiculo(Usuario admin){
        if(!admin.getEhAdm()){
            return;
        }
    }


    public void removerVeiculo(Usuario admin){
        if(!admin.getEhAdm()){
            return;
        }
    }


    //metodos de manutencao


    //metodos de registroUso



}