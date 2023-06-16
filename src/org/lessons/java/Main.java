package org.lessons.java;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:8889/dump_nations";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password)){
            System.out.println(connection.getCatalog());
            String sql = """
                    SELECT countries.country_id,countries.name,regions.name, continents.name
                            FROM `countries`
                            JOIN `regions`
                            ON countries.region_id = regions.region_id
                            JOIN `continents`
                            ON regions.continent_id = continents.continent_id
                            ORDER BY `countries`.`name` ASC;""";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()) {
                        int country_id = rs.getInt(1);
                        String country = rs.getString(2);
                        String region = rs.getString(3);
                        String continent = rs.getString(4);
                        System.out.println(country_id + " - " + country + " - " + region + " - " + continent);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
        }
    }
}
