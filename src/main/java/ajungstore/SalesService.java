package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  public String getNewNumberFactur() {
    String sql = "SELECT numberFactur FROM sales ORDER BY createdAt DESC LIMIT 1";
    try (Connection connection = Dbconnect.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery()) {

      if (resultSet.next()) {
        String lastNumberFactur = resultSet.getString("numberFactur");
        return generateNextNumberFactur(lastNumberFactur);
      } else {
        // Default value if there are no transactions
        return "AJUNG001";
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }

  private String generateNextNumberFactur(String lastNumberFactur) {
    // Extract the numeric part from the last number factur
    String numericPart = lastNumberFactur.replaceAll("\\D+", "");
    int number = Integer.parseInt(numericPart);

    // Increment the number
    number++;

    // Format the new number factur
    return String.format("AJUNG%03d", number);
  }

}
