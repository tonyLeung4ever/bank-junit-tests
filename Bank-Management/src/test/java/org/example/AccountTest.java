package org.example;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration (
//following are REQUIRED
        url = "https://rmit.spiraservice.net/",
        login = "s3962111",
        rssToken = "{367885F4-0D92-4821-955B-0495A94EAC89}",
        projectId = 282,
        releaseId = 1329,
        testSetId = 2138
//following are OPTIONAL
)

public class AccountTest {
    Account account;

    @BeforeEach
    public void setup() {
        account = new Account();
    }
    // Invalid and Edge Cases

    @Test
    @SpiraTestCase(testCaseId = 14437)
    public void testSetNameNull() {
        account.setName(null);
        assertNull(account.getName(), "Name should be null when set to null");
    }

    @Test
    @SpiraTestCase(testCaseId = 14571)
    public void testSetNameEmpty() {
        account.setName("");
        assertEquals("", account.getName(), "Name should be set to empty string");
    }

    @Test
    @SpiraTestCase(testCaseId = 14572)
    public void testSetAccountNumberNegative() {
        account.setAccountNumber(-12345678);
        assertEquals(-12345678, account.getAccountNumber(), "Account number should be set to -12345678");
    }

    @Test
    @SpiraTestCase(testCaseId = 14577)
    public void testSetAccountNumberZero() {
        account.setAccountNumber(0);
        assertEquals(0, account.getAccountNumber(), "Account number should be set to 0");
    }

    @Test
    @SpiraTestCase(testCaseId = 14578)
    public void testSetPINTooShort() {
        account.setPIN("123");
        assertEquals("123", account.getPIN(), "PIN should be set to 123 (even if too short for real use case)");
    }

    @Test
    @SpiraTestCase(testCaseId = 14580)
    public void testSetPINTooLong() {
        account.setPIN("1234567890");
        assertEquals("1234567890", account.getPIN(), "PIN should be set to 1234567890 (even if too long for real use case)");
    }

    @Test
    @SpiraTestCase(testCaseId = 14582)
    public void testSetAmountNegative() {
        account.setAmount(-100.0);
        assertEquals(-100.0, account.getAmount(), 0.001, "Amount should be set to -100.0");
    }

    @Test
    @SpiraTestCase(testCaseId = 14583)
    public void testSetAmountZero() {
        account.setAmount(0.0);
        assertEquals(0.0, account.getAmount(), 0.001, "Amount should be set to 0.0");
    }

    @Test
    @SpiraTestCase(testCaseId = 14584)
    public void testSetAmountVeryLarge() {
        account.setAmount(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, account.getAmount(), 0.001, "Amount should be set to Double.MAX_VALUE");
    }

    @Test
    @SpiraTestCase(testCaseId = 14585)
    public void testConstructor(){
        account = new Account("John Doe", 12345678, "1234", 1000.0);
        assertEquals(2000, account.getAmount(), 0.001, "Amount should be set to 1000");
    }
}
