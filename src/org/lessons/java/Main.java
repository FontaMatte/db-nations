package org.lessons.java;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:8889/dump_nations";
        String user = "root";
        String password = "root";

        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(url, user, password)){
            System.out.println(connection.getCatalog());

            System.out.println("which country are you looking for? ");
            String countrySearched = scanner.nextLine();

            String sql = """
                    SELECT countries.country_id,countries.name,regions.name, continents.name
                            FROM `countries`
                            JOIN `regions`
                            ON countries.region_id = regions.region_id
                            JOIN `continents`
                            ON regions.continent_id = continents.continent_id
                            WHERE `countries`.`name` LIKE ?
                            ORDER BY `countries`.`name` ASC;""";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1,"%" + countrySearched + "%");
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
