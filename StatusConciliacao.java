package com.sistema.conciliacao.model;

public enum StatusConciliacao {
    NAO_CONCILIADO("Não Conciliado"),
    CONCILIADO("Conciliado"),
    PENDENTE("Pendente"),
    AJUSTADO("Ajustado"),
    EM_ANALISE("Em Análise"),
    CANCELADO("Cancelado");
    
    private final String descricao;
    
    StatusConciliacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}