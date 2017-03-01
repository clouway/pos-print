package com.clouway.servicebroker.print;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PrinterError {

  private static final PrinterError FISCAL_NUMBER_READ_ERROR = new PrinterError("Фискалния номер неможе да бъде прочетен.");
    
  private static final PrinterError PRINTER_STATUS_READ_ERROR = new PrinterError("Състоянието на принтера неможе да бъде прочетено.");

  private static final PrinterError TEXT_MESSAGE_CONVERSION_ERROR = new PrinterError("Въведения текст неможе да бъде конвертиран във формата на принтера.");
  private static final PrinterError TEXT_MESSAGE_PRINT_ERROR = new PrinterError("Възникнала е грешка при отпечатването на текста.");

  private static final PrinterError PAYMENT_ERROR = new PrinterError("Възникнала е грешка при затваряне на бона.");

  private static final PrinterError OPEN_BON_ERROR = new PrinterError("Бонът неможе да бъде отворен.");
  private static final PrinterError CLOSE_BON_ERROR = new PrinterError("Бонът неможе да бъде затворен.");

  private static final PrinterError LINE_FEED_ERROR = new PrinterError("Бонът неможе да бъде затворен.");

  private static final PrinterError DEVICE_ERROR = new PrinterError("Възникнал е проблем с устройството.");


  private static final PrinterError OPEN_FISCAL_BON_ERROR = new PrinterError("Фискалния бон неможе да бъде отворен.");
  private static final PrinterError CLOSE_FISCAL_BON_ERROR = new PrinterError("Фискалния бон неможе да бъде затворен.");

  private static final PrinterError SYSTEM_ERROR = new PrinterError("Възникнала е системна грешка в приложението.");

  private static final PrinterError BON_ALREADY_OPEN = new PrinterError("Бонът е вече отворен.");

  private static final PrinterError DATA_SYNTAX_ERROR = new PrinterError("Възникнала е грешка при проблем с данните.");

  public static PrinterError systemError() {
    return SYSTEM_ERROR;
  }


  public static PrinterError dataSyntax() {
    return DATA_SYNTAX_ERROR;
  }


  public static PrinterError closeFiscalBon() {
    return CLOSE_FISCAL_BON_ERROR;
  }

  public static PrinterError closeBon() {
    return CLOSE_BON_ERROR;
  }

  public static PrinterError deviceError() {
    return DEVICE_ERROR;
  }

  public static PrinterError openFiscalBon() {
    return OPEN_FISCAL_BON_ERROR;
  }

  public static PrinterError lineFeed() {
    return LINE_FEED_ERROR;
  }

  public static PrinterError payment() {
    return PAYMENT_ERROR;
  }

  public static PrinterError fiscalNumber() {
    return FISCAL_NUMBER_READ_ERROR;
  }

  public static PrinterError readStatus() {
    return PRINTER_STATUS_READ_ERROR;
  }

  public static PrinterError invalidTextEncoding() {
    return TEXT_MESSAGE_CONVERSION_ERROR;
  }

  public static PrinterError printText() {
    return TEXT_MESSAGE_PRINT_ERROR;
  }

  public static PrinterError openBon() {
    return OPEN_BON_ERROR;
  }

  public static PrinterError bonAlreadyOpened() {
    return BON_ALREADY_OPEN;
  }

  private final String errorMessage;

  private PrinterError(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PrinterError that = (PrinterError) o;

    return errorMessage.equals(that.errorMessage);
  }


  @Override
  public int hashCode() {
    return errorMessage.hashCode();
  }

}
