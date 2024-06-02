package ajungstore;

import java.time.LocalDate;

public class SalesService {
  protected int idSale;
  protected int customerId;
  protected int userId;
  protected LocalDate transactionDate;
  protected String salesStatus;
  protected String numberFactur;
  protected int totalQuantity;
  protected double totalSales;
  protected double totalPayment;

  public void setIdSale(int id) {
    idSale = id;
  }

  public int getSaleId() {
    return idSale;
  }

  public void setCustomerId(int id) {
    customerId = id;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void setUserId(int id) {
    userId = id;
  }

  public int getUserId() {
    return userId;
  }

  public void setTransactionDate(LocalDate date) {
    transactionDate = date;
  }

  public LocalDate getTransactionDate() {
    return transactionDate;
  }

  public void setStatus(String status) {
    salesStatus = status;
  }

  public String getStatus() {
    return salesStatus;
  }

  public void setNumberFactur(String number) {
    numberFactur = number;
  }

  public String getNumberFactur() {
    return numberFactur;
  }

  public void setTotalQuantity(int quantity) {
    totalQuantity = quantity;
  }

  public int getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalSales(double sales) {
    totalSales = sales;
  }

  public double getTotalSales() {
    return totalSales;
  }

  public void setTotalPayment(double payment) {
    totalPayment = payment;
  }

  public double getTotalPayment() {
    return totalPayment;
  }

}
