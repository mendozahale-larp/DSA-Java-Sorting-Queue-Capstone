import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {

    // ===== DATA: parallel ArrayLists (index i = same participant) =====
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<Integer> regNumbers = new ArrayList<>();
    static ArrayList<String> regDates = new ArrayList<>();
    static ArrayList<String> ticketTypes = new ArrayList<>();
    static ArrayList<Boolean> checkedIn = new ArrayList<>();

    // ===== QUEUE: holds registration numbers in arrival order =====
    static Queue<Integer> checkInQueue = new LinkedList<>();

    static int nextRegNumber = 1001;
    static Scanner scanner = new Scanner(System.in);

    // ===== MAIN MENU LOOP =====
    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ", 1, 10);
            switch (choice) {
                case 1: registerParticipant(); break;
                case 2: displayAll(); break;
                case 3: searchParticipant(); break;
                case 4: enqueueParticipant(); break;
                case 5: dequeueParticipant(); break;
                case 6: peekQueue(); break;
                case 7: displayQueue(); break;
                case 8: sortByName(); break;
                case 9: sortByRegNumber(); break;
                case 10: System.out.println("Thank you! Goodbye."); break;
            }
        } while (choice != 10);
    }

    static void showMenu() {
        System.out.println("\n=== EVENT CHECK-IN SYSTEM ===");
        System.out.println("1. Register participant");
        System.out.println("2. Display all participants");
        System.out.println("3. Search participant");
        System.out.println("4. Add participant to check-in queue");
        System.out.println("5. Check in next participant");
        System.out.println("6. Peek next participant");
        System.out.println("7. Display check-in queue");
        System.out.println("8. Sort by name (Insertion Sort)");
        System.out.println("9. Sort by registration number (Selection Sort)");
        System.out.println("10. Exit");
    }

    // ===== INPUT VALIDATION HELPERS =====
    // Keeps asking until the user types a whole number within [min, max]
    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: '" + input + "' is not a valid number.");
            }
        }
    }

    // Keeps asking until the user types something that isn't blank
    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: input cannot be empty.");
        }
    }

    // Keeps asking until the date looks like YYYY-MM-DD with a sensible month and day
    static String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                int month = Integer.parseInt(input.substring(5, 7));
                int day = Integer.parseInt(input.substring(8, 10));
                if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                    return input;
                }
            }
            System.out.println("Error: use the format YYYY-MM-DD (example: 2026-10-15).");
        }
    }

    // ===== OPTION 1: REGISTER =====
    static void registerParticipant() {
        System.out.println("\n--- Register Participant ---");
        String name = readText("Enter participant name: ");
        String date = readDate("Enter registration date (YYYY-MM-DD): ");
        int typeChoice = readInt("Ticket type (1 = Regular, 2 = Special Guest): ", 1, 2);
        String type = (typeChoice == 1) ? "Regular" : "Special Guest";

        // Add to ALL lists so the indexes stay in sync
        names.add(name);
        regNumbers.add(nextRegNumber);
        regDates.add(date);
        ticketTypes.add(type);
        checkedIn.add(false);

        System.out.println("Registered " + name + " with registration number " + nextRegNumber + ".");
        nextRegNumber++;
    }

    // ===== OPTION 2: DISPLAY ALL =====
    static void displayAll() {
        if (names.isEmpty()) {
            System.out.println("No participants registered yet.");
            return;
        }
        System.out.println("\n--- All Participants ---");
        printHeader();
        for (int i = 0; i < names.size(); i++) {
            printRow(i);
        }
    }

    // ===== SHARED DISPLAY HELPERS (we will reuse these later) =====
    static void printHeader() {
        System.out.printf("%-8s %-20s %-12s %-14s %s%n",
                "Reg No.", "Name", "Reg Date", "Ticket", "Checked In");
    }

    static void printRow(int i) {
        System.out.printf("%-8s %-20s %-12s %-14s %s%n",
                regNumbers.get(i), names.get(i), regDates.get(i),
                ticketTypes.get(i), checkedIn.get(i) ? "Yes" : "No");
    }

    // ===== OPTION 3: SEARCH =====
    static void searchParticipant() {
        if (names.isEmpty()) {
            System.out.println("No participants registered yet.");
            return;
        }

        System.out.println("\n--- Search Participant ---");
        int option = readInt("Search by (1 = Registration number, 2 = Name): ", 1, 2);

        if (option == 1) {
            int regNo = readInt("Enter registration number: ", 1, Integer.MAX_VALUE);
            int index = findIndexByRegNumber(regNo);
            if (index == -1) {
                System.out.println("No participant found with registration number " + regNo + ".");
            } else {
                printHeader();
                printRow(index);
            }
        } else {
            String keyword = readText("Enter name (or part of it): ").toLowerCase();
            boolean found = false;
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).toLowerCase().contains(keyword)) {
                    if (!found) {
                        printHeader();   // print the header only once, before the first match
                        found = true;
                    }
                    printRow(i);
                }
            }
            if (!found) {
                System.out.println("No participant found with a name containing '" + keyword + "'.");
            }
        }
    }

    // Returns the list position of a registration number, or -1 if it isn't there
    static int findIndexByRegNumber(int regNo) {
        for (int i = 0; i < regNumbers.size(); i++) {
            if (regNumbers.get(i) == regNo) {
                return i;
            }
        }
        return -1;
    }

    // ===== OPTION 4: ENQUEUE =====
    static void enqueueParticipant() {
        if (names.isEmpty()) {
            System.out.println("No participants registered yet. Register someone first.");
            return;
        }
        int regNo = readInt("Enter registration number to add to the queue: ", 1, Integer.MAX_VALUE);
        int index = findIndexByRegNumber(regNo);

        if (index == -1) {
            System.out.println("No participant found with registration number " + regNo + ".");
        } else if (checkedIn.get(index)) {
            System.out.println(names.get(index) + " is already checked in.");
        } else if (checkInQueue.contains(regNo)) {
            System.out.println(names.get(index) + " is already in the queue.");
        } else {
            checkInQueue.add(regNo);   // joins the BACK of the line
            System.out.println(names.get(index) + " added to the check-in queue. Position: "
                    + checkInQueue.size());
        }
    }

    // ===== OPTION 5: DEQUEUE =====
    static void dequeueParticipant() {
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty. Nobody to check in.");
            return;
        }
        int regNo = checkInQueue.poll();   // removes the FRONT of the line
        int index = findIndexByRegNumber(regNo);
        checkedIn.set(index, true);
        System.out.println("Checked in: " + names.get(index) + " (Reg No. " + regNo + ", "
                + ticketTypes.get(index) + ")");
        System.out.println("People still waiting: " + checkInQueue.size());
    }

    // ===== OPTION 6: PEEK =====
    static void peekQueue() {
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }
        int regNo = checkInQueue.peek();   // looks at the front WITHOUT removing
        int index = findIndexByRegNumber(regNo);
        System.out.println("Next in line:");
        printHeader();
        printRow(index);
    }

    // ===== OPTION 7: DISPLAY QUEUE =====
    static void displayQueue() {
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }
        System.out.println("\n--- Check-In Queue (front to back) ---");
        int position = 1;
        for (int regNo : checkInQueue) {   // iterates in FIFO order
            int index = findIndexByRegNumber(regNo);
            System.out.printf("%d. %s (Reg No. %d, %s)%n",
                    position, names.get(index), regNo, ticketTypes.get(index));
            position++;
        }
        System.out.println("Total waiting: " + checkInQueue.size());
    }

    // ===== SWAP HELPER: swaps position i and j in ALL five lists =====
    static void swapAll(int i, int j) {
        String tempName = names.get(i);
        names.set(i, names.get(j));
        names.set(j, tempName);

        Integer tempReg = regNumbers.get(i);
        regNumbers.set(i, regNumbers.get(j));
        regNumbers.set(j, tempReg);

        String tempDate = regDates.get(i);
        regDates.set(i, regDates.get(j));
        regDates.set(j, tempDate);

        String tempType = ticketTypes.get(i);
        ticketTypes.set(i, ticketTypes.get(j));
        ticketTypes.set(j, tempType);

        Boolean tempChecked = checkedIn.get(i);
        checkedIn.set(i, checkedIn.get(j));
        checkedIn.set(j, tempChecked);
    }

    // ===== OPTION 8: INSERTION SORT BY NAME (A to Z) =====
    static void sortByName() {
        if (names.size() < 2) {
            System.out.println("Need at least 2 participants to sort.");
            return;
        }
        System.out.println("\n--- BEFORE Insertion Sort (by name) ---");
        displayAll();

        for (int i = 1; i < names.size(); i++) {
            int j = i;
            // slide the new item left until it sits in its correct place
            while (j > 0 && names.get(j - 1).compareToIgnoreCase(names.get(j)) > 0) {
                swapAll(j - 1, j);
                j--;
            }
        }

        System.out.println("\n--- AFTER Insertion Sort (by name) ---");
        displayAll();
    }

    // ===== OPTION 9: SELECTION SORT BY REGISTRATION NUMBER (low to high) =====
    static void sortByRegNumber() {
        if (names.size() < 2) {
            System.out.println("Need at least 2 participants to sort.");
            return;
        }
        System.out.println("\n--- BEFORE Selection Sort (by registration number) ---");
        displayAll();

        for (int i = 0; i < regNumbers.size() - 1; i++) {
            int minIndex = i;
            // find the smallest registration number in the unsorted part
            for (int j = i + 1; j < regNumbers.size(); j++) {
                if (regNumbers.get(j) < regNumbers.get(minIndex)) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                swapAll(i, minIndex);
            }
        }

        System.out.println("\n--- AFTER Selection Sort (by registration number) ---");
        displayAll();
    }
}