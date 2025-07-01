package com.sistema.conciliacao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AjusteConciliacao {
    private Long id;
    private TipoAjuste tipoAjuste;
    private String descricao;
    private BigDecimal valor;
    private String justificativa;
    private LocalDateTime dataAjuste;
    private String usuarioAjuste;
    private String contaContabil;
    private StatusAjuste statusAjuste;
    
    // Construtores
    public AjusteConciliacao() {
        this.dataAjuste = LocalDateTime.now();
        this.statusAjuste = StatusAjuste.PENDENTE;
    }
    
    public AjusteConciliacao(TipoAjuste tipoAjuste, String descricao, BigDecimal valor, String justificativa) {
        this();
        this.tipoAjuste = tipoAjuste;
        this.descricao = descricao;
        this.valor = valor;
        this.justificativa = justificativa;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public TipoAjuste getTipoAjuste() { return tipoAjuste; }
    public void setTipoAjuste(TipoAjuste tipoAjuste) { this.tipoAjuste = tipoAjuste; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    
    public LocalDateTime getDataAjuste() { return dataAjuste; }
    public void setDataAjuste(LocalDateTime dataAjuste) { this.dataAjuste = dataAjuste; }
    
    public String getUsuarioAjuste() { return usuarioAjuste; }
    public void setUsuarioAjuste(String usuarioAjuste) { this.usuarioAjuste = usuarioAjuste; }
    
    public String getContaContabil() { return contaContabil; }
    public void setContaContabil(String contaContabil) { this.contaContabil = contaContabil; }
    
    public StatusAjuste getStatusAjuste() { return statusAjuste; }
    public void setStatusAjuste(StatusAjuste statusAjuste) { this.statusAjuste = statusAjuste; }
    
    // Enums internos
    public enum TipoAjuste {
        TAXA_BANCARIA("Taxa Bancária"),
        JUROS("Juros"),
        ERRO_DIGITACAO("Erro de Digitação"),
        LANCAMENTO_DUPLICADO("Lançamento Duplicado"),
        DIFERENCA_CAMBIAL("Diferença Cambial"),
        OUTROS("Outros");
        
        private final String descricao;
        
        TipoAjuste(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    public enum StatusAjuste {
        PENDENTE("Pendente"),
        APROVADO("Aprovado"),
        REJEITADO("Rejeitado"),
        APLICADO("Aplicado");
        
        private final String descricao;
        
        StatusAjuste(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
}