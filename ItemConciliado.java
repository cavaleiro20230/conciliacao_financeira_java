package com.sistema.conciliacao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemConciliado {
    private Long id;
    private LancamentoBancario lancamentoBancario;
    private RegistroInterno registroInterno;
    private TipoCorrespondencia tipoCorrespondencia;
    private BigDecimal diferenca;
    private String observacoes;
    private LocalDateTime dataConciliacao;
    private String usuarioConciliacao;
    
    // Construtores
    public ItemConciliado() {
        this.dataConciliacao = LocalDateTime.now();
    }
    
    public ItemConciliado(LancamentoBancario lancamentoBancario, RegistroInterno registroInterno, 
                         TipoCorrespondencia tipoCorrespondencia) {
        this();
        this.lancamentoBancario = lancamentoBancario;
        this.registroInterno = registroInterno;
        this.tipoCorrespondencia = tipoCorrespondencia;
        calcularDiferenca();
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LancamentoBancario getLancamentoBancario() { return lancamentoBancario; }
    public void setLancamentoBancario(LancamentoBancario lancamentoBancario) { 
        this.lancamentoBancario = lancamentoBancario;
        calcularDiferenca();
    }
    
    public RegistroInterno getRegistroInterno() { return registroInterno; }
    public void setRegistroInterno(RegistroInterno registroInterno) { 
        this.registroInterno = registroInterno;
        calcularDiferenca();
    }
    
    public TipoCorrespondencia getTipoCorrespondencia() { return tipoCorrespondencia; }
    public void setTipoCorrespondencia(TipoCorrespondencia tipoCorrespondencia) { this.tipoCorrespondencia = tipoCorrespondencia; }
    
    public BigDecimal getDiferenca() { return diferenca; }
    public void setDiferenca(BigDecimal diferenca) { this.diferenca = diferenca; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public LocalDateTime getDataConciliacao() { return dataConciliacao; }
    public void setDataConciliacao(LocalDateTime dataConciliacao) { this.dataConciliacao = dataConciliacao; }
    
    public String getUsuarioConciliacao() { return usuarioConciliacao; }
    public void setUsuarioConciliacao(String usuarioConciliacao) { this.usuarioConciliacao = usuarioConciliacao; }
    
    // Métodos utilitários
    private void calcularDiferenca() {
        if (lancamentoBancario != null && registroInterno != null) {
            this.diferenca = lancamentoBancario.getValor().subtract(registroInterno.getValor());
        }
    }
    
    public boolean temDiferenca() {
        return diferenca != null && diferenca.compareTo(BigDecimal.ZERO) != 0;
    }
    
    public boolean isConciliacaoExata() {
        return !temDiferenca();
    }
}