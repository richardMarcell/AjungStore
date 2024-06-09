package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class CustomerService {

  private String name;
  private String phoneNumber;
  private String address;

  public void setName(String name) {
    this.name = name;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public List<String> getAllCustomerNames() {
    List<String> customerNames = new ArrayList<>();
    try (Connection connection = Dbconnect.getConnect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT name FROM customers")) {
      while (resultSet.next()) {
        customerNames.add(resultSet.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return customerNames;
  }

  public String getCustomerNameById(int id) {
    String sql = "SELECT name FROM customers WHERE id = ?";
    try (Connection connection = Dbconnect.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString("name");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }

  public int getCustomerIdByName(String name) {
    String sql = "SELECT id FROM customers WHERE name = ?";
    try (Connection connection = Dbconnect.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, name);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("id");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

}
