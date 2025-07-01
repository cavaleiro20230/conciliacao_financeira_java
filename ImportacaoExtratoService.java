package com.sistema.conciliacao.service;

import com.sistema.conciliacao.model.LancamentoBancario;
import com.sistema.conciliacao.model.TipoTransacao;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ImportacaoExtratoService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Importa lançamentos de um arquivo CSV
     */
    public List<LancamentoBancario> importarCSV(String caminhoArquivo) throws IOException {
        List<LancamentoBancario> lancamentos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeiraLinha = true;
            
            while ((linha = reader.readLine()) != null) {
                // Pular cabeçalho
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                
                LancamentoBancario lancamento = parsearLinhaCSV(linha);
                if (lancamento != null) {
                    lancamentos.add(lancamento);
                }
            }
        }
        
        return lancamentos;
    }
    
    /**
     * Importa lançamentos de um arquivo OFX
     */
    public List<LancamentoBancario> importarOFX(String caminhoArquivo) throws IOException {
        List<LancamentoBancario> lancamentos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            StringBuilder conteudo = new StringBuilder();
            String linha;
            
            while ((linha = reader.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
            
            lancamentos = parsearOFX(conteudo.toString());
        }
        
        return lancamentos;
    }
    
    /**
     * Importa lançamentos de um arquivo TXT com formato fixo
     */
    public List<LancamentoBancario> importarTXT(String caminhoArquivo) throws IOException {
        List<LancamentoBancario> lancamentos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            
            while ((linha = reader.readLine()) != null) {
                LancamentoBancario lancamento = parsearLinhaTXT(linha);
                if (lancamento != null) {
                    lancamentos.add(lancamento);
                }
            }
        }
        
        return lancamentos;
    }
    
    /**
     * Parseia uma linha CSV no formato: Data;Histórico;Valor;Tipo;Agência;Conta
     */
    private LancamentoBancario parsearLinhaCSV(String linha) {
        try {
            String[] campos = linha.split(";");
            
            if (campos.length < 6) {
                return null;
            }
            
            LancamentoBancario lancamento = new LancamentoBancario();
            lancamento.setId(UUID.randomUUID().toString());
            lancamento.setDataLancamento(LocalDate.parse(campos[0], DATE_FORMATTER));
            lancamento.setHistorico(campos[1].trim());
            lancamento.setValor(new BigDecimal(campos[2].replace(",", ".")));
            lancamento.setTipoTransacao(parsearTipoTransacao(campos[3]));
            lancamento.setAgencia(campos[4].trim());
            lancamento.setConta(campos[5].trim());
            
            return lancamento;
            
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha CSV: " + linha + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parseia uma linha TXT com posições fixas
     * Formato: DDMMAAAA + 50 chars histórico + 15 chars valor + 1 char tipo + 10 chars agência + 15 chars conta
     */
    private LancamentoBancario parsearLinhaTXT(String linha) {
        try {
            if (linha.length() < 99) {
                return null;
            }
            
            LancamentoBancario lancamento = new LancamentoBancario();
            lancamento.setId(UUID.randomUUID().toString());
            
            // Data (posições 0-7)
            String dataStr = linha.substring(0, 8);
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("ddMMyyyy"));
            lancamento.setDataLancamento(data);
            
            // Histórico (posições 8-57)
            lancamento.setHistorico(linha.substring(8, 58).trim());
            
            // Valor (posições 58-72)
            String valorStr = linha.substring(58, 73).trim().replace(",", ".");
            lancamento.setValor(new BigDecimal(valorStr));
            
            // Tipo (posição 73)
            String tipoStr = linha.substring(73, 74);
            lancamento.setTipoTransacao(tipoStr.equals("C") ? TipoTransacao.CREDITO : TipoTransacao.DEBITO);
            
            // Agência (posições 74-83)
            lancamento.setAgencia(linha.substring(74, 84).trim());
            
            // Conta (posições 84-98)
            lancamento.setConta(linha.substring(84, 99).trim());
            
            return lancamento;
            
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha TXT: " + linha + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parseia conteúdo OFX (implementação simplificada)
     */
    private List<LancamentoBancario> parsearOFX(String conteudo) {
        List<LancamentoBancario> lancamentos = new ArrayList<>();
        
        // Implementação simplificada - em produção, usar biblioteca específica para OFX
        String[] transacoes = conteudo.split("<STMTTRN>");
        
        for (String transacao : transacoes) {
            if (transacao.contains("<DTPOSTED>")) {
                try {
                    LancamentoBancario lancamento = new LancamentoBancario();
                    lancamento.setId(UUID.randomUUID().toString());
                    
                    // Extrair data
                    String data = extrairTagOFX(transacao, "DTPOSTED");
                    if (data.length() >= 8) {
                        LocalDate dataLancamento = LocalDate.parse(data.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        lancamento.setDataLancamento(dataLancamento);
                    }
                    
                    // Extrair valor
                    String valor = extrairTagOFX(transacao, "TRNAMT");
                    lancamento.setValor(new BigDecimal(valor));
                    
                    // Extrair histórico
                    String memo = extrairTagOFX(transacao, "MEMO");
                    lancamento.setHistorico(memo);
                    
                    // Determinar tipo baseado no valor
                    lancamento.setTipoTransacao(lancamento.getValor().compareTo(BigDecimal.ZERO) >= 0 ? 
                                              TipoTransacao.CREDITO : TipoTransacao.DEBITO);
                    
                    lancamentos.add(lancamento);
                    
                } catch (Exception e) {
                    System.err.println("Erro ao parsear transação OFX: " + e.getMessage());
                }
            }
        }
        
        return lancamentos;
    }
    
    private String extrairTagOFX(String conteudo, String tag) {
        String tagInicio = "<" + tag + ">";
        String tagFim = "</" + tag + ">";
        
        int inicio = conteudo.indexOf(tagInicio);
        if (inicio == -1) return "";
        
        inicio += tagInicio.length();
        int fim = conteudo.indexOf(tagFim, inicio);
        if (fim == -1) return "";
        
        return conteudo.substring(inicio, fim).trim();
    }
    
    private TipoTransacao parsearTipoTransacao(String tipo) {
        switch (tipo.toUpperCase()) {
            case "C":
            case "CREDITO":
            case "ENTRADA":
                return TipoTransacao.CREDITO;
            case "D":
            case "DEBITO":
            case "SAIDA":
                return TipoTransacao.DEBITO;
            default:
                return TipoTransacao.DEBITO;
        }
    }
    
    /**
     * Valida se o arquivo tem o formato esperado
     */
    public boolean validarFormatoArquivo(String caminhoArquivo, String tipoArquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String primeiraLinha = reader.readLine();
            
            switch (tipoArquivo.toUpperCase()) {
                case "CSV":
                    return primeiraLinha != null && primeiraLinha.contains(";");
                case "OFX":
                    return primeiraLinha != null && primeiraLinha.contains("OFX");
                case "TXT":
                    return primeiraLinha != null && primeiraLinha.length() >= 99;
                default:
                    return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}