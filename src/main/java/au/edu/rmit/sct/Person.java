package au.edu.rmit.sct;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private boolean isSuspended = false;
    private List<DemeritRecord> demeritRecords = new ArrayList<>();

    static class DemeritRecord {
        String date;
        int points;

        DemeritRecord(String date, int points) {
            this.date = date;
            this.points = points;
        }
    }

    public boolean addPerson(String personID, String firstName, String lastName, String address, String birthDate) {
        if (!isValidID(personID) || !isValidAddress(address) || !isValidDate(birthDate)) {
            return false;
        }

        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;

        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(String.join(",", personID, firstName, lastName, address, birthDate) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String addDemeritPoints(String date, int points) {
        if (!isValidDate(date) || points < 1 || points > 6) return "Failed";

        demeritRecords.add(new DemeritRecord(date, points));

        int totalPoints = calculateRecentPoints();
        int age = calculateAge();

        // Check suspension logic
        if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints >= 12)) {
            isSuspended = true;
        }

        // Write to demerits.txt
        try (FileWriter writer = new FileWriter("demerits.txt", true)) {
            writer.write(String.join(",", this.personID, date, String.valueOf(points), isSuspended ? "Suspended" : "Active") + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed";
        }

        return "Success";
    }


    private int calculateRecentPoints() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

        return demeritRecords.stream()
                .map(r -> new AbstractMap.SimpleEntry<>(LocalDate.parse(r.date, formatter), r.points))
                .filter(entry -> entry.getKey().isAfter(twoYearsAgo))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    public int getDemeritRecordCount() {
        return demeritRecords.size();
    }



    private int calculateAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birth = LocalDate.parse(birthDate, formatter);
        LocalDate today = LocalDate.now();
        return today.getYear() - birth.getYear() - (today.getDayOfYear() < birth.getDayOfYear() ? 1 : 0);
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    private boolean isValidID(String id) {
        if (id.length() != 10) return false;
        if (!id.substring(0, 2).matches("[2-9]{2}")) return false;
        if (!id.substring(8, 10).matches("[A-Z]{2}")) return false;
        String middle = id.substring(2, 8);
        int specialCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        return specialCount >= 2;
    }

    private boolean isValidAddress(String address) {
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[3].equalsIgnoreCase("Victoria");
    }

    private boolean isValidDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(date, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            boolean isBirthdayChanged = !this.birthDate.equals(newBirthDate);
            boolean isIDChanged = !this.personID.equals(newID);
            boolean isNameChanged = !this.firstName.equals(newFirstName) || !this.lastName.equals(newLastName);
            boolean isAddressChanged = !this.address.equals(newAddress);


            if (isBirthdayChanged && (isIDChanged || isNameChanged || isAddressChanged)) {
                return false;
            }


            int age = calculateAge();
            if (age < 18 && isAddressChanged) {
                return false;
            }


            char firstDigit = this.personID.charAt(0);
            if (Character.isDigit(firstDigit) && ((firstDigit - '0') % 2 == 0) && isIDChanged) {
                return false;
            }


            if (!isValidID(newID) || !isValidAddress(newAddress) || !isValidDate(newBirthDate)) {
                return false;
            }


            this.personID = newID;
            this.firstName = newFirstName;
            this.lastName = newLastName;
            this.address = newAddress;
            this.birthDate = newBirthDate;

            return true;

        } catch (Exception e) {
            return false;
        }
    }

}