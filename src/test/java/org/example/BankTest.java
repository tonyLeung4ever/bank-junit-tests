package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration (
//following are REQUIRED
        url = "https://rmit.spiraservice.net/",
        login = "s3962111",
        rssToken = "{367885F4-0D92-4821-955B-0495A94EAC89}",
        projectId = 282,
        releaseId = 1327,
        testSetId = 2125
//following are OPTIONAL
)

public class BankTest {

    Bank bank;
    Account account;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private File tempFile;

    @BeforeEach
    public void setup() throws IOException {
        bank = new Bank();
        account = new Account();

        // Capture System.out for testing print methods
        System.setOut(new PrintStream(outContent));
    }
    // Test for addNewRecord method with mocked input
    @Test
    @SpiraTestCase(testCaseId = 14257)
    public void addNewRecordTest() {
        String simulatedUserInput = "John Doe\n12345678\n1234\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        bank.addNewRecord();  // Uses the mocked input

        assertEquals(1, bank.AL.size(), "Account list size should be 1 after adding a new record");

        Account acc = bank.AL.get(0);
        assertAll("Account properties should be correct",
                () -> assertEquals("John Doe", acc.getName(), "Name should be John Doe"),
                () -> assertEquals(12345678, acc.getAccountNumber(), "Account number should be 12345678"),
                () -> assertEquals("1234", acc.getPIN(), "PIN should be 1234"),
                () -> assertEquals(1500.0, acc.getAmount(), "Initial amount should be 1500 (1000 default + 500)")
        );
    }

    // Test for transfer method with mocked input
    @Test
    @SpiraTestCase(testCaseId = 14363)
    public void transferTest() {
        // First, simulate adding two accounts
        String simulatedInputForAccount1 = "Alice\n11111111\n1111\n1000\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount1.getBytes()));
        bank.addNewRecord();

        String simulatedInputForAccount2 = "Bob\n22222222\n2222\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount2.getBytes()));
        bank.addNewRecord();

        // Now simulate the transfer input
        String simulatedTransferInput = "11111111\n1111\n22222222\n200\n";
        System.setIn(new ByteArrayInputStream(simulatedTransferInput.getBytes()));

        bank.transfer();  // Uses the mocked transfer input

        Account alice = bank.AL.get(0);
        Account bob = bank.AL.get(1);

        assertAll("Transfer should correctly adjust balances",
                () -> assertEquals(800.0, alice.getAmount(), "Alice's balance should be reduced by 200"),
                () -> assertEquals(700.0, bob.getAmount(), "Bob's balance should be increased by 200")
        );
    }

    // Test for withdraw method with mocked input
    @Test
    @SpiraTestCase(testCaseId = 14364)
    public void withdrawTest() {
        // Setup: Add an account with a known balance
        String simulatedInputForAccount = "Charlie\n33333333\n3333\n2000\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount.getBytes()));
        bank.addNewRecord();  // This should add Charlie with an initial balance of 2000

        // Now simulate the withdrawal input
        String simulatedWithdrawInput = "33333333\n3333\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedWithdrawInput.getBytes()));

        // Perform the withdrawal
        bank.withdraw();  // This should deduct 500 from Charlie's account balance

        // Verify the balance after withdrawal
        Account charlie = bank.AL.get(0);
        assertEquals(1500.0, charlie.getAmount(), "Charlie's balance should be reduced by 500 after withdrawal");
    }

    // Test for insufficient funds during withdraw with mocked input
    @Test
    @SpiraTestCase(testCaseId = 14367)
    public void withdrawInsufficientFundsTest() {
        // First, simulate adding an account
        String simulatedInputForAccount = "Charlie\n33333333\n3333\n100\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount.getBytes()));
        bank.addNewRecord();

        // Now simulate an invalid withdrawal input (amount greater than balance)
        String simulatedWithdrawInput = "33333333\n3333\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedWithdrawInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.withdraw();
        });

        assertEquals("Insufficient balance", exception.getMessage(), "Error message should indicate insufficient funds");
    }

    // Test for print method with mocked input
    @Test
    @SpiraTestCase(testCaseId = 14370)
    public void printTest() {
        // Simulate adding two accounts
        String simulatedInputForAccount1 = "Alice\n11111111\n1111\n1000\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount1.getBytes()));
        bank.addNewRecord();

        String simulatedInputForAccount2 = "Bob\n22222222\n2222\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedInputForAccount2.getBytes()));
        bank.addNewRecord();

        bank.print();  // Print method will output account details to System.out

        String output = outContent.toString();  // Capture System.out output
        assertTrue(output.contains("Alice"), "Print output should contain Alice's details");
        assertTrue(output.contains("Bob"), "Print output should contain Bob's details");
    }

    // Test for checking if the bank is empty before adding accounts
    @Test
    @SpiraTestCase(testCaseId = 14373)
    public void isEmptyTest() {
        assertTrue(bank.AL.isEmpty(), "Bank should start with no accounts");
    }

    // Test for invalid account number format (8 digits expected)
    @Test
    @SpiraTestCase(testCaseId = 14377)
    public void addNewRecordInvalidAccountNumberTest() {
        String invalidInput = "Jane Doe\n123\n1234\n500\n";
        System.setIn(new ByteArrayInputStream(invalidInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.addNewRecord();
        });

        assertEquals("Invalid account number", exception.getMessage(), "Expected invalid account number error");
    }

    // Test for invalid PIN format
    @Test
    @SpiraTestCase(testCaseId = 14380)
    public void addNewRecordInvalidPinTest() {
        String invalidPinInput = "Jane Doe\n12345678\n12345\n500\n";
        System.setIn(new ByteArrayInputStream(invalidPinInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.addNewRecord();
        });

        assertEquals("Invalid PIN format", exception.getMessage(), "Expected invalid PIN format error");
    }

    // Test for transferring to an invalid account
    @Test
    @SpiraTestCase(testCaseId = 14391)
    public void transferToNonExistentAccountTest() {
        // Add one valid account
        String validAccountInput = "John Doe\n11111111\n1111\n1000\n";
        System.setIn(new ByteArrayInputStream(validAccountInput.getBytes()));
        bank.addNewRecord();

        // Simulate transfer to an invalid account
        String invalidTransferInput = "11111111\n1111\n99999999\n200\n";
        System.setIn(new ByteArrayInputStream(invalidTransferInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.transfer();
        });

        assertEquals("Account not found", exception.getMessage(), "Expected account not found error");
    }

    // Test withdraw method when amount is zero
    @Test
    @SpiraTestCase(testCaseId = 14395)
    public void withdrawZeroAmountTest() {
        String validInput = "John Doe\n11111111\n1111\n1000\n";
        System.setIn(new ByteArrayInputStream(validInput.getBytes()));
        bank.addNewRecord();

        String zeroWithdrawInput = "11111111\n1111\n0\n";
        System.setIn(new ByteArrayInputStream(zeroWithdrawInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.withdraw();
        });

        assertEquals("Amount must be greater than zero", exception.getMessage(), "Expected error for zero amount");
    }

    // Test transfer method with invalid PIN
    @Test
    @SpiraTestCase(testCaseId = 14396)
    public void transferInvalidPinTest() {
        String validInput1 = "John Doe\n11111111\n1111\n1000\n";
        String validInput2 = "Jane Doe\n22222222\n2222\n500\n";
        System.setIn(new ByteArrayInputStream(validInput1.getBytes()));
        bank.addNewRecord();
        System.setIn(new ByteArrayInputStream(validInput2.getBytes()));
        bank.addNewRecord();

        String transferWithInvalidPinInput = "11111111\n1234\n22222222\n200\n";
        System.setIn(new ByteArrayInputStream(transferWithInvalidPinInput.getBytes()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.transfer();
        });

        assertEquals("Invalid PIN", exception.getMessage(), "Expected error for invalid PIN");
    }

    // Test valid load from file
    @Test
    @SpiraTestCase(testCaseId = 14397)
    public void loadTest() {
        // Mock a saved file with serialized accounts
        Account acc1 = new Account("Alice", 11111111, "1111", 1000.0);
        Account acc2 = new Account("Bob", 22222222, "2222", 500.0);

        // Simulate a file content with serialized account data
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(acc1);
            out.writeObject(acc2);
            out.flush();

            // Use ByteArrayInputStream to mock reading from a file
            // Create a temporary file and write the serialized data to it
            try (FileOutputStream fos = new FileOutputStream("BankRecord.txt")) {
                bos.writeTo(fos);
            }

            // Use the file path in load()
            bank.load();  // Call the load method to read from the file

            // Verify that the accounts were loaded correctly
            assertEquals(2, bank.AL.size(), "Two accounts should have been loaded");
            assertEquals("Alice", bank.AL.get(0).getName(), "First account should be Alice");
            assertEquals("Bob", bank.AL.get(1).getName(), "Second account should be Bob");
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 14400)
    public void saveTest() {
        // First, simulate adding two accounts
        Account acc1 = new Account("Alice", 11111111, "1111", 1000.0);
        Account acc2 = new Account("Bob", 22222222, "2222", 500.0);
        bank.AL.add(acc1);
        bank.AL.add(acc2);

        // Capture the output stream where the accounts would be saved
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            bank.save();  // Call the save method to write to the stream

            // Now, deserialize the captured output to check if the accounts were saved correctly
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                Account loadedAcc1 = (Account) in.readObject();
                Account loadedAcc2 = (Account) in.readObject();

                // Check if the accounts match the original data
                assertEquals(acc1.getName(), loadedAcc1.getName(), "First account should match");
                assertEquals(acc2.getName(), loadedAcc2.getName(), "Second account should match");
            }
        } catch (Exception e){
            fail("Exception thrown during test: " + e.getMessage());
        }
    }
}
