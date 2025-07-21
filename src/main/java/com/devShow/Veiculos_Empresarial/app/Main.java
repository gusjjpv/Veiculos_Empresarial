package main.java.com.devShow.Veiculos_Empresarial.app;
import main.java.com.devShow.Veiculos_Empresarial.service.*;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;

public class Main{
    private static UsuarioService usuarioService = new UsuarioService();
    private static MotoristaService motoristaService = new MotoristaService();
    private static VeiculoService veiculoService = new VeiculoService();
    private static RegistroUsoService registroUsoService = new RegistroUsoService();
    private static ManutencaoService manutencaoService = new ManutencaoService();
    
    public static void main(String[] args){
        DatabaseConnection.getInstance();
        Scanner input = new Scanner(System.in);
        String nome, username, senha;
        int opcao;
        do {
            limparTela();
            System.out.print("===== MENU =====\n1. REGISTRO\n2. LOGIN\n0. DESLIGAR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            if(opcao == 1){
                limparTela();
                System.out.println("CADASTRO ADMINISTRAÇÃO\nNOME:");
                nome = input.nextLine();
                System.out.println("USERNAME:");
                username = input.next();
                input.nextLine();
                System.out.println("SENHA:");
                senha = input.next();
                usuarioService.cadastrarUsuario(nome, username, senha, true);
            }else if(opcao == 2){
                limparTela();
                System.out.println("===LOGIN===\nUSERNAME:");
                username = input.next();
                input.nextLine();
                System.out.println("SENHA:");
                senha = input.nextLine();
                Usuario novoLogin = usuarioService.autenticar(username, senha);

                if(novoLogin != null){
                    limparTela();
                    System.out.println("Login Bem-sucedido!");

                    if(novoLogin.getEhAdm()){
                        menuAdmin(novoLogin, input);
                    }else{
                        Motorista motoristaCompleto = motoristaService.buscarMotoristaPorId(novoLogin.getId());
                        if(motoristaCompleto != null){
                            menuMotorista(motoristaCompleto, input);
                        }else{
                            System.err.println("ERRO CRÍTICO: Este usuário é um funcionário, mas seus dados de motorista não foram encontrados no sistema.");
                            System.out.println("Pressione ENTER para continuar...");
                            input.nextLine();
                        }
                    }
                }
            }else if(opcao == 0){
                System.out.println("Desligando...");
            }else{
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);
        input.close();
    }

    public static void menu(){
    }

    public static void menuAdmin(Usuario admin, Scanner input){
        int opcao;
        do {
            System.out.println("AREA ADMINISTRATIVA\n1. Gerenciamento de Motoristas\n2. Gerenciar Veiculos\n3. Controlar manutenção\n4. Visualizar Registros de uso\n0. Sair");
            opcao = input.nextInt();
            if(opcao == 1){
                limparTela();
                menuGerenciamentoDeMotorista(admin, input);
            }else if(opcao == 2){
                limparTela();
                menuGerenciamentoVeiculos(admin, input);
            }else if(opcao == 3){
                limparTela();
                menuControleDeManutencao(admin, input);
            }else if(opcao == 4){
                limparTela();
                menuHistoricoViagens(admin, input);
            }else if(opcao == 0){
                limparTela();
                break;
            }else{
                limparTela();
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);
    }

    public static void menuMotorista(Motorista motorista, Scanner input){
        int opcao;
        String placa, destino;
        int idRegistro;
        double quilometragemFinal;
        
        do {
            System.out.println("===== ÁREA DO MOTORISTA =====");
            System.out.println("Bem-vindo, " + motorista.getNome() + "!");
            System.out.print("1. VER VEÍCULOS DISPONÍVEIS\n");
            System.out.print("2. INICIAR USO DE VEÍCULO\n");
            System.out.print("3. FINALIZAR USO DE VEÍCULO\n");
            System.out.print("0. SAIR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    limparTela();
                    System.out.println("===== VEÍCULOS DISPONÍVEIS =====");
                    List<Veiculo> disponiveis = veiculoService.listarVeiculosDisponiveis();
                    if(disponiveis.isEmpty()) {
                        System.out.println("Nenhum veículo disponível no momento.");
                    } else {
                        for(Veiculo v : disponiveis) {
                            System.out.printf("🚗 %s - %s %s (%d) - %.1f km\n", 
                                v.getPlaca(), v.getMarca(), v.getModelo(), 
                                v.getAno(), v.getQuilometragemAtual());
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("DESTINO/FINALIDADE: ");
                    destino = input.nextLine();
                    
                    motoristaService.iniciarViagem(motorista, placa, destino);
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 3:
                    limparTela();
                    System.out.print("ID DO REGISTRO: ");
                    idRegistro = input.nextInt();
                    System.out.print("QUILOMETRAGEM FINAL: ");
                    quilometragemFinal = input.nextDouble();
                    input.nextLine();
                    
                    if(registroUsoService.finalizarUsoVeiculo(idRegistro, quilometragemFinal)) {
                        System.out.println("✅ Uso do veículo finalizado com sucesso!");
                        System.out.println("🏁 Obrigado por utilizar nossos serviços!");
                    } else {
                        System.out.println("❌ Erro ao finalizar uso do veículo!");
                        System.out.println("Verifique o ID do registro.");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                case 0:
                    limparTela();
                    break;
                    
                default:
                    limparTela();
                    System.err.println("ERRO: opção inválida");
                    break;
            }
        } while (opcao != 0);
    }


    public static void menuGerenciamentoDeMotorista(Usuario admin, Scanner input){
        int opcao;
        String nome, username, senha, setor, cnh;
        UsuarioService usuarioService = new UsuarioService();
        do {
            System.out.print("Gerenciamento de Motoristas\n1. CADASTRAR NOVO MOTORISTA\n2. EDITAR INFORMAÇÕES\n3. LISTAR MOTORISTAS\n4. REMOVER MOTORISTA\n0. VOLTAR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            if(opcao == 1){
                limparTela();
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
            }else if(opcao == 2){
                // refatorar para poder escolher oq quer editar
                limparTela();
                System.out.print("CNH DO MOTORISTA:");
                cnh = input.next();
                input.nextLine();
                System.out.print("NOME:");
                nome = input.nextLine();
                System.out.print("USERNAME:");
                username = input.next();
                input.nextLine();
                System.out.print("SENHA:");
                senha = input.nextLine();
                System.out.print("SETOR:");
                setor = input.nextLine();
                usuarioService.editarMotorista(admin, nome, username, senha, setor, cnh);
            }else if(opcao == 3){
                limparTela();
                usuarioService.listarMotoristas(admin);
            }else if(opcao == 4){
                limparTela();
                System.out.print("CNH DO MOTORISTA:");
                cnh = input.next();
                input.nextLine();
                usuarioService.excluirMotorista(admin, cnh);
            }else if(opcao == 0){
                limparTela();
                break;
            }else{
                limparTela();
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);
    }


    public static void menuGerenciamentoVeiculos(Usuario admin, Scanner input){
        int opcao;
        String placa, modelo, marca, cor;
        int ano;
        double quilometragem;
        
        do {
            System.out.print("===== GERENCIAMENTO DE VEÍCULOS =====\n");
            System.out.print("1. CADASTRAR NOVO VEÍCULO\n2. EDITAR INFORMACOES\n3. REMOVER VEICULO\n0. VOLTAR\n>>");
            //System.out.print("7. ATUALIZAR STATUS DO VEÍCULO\n"); // add ao um sub menu de edicao
            //System.out.print("8. ATUALIZAR QUILOMETRAGEM\n"); // add ao um sub menu de editcao
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    limparTela();
                    System.out.print("PLACA: ");
                    placa = input.nextLine();
                    System.out.print("MODELO: ");
                    modelo = input.nextLine();
                    System.out.print("MARCA: ");
                    marca = input.nextLine();
                    System.out.print("ANO: ");
                    ano = input.nextInt();
                    input.nextLine();
                    System.out.print("COR: ");
                    cor = input.nextLine();
                    System.out.print("QUILOMETRAGEM INICIAL: ");
                    quilometragem = input.nextDouble();
                    input.nextLine();
                    
                    if(usuarioService.adicionarVeiculos(admin, placa, modelo, marca, ano, cor, quilometragem)) {
                        System.out.println("✅ Veículo cadastrado com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao cadastrar veículo!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.print("PLACA DO VEICULO: ");
                    placa = input.nextLine();
                    System.out.print("NOVO MODELO: ");
                    modelo = input.nextLine();
                    System.out.print("NOVA MARCA: ");
                    marca = input.nextLine();
                    System.out.print("NOVO ANO: ");
                    ano = input.nextInt();
                    input.nextLine();
                    System.out.print("NOVA COR: ");
                    cor = input.nextLine();
                    usuarioService.editarVeiculo(admin, placa, modelo, marca, ano, cor);
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                case 3:
                    limparTela();
                    System.out.print("PLACA DO VEICULO: ");
                    placa = input.nextLine();
                    //resolver erro dps tenta voltar para o menu.
                    usuarioService.removerVeiculo(admin, placa);
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                case 0:
                    limparTela();
                    break;
                default:
                    limparTela();
                    System.err.println("ERRO: opção inválida");
                    break;
            }
        } while (opcao != 0);
    }

    public static void menuControleDeManutencao(Usuario admin, Scanner input){
        int opcao;
        String placa, descricao, oficina;
        double custo;
        int dia, mes, ano;
        
        do {
            System.out.print("===== CONTROLE DE MANUTENÇÃO =====\n");
            System.out.print("1. INICIAR MANUTENÇÃO\n2. FINALIZAR MANUTENÇÃO\n3. LISTAR MANUTENCAO\n4. EXCLUIR MANUTENCAO\n0. VOLTAR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("DESCRIÇÃO DO SERVIÇO: ");
                    descricao = input.nextLine();
                    System.out.print("NOME DA OFICINA: ");
                    oficina = input.nextLine();
                    System.out.print("DATA PREVISTA DE SAÍDA (DD/MM/AAAA): ");
                    String dataStr = input.nextLine();
                    
                    LocalDate dataPrevista = LocalDate.now();
                    try {
                        // Aceita formato DD/MM/AAAA
                        String[] partesData = dataStr.split("/");
                        if (partesData.length == 3) {
                            dia = Integer.parseInt(partesData[0]);
                            mes = Integer.parseInt(partesData[1]);
                            ano = Integer.parseInt(partesData[2]);
                            dataPrevista = LocalDate.of(ano, mes, dia);
                        } else {
                            throw new IllegalArgumentException("Formato de data inválido");
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Erro: Formato de data inválido! Use DD/MM/AAAA (ex: 19/08/2025)");
                        System.out.println("Pressione ENTER para continuar...");
                        input.nextLine();
                        break;
                    }
                
                    
                     if (manutencaoService.iniciarManutencao(placa, descricao, oficina, java.sql.Date.valueOf(dataPrevista))) {
                    System.out.println("✅ Manutenção iniciada com sucesso!");
                } else {
                    System.out.println("❌ Erro ao iniciar manutenção!");
                }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("CUSTO REAL DA MANUTENÇÃO: R$ ");
                    custo = input.nextDouble();
                    input.nextLine();
                    
                    usuarioService.concluirManutencao(admin, placa, custo);
                     //if(manutencaoService.concluirManutencao(placa, new java.util.Date(), custo)) {
                     //    System.out.println("✅ Manutenção finalizada com sucesso!");
                     //} else {
                     //    System.out.println("❌ Erro ao finalizar manutenção!");
                     //}
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                case 3:
                    limparTela();
                    List<Manutencao> listaDeManutencoes = usuarioService.listarManutencao(admin);
                    if(listaDeManutencoes.isEmpty()){
                        System.out.println("Nenhuma manutencao encontrada");
                    }else{
                        for(Manutencao manutencao:listaDeManutencoes){
                            System.out.println(manutencao);
                        }
                    }

                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 4:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    usuarioService.excluirManutencao(admin, placa);
                    break;
                    
                case 0:
                    limparTela();
                    break;
                    
                default:
                    limparTela();
                    System.err.println("ERRO: opção inválida");
                    break;
            }
        } while (opcao != 0);
    }


    public static void menuRegistros(Scanner input){
        int opcao;
        String placa, cnh;
        int idRegistro;
        double quilometragemFinal;
        
        do {
            System.out.print("===== REGISTROS DE USO =====\n");
            System.out.print("1. LISTAR REGISTROS ATIVOS (em andamento)\n");
            System.out.print("2. LISTAR REGISTROS FINALIZADOS\n");
            System.out.print("3. BUSCAR REGISTROS POR MOTORISTA\n");
            System.out.print("4. BUSCAR REGISTROS POR VEÍCULO\n");
            System.out.print("5. ESTATÍSTICAS DE USO\n");
            System.out.print("6. INICIAR USO DE VEÍCULO\n");
            System.out.print("7. FINALIZAR USO DE VEÍCULO\n");
            System.out.print("0. VOLTAR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    limparTela();
                    System.out.println("===== REGISTROS ATIVOS =====");
                    List<RegistroUso> ativos = registroUsoService.listarRegistrosAtivos();
                    if(ativos.isEmpty()) {
                        System.out.println("Nenhum registro ativo.");
                    } else {
                        for(RegistroUso r : ativos) {
                            System.out.println(r);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.println("===== REGISTROS FINALIZADOS =====");
                    List<RegistroUso> finalizados = registroUsoService.listarRegistrosFinalizados();
                    if(finalizados.isEmpty()) {
                        System.out.println("Nenhum registro finalizado.");
                    } else {
                        for(RegistroUso r : finalizados) {
                            System.out.println(r);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 3:
                    limparTela();
                    System.out.print("CNH DO MOTORISTA: ");
                    cnh = input.nextLine();
                    System.out.println("===== REGISTROS DO MOTORISTA =====");
                    List<RegistroUso> registrosMotorista = registroUsoService.buscarRegistrosPorMotorista(cnh);
                    if(registrosMotorista.isEmpty()) {
                        System.out.println("Nenhum registro encontrado para este motorista.");
                    } else {
                        for(RegistroUso r : registrosMotorista) {
                            System.out.println(r);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 4:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.println("===== REGISTROS DO VEÍCULO =====");
                    List<RegistroUso> registrosVeiculo = registroUsoService.buscarRegistrosPorVeiculo(placa);
                    if(registrosVeiculo.isEmpty()) {
                        System.out.println("Nenhum registro encontrado para este veículo.");
                    } else {
                        for(RegistroUso r : registrosVeiculo) {
                            System.out.println(r);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 5:
                    limparTela();
                    System.out.println("===== ESTATÍSTICAS DE USO =====");
                  //  String estatisticas = registroUsoService.gerarEstatisticasUso();
                  //  System.out.println(estatisticas);
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 6:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("CNH DO MOTORISTA: ");
                    cnh = input.nextLine();
                    System.out.print("DESTINO/FINALIDADE: ");
                    String destino = input.nextLine();
                    
                    // int novoRegistroId = registroUsoService.iniciarUsoVeiculo(placa, cnh, destino);
                    // if(novoRegistroId > 0) {
                    //     System.out.println("✅ Uso do veículo iniciado com sucesso! ID do Registro: " + novoRegistroId);
                    // } else {
                    //     System.out.println("❌ Erro ao iniciar uso do veículo!");
                    // }
                    // System.out.println("Pressione ENTER para continuar...");
                    // input.nextLine();
                    break;
                    
                case 7:
                    limparTela();
                    System.out.print("ID DO REGISTRO: ");
                    idRegistro = input.nextInt();
                    System.out.print("QUILOMETRAGEM FINAL: ");
                    quilometragemFinal = input.nextDouble();
                    input.nextLine();
                    
                    if(registroUsoService.finalizarUsoVeiculo(idRegistro, quilometragemFinal)) {
                        System.out.println("✅ Uso do veículo finalizado com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao finalizar uso do veículo!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 0:
                    limparTela();
                    break;
                    
                default:
                    limparTela();
                    System.err.println("ERRO: opção inválida");
                    break;
            }
        } while (opcao != 0);
    }

    public static void limparTela() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void menuHistoricoViagens(Usuario admin, Scanner input){
        int opcao;
        String cnh, placa;
        int idRegistro;
        
        do {
            System.out.print("===== HISTÓRICO DE VIAGENS (ADMINISTRADOR) =====\n");
            System.out.print("1. VISUALIZAR TODAS AS VIAGENS\n");
            System.out.print("2. FILTRAR POR MOTORISTA (CNH)\n");
            System.out.print("3. FILTRAR POR VEÍCULO (PLACA)\n");
            System.out.print("4. EXCLUIR REGISTRO DE VIAGEM\n");
            System.out.print("0. VOLTAR\n>>");
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    limparTela();
                    System.out.println("===== HISTÓRICO COMPLETO DE VIAGENS =====");
                    List<RegistroUso> todasViagens = usuarioService.visualizarHistoricoCompleto(admin);
                    if(!todasViagens.isEmpty()) {
                        for(RegistroUso r : todasViagens) {
                            System.out.println(formatarRegistroDetalhado(r));
                            System.out.println("─────────────────────────────────────────────────");
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.print("CNH DO MOTORISTA: ");
                    cnh = input.nextLine();
                    System.out.println("===== HISTÓRICO POR MOTORISTA =====");
                    List<RegistroUso> viagensMotorista = usuarioService.visualizarHistoricoPorMotorista(admin, cnh);
                    if(!viagensMotorista.isEmpty()) {
                        for(RegistroUso r : viagensMotorista) {
                            System.out.println(formatarRegistroDetalhado(r));
                            System.out.println("─────────────────────────────────────────────────");
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 3:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.println("===== HISTÓRICO POR VEÍCULO =====");
                    List<RegistroUso> viagensVeiculo = usuarioService.visualizarHistoricoPorVeiculo(admin, placa);
                    if(!viagensVeiculo.isEmpty()) {
                        for(RegistroUso r : viagensVeiculo) {
                            System.out.println(formatarRegistroDetalhado(r));
                            System.out.println("─────────────────────────────────────────────────");
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 4:
                    limparTela();
                    System.out.print("ID DO REGISTRO PARA EXCLUIR: ");
                    idRegistro = input.nextInt();
                    input.nextLine();
                    
                    System.out.print("⚠️ CONFIRMAÇÃO: Deseja realmente excluir o registro ID " + idRegistro + "? (S/N): ");
                    String confirmacao = input.nextLine();
                    
                    if(confirmacao.toLowerCase().startsWith("s")) {
                        if(usuarioService.excluirRegistroViagem(admin, idRegistro)) {
                            System.out.println("✅ Registro excluído com sucesso!");
                        } else {
                            System.out.println("❌ Erro ao excluir registro!");
                        }
                    } else {
                        System.out.println("❌ Operação cancelada.");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 0:
                    limparTela();
                    break;
                    
                default:
                    limparTela();
                    System.err.println("ERRO: opção inválida");
                    break;
            }
        } while (opcao != 0);
    }

    public static String formatarRegistroDetalhado(RegistroUso registro) {
        StringBuilder sb = new StringBuilder();
        sb.append("🆔 ID: ").append(registro.getId()).append("\n");
        sb.append("👤 Motorista: ").append(registro.getMotorista().getNome())
          .append(" (CNH: ").append(registro.getMotorista().getCnh()).append(")\n");
        sb.append("🚗 Veículo: ").append(registro.getVeiculo().getPlaca())
          .append(" - ").append(registro.getVeiculo().getMarca())
          .append(" ").append(registro.getVeiculo().getModelo()).append("\n");
        sb.append("📍 Destino: ").append(registro.getDestinoOuFinalidade()).append("\n");
        sb.append("🕐 Saída: ").append(registro.getDataHoraSaida()).append("\n");
        
        if(registro.getDataHoraRetorno() != null) {
            sb.append("🏁 Retorno: ").append(registro.getDataHoraRetorno()).append("\n");
            sb.append("🛣️ Km Inicial: ").append(registro.getKmSaida())
              .append(" | Km Final: ").append(registro.getKmRetorno()).append("\n");
            sb.append("📊 Distância: ").append(registro.getKmRetorno() - registro.getKmSaida())
              .append(" km\n");
            sb.append("✅ Status: FINALIZADA");
        } else {
            sb.append("🛣️ Km Inicial: ").append(registro.getKmSaida()).append("\n");
            sb.append("⏳ Status: EM ANDAMENTO");
        }
        
        return sb.toString();
    }
}