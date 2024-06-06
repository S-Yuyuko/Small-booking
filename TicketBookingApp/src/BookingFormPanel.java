import com.toedter.calendar.JDateChooser;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BookingFormPanel extends JPanel {
    private RoundedComboBox<String> typeComboBox;
    private RoundedComboBox<String> specificTypeComboBox;
    private RoundedComboBox<String> filmNameComboBox;
    private JSpinner showTimeSpinner;
    private JDateChooser showDateChooser;
    private RoundedTextField locationField;
    private List<Film> films;
    private DatabaseManager dbManager;

    public BookingFormPanel() {
        dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/ticket_booking", "root", "password");
        loadFilms();

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Book Your Tickets");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(typeLabel, gbc);

        typeComboBox = new RoundedComboBox<>(new String[]{"All", "Genre", "Director"});
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(typeComboBox, gbc);

        JLabel specificTypeLabel = new JLabel("Specific Type:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(specificTypeLabel, gbc);

        specificTypeComboBox = new RoundedComboBox<>(new String[]{});
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(specificTypeComboBox, gbc);

        JLabel filmNameLabel = new JLabel("Film Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(filmNameLabel, gbc);

        filmNameComboBox = new RoundedComboBox<>(new String[]{});
        updateFilmNameComboBox("All", null);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(filmNameComboBox, gbc);

        JLabel showTimeLabel = new JLabel("Show Time:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(showTimeLabel, gbc);

        SpinnerDateModel timeModel = new SpinnerDateModel();
        showTimeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(showTimeSpinner, "HH:mm:ss");
        showTimeSpinner.setEditor(timeEditor);
        showTimeSpinner.setValue(new Date()); // set current time as default
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(showTimeSpinner, gbc);

        JLabel showDateLabel = new JLabel("Show Date:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(showDateLabel, gbc);

        showDateChooser = new JDateChooser();
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(showDateChooser, gbc);

        JLabel locationLabel = new JLabel("Location:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(locationLabel, gbc);

        locationField = new RoundedTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(locationField, gbc);

        JButton locateButton = new JButton("Locate Me");
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(locateButton, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(66, 133, 244));
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBookingToDatabase();
            }
        });

        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeComboBox.getSelectedItem();
                updateSpecificTypeComboBox(selectedType);
                updateFilmNameComboBox(selectedType, null);
           
            }
        });

        specificTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeComboBox.getSelectedItem();
                String specificType = (String) specificTypeComboBox.getSelectedItem();
                updateFilmNameComboBox(selectedType, specificType);
            }
        });

        locateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                locateUser();
            }
        });
    }

    private void loadFilms() {
        films = new ArrayList<>();
        String csvFile = "films.csv";
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] filmData = line.split(csvSplitBy);
                if (filmData.length == 5) {
                    try {
                        String title = filmData[0].trim();
                        String genre = filmData[1].trim();
                        String director = filmData[2].trim();
                        int releaseYear = Integer.parseInt(filmData[3].trim());
                        int duration = Integer.parseInt(filmData[4].trim());

                        films.add(new Film(title, genre, director, releaseYear, duration));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing number from line: " + line);
                    }
                } else {
                    System.err.println("Incorrect number of fields in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSpecificTypeComboBox(String selectedType) {
        specificTypeComboBox.removeAllItems();
        List<String> items = new ArrayList<>();

        if (selectedType.equals("Genre")) {
            items = films.stream().map(Film::getGenre).distinct().collect(Collectors.toList());
        } else if (selectedType.equals("Director")) {
            items = films.stream().map(Film::getDirector).distinct().collect(Collectors.toList());
        }

        for (String item : items) {
            specificTypeComboBox.addItem(item);
        }
    }

    private void updateFilmNameComboBox(String selectedType, String specificType) {
        filmNameComboBox.removeAllItems();
        List<Film> filteredFilms = films;

        if (selectedType.equals("Genre") && specificType != null) {
            filteredFilms = films.stream()
                .filter(film -> film.getGenre().equals(specificType))
                .collect(Collectors.toList());
        } else if (selectedType.equals("Director") && specificType != null) {
            filteredFilms = films.stream()
                .filter(film -> film.getDirector().equals(specificType))
                .collect(Collectors.toList());
        }

        for (Film film : filteredFilms) {
            filmNameComboBox.addItem(film.getTitle());
        }
    }

    private void locateUser() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ipinfo.io/json"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            JSONObject jsonObject = new JSONObject(responseBody);
            String location = jsonObject.getString("loc");
            locationField.setText(location);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve location: " + e.getMessage());
        }
    }

    private void addBookingToDatabase() {
        String filmName = (String) filmNameComboBox.getSelectedItem();
        Date showTime = (Date) showTimeSpinner.getValue();
        Date showDate = showDateChooser.getDate();
        String location = locationField.getText();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dbManager.addBooking(filmName, timeFormat.format(showTime), dateFormat.format(showDate), location);
            JOptionPane.showMessageDialog(this, "Booking Successful!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking Failed: " + e.getMessage());
        }
    }
}
