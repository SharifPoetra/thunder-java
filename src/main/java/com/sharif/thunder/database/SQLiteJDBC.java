package com.sharif.thunder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import lombok.Getter;

public class SQLiteJDBC {

  @Getter private Connection connection = null;
  @Getter private Statement statement;

  public SQLiteJDBC() {
    try {
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection("jdbc:h2:./database/test");
      statement = connection.createStatement();
      statement.setQueryTimeout(30);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("SUCCESS: SQLite database connection successfully.");
  }

  public String initializeTable(String name, String columns) {
    String pluginName = new Exception().getStackTrace()[1].getClassName();
    String[] split = pluginName.toLowerCase().split("\\.");
    String tableName = split[split.length - 1] + "_" + name;
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
    try {
      PreparedStatement ps = connection.prepareStatement(sql);
      ps.executeUpdate();
      ps.close();
      System.out.println("DATABASE: table " + tableName + " loaded.");
      return tableName;

    } catch (SQLException ex) {
      ex.printStackTrace();
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
      return null;
    }
  }
}
