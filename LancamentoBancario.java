package com.sistema.conciliacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class LancamentoBancario {
    private String id;
    private LocalDate dataLancamento;
    private String historico;
    private BigDecimal valor;
    private TipoTransacao tipoTransacao;
    private String agencia;
    private String conta;
    private BigDecimal saldoApos;
    private String identificadorUnico;
    
    // Construtores
    public LancamentoBancario() {}
    
    public LancamentoBancario(String id, LocalDate dataLancamento, String historico, 
                             BigDecimal valor, TipoTransacao tipoTransacao, 
                             String agencia, String conta) {
        this.id = id;
        this.dataLancamento = dataLancamento;
        this.historico = historico;
        this.valor = valor;
        this.tipoTransacao = tipoTransacao;
        this.agencia = agencia;
        this.conta = conta;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }
    
    public String getHistorico() { return historico; }
    public void setHistorico(String historico) { this.historico = historico; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public TipoTransacao getTipoTransacao() { return tipoTransacao; }
    public void setTipoTransacao(TipoTransacao tipoTransacao) { this.tipoTransacao = tipoTransacao; }
    
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    
    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }
    
    public BigDecimal getSaldoApos() { return saldoApos; }
    public void setSaldoApos(BigDecimal saldoApos) { this.saldoApos = saldoApos; }
    
    public String getIdentificadorUnico() { return identificadorUnico; }
    public void setIdentificadorUnico(String identificadorUnico) { this.identificadorUnico = identificadorUnico; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LancamentoBancario that = (LancamentoBancario) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(dataLancamento, that.dataLancamento) &&
               Objects.equals(valor, that.valor) &&
               tipoTransacao == that.tipoTransacao;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, dataLancamento, valor, tipoTransacao);
    }
    
    @Override
    public String toString() {
        return String.format("LancamentoBancario{id='%s', data=%s, valor=%s, tipo=%s, historico='%s'}", 
                           id, dataLancamento, valor, tipoTransacao, historico);
    }
}