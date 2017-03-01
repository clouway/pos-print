package com.clouway.pos.print.client;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ReceiptDetails {
  public static ReceiptDetails with(String customerName, String address, String contractNumber, Date contractDate){
    ReceiptDetails customerDetails = new ReceiptDetails();
    customerDetails.customerName = customerName;
    customerDetails.address = address;
    customerDetails.contractNumber = contractNumber;
    customerDetails.contractDate = contractDate;
    return customerDetails;
  }

  /** Full client name **/
  private String customerName;

  /** Address where service is provided **/
  private String address;

  /** Number of the contract **/
  private String contractNumber;

  /** Date on last annex **/
  private Date contractDate;

  private ReceiptDetails() {
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getAddress() {
    return address;
  }

  public String getContractNumber() {
    return contractNumber;
  }

  public Date getContractDate() {
    return contractDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReceiptDetails that = (ReceiptDetails) o;

    if (address != null ? !address.equals(that.address) : that.address != null) return false;
    if (contractDate != null ? !contractDate.equals(that.contractDate) : that.contractDate != null) return false;
    if (contractNumber != null ? !contractNumber.equals(that.contractNumber) : that.contractNumber != null)
      return false;
    if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = customerName != null ? customerName.hashCode() : 0;
    result = 31 * result + (address != null ? address.hashCode() : 0);
    result = 31 * result + (contractNumber != null ? contractNumber.hashCode() : 0);
    result = 31 * result + (contractDate != null ? contractDate.hashCode() : 0);
    return result;
  }
}
