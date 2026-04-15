package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.Stop;

/**
 * This class handles user input for the public transport system.
 * It validates and processes inputs related to stops, departure times, and preferences.
 * 
 * It also provides methods to retrieve stop names and similar stops based on user input.
 */
public class InputHandler {

  private final Map<String, Stop> stops_;
  private final Scanner scanner_;

  private String starting_stop_id;
  private String destination_stop_id;
  private int departure_time_seconds;
  private int preference;

  public InputHandler(Map<String, Stop> stops) {
    this(stops, new Scanner(System.in));
  }

  public InputHandler(Map<String, Stop> stops, Scanner scanner) {
    stops_ = stops;
    scanner_ = scanner;
  }

  private String readLine() {
    if (!scanner_.hasNextLine()) {
      return "";
    }
    return scanner_.nextLine().trim();
  }

  /**
   * Gets the name of a stop by its ID.
   *
   * @param stop_id the ID of the stop
   * @return the stop's name, or "Unknown Stop" if it doesn't exist
   */
  public String getStopName(String stop_id) {
    Stop stop = stops_.get(stop_id);
    if (stop != null) {
        return stop.getName();
    } else {
        return "Unknown Stop";
    }
  }

 /**
   * Finds stops with names similar to the input.
   * Example: searching "Central" might return IDs for "Brussels Central", "Central Station", etc.
   * Only one ID is returned per unique stop name.
   *
   * @param stopName the (partial) name to search for
   * @return a list of matching stop IDs
   */
  public List<String> getSimilarStops(String stopName) {
    System.out.println("Searching for similar stops to: " + stopName);

    Map<String, String> unique_similar_stop_names = new HashMap<>();
    for (Map.Entry<String, Stop> entry : stops_.entrySet()) {
      String stop_id = entry.getKey();
      Stop stop = entry.getValue();
      if (stop.getName().toLowerCase().contains(stopName.toLowerCase())) {
        // Only keep one ID per unique stop name (They're all next to each other anyway)
        // Or they refer to the same stop
        unique_similar_stop_names.putIfAbsent(stop.getName(), stop_id);
      }
    }
    // Convert to list and return 
    return  new ArrayList<>(unique_similar_stop_names.values());
  }

   /**
   * Checks if the given input is a valid positive integer.
   *
   * @param input the input string
   * @return true if it's a number > 0, false otherwise
   */
  public static boolean isValidIntInput(String input) {
    try {
      int input_integer = Integer.parseInt(input);
      return input_integer > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Checks if the given string is valid input. 
   * A valid string is not null, not empty, and between 3 and 50 characters.
   *
   * @param input the user input string
   * @return true if valid, false otherwise
   */
  public static boolean isValidStringInput(String input) {
    if (input != null && !input.trim().isEmpty()) {
      if (input.length() > 2 && input.length() < 50) {
        return true;
      } else {
        System.out.println("Input must be at least 3 characters long and no longer than 50 characters.");
        return false;
      }
    }
    System.out.println("Input cannot be null or empty.");
    return false;
  }

  /**
   * Validates and parses a user’s preference input.
   *
   * This method performs the following checks in order:
   * If the input is null, empty, or contains only whitespace, returns 0.
   * If the input length exceeds 2 characters, prints an error message and returns -1.
   * If the input is a single character between '1' and '6', returns its integer value.
   * If the input is two distinct characters each between '1' and '6', returns the combined
   *    two‐digit integer (e.g., "23" → 23).
   * For any other case (invalid characters, duplicate digits in a two‐character string),
   *     returns 0.
   *
   * @param input the raw preference string to validate and parse
   * @return the parsed preference as an integer
   */
  public static int validateAndParsePreferencesInput(String input) {
    if (input == null || input.trim().isEmpty()) {
      System.out.println("No preferences selected.");
      return 0;
    }
    if (input.length() > 2) { // I disallowed more than 2 preferences cuz that would just complicate my algorithm
      System.out.println("Invalid input. Please enter a valid number for given preferences and max 2 preferences:");
      return -1; // -1 is just to indicate that the input was invalid to the caller
    }
    int preference = 0;
    input = input.trim();
    if (input.length() == 1) {
      char c = input.charAt(0);
      if (c >= '1' && c <= '6') {
        preference = c - '0'; // This bascially does unicode '0' - unicode 'number' to get the int value
        System.out.println("Preference selected: " + preference);
      }
    } else if (input.length() == 2) { // Check if the two characters are different
      char c1 = input.charAt(0);
      char c2 = input.charAt(1);
      if (c1 != c2 && c1 >= '1' && c1 <= '6' && c2 >= '1' && c2 <= '6') {
        System.out.println("Preference selected: " + c1 + " and " + c2);
        preference = (c1 - '0') * 10 + (c2 - '0');
      }
    }
    return preference;
  }

  /**
   * Ask the user for a stop name and handles the input.
   *
   * This method prompts the user to enter a stop name, validates the input, and returns
   * the selected stop ID.
   * 
   * @return The ID of the selected stop.
   */
  public String askAndHandleStopInput() {
    while (true) {
      String input = readLine();
      if (!isValidStringInput(input)) {
        continue;
      }

      List<String> similarStops = getSimilarStops(input);
      while (similarStops.isEmpty()) {
        System.out.println("No similar stops found. Please try again:");
        input = readLine();
        if (!isValidStringInput(input)) {
          continue;
        }
        similarStops = getSimilarStops(input);
      }

      System.out.println("Found following stops:");
      System.out.println("--------------------------------------------------------");
      for (int i = 0; i < similarStops.size(); i++) {
        System.out.println((i + 1) + ". " + getStopName(similarStops.get(i)));
      }
      System.out.println("--------------------------------------------------------");
      System.out.println((similarStops.size() + 1) + ". Search for a different stop");
      System.out.println("--------------------------------------------------------");
      System.out.println("Please select a stop by entering the corresponding number:");
      System.out.println("--------------------------------------------------------");

      String first_selected_input = readLine();
      while (!isValidIntInput(first_selected_input)) {
        System.out.println("Invalid input. Please enter a number corresponding to the given stops:");
        first_selected_input = readLine();
      }

      int selected_idx = Integer.parseInt(first_selected_input) - 1;
      if (selected_idx == similarStops.size()) {
        System.out.println("Enter a new stop name to search: ");
        System.out.println("--------------------------------------------------------");
        continue;
      }

      while (selected_idx < 0 || selected_idx >= similarStops.size()) {
        System.out.println("Invalid selection. Please select a valid stop:");
        String selected_input = readLine();
        while (!isValidIntInput(selected_input)) {
          System.out.println("Invalid input. Please enter a number corresponding to the given stops:");
          selected_input = readLine();
        }
        selected_idx = Integer.parseInt(selected_input) - 1;
      }

      String selected_stop_id = similarStops.get(selected_idx);
      System.out.println("--------------------------------------------------------");
      System.out.println("You selected: " + getStopName(selected_stop_id));
      return selected_stop_id;
    }
  }


  /**
   * Ask the user for a departure time and handles the input.
   *
   * This method prompts the user to enter a departure time in HH:MM format, validates the input,
   * and returns the time in seconds.
   * 
   * @return The departure time in seconds.
   */
  public int askAndHandleTimeInput() {
    System.out.println("--------------------------------------------------------");
    System.out.println("Please enter the departure time in (HH:MM) format. Example is 09:30 :");
    String input = readLine();
  
    // Check if the input is null or not in the correct format
    if (input == null || !input.matches("\\d{2}:\\d{2}")) {
      System.out.println("Invalid time format or empty. Please enter a valid time with HH:MM format:");
      return askAndHandleTimeInput();
    }

    String[] time_parts = input.split(":");
    int hours = Integer.parseInt(time_parts[0]);
    int minutes = Integer.parseInt(time_parts[1]);
    if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
      System.out.println("Invalid time. Please enter a valid time in HH:MM format:");
      return askAndHandleTimeInput();
    }
    return hours * 3600 + minutes * 60; // Convert to seconds and return
  }

   /**
   * Prompts the user to enter their travel preferences and validates them.
   *
   * The user can enter up to two digits (1–6) based on predefined options,
   * or leave it empty to skip. Repeats if the input is invalid.
   *
   * @return the encoded preferences as an integer (e.g., 2 or 14)
   */
  public int askAndHandlePreferenceInput() {
    System.out.println("Please select your preference(max. 2) (Example: 53 or 1 or 23):");
    System.out.println("--------------------------------------------------------");
    System.out.println("-> Leave empty for no preferences.");
    System.out.println("1. Less transfers");
    System.out.println("2. Less walking");
    System.out.println("3. Less trains");
    System.out.println("4. Less buses");
    System.out.println("5. Less trams");
    System.out.println("6. Less metro");
    System.out.println("--------------------------------------------------------");

    String input = readLine();
    int preference = validateAndParsePreferencesInput(input);
    if (preference == -1) {
      return askAndHandlePreferenceInput();
    }
    return preference;
  }

  public boolean askForAnotherPath() {
    while (true) {
      String answer = readLine();
      if (answer.equalsIgnoreCase("y")) {
        return true;
      }
      if (answer.equalsIgnoreCase("n")) {
        return false;
      }
      System.out.println("Invalid input. Please enter y or n:");
    }
  }

  /**
   * Collects all required user inputs:
   * starting stop, destination stop, departure time, and preferences.
   */
  public void handleInput() {
    System.out.println("Please enter the starting stop name:");
    starting_stop_id = askAndHandleStopInput();
    System.out.println("--------------------------------------------------------");
    System.out.println("Please enter the destination stop name:");
    destination_stop_id = askAndHandleStopInput();

    departure_time_seconds = askAndHandleTimeInput();
    preference = askAndHandlePreferenceInput();
  }


  // Getters that clear the internal state after being read:

  // getter+clean for the starting stop ID
  public String getStartingStopId() {
    String temp  = starting_stop_id;
    starting_stop_id = null;
    return temp;
  }

  // getter+clean for the destination stop ID
  public String getDestinationStopId() {
    String temp = destination_stop_id;
    destination_stop_id = null;
    return temp;
  }

  // getter+clean for the departure time in seconds
  public int getDepartureTimeSeconds() {
    int temp = departure_time_seconds;
    departure_time_seconds = 0;
    return temp;
  }

  // getter+clean for the preference
  public int getPreference() {
    int temp = preference;
    preference = 0;
    return temp;
  }
}