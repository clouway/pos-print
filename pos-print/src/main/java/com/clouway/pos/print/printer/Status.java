package com.clouway.pos.print.printer;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public enum Status {
  SYNTAX_ERROR(0, 0, false),
  INVALID_COMMAND(0, 1, false),
  BROKEN_PRINTIN_MECHANISM(0, 4, false),
  GENERAL_ERROR(0, 5, false),
  COVER_IS_OPEN(0, 6, false),

  OVERFLOW_COMMAND(1, 0, false),
  COMMAND_NOT_PERMITTED(1, 1, false),

  END_OF_PAPER(2, 0, true),
  NEAR_PAPER_END(2, 1, true),
  EJ_IS_FULL(2, 2, false),
  FISCAL_RECEIPT_IS_OPEN(2, 3, true),
  EJ_NEARLY_FULL(2, 4, true),
  NON_FISCAL_RECEIPT_IS_OPEN(2, 5, true),

  FM_WRITE_ERROR(4, 0, false),
  TAX_NUMBER_NOT_SET(4, 0, false),
  TAX_NUMBER_IS_SET(4, 1, false),
  SN_AND_FM_ARE_SET(4, 2, false),
  FISCAL_MEMORY_ALMOST_FULL(4, 3, true),
  FISCAL_MEMORY_IS_FULL(4, 4, true),

  FM_IS_FORMATTED(5, 1, false),
  DEVICE_IS_FISCALIZED(5, 3, false),
  VAT_IS_SET(5, 4, false);

  private final int statusByte;
  private final int bit;
  private final boolean warning;

  Status(int statusByte, int bit, boolean warning) {
    this.statusByte = statusByte;
    this.bit = bit;
    this.warning = warning;
  }

  boolean isForWarning() {
    return warning;
  }

  boolean isSetIn(byte[] status) {
    byte statusx = status[statusByte];
    return (statusx & (1 << bit)) != 0;
  }
}
