package ajungstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class ProductService {
  public List<String> getAllProductName() {
    List<String> productNames = new ArrayList<>();
    try (Connection connection = Dbconnect.getConnect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT name FROM products")) {
      while (resultSet.next()) {
        productNames.add(resultSet.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return productNames;
  }

  public int getProductIdByName(String productName) {
    int productId = -1; // Set default value to handle cases where no product is found
    String query = "SELECT id FROM products WHERE name = ?";
    try (Connection connection = Dbconnect.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, productName);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          productId = resultSet.getInt("id");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return productId;
  }

  public double getProductPrice(String productName) {
    double productPrice = 0.0; // Set default value to handle cases where no product is found
    String query = "SELECT price FROM products WHERE name = ?";
    try (Connection connection = Dbconnect.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, productName);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          productPrice = resultSet.getDouble("price");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return productPrice;
  }

}
