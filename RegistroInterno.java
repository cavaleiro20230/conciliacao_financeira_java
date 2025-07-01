package com.sistema.conciliacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RegistroInterno {
    private Long id;
    private LocalDate dataRegistro;
    private String descricao;
    private BigDecimal valor;
    private TipoMovimento tipoMovimento;
    private StatusConciliacao statusConciliacao;
    private Long identificadorInterno;
    private String referenciaBancaria;
    private String observacoes;
    
    // Construtores
    public RegistroInterno() {
        this.statusConciliacao = StatusConciliacao.NAO_CONCILIADO;
    }
    
    public RegistroInterno(Long id, LocalDate dataRegistro, String descricao, 
                          BigDecimal valor, TipoMovimento tipoMovimento) {
        this.id = id;
        this.dataRegistro = dataRegistro;
        this.descricao = descricao;
        this.valor = valor;
        this.tipoMovimento = tipoMovimento;
        this.statusConciliacao = StatusConciliacao.NAO_CONCILIADO;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public TipoMovimento getTipoMovimento() { return tipoMovimento; }
    public void setTipoMovimento(TipoMovimento tipoMovimento) { this.tipoMovimento = tipoMovimento; }
    
    public StatusConciliacao getStatusConciliacao() { return statusConciliacao; }
    public void setStatusConciliacao(StatusConciliacao statusConciliacao) { this.statusConciliacao = statusConciliacao; }
    
    public Long getIdentificadorInterno() { return identificadorInterno; }
    public void setIdentificadorInterno(Long identificadorInterno) { this.identificadorInterno = identificadorInterno; }
    
    public String getReferenciaBancaria() { return referenciaBancaria; }
    public void setReferenciaBancaria(String referenciaBancaria) { this.referenciaBancaria = referenciaBancaria; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // Método para marcar como conciliado
    public void marcarComoConciliado(String referenciaBancaria) {
        this.statusConciliacao = StatusConciliacao.CONCILIADO;
        this.referenciaBancaria = referenciaBancaria;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroInterno that = (RegistroInterno) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("RegistroInterno{id=%d, data=%s, valor=%s, tipo=%s, status=%s}", 
                           id, dataRegistro, valor, tipoMovimento, statusConciliacao);
    }
}