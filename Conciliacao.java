package com.sistema.conciliacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Conciliacao {
    private Long id;
    private LocalDate dataConciliacao;
    private LocalDateTime dataProcessamento;
    private String contaBancaria;
    private String agencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    // Saldos
    private BigDecimal saldoInicialBanco;
    private BigDecimal saldoFinalBanco;
    private BigDecimal saldoInicialInterno;
    private BigDecimal saldoFinalInterno;
    private BigDecimal diferenca;
    
    // Listas de itens
    private List<ItemConciliado> itensConciliados;
    private List<LancamentoBancario> lancamentosNaoConciliadosBanco;
    private List<RegistroInterno> registrosNaoConciliadosInterno;
    private List<AjusteConciliacao> ajustes;
    
    // Status da conciliação
    private StatusConciliacao statusConciliacao;
    private String observacoes;
    private String usuarioResponsavel;
    
    // Construtores
    public Conciliacao() {
        this.itensConciliados = new ArrayList<>();
        this.lancamentosNaoConciliadosBanco = new ArrayList<>();
        this.registrosNaoConciliadosInterno = new ArrayList<>();
        this.ajustes = new ArrayList<>();
        this.statusConciliacao = StatusConciliacao.NAO_CONCILIADO;
        this.dataProcessamento = LocalDateTime.now();
    }
    
    public Conciliacao(String contaBancaria, String agencia, LocalDate dataInicio, LocalDate dataFim) {
        this();
        this.contaBancaria = contaBancaria;
        this.agencia = agencia;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataConciliacao = LocalDate.now();
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDataConciliacao() { return dataConciliacao; }
    public void setDataConciliacao(LocalDate dataConciliacao) { this.dataConciliacao = dataConciliacao; }
    
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDateTime dataProcessamento) { this.dataProcessamento = dataProcessamento; }
    
    public String getContaBancaria() { return contaBancaria; }
    public void setContaBancaria(String contaBancaria) { this.contaBancaria = contaBancaria; }
    
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    
    public BigDecimal getSaldoInicialBanco() { return saldoInicialBanco; }
    public void setSaldoInicialBanco(BigDecimal saldoInicialBanco) { this.saldoInicialBanco = saldoInicialBanco; }
    
    public BigDecimal getSaldoFinalBanco() { return saldoFinalBanco; }
    public void setSaldoFinalBanco(BigDecimal saldoFinalBanco) { this.saldoFinalBanco = saldoFinalBanco; }
    
    public BigDecimal getSaldoInicialInterno() { return saldoInicialInterno; }
    public void setSaldoInicialInterno(BigDecimal saldoInicialInterno) { this.saldoInicialInterno = saldoInicialInterno; }
    
    public BigDecimal getSaldoFinalInterno() { return saldoFinalInterno; }
    public void setSaldoFinalInterno(BigDecimal saldoFinalInterno) { this.saldoFinalInterno = saldoFinalInterno; }
    
    public BigDecimal getDiferenca() { return diferenca; }
    public void setDiferenca(BigDecimal diferenca) { this.diferenca = diferenca; }
    
    public List<ItemConciliado> getItensConciliados() { return itensConciliados; }
    public void setItensConciliados(List<ItemConciliado> itensConciliados) { this.itensConciliados = itensConciliados; }
    
    public List<LancamentoBancario> getLancamentosNaoConciliadosBanco() { return lancamentosNaoConciliadosBanco; }
    public void setLancamentosNaoConciliadosBanco(List<LancamentoBancario> lancamentosNaoConciliadosBanco) { 
        this.lancamentosNaoConciliadosBanco = lancamentosNaoConciliadosBanco; 
    }
    
    public List<RegistroInterno> getRegistrosNaoConciliadosInterno() { return registrosNaoConciliadosInterno; }
    public void setRegistrosNaoConciliadosInterno(List<RegistroInterno> registrosNaoConciliadosInterno) { 
        this.registrosNaoConciliadosInterno = registrosNaoConciliadosInterno; 
    }
    
    public List<AjusteConciliacao> getAjustes() { return ajustes; }
    public void setAjustes(List<AjusteConciliacao> ajustes) { this.ajustes = ajustes; }
    
    public StatusConciliacao getStatusConciliacao() { return statusConciliacao; }
    public void setStatusConciliacao(StatusConciliacao statusConciliacao) { this.statusConciliacao = statusConciliacao; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }
    
    // Métodos utilitários
    public void calcularDiferenca() {
        if (saldoFinalBanco != null && saldoFinalInterno != null) {
            this.diferenca = saldoFinalBanco.subtract(saldoFinalInterno);
        }
    }
    
    public void adicionarItemConciliado(ItemConciliado item) {
        this.itensConciliados.add(item);
    }
    
    public void adicionarLancamentoNaoConciliado(LancamentoBancario lancamento) {
        this.lancamentosNaoConciliadosBanco.add(lancamento);
    }
    
    public void adicionarRegistroNaoConciliado(RegistroInterno registro) {
        this.registrosNaoConciliadosInterno.add(registro);
    }
    
    public void adicionarAjuste(AjusteConciliacao ajuste) {
        this.ajustes.add(ajuste);
    }
    
    public boolean isConciliacaoCompleta() {
        return diferenca != null && diferenca.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public int getTotalItensConciliados() {
        return itensConciliados.size();
    }
    
    public int getTotalItensNaoConciliados() {
        return lancamentosNaoConciliadosBanco.size() + registrosNaoConciliadosInterno.size();
    }
}