package com.sistema.conciliacao.model;

public enum TipoTransacao {
    ENTRADA("Entrada", 1),
    SAIDA("Saída", -1),
    DEBITO("Débito", -1),
    CREDITO("Crédito", 1);
    
    private final String descricao;
    private final int multiplicador;
    
    TipoTransacao(String descricao, int multiplicador) {
        this.descricao = descricao;
        this.multiplicador = multiplicador;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public int getMultiplicador() {
        return multiplicador;
    }
    
    public boolean isEntrada() {
        return multiplicador > 0;
    }
    
    public boolean isSaida() {
        return multiplicador < 0;
    }
}