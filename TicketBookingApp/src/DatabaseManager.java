import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private String url;
    private String username;
    private String password;

    public DatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void addBooking(String filmName, String showTime, String showDate, String location) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String sql = "INSERT INTO bookings (film_name, show_time, show_date, location) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, filmName);
        statement.setString(2, showTime);
        statement.setString(3, showDate);
        statement.setString(4, location);
        statement.executeUpdate();
        connection.close();
    }
}
