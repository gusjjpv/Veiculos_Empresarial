package main.java.com.devShow.Veiculos_Empresarial.app;
import main.java.com.devShow.Veiculos_Empresarial.service.*;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.Scanner;
import java.util.List;

public class Main{
    public static void main(String[] args){
        DatabaseConnection.getInstance();
        UsuarioService usuarioService = new UsuarioService();
        MotoristaService motoristaService = new MotoristaService();
        Scanner input = new Scanner(System.in);
        String nome, username, senha, setor, cnh;
        int opcao;
        do {
            System.out.print("===== MENU =====\n1. REGISTRO\n2. LOGIN\n>>");
            opcao = input.nextInt();
            input.nextLine();
            if(opcao == 1){
                System.out.println("CADASTRO ADMINISTRAÇÃO\nNOME:");
                nome = input.nextLine();
                System.out.println("USERNAME:");
                username = input.next();
                input.nextLine();
                System.out.println("SENHA:");
                senha = input.next();
                usuarioService.cadastrarUsuario(nome, username, senha);
            }else if(opcao == 2){
                System.out.println("===LOGIN===\nUSERNAME:");
                username = input.next();
                input.nextLine();
                System.out.println("SENHA:");
                senha = input.nextLine();
                Usuario novoLogin = usuarioService.autenticar(username, senha);

                if(novoLogin != null){
                    System.out.println("Login Bem-sucedido!");
                    if(novoLogin.getEhAdm()){
                        menuAdmin(novoLogin);
                    }else{
                        menuMotorista();
                    }
                }
            }
        } while (opcao != 0);
    }

    public static void menu(){
    }

    public static void menuAdmin(Usuario admin){
        int opcao;
        Scanner input = new Scanner(System.in);
        do {
            System.out.println("AREA ADMINISTRATIVA\n1. Gerenciamento de Motoristas\n2. Gerenciar Veiculos\n3. Controlar manutenção\n4. Visualizar Registros de uso");
            opcao = input.nextInt();
            if(opcao == 1){
                menuGerenciamentoDeMotorista(admin);
            }else if(opcao == 2){
                menuGerenciamentoVeiculos();
            }else if(opcao == 3){

            }
        } while (opcao != 0);
        
    }

    
    public static void menuMotorista(){
        
    }


    public static void menuGerenciamentoDeMotorista(Usuario admin){
        int opcao;
        Scanner input = new Scanner(System.in);
        String nome, username, senha, setor, cnh;
        UsuarioService usuarioService = new UsuarioService();
        do {
            System.out.print("Gerenciamento de Motoristas\n1. CADASTRAR NOVO MOTORISTA\n2. EDITAR INFORMAÇÕES\n3. LISTAR MOTORISTAS\n4. REMOVER MOTORISTA\n>>");
            opcao = input.nextInt();
            input.nextLine();
            if(opcao == 1){
                System.out.print("NOME:");
                nome = input.nextLine();
                System.out.print("USERNAME:");
                username = input.next();
                input.nextLine();
                System.out.print("SENHA:");
                senha = input.nextLine();
                System.out.print("SETOR:");
                setor = input.nextLine();
                System.out.print("CNH:");
                cnh = input.next();
                input.nextLine();
                usuarioService.cadastrarMotorista(admin, nome, username, senha, setor, cnh);
            }
        } while (opcao != 0);

    }


    public static void menuGerenciamentoVeiculos(){

    }

    public static void menuControleDeManutencao(){

    }


    public static void menuRegistros(){

    }
}

