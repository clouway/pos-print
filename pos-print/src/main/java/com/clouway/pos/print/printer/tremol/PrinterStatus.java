package com.clouway.pos.print.printer.tremol;


class PrinterStatus {

    protected byte[] m_status;

    /** Creates a new instance of PrinterStatus */
    public PrinterStatus(byte[] output, int outputLen, int lang) throws ZFPException
    {
        if (12 != outputLen)
            throw new ZFPException(0x106, lang);

        m_status = new byte[5];

        try {
            System.arraycopy(output, 4, m_status, 0, 5);
        } catch (Exception e) {
            throw new ZFPException(e);
        }
    }

    /** Gets Tax Memory read status
     *  @return Read Only when true
     */
    public boolean getReadOnlyFM()
    {
        return (0 < (m_status[0] & 0x01)) ? true : false;
    }

    /** Gets Power Down status
     *  @return Preceding power down when true
     */
    public boolean isPowerDown()
    {
        return (0 < (m_status[0] & 0x02)) ? true : false;
    }

    /** Gets Printer head status
     *  @return Printer overheated when true
     */
    public boolean isPrinterOverheat()
    {
        return (0 < (m_status[0] & 0x04)) ? true : false;
    }

    /** Gets time status
     *  @return Time wrong when true
     */
    public boolean isWrongTimer()
    {
        return (0 < (m_status[0] & 0x08)) ? true : false;
    }

    /** Gets date status
     *  @return Date wrong when true
     */
    public boolean istWrongDate()
    {
        return (0 < (m_status[0] & 0x10)) ? true : false;
    }

    /** Gets RAM status
     *  @return wrong RAM when true
     */
    public boolean isWrongRAM()
    {
        return (0 < (m_status[0] & 0x20)) ? true : false;
    }

    /** Gets clock hardware status
     *  @return clock hardware error when true
     */
    public boolean isClockHardwareError()
    {
        return (0 < (m_status[0] & 0x40)) ? true : false;
    }

    /** Gets printer paper status
     *  @return Out of paper when true
     */
    public boolean isPaperOut()
    {
        return (0 < (m_status[1] & 0x01)) ? true : false;
    }

    /** Gets daily reports accumulator overflow status
     *  @return overflow when true
     */
    public boolean isReportSumOverflow()
    {
        return (0 < (m_status[1] & 0x02)) ? true : false;
    }

    /** Gets status of 24h Daily Report block
     *  @return Blocked because of 24h Daily Report block when true (valid for Romania only)
     */
    public boolean isBlocked24HoursReport()
    {
        return (0 < (m_status[1] & 0x04)) ? true : false;
    }

    /** Gets zero status of Daily Report
     *  @return Not zero when true
     */
    public boolean isNonzeroDailyReport()
    {
        return (0 < (m_status[1] & 0x08)) ? true : false;
    }

    /** Gets zero status of Item Report
     *  @return Not zero when true
     */
    public boolean isNonzeroArticleReport()
    {
        return (0 < (m_status[1] & 0x10)) ? true : false;
    }

    /** Gets zero status of Operators Report
     *  @return Not zero when true
     */
    public boolean isNonzeroOperatorReport()
    {
        return (0 < (m_status[1] & 0x20)) ? true : false;
    }

    /** Gets duplicate print status
     *  @return Not printed when true
     */
    public boolean isDuplicateNotPrinted()
    {
        return (0 < (m_status[1] & 0x40)) ? true : false;
    }

    /** Gets offical receipt status
     *  @return Official receipt opened when true
     */
    public boolean isOpenOfficialBon()
    {
        return (0 < (m_status[2] & 0x01)) ? true : false;
    }

    /** Gets legal receipt status
     *  @return Legal receipt opened when true
     */
    public boolean isOpenFiscalBon()
    {
        return (0 < (m_status[2] & 0x02)) ? true : false;
    }

    /** Gets receipt format status
     *  @return detailed receipt opened when true
     */
    public boolean isDetailedInfo()
    {
        return (0 < (m_status[2] & 0x04)) ? true : false;
    }

    /** Gets VAT print out status
     *  @return detailed VAT print out in the receipt when true
     */
    public boolean isVATinfo()
    {
        return (0 < (m_status[2] & 0x08)) ? true : false;
    }

    /** Check Tax Memory presence
     *  @return missing Tax Memory when true
     */
    public boolean isMissingFiscalMemory()
    {
        return (0 < (m_status[3] & 0x01)) ? true : false;
    }

    /** Get Tax Memory propriety status
     *  @return Wrong Tax Memory when true
     */
    public boolean isWrongFiscalMemory()
    {
        return (0 < (m_status[3] & 0x02)) ? true : false;
    }

    /** Get Tax Memory load status
     *  @return Full Tax Memory when true
     */
    public boolean isFullFiscalMemory()
    {
        return (0 < (m_status[3] & 0x04)) ? true : false;
    }

    /** Get Tax Memory load status
     *  @return Tax Memory nearly full when true
     */
    public boolean isFiscalMemoryLimitNear()
    {
        return (0 < (m_status[3] & 0x08)) ? true : false;
    }

    /** Get decimal point status
     *  @return sums with decimal point when true
     */

    public boolean hasDecimalPoint()
    {
        return (0 < (m_status[3] & 0x10)) ? true : false;
    }

    /** Get fiscal status
     *  @return Printer is Fiscalized when true
     */
    public boolean isFiscal()
    {
        return (0 < (m_status[3] & 0x20)) ? true : false;
    }

    /** Get fiscal and factory number status
     *  @return Fiscal and factory numbers are set when true
     */
    public boolean hasFiscalAndFactoryNum()
    {
        return (0 < (m_status[3] & 0x40)) ? true : false;
    }

    /** Get cutter status
     *  @return Has automatic cutter when true
     */
    public boolean hasAutoCutter()
    {
        return (0 < (m_status[4] & 0x01)) ? true : false;
    }

    /** Get display status
     *  @return The display is transparent when true
     */
    public boolean hasTransparentDisplay()
    {
        return (0 < (m_status[4] & 0x02)) ? true : false;
    }

    /** Get baud rate settings
     *  @return The baud rate 9600 or 19200
     */
    public int getBaud()
    {
        return (0 < (m_status[4] & 0x04)) ? 9600 : 19200;
    }

    /** Get cash drawer opening status
     *  @return Drawer is open automaticaly when true
     */
    public boolean isAutoTillOpen()
    {
        return (0 < (m_status[4] & 0x10)) ? true : false;
    }

    /** Get graphic logo printing status
     *  @return The logo is printed when true
     */
    public boolean isPrintLogo()
    {
        return (0 < (m_status[4] & 0x20)) ? true : false;
    }

    /** Check external display presence
     *  @return The display is missing when true (valid only for Romania)
     */
    public boolean isMissingDisplay()
    {
        return (0 < (m_status[4] & 0x08)) ? true : false;
    }
}

