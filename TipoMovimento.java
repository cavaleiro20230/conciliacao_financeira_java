package com.sistema.conciliacao.model;

public enum TipoMovimento {
    RECEBIMENTO("Recebimento", 1),
    PAGAMENTO("Pagamento", -1),
    TRANSFERENCIA_ENTRADA("Transferência - Entrada", 1),
    TRANSFERENCIA_SAIDA("Transferência - Saída", -1),
    AJUSTE_POSITIVO("Ajuste Positivo", 1),
    AJUSTE_NEGATIVO("Ajuste Negativo", -1);
    
    private final String descricao;
    private final int multiplicador;
    
    TipoMovimento(String descricao, int multiplicador) {
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