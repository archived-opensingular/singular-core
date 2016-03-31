/**
 * Mirante Tecnologia - Fábrica de Software
 * www.mirante.net.br
 */
package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

/**
 * @author Denis Coutinho
 * @since 16/12/2011
 *
 */
public enum TipoAnexo {

  OFICIO("O", "Oficio"),
  PARECER("P", "Parecer"),
  OUTROS_ARQUIVOS("X", "Outros Arquivos"),
  DESPACHO("D", "Despacho"),
  ATA("A", "Ata");

  private String codigo;

  private String descricao;

  private TipoAnexo(String codigo, String descricao) {
    this.codigo = codigo;
    this.descricao = descricao;
  }

  /**
   * @return the codigo
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * @param codigo
   *          the codigo to set
   */
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  /**
   * @return the descricao
   */
  public String getDescricao() {
    return descricao;
  }

  /**
   * @param descricao
   *          the descricao to set
   */
  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public static TipoAnexo valueOfEnum(String codigo) {
    TipoAnexo status[] = TipoAnexo.values();

    for (TipoAnexo st : status) {
      if (codigo != null && codigo.equals(st.getCodigo())) {
        return st;
      }
    }
    return null;
  }

}
