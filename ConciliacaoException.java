package com.sistema.conciliacao.exception;

public class ConciliacaoException extends Exception {
    
    private String codigo;
    
    public ConciliacaoException(String mensagem) {
        super(mensagem);
    }
    
    public ConciliacaoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }
    
    public ConciliacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
    
    public ConciliacaoException(String codigo, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    // Exceções específicas
    public static class ArquivoInvalidoException extends ConciliacaoException {
        public ArquivoInvalidoException(String mensagem) {
            super("ARQUIVO_INVALIDO", mensagem);
        }
    }
    
    public static class DadosInconsistentesException extends ConciliacaoException {
        public DadosInconsistentesException(String mensagem) {
            super("DADOS_INCONSISTENTES", mensagem);
        }
    }
    
    public static class ConciliacaoJaExisteException extends ConciliacaoException {
        public ConciliacaoJaExisteException(String mensagem) {
            super("CONCILIACAO_JA_EXISTE", mensagem);
        }
    }
    
    public static class PermissaoNegadaException extends ConciliacaoException {
        public PermissaoNegadaException(String mensagem) {
            super("PERMISSAO_NEGADA", mensagem);
        }
    }
}