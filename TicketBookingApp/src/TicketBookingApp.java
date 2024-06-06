import javax.swing.*;
import java.awt.*;

public class TicketBookingApp extends JFrame {
    public TicketBookingApp() {
        setTitle("Ticket Booking Application");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Initialize the booking form panel and add it to the frame
        BookingFormPanel bookingFormPanel = new BookingFormPanel();
        add(bookingFormPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TicketBookingApp();
            }
        });
    }
}
