package main.java.com.devShow.Veiculos_Empresarial.app;
import main.java.com.devShow.Veiculos_Empresarial.service.*;
import main.java.com.devShow.Veiculos_Empresarial.model.*;

import java.util.List;

public class Main{
    public static void main(String[] args){
        MotoristaService motoristaService = new MotoristaService();

        //motoristaService.cadastrarMotorista("Thyago", "th.fb", "123", "carga leve", "12345678");
        //imprimirListaMotoristas();
        //motoristaService.atualizarDadosDeMotorista("33333", "Dinarte", "carga pesada", "dinart.filho", "321");
        //imprimirListaMotoristas();
        System.out.println(motoristaService.buscarMotorista("33333"));
    }

    public static void imprimirListaMotoristas(){
        MotoristaService m = new MotoristaService();

        List<Motorista> listaMotoristas = m.listarTodosMotoristas();

        for(Motorista motorista: listaMotoristas){
            System.out.println(motorista);
        }
    }
}

