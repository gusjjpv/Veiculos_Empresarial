package app;
import main.java.com.devShow.Veiculos_Empresarial.service.*;
import main.java.com.devShow.Veiculos_Empresarial.database.DatabaseConnection;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;

public class Main{
    // Services globais para uso em toda a aplicação
    private static UsuarioService usuarioService = new UsuarioService();
    private static MotoristaService motoristaService = new MotoristaService();
    private static VeiculoService veiculoService = new VeiculoService();
    private static RegistroUsoService registroUsoService = new RegistroUsoService();
    private static ManutencaoService manutencaoService = new ManutencaoService();
    
    public static void main(String[] args){
        DatabaseConnection.getInstance();
        Scanner input = new Scanner(System.in);
        String nome, username, senha, setor, cnh;
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
                usuarioService.cadastrarUsuario(nome, username, senha, true); // true = admin
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
                        menuAdmin(novoLogin);
                    }else{
                        menuMotorista(novoLogin);
                    }
                }
            }else if(opcao == 0){
                System.out.println("Desligando...");
            }else{
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);
    }

    public static void menu(){
    }

    public static void menuAdmin(Usuario admin){
        int opcao;
        Scanner input = new Scanner(System.in);
        do {
            System.out.println("AREA ADMINISTRATIVA\n1. Gerenciamento de Motoristas\n2. Gerenciar Veiculos\n3. Controlar manutenção\n4. Visualizar Registros de uso\n0. Sair");
            opcao = input.nextInt();
            if(opcao == 1){
                limparTela();
                menuGerenciamentoDeMotorista(admin);
            }else if(opcao == 2){
                limparTela();
                menuGerenciamentoVeiculos();
            }else if(opcao == 3){
                limparTela();
                menuControleDeManutencao();
            }else if(opcao == 4){
                limparTela();
                menuRegistros();
            }else if(opcao == 0){
                limparTela();
                break;
            }else{
                limparTela();
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);
        
    }

    public static void menuMotorista(Usuario motorista){
        int opcao;
        Scanner input = new Scanner(System.in);
        String placa, destino;
        int idRegistro;
        double quilometragemFinal;
        
        do {
            System.out.println("===== ÁREA DO MOTORISTA =====");
            System.out.println("Bem-vindo, " + motorista.getNome() + "!");
            System.out.print("1. VER VEÍCULOS DISPONÍVEIS\n");
            System.out.print("2. INICIAR USO DE VEÍCULO\n");
            System.out.print("3. FINALIZAR USO DE VEÍCULO\n");
            System.out.print("4. MEUS REGISTROS DE USO\n");
            System.out.print("5. MEUS REGISTROS ATIVOS\n");
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
                    // Buscar motorista pelo ID do usuário
                    Motorista motoristaObj = motoristaService.buscarMotoristaPorId(motorista.getId());
                    if(motoristaObj == null) {
                        System.out.println("❌ Erro: Usuário não é um motorista válido!");
                        System.out.println("Pressione ENTER para continuar...");
                        input.nextLine();
                        break;
                    }
                    
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("DESTINO/FINALIDADE: ");
                    destino = input.nextLine();
                    
                    int novoRegistroId = registroUsoService.iniciarUsoVeiculo(placa, motoristaObj.getCnh(), destino);
                    if(novoRegistroId > 0) {
                        System.out.println("✅ Uso do veículo iniciado com sucesso!");
                        System.out.println("📋 ID do Registro: " + novoRegistroId);
                        System.out.println("🚗 Veículo: " + placa);
                        System.out.println("📍 Destino: " + destino);
                    } else {
                        System.out.println("❌ Erro ao iniciar uso do veículo!");
                        System.out.println("Verifique se o veículo está disponível.");
                    }
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
                    
                case 4:
                    limparTela();
                    // Buscar o motorista novamente para obter a CNH
                    Motorista motoristaObj2 = motoristaService.buscarMotoristaPorId(motorista.getId());
                    if(motoristaObj2 == null) {
                        System.out.println("❌ Erro: Usuário não é um motorista válido!");
                        System.out.println("Pressione ENTER para continuar...");
                        input.nextLine();
                        break;
                    }
                    
                    System.out.println("===== MEUS REGISTROS DE USO =====");
                    List<RegistroUso> meusRegistros = registroUsoService.buscarRegistrosPorMotorista(motoristaObj2.getCnh());
                    if(meusRegistros.isEmpty()) {
                        System.out.println("Você ainda não possui registros de uso.");
                    } else {
                        for(RegistroUso r : meusRegistros) {
                            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            System.out.printf("🆔 ID: %d\n", r.getId());
                            System.out.printf("🚗 Veículo: %s\n", r.getVeiculo().getPlaca());
                            System.out.printf("📅 Saída: %s\n", r.getDataHoraSaida());
                            System.out.printf("📍 Destino: %s\n", r.getDestinoOuFinalidade());
                            if(r.getDataHoraRetorno() != null) {
                                System.out.printf("🏁 Retorno: %s\n", r.getDataHoraRetorno());
                                System.out.printf("📏 KM Rodados: %.1f km\n", r.calcularKmRodados());
                            } else {
                                System.out.println("⏳ Status: EM ANDAMENTO");
                            }
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 5:
                    limparTela();
                    System.out.println("===== MEUS REGISTROS ATIVOS =====");
                    List<RegistroUso> registrosAtivos = registroUsoService.listarRegistrosAtivos();
                    List<RegistroUso> meusAtivos = registrosAtivos.stream()
                        .filter(r -> r.getMotorista().getUsuario().getId() == motorista.getId())
                        .toList();
                    
                    if(meusAtivos.isEmpty()) {
                        System.out.println("Você não possui registros ativos no momento.");
                    } else {
                        for(RegistroUso r : meusAtivos) {
                            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            System.out.printf("🆔 ID: %d\n", r.getId());
                            System.out.printf("🚗 Veículo: %s (%s %s)\n", 
                                r.getVeiculo().getPlaca(),
                                r.getVeiculo().getMarca(),
                                r.getVeiculo().getModelo());
                            System.out.printf("📅 Saída: %s\n", r.getDataHoraSaida());
                            System.out.printf("📍 Destino: %s\n", r.getDestinoOuFinalidade());
                            System.out.printf("📏 KM Inicial: %.1f km\n", r.getKmSaida());
                            System.out.println("⏳ Status: EM ANDAMENTO");
                        }
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


    public static void menuGerenciamentoDeMotorista(Usuario admin){
        int opcao;
        Scanner input = new Scanner(System.in);
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
                System.out.println("falta implementar");
            }else if(opcao == 0){
                limparTela();
                break;
            }else{
                limparTela();
                System.err.println("ERRO: opcao invalida");
            }
        } while (opcao != 0);

    }


    public static void menuGerenciamentoVeiculos(){
        int opcao;
        Scanner input = new Scanner(System.in);
        String placa, modelo, marca, cor;
        int ano;
        double quilometragem;
        
        do {
            System.out.print("===== GERENCIAMENTO DE VEÍCULOS =====\n");
            System.out.print("1. CADASTRAR NOVO VEÍCULO\n");
            System.out.print("2. LISTAR TODOS OS VEÍCULOS\n");
            System.out.print("3. LISTAR VEÍCULOS DISPONÍVEIS\n");
            System.out.print("4. LISTAR VEÍCULOS EM USO\n");
            System.out.print("5. LISTAR VEÍCULOS EM MANUTENÇÃO\n");
            System.out.print("6. BUSCAR VEÍCULO POR PLACA\n");
            System.out.print("7. ATUALIZAR STATUS DO VEÍCULO\n");
            System.out.print("8. ATUALIZAR QUILOMETRAGEM\n");
            System.out.print("9. ESTATÍSTICAS DA FROTA\n");
            System.out.print("0. VOLTAR\n>>");
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
                    
                    if(veiculoService.cadastrarVeiculo(placa, modelo, marca, ano, cor, quilometragem)) {
                        System.out.println("✅ Veículo cadastrado com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao cadastrar veículo!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 2:
                    limparTela();
                    System.out.println("===== TODOS OS VEÍCULOS =====");
                    List<Veiculo> todosVeiculos = veiculoService.listarTodosVeiculos();
                    if(todosVeiculos.isEmpty()) {
                        System.out.println("Nenhum veículo cadastrado.");
                    } else {
                        for(Veiculo v : todosVeiculos) {
                            System.out.println(v);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 3:
                    limparTela();
                    System.out.println("===== VEÍCULOS DISPONÍVEIS =====");
                    List<Veiculo> disponiveis = veiculoService.listarVeiculosDisponiveis();
                    if(disponiveis.isEmpty()) {
                        System.out.println("Nenhum veículo disponível.");
                    } else {
                        for(Veiculo v : disponiveis) {
                            System.out.println(v);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 4:
                    limparTela();
                    System.out.println("===== VEÍCULOS EM USO =====");
                    List<Veiculo> emUso = veiculoService.listarVeiculosEmUso();
                    if(emUso.isEmpty()) {
                        System.out.println("Nenhum veículo em uso.");
                    } else {
                        for(Veiculo v : emUso) {
                            System.out.println(v);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 5:
                    limparTela();
                    System.out.println("===== VEÍCULOS EM MANUTENÇÃO =====");
                    List<Veiculo> emManutencao = veiculoService.listarVeiculosEmManutencao();
                    if(emManutencao.isEmpty()) {
                        System.out.println("Nenhum veículo em manutenção.");
                    } else {
                        for(Veiculo v : emManutencao) {
                            System.out.println(v);
                        }
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 6:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    Veiculo veiculo = veiculoService.buscarVeiculoPorPlaca(placa);
                    if(veiculo != null) {
                        System.out.println("Veículo encontrado:");
                        System.out.println(veiculo);
                    } else {
                        System.out.println("❌ Veículo não encontrado!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 7:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.println("Status disponíveis:");
                    System.out.println("1. DISPONIVEL");
                    System.out.println("2. EM_USO");
                    System.out.println("3. MANUTENCAO");
                    System.out.print("Escolha o novo status (1-3): ");
                    int statusEscolha = input.nextInt();
                    input.nextLine();
                    
                    StatusVeiculo novoStatus = null;
                    switch(statusEscolha) {
                        case 1: novoStatus = StatusVeiculo.DISPONIVEL; break;
                        case 2: novoStatus = StatusVeiculo.EM_USO; break;
                        case 3: novoStatus = StatusVeiculo.MANUTENCAO; break;
                        default: System.out.println("❌ Status inválido!"); break;
                    }
                    
                    if(novoStatus != null && veiculoService.atualizarStatusVeiculo(placa, novoStatus)) {
                        System.out.println("✅ Status atualizado com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao atualizar status!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 8:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    System.out.print("NOVA QUILOMETRAGEM: ");
                    quilometragem = input.nextDouble();
                    input.nextLine();
                    
                    if(veiculoService.atualizarQuilometragem(placa, quilometragem)) {
                        System.out.println("✅ Quilometragem atualizada com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao atualizar quilometragem!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 9:
                    limparTela();
                    System.out.println("===== ESTATÍSTICAS DA FROTA =====");
                    String estatisticas = veiculoService.gerarEstatisticasFrota();
                    System.out.println(estatisticas);
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

    public static void menuControleDeManutencao(){
        int opcao;
        Scanner input = new Scanner(System.in);
        String placa, descricao, oficina;
        double custo;
        int dia, mes, ano;
        
        do {
            System.out.print("===== CONTROLE DE MANUTENÇÃO =====\n");
            System.out.print("1. INICIAR MANUTENÇÃO\n");
            System.out.print("2. FINALIZAR MANUTENÇÃO\n");
            System.out.print("3. VERIFICAR SE VEÍCULO PODE ENTRAR EM MANUTENÇÃO\n");
            System.out.print("4. VERIFICAR SE VEÍCULO ESTÁ EM MANUTENÇÃO\n");
            System.out.print("5. RELATÓRIO DE MANUTENÇÕES\n");
            System.out.print("0. VOLTAR\n>>");
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
                    
                    LocalDate dataPrevista = null;
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
                    
                    if(manutencaoService.iniciarManutencao(placa, descricao, oficina, dataPrevista)) {
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
                    
                    if(manutencaoService.finalizarManutencao(placa, custo)) {
                        System.out.println("✅ Manutenção finalizada com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao finalizar manutenção!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 3:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    
                    if(manutencaoService.podeEntrarEmManutencao(placa)) {
                        System.out.println("✅ Veículo pode entrar em manutenção!");
                    } else {
                        System.out.println("❌ Veículo NÃO pode entrar em manutenção (pode estar em uso ou já em manutenção)!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 4:
                    limparTela();
                    System.out.print("PLACA DO VEÍCULO: ");
                    placa = input.nextLine();
                    
                    if(manutencaoService.veiculoEstaEmManutencao(placa)) {
                        System.out.println("🔧 Veículo está em manutenção!");
                    } else {
                        System.out.println("✅ Veículo NÃO está em manutenção!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
                    break;
                    
                case 5:
                    limparTela();
                    System.out.println("===== RELATÓRIO DE MANUTENÇÕES =====");
                    String relatorio = manutencaoService.gerarRelatorioManutencoes();
                    System.out.println(relatorio);
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


    public static void menuRegistros(){
        int opcao;
        Scanner input = new Scanner(System.in);
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
                    String estatisticas = registroUsoService.gerarEstatisticasUso();
                    System.out.println(estatisticas);
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
                    
                    int novoRegistroId = registroUsoService.iniciarUsoVeiculo(placa, cnh, destino);
                    if(novoRegistroId > 0) {
                        System.out.println("✅ Uso do veículo iniciado com sucesso! ID do Registro: " + novoRegistroId);
                    } else {
                        System.out.println("❌ Erro ao iniciar uso do veículo!");
                    }
                    System.out.println("Pressione ENTER para continuar...");
                    input.nextLine();
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
}