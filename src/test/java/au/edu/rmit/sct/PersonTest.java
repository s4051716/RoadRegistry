package au.edu.rmit.sct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonTest {


    //ADD DEMERIT POINTS TESTS


    // Demerit test: valid points, no suspension expected
    @Test
    public void testAddDemeritPoints_ValidNotSuspended() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Alex", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String result = p.addDemeritPoints("01-01-2024", 3);
        assertEquals("Success", result);
        assertFalse(p.isSuspended());
    }

    // Demerit test: suspension expected for person under 21
    @Test
    public void testAddDemeritPoints_ValidSuspensionUnder21() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Ella", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2007"); // Age < 21

        p.addDemeritPoints("01-01-2024", 4);
        p.addDemeritPoints("01-06-2024", 3);

        assertTrue(p.isSuspended()); // Under 21 and total > 6
    }

    // Demerit test: suspension expected for person over 21
    @Test
    public void testAddDemeritPoints_ValidSuspensionOver21() {
        Person p = new Person();
        boolean added = p.addPerson("56s_d%&fAB", "Chris", "Doe", "12|Main Road|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(added);

        String recentDate1 = LocalDate.now().minusMonths(6).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String recentDate2 = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));


        String result1 = p.addDemeritPoints(recentDate1, 6);
        String result2 = p.addDemeritPoints(recentDate2, 6);


        assertEquals(2, p.getDemeritRecordCount());
        assertTrue(p.isSuspended());
    }

    // Invalid date format should fail
    @Test
    public void testAddDemeritPoints_InvalidDateFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String result = p.addDemeritPoints("2024-01-01", 5);
        assertEquals("Failed", result);
    }

    // Points out of valid range should fail
    @Test
    public void testAddDemeritPoints_PointsOutOfRange_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Maya", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String low = p.addDemeritPoints("01-01-2024", 0);
        String high = p.addDemeritPoints("01-01-2024", 10);

        assertEquals("Failed", low);
        assertEquals("Failed", high);
    }


    // ADD PERSON TESTS


    //Succeeded account creation
    @Test
    public void testAddPerson_Valid() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(result);
    }

    //Fails if ID format is invalid
    @Test
    public void testAddPerson_InvalidID() {
        Person p = new Person();
        boolean result = p.addPerson("12345678AB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);
    }

    //Fails if address format is invalid
    @Test
    public void testAddPerson_InvalidAddress() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Sydney|NSW|Australia", "15-11-1990");
        assertFalse(result);
    }

    //Fails if date format is invalid
    @Test
    public void testAddPerson_InvalidDate() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(result);
    }


    // UPDATE PERSONAL DETAILS TESTS


    // Update personal detail: Valid change
    @Test
    public void testUpdateDetails_ValidUpdate() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Johnny", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(updated);
    }

    // Fail due to changing birthday + other fields
    @Test
    public void testUpdateDetails_BirthdayChangedAndOthersChanged() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("99x_@!Z9XY", "Johnny", "Doe", "15|Queen Street|Melbourne|Victoria|Australia", "01-01-1999");
        assertFalse(updated);
    }

    // Under 18 cannot change address
    @Test
    public void testUpdateDetails_AddressChangeUnder18_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2007"); // Age < 18

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "55|New Road|Melbourne|Victoria|Australia", "15-11-2007");
        assertFalse(updated); // Under 18 cannot change address
    }

    // ID cannot be changed if it starts with even digit
    @Test
    public void testUpdateDetails_IDStartsWithEvenDigit_ShouldFailToChangeID() {
        Person p = new Person();
        p.addPerson("26s_&*fABZ", "Eve", "Doe", "5|Example Rd|Melbourne|Victoria|Australia", "15-11-1995"); // Starts with 2 (even)

        boolean updated = p.updatePersonalDetails("88x_!@tRPZ", "Eve", "Doe", "5|Example Rd|Melbourne|Victoria|Australia", "15-11-1995");
        assertFalse(updated);
    }

    // Update fails due to invalid address (wrong state or structure)
    @Test
    public void testUpdateDetails_InvalidAddress_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "12|King Street|Sydney|NSW|Australia", "15-11-2000");
        assertFalse(updated);
    }

    // Update fails due to malformed ID (doesn’t meet format criteria)
    @Test
    public void testUpdateDetails_InvalidIDFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("1234567890", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }

    // Update succeeds when only birthdate is changed, and all other fields remain the same
    @Test
    public void testUpdateDetails_BirthdayOnlyChanged_ShouldSucceed() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "01-01-2001");
        assertTrue(updated);
    }

    // Update fails when all fields are empty (invalid input)
    @Test
    public void testUpdateDetails_EmptyFields_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("", "", "", "", "");
        assertFalse(updated);
    }

    // Update fails when all inputs are null (null safety and validation check)
    @Test
    public void testUpdateDetails_NullValues_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails(null, null, null, null, null);
        assertFalse(updated);
    }

    // Update fails due to invalid birthdate format (e.g., using slashes instead of dashes)
    @Test
    public void testUpdateDetails_InvalidBirthdateFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "2000/11/15");
        assertFalse(updated);
    }

    // Update fails because the new ID is too short (violates length requirement)
    @Test
    public void testUpdateDetails_ShortID_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }

    // Update fails due to address missing required parts (should contain 5 parts including state)
    @Test
    public void testUpdateDetails_AddressMissingParts_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }
}