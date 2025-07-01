package com.sistema.conciliacao.service;

import com.sistema.conciliacao.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ConciliacaoService {
    
    private static final int TOLERANCIA_DIAS = 3;
    private static final BigDecimal TOLERANCIA_VALOR = new BigDecimal("0.01");
    
    /**
     * Executa o processo completo de conciliação bancária
     */
    public Conciliacao executarConciliacao(List<LancamentoBancario> lancamentosBanco,
                                         List<RegistroInterno> registrosInternos,
                                         String contaBancaria, String agencia,
                                         LocalDate dataInicio, LocalDate dataFim) {
        
        Conciliacao conciliacao = new Conciliacao(contaBancaria, agencia, dataInicio, dataFim);
        
        // Filtrar lançamentos por período
        List<LancamentoBancario> lancamentosFiltrados = filtrarLancamentosPorPeriodo(lancamentosBanco, dataInicio, dataFim);
        List<RegistroInterno> registrosFiltrados = filtrarRegistrosPorPeriodo(registrosInternos, dataInicio, dataFim);
        
        // Calcular saldos
        calcularSaldos(conciliacao, lancamentosFiltrados, registrosFiltrados);
        
        // Executar algoritmos de matching
        executarMatching(conciliacao, lancamentosFiltrados, registrosFiltrados);
        
        // Calcular diferença final
        conciliacao.calcularDiferenca();
        
        // Definir status da conciliação
        definirStatusConciliacao(conciliacao);
        
        return conciliacao;
    }
    
    /**
     * Executa os algoritmos de matching em ordem de prioridade
     */
    private void executarMatching(Conciliacao conciliacao, 
                                List<LancamentoBancario> lancamentosBanco,
                                List<RegistroInterno> registrosInternos) {
        
        List<LancamentoBancario> lancamentosNaoConciliados = new ArrayList<>(lancamentosBanco);
        List<RegistroInterno> registrosNaoConciliados = new ArrayList<>(registrosInternos);
        
        // 1. Correspondência Exata
        executarMatchingExato(conciliacao, lancamentosNaoConciliados, registrosNaoConciliados);
        
        // 2. Correspondência por Valor e Data Aproximada
        executarMatchingPorValorEDataAproximada(conciliacao, lancamentosNaoConciliados, registrosNaoConciliados);
        
        // 3. Correspondência por Valor e Descrição
        executarMatchingPorValorEDescricao(conciliacao, lancamentosNaoConciliados, registrosNaoConciliados);
        
        // 4. Correspondência por Faixa de Valores
        executarMatchingPorFaixaValores(conciliacao, lancamentosNaoConciliados, registrosNaoConciliados);
        
        // Adicionar itens não conciliados à conciliação
        conciliacao.setLancamentosNaoConciliadosBanco(lancamentosNaoConciliados);
        conciliacao.setRegistrosNaoConciliadosInterno(registrosNaoConciliados);
    }
    
    /**
     * Matching por correspondência exata: Data, Valor e Tipo
     */
    private void executarMatchingExato(Conciliacao conciliacao,
                                     List<LancamentoBancario> lancamentosBanco,
                                     List<RegistroInterno> registrosInternos) {
        
        Iterator<LancamentoBancario> iteratorBanco = lancamentosBanco.iterator();
        
        while (iteratorBanco.hasNext()) {
            LancamentoBancario lancamento = iteratorBanco.next();
            
            Optional<RegistroInterno> registroCorrespondente = registrosInternos.stream()
                .filter(registro -> 
                    registro.getDataRegistro().equals(lancamento.getDataLancamento()) &&
                    registro.getValor().compareTo(lancamento.getValor()) == 0 &&
                    tiposCompativeis(lancamento.getTipoTransacao(), registro.getTipoMovimento())
                )
                .findFirst();
            
            if (registroCorrespondente.isPresent()) {
                RegistroInterno registro = registroCorrespondente.get();
                
                // Criar item conciliado
                ItemConciliado item = new ItemConciliado(lancamento, registro, TipoCorrespondencia.EXATA);
                conciliacao.adicionarItemConciliado(item);
                
                // Marcar registro como conciliado
                registro.marcarComoConciliado(lancamento.getId());
                
                // Remover das listas de não conciliados
                iteratorBanco.remove();
                registrosInternos.remove(registro);
            }
        }
    }
    
    /**
     * Matching por valor e data aproximada (tolerância de dias)
     */
    private void executarMatchingPorValorEDataAproximada(Conciliacao conciliacao,
                                                       List<LancamentoBancario> lancamentosBanco,
                                                       List<RegistroInterno> registrosInternos) {
        
        Iterator<LancamentoBancario> iteratorBanco = lancamentosBanco.iterator();
        
        while (iteratorBanco.hasNext()) {
            LancamentoBancario lancamento = iteratorBanco.next();
            
            Optional<RegistroInterno> registroCorrespondente = registrosInternos.stream()
                .filter(registro -> 
                    registro.getValor().compareTo(lancamento.getValor()) == 0 &&
                    tiposCompativeis(lancamento.getTipoTransacao(), registro.getTipoMovimento()) &&
                    Math.abs(ChronoUnit.DAYS.between(registro.getDataRegistro(), lancamento.getDataLancamento())) <= TOLERANCIA_DIAS
                )
                .findFirst();
            
            if (registroCorrespondente.isPresent()) {
                RegistroInterno registro = registroCorrespondente.get();
                
                ItemConciliado item = new ItemConciliado(lancamento, registro, TipoCorrespondencia.PARCIAL_DATA);
                item.setObservacoes("Conciliado com tolerância de data: " + 
                    ChronoUnit.DAYS.between(registro.getDataRegistro(), lancamento.getDataLancamento()) + " dias");
                conciliacao.adicionarItemConciliado(item);
                
                registro.marcarComoConciliado(lancamento.getId());
                
                iteratorBanco.remove();
                registrosInternos.remove(registro);
            }
        }
    }
    
    /**
     * Matching por valor e palavras-chave na descrição
     */
    private void executarMatchingPorValorEDescricao(Conciliacao conciliacao,
                                                  List<LancamentoBancario> lancamentosBanco,
                                                  List<RegistroInterno> registrosInternos) {
        
        Iterator<LancamentoBancario> iteratorBanco = lancamentosBanco.iterator();
        
        while (iteratorBanco.hasNext()) {
            LancamentoBancario lancamento = iteratorBanco.next();
            
            Optional<RegistroInterno> registroCorrespondente = registrosInternos.stream()
                .filter(registro -> 
                    registro.getValor().compareTo(lancamento.getValor()) == 0 &&
                    tiposCompativeis(lancamento.getTipoTransacao(), registro.getTipoMovimento()) &&
                    descricoesSimilares(lancamento.getHistorico(), registro.getDescricao())
                )
                .findFirst();
            
            if (registroCorrespondente.isPresent()) {
                RegistroInterno registro = registroCorrespondente.get();
                
                ItemConciliado item = new ItemConciliado(lancamento, registro, TipoCorrespondencia.PARCIAL_DESCRICAO);
                item.setObservacoes("Conciliado por similaridade de descrição");
                conciliacao.adicionarItemConciliado(item);
                
                registro.marcarComoConciliado(lancamento.getId());
                
                iteratorBanco.remove();
                registrosInternos.remove(registro);
            }
        }
    }
    
    /**
     * Matching por faixa de valores (tolerância de valor)
     */
    private void executarMatchingPorFaixaValores(Conciliacao conciliacao,
                                               List<LancamentoBancario> lancamentosBanco,
                                               List<RegistroInterno> registrosInternos) {
        
        Iterator<LancamentoBancario> iteratorBanco = lancamentosBanco.iterator();
        
        while (iteratorBanco.hasNext()) {
            LancamentoBancario lancamento = iteratorBanco.next();
            
            Optional<RegistroInterno> registroCorrespondente = registrosInternos.stream()
                .filter(registro -> {
                    BigDecimal diferenca = registro.getValor().subtract(lancamento.getValor()).abs();
                    return diferenca.compareTo(TOLERANCIA_VALOR) <= 0 &&
                           tiposCompativeis(lancamento.getTipoTransacao(), registro.getTipoMovimento()) &&
                           Math.abs(ChronoUnit.DAYS.between(registro.getDataRegistro(), lancamento.getDataLancamento())) <= TOLERANCIA_DIAS;
                })
                .findFirst();
            
            if (registroCorrespondente.isPresent()) {
                RegistroInterno registro = registroCorrespondente.get();
                
                ItemConciliado item = new ItemConciliado(lancamento, registro, TipoCorrespondencia.PARCIAL_VALOR);
                item.setObservacoes("Conciliado com tolerância de valor: " + item.getDiferenca());
                conciliacao.adicionarItemConciliado(item);
                
                registro.marcarComoConciliado(lancamento.getId());
                
                iteratorBanco.remove();
                registrosInternos.remove(registro);
            }
        }
    }
    
    /**
     * Conciliação manual de um lançamento bancário com um registro interno
     */
    public ItemConciliado conciliarManualmente(LancamentoBancario lancamento, 
                                             RegistroInterno registro, 
                                             String observacoes, 
                                             String usuario) {
        
        ItemConciliado item = new ItemConciliado(lancamento, registro, TipoCorrespondencia.MANUAL);
        item.setObservacoes(observacoes);
        item.setUsuarioConciliacao(usuario);
        
        registro.marcarComoConciliado(lancamento.getId());
        
        return item;
    }
    
    /**
     * Desconciliar um item previamente conciliado
     */
    public void desconciliar(ItemConciliado item) {
        if (item.getRegistroInterno() != null) {
            item.getRegistroInterno().setStatusConciliacao(StatusConciliacao.NAO_CONCILIADO);
            item.getRegistroInterno().setReferenciaBancaria(null);
        }
    }
    
    // Métodos auxiliares
    
    private List<LancamentoBancario> filtrarLancamentosPorPeriodo(List<LancamentoBancario> lancamentos,
                                                                LocalDate dataInicio, LocalDate dataFim) {
        return lancamentos.stream()
            .filter(l -> !l.getDataLancamento().isBefore(dataInicio) && !l.getDataLancamento().isAfter(dataFim))
            .collect(Collectors.toList());
    }
    
    private List<RegistroInterno> filtrarRegistrosPorPeriodo(List<RegistroInterno> registros,
                                                           LocalDate dataInicio, LocalDate dataFim) {
        return registros.stream()
            .filter(r -> !r.getDataRegistro().isBefore(dataInicio) && !r.getDataRegistro().isAfter(dataFim))
            .collect(Collectors.toList());
    }
    
    private void calcularSaldos(Conciliacao conciliacao, 
                              List<LancamentoBancario> lancamentosBanco,
                              List<RegistroInterno> registrosInternos) {
        
        // Calcular saldo bancário
        BigDecimal saldoBanco = lancamentosBanco.stream()
            .map(l -> l.getValor().multiply(BigDecimal.valueOf(l.getTipoTransacao().getMultiplicador())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular saldo interno
        BigDecimal saldoInterno = registrosInternos.stream()
            .map(r -> r.getValor().multiply(BigDecimal.valueOf(r.getTipoMovimento().getMultiplicador())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        conciliacao.setSaldoFinalBanco(saldoBanco);
        conciliacao.setSaldoFinalInterno(saldoInterno);
    }
    
    private boolean tiposCompativeis(TipoTransacao tipoTransacao, TipoMovimento tipoMovimento) {
        return (tipoTransacao.isEntrada() && tipoMovimento.isEntrada()) ||
               (tipoTransacao.isSaida() && tipoMovimento.isSaida());
    }
    
    private boolean descricoesSimilares(String historico, String descricao) {
        if (historico == null || descricao == null) {
            return false;
        }
        
        String[] palavrasHistorico = historico.toLowerCase().split("\\s+");
        String[] palavrasDescricao = descricao.toLowerCase().split("\\s+");
        
        // Verificar se pelo menos 50% das palavras são comuns
        Set<String> palavrasComuns = new HashSet<>(Arrays.asList(palavrasHistorico));
        palavrasComuns.retainAll(Arrays.asList(palavrasDescricao));
        
        int totalPalavras = Math.max(palavrasHistorico.length, palavrasDescricao.length);
        return palavrasComuns.size() >= (totalPalavras * 0.5);
    }
    
    private void definirStatusConciliacao(Conciliacao conciliacao) {
        if (conciliacao.isConciliacaoCompleta()) {
            conciliacao.setStatusConciliacao(StatusConciliacao.CONCILIADO);
        } else if (conciliacao.getTotalItensNaoConciliados() > 0) {
            conciliacao.setStatusConciliacao(StatusConciliacao.PENDENTE);
        } else {
            conciliacao.setStatusConciliacao(StatusConciliacao.EM_ANALISE);
        }
    }
}