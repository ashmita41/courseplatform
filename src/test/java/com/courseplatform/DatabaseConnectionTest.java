package com.courseplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple database connection test utility
 * Run this to test if the database connection is working
 */
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseConnectionTest {

    @Test
    public void testSupabaseConnection() {
        String url = "jdbc:postgresql://db.bgusbzjaawyitzhbrjql.supabase.co:5432/postgres?sslmode=require";
        String username = "postgres";
        String password = "bT3oXbCd5q7UfNtu";
        
        System.out.println("=========================================");
        System.out.println("Testing Supabase Database Connection");
        System.out.println("=========================================");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Attempting connection...");
        
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            // Set connection properties
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("ssl", "true");
            props.setProperty("sslmode", "require");
            props.setProperty("connectTimeout", "10");
            
            // Attempt connection
            long startTime = System.currentTimeMillis();
            Connection conn = DriverManager.getConnection(url, props);
            long endTime = System.currentTimeMillis();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ SUCCESS: Connection established!");
                System.out.println("Connection time: " + (endTime - startTime) + " ms");
                System.out.println("Database: " + conn.getCatalog());
                System.out.println("Auto-commit: " + conn.getAutoCommit());
                conn.close();
                System.out.println("Connection closed successfully.");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERROR: PostgreSQL driver not found!");
            System.err.println("Make sure postgresql dependency is in pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ ERROR: Connection failed!");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            
            // Check for specific error types
            if (e.getMessage().contains("UnknownHostException") || 
                e.getMessage().contains("could not translate host name")) {
                System.err.println("\n→ DIAGNOSIS: Hostname cannot be resolved");
                System.err.println("  Possible causes:");
                System.err.println("  1. Supabase database is PAUSED");
                System.err.println("  2. Database was deleted");
                System.err.println("  3. Network/DNS issues");
                System.err.println("\n→ SOLUTION:");
                System.err.println("  1. Go to https://supabase.com/dashboard");
                System.err.println("  2. Check if your project is paused");
                System.err.println("  3. Click 'Resume' if paused");
                System.err.println("  4. Wait 2-3 minutes for database to start");
            } else if (e.getMessage().contains("password authentication failed")) {
                System.err.println("\n→ DIAGNOSIS: Authentication failed");
                System.err.println("  Check your password in application.properties");
            } else if (e.getMessage().contains("Connection refused")) {
                System.err.println("\n→ DIAGNOSIS: Connection refused");
                System.err.println("  Possible causes:");
                System.err.println("  1. Database is paused");
                System.err.println("  2. Firewall blocking connection");
                System.err.println("  3. Wrong port number");
            }
            
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ ERROR: Unexpected error!");
            e.printStackTrace();
        }
        
        System.out.println("=========================================");
    }
}
