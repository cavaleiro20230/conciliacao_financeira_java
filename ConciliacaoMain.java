package com.sistema.conciliacao;

import com.sistema.conciliacao.model.*;
import com.sistema.conciliacao.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Classe principal para demonstrar o uso do sistema de conciliação bancária
 */
public class ConciliacaoMain {
    
    public static void main(String[] args) {
        try {
            // Exemplo de uso do sistema
            exemploCompleto();
        } catch (Exception e) {
            System.err.println("Erro na execução: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void exemploCompleto() {
        System.out.println("=== SISTEMA DE CONCILIAÇÃO BANCÁRIA ===\n");
        
        // 1. Criar dados de exemplo
        List<LancamentoBancario> lancamentosBanco = criarLancamentosBanco();
        List<RegistroInterno> registrosInternos = criarRegistrosInternos();
        
        System.out.println("Dados carregados:");
        System.out.println("- Lançamentos do banco: " + lancamentosBanco.size());
        System.out.println("- Registros internos: " + registrosInternos.size());
        System.out.println();
        
        // 2. Executar conciliação
        ConciliacaoService conciliacaoService = new ConciliacaoService();
        
        Conciliacao conciliacao = conciliacaoService.executarConciliacao(
            lancamentosBanco,
            registrosInternos,
            "12345-6",
            "0001",
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        );
        
        // 3. Exibir resultados
        System.out.println("=== RESULTADOS DA CONCILIAÇÃO ===");
        System.out.println("Status: " + conciliacao.getStatusConciliacao().getDescricao());
        System.out.println("Saldo Banco: R$ " + conciliacao.getSaldoFinalBanco());
        System.out.println("Saldo Interno: R$ " + conciliacao.getSaldoFinalInterno());
        System.out.println("Diferença: R$ " + conciliacao.getDiferenca());
        System.out.println("Itens conciliados: " + conciliacao.getTotalItensConciliados());
        System.out.println("Itens não conciliados: " + conciliacao.getTotalItensNaoConciliados());
        System.out.println();
        
        // 4. Gerar relatório
        RelatorioService relatorioService = new RelatorioService();
        String relatorio = relatorioService.gerarRelatorioCompleto(conciliacao);
        
        System.out.println("=== RELATÓRIO COMPLETO ===");
        System.out.println(relatorio);
        
        // 5. Exemplo de conciliação manual
        if (!conciliacao.getLancamentosNaoConciliadosBanco().isEmpty() && 
            !conciliacao.getRegistrosNaoConciliadosInterno().isEmpty()) {
            
            System.out.println("=== EXEMPLO DE CONCILIAÇÃO MANUAL ===");
            
            LancamentoBancario lancamento = conciliacao.getLancamentosNaoConciliadosBanco().get(0);
            RegistroInterno registro = conciliacao.getRegistrosNaoConciliadosInterno().get(0);
            
            ItemConciliado itemManual = conciliacaoService.conciliarManualmente(
                lancamento, 
                registro, 
                "Conciliação manual realizada pelo usuário", 
                "admin"
            );
            
            System.out.println("Item conciliado manualmente:");
            System.out.println("- Lançamento: " + lancamento.getHistorico());
            System.out.println("- Registro: " + registro.getDescricao());
            System.out.println("- Diferença: R$ " + itemManual.getDiferenca());
        }
        
        // 6. Exemplo de importação de arquivo
        exemploImportacao();
    }
    
    private static void exemploImportacao() {
        System.out.println("\n=== EXEMPLO DE IMPORTAÇÃO ===");
        
        ImportacaoExtratoService importacaoService = new ImportacaoExtratoService();
        
        // Simular validação de arquivo
        String arquivoTeste = "extrato_exemplo.csv";
        boolean arquivoValido = importacaoService.validarFormatoArquivo(arquivoTeste, "CSV");
        
        System.out.println("Validação de arquivo CSV: " + (arquivoValido ? "Válido" : "Inválido"));
        
        // Em um cenário real, você faria:
        // List<LancamentoBancario> lancamentos = importacaoService.importarCSV("caminho/para/arquivo.csv");
    }
    
    private static List<LancamentoBancario> criarLancamentosBanco() {
        return Arrays.asList(
            new LancamentoBancario("1", LocalDate.of(2024, 1, 15), 
                                 "PAGAMENTO PIX", new BigDecimal("1500.00"), 
                                 TipoTransacao.DEBITO, "0001", "12345-6"),
            
            new LancamentoBancario("2", LocalDate.of(2024, 1, 16), 
                                 "DEPOSITO EM CONTA", new BigDecimal("2000.00"), 
                                 TipoTransacao.CREDITO, "0001", "12345-6"),
            
            new LancamentoBancario("3", LocalDate.of(2024, 1, 17), 
                                 "TED ENVIADA", new BigDecimal("800.00"), 
                                 TipoTransacao.DEBITO, "0001", "12345-6"),
            
            new LancamentoBancario("4", LocalDate.of(2024, 1, 18), 
                                 "TAXA MANUTENCAO", new BigDecimal("25.00"), 
                                 TipoTransacao.DEBITO, "0001", "12345-6"),
            
            new LancamentoBancario("5", LocalDate.of(2024, 1, 20), 
                                 "RECEBIMENTO DOC", new BigDecimal("3500.00"), 
                                 TipoTransacao.CREDITO, "0001", "12345-6")
        );
    }
    
    private static List<RegistroInterno> criarRegistrosInternos() {
        return Arrays.asList(
            new RegistroInterno(1L, LocalDate.of(2024, 1, 15), 
                              "Pagamento fornecedor via PIX", new BigDecimal("1500.00"), 
                              TipoMovimento.PAGAMENTO),
            
            new RegistroInterno(2L, LocalDate.of(2024, 1, 16), 
                              "Recebimento cliente", new BigDecimal("2000.00"), 
                              TipoMovimento.RECEBIMENTO),
            
            new RegistroInterno(3L, LocalDate.of(2024, 1, 17), 
                              "Transferência para filial", new BigDecimal("800.00"), 
                              TipoMovimento.TRANSFERENCIA_SAIDA),
            
            new RegistroInterno(4L, LocalDate.of(2024, 1, 20), 
                              "Recebimento de vendas", new BigDecimal("3500.00"), 
                              TipoMovimento.RECEBIMENTO),
            
            new RegistroInterno(5L, LocalDate.of(2024, 1, 22), 
                              "Pagamento não identificado no banco", new BigDecimal("500.00"), 
                              TipoMovimento.PAGAMENTO)
        );
    }
}