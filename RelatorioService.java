package com.sistema.conciliacao.service;

import com.sistema.conciliacao.model.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Gera relatório completo de conciliação
     */
    public String gerarRelatorioCompleto(Conciliacao conciliacao) {
        StringBuilder relatorio = new StringBuilder();
        
        // Cabeçalho
        relatorio.append("=".repeat(80)).append("\n");
        relatorio.append("RELATÓRIO DE CONCILIAÇÃO BANCÁRIA\n");
        relatorio.append("=".repeat(80)).append("\n");
        relatorio.append(String.format("Conta: %s - Agência: %s\n", 
                                      conciliacao.getContaBancaria(), conciliacao.getAgencia()));
        relatorio.append(String.format("Período: %s a %s\n", 
                                      conciliacao.getDataInicio().format(DATE_FORMATTER),
                                      conciliacao.getDataFim().format(DATE_FORMATTER)));
        relatorio.append(String.format("Data da Conciliação: %s\n", 
                                      conciliacao.getDataConciliacao().format(DATE_FORMATTER)));
        relatorio.append(String.format("Status: %s\n", conciliacao.getStatusConciliacao().getDescricao()));
        relatorio.append("\n");
        
        // Resumo dos saldos
        relatorio.append("RESUMO DOS SALDOS\n");
        relatorio.append("-".repeat(40)).append("\n");
        relatorio.append(String.format("Saldo Final Banco: %s\n", 
                                      formatarValor(conciliacao.getSaldoFinalBanco())));
        relatorio.append(String.format("Saldo Final Interno: %s\n", 
                                      formatarValor(conciliacao.getSaldoFinalInterno())));
        relatorio.append(String.format("Diferença: %s\n", 
                                      formatarValor(conciliacao.getDiferenca())));
        relatorio.append("\n");
        
        // Estatísticas
        relatorio.append("ESTATÍSTICAS\n");
        relatorio.append("-".repeat(40)).append("\n");
        relatorio.append(String.format("Total de Itens Conciliados: %d\n", 
                                      conciliacao.getTotalItensConciliados()));
        relatorio.append(String.format("Lançamentos Não Conciliados (Banco): %d\n", 
                                      conciliacao.getLancamentosNaoConciliadosBanco().size()));
        relatorio.append(String.format("Registros Não Conciliados (Interno): %d\n", 
                                      conciliacao.getRegistrosNaoConciliadosInterno().size()));
        relatorio.append(String.format("Total de Ajustes: %d\n", 
                                      conciliacao.getAjustes().size()));
        relatorio.append("\n");
        
        // Itens conciliados
        if (!conciliacao.getItensConciliados().isEmpty()) {
            relatorio.append("ITENS CONCILIADOS\n");
            relatorio.append("-".repeat(80)).append("\n");
            relatorio.append(String.format("%-12s %-15s %-30s %-15s %-15s\n", 
                                          "Data", "Valor", "Descrição", "Tipo Match", "Diferença"));
            relatorio.append("-".repeat(80)).append("\n");
            
            for (ItemConciliado item : conciliacao.getItensConciliados()) {
                relatorio.append(String.format("%-12s %-15s %-30s %-15s %-15s\n",
                    item.getLancamentoBancario().getDataLancamento().format(DATE_FORMATTER),
                    formatarValor(item.getLancamentoBancario().getValor()),
                    truncarTexto(item.getLancamentoBancario().getHistorico(), 30),
                    item.getTipoCorrespondencia().name(),
                    formatarValor(item.getDiferenca())
                ));
            }
            relatorio.append("\n");
        }
        
        // Lançamentos não conciliados do banco
        if (!conciliacao.getLancamentosNaoConciliadosBanco().isEmpty()) {
            relatorio.append("LANÇAMENTOS NÃO CONCILIADOS - BANCO\n");
            relatorio.append("-".repeat(80)).append("\n");
            relatorio.append(String.format("%-12s %-15s %-15s %-30s\n", 
                                          "Data", "Valor", "Tipo", "Histórico"));
            relatorio.append("-".repeat(80)).append("\n");
            
            for (LancamentoBancario lancamento : conciliacao.getLancamentosNaoConciliadosBanco()) {
                relatorio.append(String.format("%-12s %-15s %-15s %-30s\n",
                    lancamento.getDataLancamento().format(DATE_FORMATTER),
                    formatarValor(lancamento.getValor()),
                    lancamento.getTipoTransacao().getDescricao(),
                    truncarTexto(lancamento.getHistorico(), 30)
                ));
            }
            relatorio.append("\n");
        }
        
        // Registros não conciliados internos
        if (!conciliacao.getRegistrosNaoConciliadosInterno().isEmpty()) {
            relatorio.append("REGISTROS NÃO CONCILIADOS - INTERNO\n");
            relatorio.append("-".repeat(80)).append("\n");
            relatorio.append(String.format("%-12s %-15s %-15s %-30s\n", 
                                          "Data", "Valor", "Tipo", "Descrição"));
            relatorio.append("-".repeat(80)).append("\n");
            
            for (RegistroInterno registro : conciliacao.getRegistrosNaoConciliadosInterno()) {
                relatorio.append(String.format("%-12s %-15s %-15s %-30s\n",
                    registro.getDataRegistro().format(DATE_FORMATTER),
                    formatarValor(registro.getValor()),
                    registro.getTipoMovimento().getDescricao(),
                    truncarTexto(registro.getDescricao(), 30)
                ));
            }
            relatorio.append("\n");
        }
        
        // Ajustes
        if (!conciliacao.getAjustes().isEmpty()) {
            relatorio.append("AJUSTES REALIZADOS\n");
            relatorio.append("-".repeat(80)).append("\n");
            relatorio.append(String.format("%-15s %-15s %-20s %-30s\n", 
                                          "Tipo", "Valor", "Status", "Justificativa"));
            relatorio.append("-".repeat(80)).append("\n");
            
            for (AjusteConciliacao ajuste : conciliacao.getAjustes()) {
                relatorio.append(String.format("%-15s %-15s %-20s %-30s\n",
                    ajuste.getTipoAjuste().getDescricao(),
                    formatarValor(ajuste.getValor()),
                    ajuste.getStatusAjuste().getDescricao(),
                    truncarTexto(ajuste.getJustificativa(), 30)
                ));
            }
            relatorio.append("\n");
        }
        
        // Rodapé
        relatorio.append("=".repeat(80)).append("\n");
        relatorio.append("Relatório gerado automaticamente pelo Sistema de Conciliação Bancária\n");
        relatorio.append("=".repeat(80)).append("\n");
        
        return relatorio.toString();
    }
    
    /**
     * Gera relatório resumido
     */
    public String gerarRelatorioResumido(Conciliacao conciliacao) {
        StringBuilder relatorio = new StringBuilder();
        
        relatorio.append("RESUMO DA CONCILIAÇÃO\n");
        relatorio.append("=".repeat(50)).append("\n");
        relatorio.append(String.format("Conta: %s\n", conciliacao.getContaBancaria()));
        relatorio.append(String.format("Período: %s a %s\n", 
                                      conciliacao.getDataInicio().format(DATE_FORMATTER),
                                      conciliacao.getDataFim().format(DATE_FORMATTER)));
        relatorio.append(String.format("Status: %s\n", conciliacao.getStatusConciliacao().getDescricao()));
        relatorio.append("\n");
        
        relatorio.append(String.format("Saldo Banco: %s\n", 
                                      formatarValor(conciliacao.getSaldoFinalBanco())));
        relatorio.append(String.format("Saldo Interno: %s\n", 
                                      formatarValor(conciliacao.getSaldoFinalInterno())));
        relatorio.append(String.format("Diferença: %s\n", 
                                      formatarValor(conciliacao.getDiferenca())));
        relatorio.append("\n");
        
        relatorio.append(String.format("Conciliados: %d\n", conciliacao.getTotalItensConciliados()));
        relatorio.append(String.format("Não Conciliados: %d\n", conciliacao.getTotalItensNaoConciliados()));
        
        return relatorio.toString();
    }
    
    /**
     * Gera relatório de discrepâncias
     */
    public String gerarRelatorioDiscrepancias(Conciliacao conciliacao) {
        StringBuilder relatorio = new StringBuilder();
        
        relatorio.append("RELATÓRIO DE DISCREPÂNCIAS\n");
        relatorio.append("=".repeat(60)).append("\n");
        relatorio.append(String.format("Conta: %s - Data: %s\n", 
                                      conciliacao.getContaBancaria(),
                                      conciliacao.getDataConciliacao().format(DATE_FORMATTER)));
        relatorio.append("\n");
        
        // Itens com diferença de valor
        List<ItemConciliado> itensComDiferenca = conciliacao.getItensConciliados().stream()
            .filter(ItemConciliado::temDiferenca)
            .collect(Collectors.toList());
        
        if (!itensComDiferenca.isEmpty()) {
            relatorio.append("ITENS COM DIFERENÇA DE VALOR\n");
            relatorio.append("-".repeat(60)).append("\n");
            for (ItemConciliado item : itensComDiferenca) {
                relatorio.append(String.format("Data: %s | Diferença: %s | Obs: %s\n",
                    item.getLancamentoBancario().getDataLancamento().format(DATE_FORMATTER),
                    formatarValor(item.getDiferenca()),
                    item.getObservacoes() != null ? item.getObservacoes() : "N/A"
                ));
            }
            relatorio.append("\n");
        }
        
        // Análise por tipo de correspondência
        Map<TipoCorrespondencia, Long> estatisticasTipo = conciliacao.getItensConciliados().stream()
            .collect(Collectors.groupingBy(ItemConciliado::getTipoCorrespondencia, Collectors.counting()));
        
        relatorio.append("ESTATÍSTICAS POR TIPO DE CORRESPONDÊNCIA\n");
        relatorio.append("-".repeat(60)).append("\n");
        estatisticasTipo.forEach((tipo, quantidade) -> 
            relatorio.append(String.format("%s: %d itens\n", tipo.getDescricao(), quantidade))
        );
        
        return relatorio.toString();
    }
    
    // Métodos auxiliares
    
    private String formatarValor(BigDecimal valor) {
        if (valor == null) return "0,00";
        return String.format("%,.2f", valor).replace(",", "X").replace(".", ",").replace("X", ".");
    }
    
    private String truncarTexto(String texto, int tamanho) {
        if (texto == null) return "";
        return texto.length() > tamanho ? texto.substring(0, tamanho - 3) + "..." : texto;
    }
}