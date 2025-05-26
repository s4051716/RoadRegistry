package au.edu.rmit.sct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonTest {
    @Test
    public void testAddDemeritPoints_ValidNotSuspended() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Alex", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String result = p.addDemeritPoints("01-01-2024", 3);
        assertEquals("Success", result);
        assertFalse(p.isSuspended());
    }

    @Test
    public void testAddDemeritPoints_ValidSuspensionUnder21() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Ella", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2007"); // Age < 21

        p.addDemeritPoints("01-01-2024", 4);
        p.addDemeritPoints("01-06-2024", 3);

        assertTrue(p.isSuspended()); // Under 21 and total > 6
    }

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


    @Test
    public void testAddDemeritPoints_InvalidDateFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String result = p.addDemeritPoints("2024-01-01", 5);
        assertEquals("Failed", result);
    }

    @Test
    public void testAddDemeritPoints_PointsOutOfRange_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Maya", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        String low = p.addDemeritPoints("01-01-2024", 0);
        String high = p.addDemeritPoints("01-01-2024", 10);

        assertEquals("Failed", low);
        assertEquals("Failed", high);
    }

    @Test
    public void testAddPerson_Valid() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(result);
    }

    @Test
    public void testAddPerson_InvalidID() {
        Person p = new Person();
        boolean result = p.addPerson("12345678AB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);
    }

    @Test
    public void testAddPerson_InvalidAddress() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Sydney|NSW|Australia", "15-11-1990");
        assertFalse(result);
    }

    @Test
    public void testAddPerson_InvalidDate() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(result);
    }
    @Test
    public void testUpdateDetails_ValidUpdate() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Johnny", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(updated);
    }

    @Test
    public void testUpdateDetails_BirthdayChangedAndOthersChanged() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("99x_@!Z9XY", "Johnny", "Doe", "15|Queen Street|Melbourne|Victoria|Australia", "01-01-1999");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_AddressChangeUnder18_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2007"); // Age < 18

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "55|New Road|Melbourne|Victoria|Australia", "15-11-2007");
        assertFalse(updated); // Under 18 cannot change address
    }

    @Test
    public void testUpdateDetails_IDStartsWithEvenDigit_ShouldFailToChangeID() {
        Person p = new Person();
        p.addPerson("26s_&*fABZ", "Eve", "Doe", "5|Example Rd|Melbourne|Victoria|Australia", "15-11-1995"); // Starts with 2 (even)

        boolean updated = p.updatePersonalDetails("88x_!@tRPZ", "Eve", "Doe", "5|Example Rd|Melbourne|Victoria|Australia", "15-11-1995");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_InvalidAddress_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "12|King Street|Sydney|NSW|Australia", "15-11-2000");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_InvalidIDFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("1234567890", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }
    @Test
    public void testUpdateDetails_BirthdayOnlyChanged_ShouldSucceed() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "15-11-2000");

        // Only birthDate is changed, all other fields are unchanged
        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "12|King Street|Melbourne|Victoria|Australia", "01-01-2001");

        assertTrue(updated);
    }
    @Test
    public void testUpdateDetails_EmptyFields_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("", "", "", "", "");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_NullValues_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails(null, null, null, null, null);
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_InvalidBirthdateFormat_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "2000/11/15");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_ShortID_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }

    @Test
    public void testUpdateDetails_AddressMissingParts_ShouldFail() {
        Person p = new Person();
        p.addPerson("56s_d%&fAB", "Jane", "Doe", "10|Test St|Melbourne|Victoria|Australia", "15-11-2000");

        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "Jane", "Doe", "Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }
}