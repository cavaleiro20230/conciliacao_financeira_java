package com.sistema.conciliacao.util;

import com.sistema.conciliacao.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConciliacaoUtil {
    
    /**
     * Valida se uma conciliação está consistente
     */
    public static boolean validarConciliacao(Conciliacao conciliacao) {
        if (conciliacao == null) return false;
        
        // Validar datas
        if (conciliacao.getDataInicio() == null || conciliacao.getDataFim() == null) {
            return false;
        }
        
        if (conciliacao.getDataInicio().isAfter(conciliacao.getDataFim())) {
            return false;
        }
        
        // Validar conta bancária
        if (conciliacao.getContaBancaria() == null || conciliacao.getContaBancaria().trim().isEmpty()) {
            return false;
        }
        
        // Validar saldos
        if (conciliacao.getSaldoFinalBanco() == null || conciliacao.getSaldoFinalInterno() == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calcula o percentual de conciliação
     */
    public static double calcularPercentualConciliacao(Conciliacao conciliacao) {
        int totalItens = conciliacao.getTotalItensConciliados() + conciliacao.getTotalItensNaoConciliados();
        if (totalItens == 0) return 100.0;
        
        return (double) conciliacao.getTotalItensConciliados() / totalItens * 100.0;
    }
    
    /**
     * Filtra lançamentos por valor mínimo
     */
    public static List<LancamentoBancario> filtrarPorValorMinimo(List<LancamentoBancario> lancamentos, 
                                                               BigDecimal valorMinimo) {
        return lancamentos.stream()
            .filter(l -> l.getValor().abs().compareTo(valorMinimo) >= 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Filtra registros por período específico
     */
    public static List<RegistroInterno> filtrarPorPeriodo(List<RegistroInterno> registros,
                                                        LocalDate dataInicio, LocalDate dataFim) {
        return registros.stream()
            .filter(r -> !r.getDataRegistro().isBefore(dataInicio) && 
                        !r.getDataRegistro().isAfter(dataFim))
            .collect(Collectors.toList());
    }
    
    /**
     * Agrupa lançamentos por tipo de transação
     */
    public static java.util.Map<TipoTransacao, List<LancamentoBancario>> agruparPorTipo(
            List<LancamentoBancario> lancamentos) {
        return lancamentos.stream()
            .collect(Collectors.groupingBy(LancamentoBancario::getTipoTransacao));
    }
    
    /**
     * Calcula o total de valores por tipo de movimento
     */
    public static BigDecimal calcularTotalPorTipo(List<RegistroInterno> registros, TipoMovimento tipo) {
        return registros.stream()
            .filter(r -> r.getTipoMovimento() == tipo)
            .map(RegistroInterno::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Verifica se dois valores são aproximadamente iguais (com tolerância)
     */
    public static boolean valoresAproximados(BigDecimal valor1, BigDecimal valor2, BigDecimal tolerancia) {
        if (valor1 == null || valor2 == null) return false;
        return valor1.subtract(valor2).abs().compareTo(tolerancia) <= 0;
    }
    
    /**
     * Normaliza texto para comparação (remove acentos, espaços extras, etc.)
     */
    public static String normalizarTexto(String texto) {
        if (texto == null) return "";
        
        return texto.toLowerCase()
                   .replaceAll("[áàâãä]", "a")
                   .replaceAll("[éèêë]", "e")
                   .replaceAll("[íìîï]", "i")
                   .replaceAll("[óòôõö]", "o")
                   .replaceAll("[úùûü]", "u")
                   .replaceAll("[ç]", "c")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    /**
     * Gera ID único para conciliação
     */
    public static String gerarIdConciliacao(String conta, LocalDate data) {
        return String.format("CONC_%s_%s", conta.replaceAll("[^0-9]", ""), 
                           data.toString().replace("-", ""));
    }
}