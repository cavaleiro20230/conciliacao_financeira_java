package com.sistema.conciliacao.model;

public enum TipoCorrespondencia {
    EXATA("Correspondência Exata"),
    PARCIAL_DATA("Correspondência Parcial por Data"),
    PARCIAL_VALOR("Correspondência Parcial por Valor"),
    PARCIAL_DESCRICAO("Correspondência Parcial por Descrição"),
    MANUAL("Conciliação Manual"),
    AUTOMATICA("Conciliação Automática");
    
    private final String descricao;
    
    TipoCorrespondencia(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}