package ajungstore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class CustomerService {
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
}
