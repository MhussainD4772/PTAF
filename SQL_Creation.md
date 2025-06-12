**Automating Database Queries Using Playwright in Java**

### **1. Overview**
Playwright is primarily designed for browser automation, but you can integrate database automation within your Playwright framework using Java. This document outlines the steps to execute database queries in Playwright tests.

---

### **2. Setting Up JDBC Dependencies**
To interact with a database, you need the appropriate JDBC driver. Add the following dependencies to your `pom.xml` file:

#### **For MySQL:**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### **For PostgreSQL:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.5.0</version>
</dependency>
```

---

### **3. Creating a Database Utility Class**
Create a helper class to manage database queries:

```java
import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://your-db-host:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static ResultSet executeQuery(String query) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int executeUpdate(String query) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
```

---

### **4. Using the Database Utility Class in Playwright Tests**
Once the helper class is set up, you can use it to retrieve data for validation in your Playwright test scripts.

```java
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        String query = "SELECT * FROM users WHERE id = 1";
        ResultSet rs = DatabaseHelper.executeQuery(query);
        try {
            while (rs.next()) {
                System.out.println("User Name: " + rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

### **5. Integrating Database Queries in Playwright UI Tests**
If you need to validate database values against UI elements, you can use Playwright within your test flow:

```java
@Test
public void verifyUserFromDatabase() {
    Page page = Playwright.create().chromium().launch().newPage();
    page.navigate("https://your-app.com/login");

    ResultSet rs = DatabaseHelper.executeQuery("SELECT username FROM users WHERE id = 1");
    String expectedUsername = "";
    try {
        if (rs.next()) {
            expectedUsername = rs.getString("username");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    String actualUsername = page.locator("#username").textContent();
    Assertions.assertEquals(expectedUsername, actualUsername);
}
```

---

### **6. Key Takeaways**
- **Playwright does not handle databases directly**, but you can integrate JDBC for query execution.
- Use a **helper class** to manage database interactions.
- **Execute database queries inside Playwright tests** to validate UI against backend data.
- **Ensure proper resource management** by closing connections after execution.

This approach allows you to extend Playwright's capabilities to automate end-to-end tests involving both UI and database validation.

