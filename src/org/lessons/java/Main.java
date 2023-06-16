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
                        System.out.println("ID: " + country_id + " - " + country + " - " + region + " - " + continent);
                    }
                }
            }

            System.out.println("choose a country ID among the filtered ones: ");
            int countryId = Integer.parseInt(scanner.nextLine());

            String languageSql = """
                SELECT `language`
                FROM `languages`
                JOIN `country_languages`
                ON languages.language_id = country_languages.language_id
                WHERE country_id = ?;""";
            try (PreparedStatement languagePs = connection.prepareStatement(languageSql)) {
                languagePs.setInt(1, countryId);
                try (ResultSet languageRs = languagePs.executeQuery()) {
                    System.out.println("Languages speak: ");
                    while (languageRs.next()) {
                        String language = languageRs.getString(1);
                        System.out.println(" - " + language);
                    }
                }
            }

            String statsSql = """
                SELECT *
                FROM `countries`
                JOIN `country_stats` ON countries.country_id = country_stats.country_id
                WHERE countries.country_id = ?
                AND country_stats.year = (
                    SELECT MAX(year)
                    FROM `country_stats`
                    WHERE country_id = ?
                );""";
            try (PreparedStatement statsPs = connection.prepareStatement(statsSql)) {
                statsPs.setInt(1, countryId);
                statsPs.setInt(2, countryId);
                try (ResultSet statsRs = statsPs.executeQuery()) {
                    System.out.println("Most recently statistics: ");
                    if (statsRs.next()) {
                        long population = statsRs.getLong("country_stats.population");
                        long gdp = statsRs.getLong("country_stats.gdp");
                        int year = statsRs.getInt("country_stats.year");

                        System.out.println("Population: " + population);
                        System.out.println("GDP: " + gdp);
                        System.out.println("Year: " + year);
                    } else {
                        System.out.println("No statistics found for the ID " + countryId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
        }
    }
}

