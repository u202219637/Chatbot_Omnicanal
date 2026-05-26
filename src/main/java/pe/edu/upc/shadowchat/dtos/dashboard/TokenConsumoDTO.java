package pe.edu.upc.shadowchat.dtos.dashboard;
public class TokenConsumoDTO {
    private String dia;
    private Long tokensEntrada;
    private Long tokensSalida;
    private Long tokensTotal;
    public String getDia()                   { return dia; }
    public void setDia(String v)             { this.dia = v; }
    public Long getTokensEntrada()           { return tokensEntrada; }
    public void setTokensEntrada(Long v)     { this.tokensEntrada = v; }
    public Long getTokensSalida()            { return tokensSalida; }
    public void setTokensSalida(Long v)      { this.tokensSalida = v; }
    public Long getTokensTotal()             { return tokensTotal; }
    public void setTokensTotal(Long v)       { this.tokensTotal = v; }
}