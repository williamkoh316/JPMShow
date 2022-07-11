package com.example.jpmshow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.shell.result.DefaultResultHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.jpmshow.command.LoginCommand;
import com.example.jpmshow.repository.ShowRepository;
import com.example.jpmshow.repository.TicketRepository;
import com.example.jpmshow.service.AdminServiceImpl;
import com.example.jpmshow.service.BuyerServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BuyerCommandTest {
	
	@Autowired
	private Shell shell;

	@Autowired
	private DefaultResultHandler resultHandler;
	
	@Autowired
	private LoginCommand loginCommand;
	
	@Autowired
	private AdminServiceImpl adminService;
	@Autowired
	private BuyerServiceImpl buyerService;

	@Autowired
	private ShowRepository showRepository;
	@Autowired
	private TicketRepository ticketRepository;
	

	@BeforeEach
	void beforeTestCase() {
		// Login to buyer before test case
        Object result1 = shell.evaluate(() -> "login buyer");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(loginCommand.VAILD_LOGIN));

		// clear database before test case
        showRepository.deleteAll();
        ticketRepository.deleteAll();
	}
	
	@AfterEach
	void afterTestCase() {
		// Logout after each test case
        Object result1 = shell.evaluate(() -> "logout");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(loginCommand.LOGOUT));
	}

	void setupShow() {
		
		logoutBuyerLoginAdmin();

		// Setup show (1)
        Object result2 = shell.evaluate(() -> "setup 1 2 3 1");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(adminService.CREATE_SHOW));

		// Setup show (2)
        Object result3 = shell.evaluate(() -> "setup 2 2 3 1");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(adminService.CREATE_SHOW));
		
        logoutAdminLoginBuyer();
	}
	

	
	void logoutAdminLoginBuyer() {
		// logout admin user
        Object result1 = shell.evaluate(() -> "logout");
        assertTrue(result1.toString().contains(loginCommand.LOGOUT));
        resultHandler.handleResult(">> " + result1);
        
		//login as buyer user
        Object result2 = shell.evaluate(() -> "login buyer");
        assertTrue(result2.toString().contains(loginCommand.VAILD_LOGIN));
        resultHandler.handleResult(">> " + result2);
	}
	
	void logoutBuyerLoginAdmin() {
		// logout buyer user
        Object result1 = shell.evaluate(() -> "logout");
        assertTrue(result1.toString().contains(loginCommand.LOGOUT));
        resultHandler.handleResult(">> " + result1);
        
		//login as admin user
        Object result2 = shell.evaluate(() -> "login admin");
        assertTrue(result2.toString().contains(loginCommand.VAILD_LOGIN));
        resultHandler.handleResult(">> " + result2);
	}
	
	@Test
	void availabilityBuyerCommandTesting() {
		System.out.println("********** availabilityBuyerCommandTesting **********");

		// Test admin command (setup), because buyer cannot access to admin command
        // result will contain CommandNotCurrentlyAvailable
        Object result1 = shell.evaluate(() -> "setup 1 2 3");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains("CommandNotCurrentlyAvailable"));

		// Test admin command (view), because buyer cannot access to admin command
        // result will contain CommandNotCurrentlyAvailable
        Object result2 = shell.evaluate(() -> "view 1");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains("CommandNotCurrentlyAvailable"));
	}
	

	@Test
	void availabilityCommandTesting() {
		System.out.println("********** availabilityCommandTesting **********");

		// Test availability command for show(1), result will contain NO_SHOW_RECORD string
        Object result1 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(buyerService.NO_SHOW_RECORD));

		// Test availability command for show(2), result will contain NO_SHOW_RECORD string
        Object result2 = shell.evaluate(() -> "availability 2");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(buyerService.NO_SHOW_RECORD));
        
        setupShow();

		// Test availability command for show(1)
        Object result3 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(buyerService.AVAILABLE + "1"));
        assertTrue(result3.toString().contains("[A1, A2, A3, B1, B2, B3]"));

		// Test availability command for show(2)
        Object result4 = shell.evaluate(() -> "availability 2");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains(buyerService.AVAILABLE + "2"));
        assertTrue(result4.toString().contains("[A1, A2, A3, B1, B2, B3]"));

	}
	
	
	@Test
	void bookCommandTesting() {
		System.out.println("********** bookCommandTesting **********");

		// Test book command for show(1), result will contain NO_SHOW_RECORD string
        Object result1 = shell.evaluate(() -> "book 1 88888888 A1,B2");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(buyerService.NO_SHOW_RECORD));

		// Test book command for show(2), result will contain NO_SHOW_RECORD string
        Object result2 = shell.evaluate(() -> "book 2 88888888 A1,B2");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(buyerService.NO_SHOW_RECORD));
        
        setupShow();

		// Test book command for show(1), allow lower case for row
        Object result3 = shell.evaluate(() -> "book 1 88888888 a1,b2");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(buyerService.BOOKED));
        assertTrue(result3.toString().contains("Show number: 1 Ticket number: 1 Seat number: A1"));
        assertTrue(result3.toString().contains("Show number: 1 Ticket number: 2 Seat number: B2"));

		// Test book command for show(2)
        Object result4 = shell.evaluate(() -> "book 2 88888888 A1,B2");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains(buyerService.BOOKED));
        assertTrue(result4.toString().contains("Show number: 2 Ticket number: 3 Seat number: A1"));
        assertTrue(result4.toString().contains("Show number: 2 Ticket number: 4 Seat number: B2"));

		// Test book command for show(1) using same phone as previous booking, result will contain PHONE_USED string
        Object result5 = shell.evaluate(() -> "book 1 88888888 A1,B2");
        resultHandler.handleResult(">> " + result5);
        assertTrue(result5.toString().contains(buyerService.PHONE_USED));

		// Test book command for show(2) using same phone as previous booking
        Object result6 = shell.evaluate(() -> "book 2 88888888 A1,B2");
        resultHandler.handleResult(">> " + result6);
        assertTrue(result6.toString().contains(buyerService.PHONE_USED));

		// Test book command for show(1) using different phone as previous booking
        // But the seats are the same as previous booking
        Object result7 = shell.evaluate(() -> "book 1 88888889 A1,B2");
        resultHandler.handleResult(">> " + result7);
        assertTrue(result7.toString().contains(buyerService.INVALID_SEAT));

		// Test book command for show(2) using different phone as previous booking
        // But the seats are the same as previous booking
        Object result8 = shell.evaluate(() -> "book 2 88888889 A1,B2");
        resultHandler.handleResult(">> " + result8);
        assertTrue(result8.toString().contains(buyerService.INVALID_SEAT));

		// Test book command for show(1) using different phone as previous booking
        // But the seat is invalid (out of range from the setup)
        Object result9 = shell.evaluate(() -> "book 1 88888889 A9");
        resultHandler.handleResult(">> " + result9);
        assertTrue(result9.toString().contains(buyerService.INVALID_SEAT));

		// Test book command for show(2) using different phone as previous booking
        // But the seat is invalid (out of range from the setup)
        Object result10 = shell.evaluate(() -> "book 2 88888889 F1");
        resultHandler.handleResult(">> " + result10);
        assertTrue(result10.toString().contains(buyerService.INVALID_SEAT));

		// Test book command for show(1) using different phone as previous booking
        // One of the seats is invalid (out of range from the setup)
        // One of the seats is valid, ticket will issue for the valid seat
        Object result11 = shell.evaluate(() -> "book 1 88888889 A9,B1");
        resultHandler.handleResult(">> " + result11);
        assertTrue(result11.toString().contains(buyerService.BOOKED));
        assertTrue(result11.toString().contains("Show number: 1 Ticket number: 5 Seat number: B1"));

		// Test book command for show(2) using different phone as previous booking
        // One of the seats is invalid (out of range from the setup)
        // One of the seats is valid, ticket will issue for the valid seat
        Object result12 = shell.evaluate(() -> "book 2 88888889 F1,B1");
        resultHandler.handleResult(">> " + result12);
        assertTrue(result12.toString().contains(buyerService.BOOKED));
        assertTrue(result12.toString().contains("Show number: 2 Ticket number: 6 Seat number: B1"));

		// Test book command for show(2) using less than digits phone number
        // result will contain PHONE_NOT_8_Digits string
        Object result13 = shell.evaluate(() -> "book 1 8888887 D1");
        resultHandler.handleResult(">> " + result13);
        assertTrue(result13.toString().contains(buyerService.PHONE_NOT_8_Digits));

		// Test book command for show(2) using more than digits phone number
        // result will contain PHONE_NOT_8_Digits string
        Object result14 = shell.evaluate(() -> "book 1 888888887 D1");
        resultHandler.handleResult(">> " + result14);
        assertTrue(result14.toString().contains(buyerService.PHONE_NOT_8_Digits));
	}
	
	
	@Test
	void cancelCommandTesting() {
		System.out.println("********** cancelCommandTesting **********");

        setupShow();

		// Test cancel command for show(1), result will contain NO_TICKET_RECORD string
        Object result1 = shell.evaluate(() -> "cancel 1 88888888");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(buyerService.NO_TICKET_RECORD));

		// Test cancel command for show(2), result will contain NO_TICKET_RECORD string
        Object result2 = shell.evaluate(() -> "cancel 3 88888888");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(buyerService.NO_TICKET_RECORD));

		// Test book command for show(1)
        Object result3 = shell.evaluate(() -> "book 1 88888888 A1,B2");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(buyerService.BOOKED));
        assertTrue(result3.toString().contains("Show number: 1 Ticket number: 1 Seat number: A1"));
        assertTrue(result3.toString().contains("Show number: 1 Ticket number: 2 Seat number: B2"));

		// Test book command for show(2)
        Object result4 = shell.evaluate(() -> "book 2 88888888 A1,B2");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains(buyerService.BOOKED));
        assertTrue(result4.toString().contains("Show number: 2 Ticket number: 3 Seat number: A1"));
        assertTrue(result4.toString().contains("Show number: 2 Ticket number: 4 Seat number: B2"));

		// Test cancel command for show(1), but wrong phone number, result will contain INCORRECT_PHONE string
        Object result5 = shell.evaluate(() -> "cancel 1 88888887");
        resultHandler.handleResult(">> " + result5);
        assertTrue(result5.toString().contains(buyerService.INCORRECT_PHONE));

		// Test cancel command for show(2), but wrong phone number, result will contain INCORRECT_PHONE string
        Object result6 = shell.evaluate(() -> "cancel 3 88888887");
        resultHandler.handleResult(">> " + result6);
        assertTrue(result6.toString().contains(buyerService.INCORRECT_PHONE));
        
		// Test availability command for show(1), seat A1 is not Available
        Object result7 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result7);
        assertFalse(result7.toString().contains("A1"));
        assertFalse(result7.toString().contains("B2"));

		// Test availability command for show(2), seat A1 is not Available
        Object result8 = shell.evaluate(() -> "availability 2");
        resultHandler.handleResult(">> " + result8);
        assertFalse(result8.toString().contains("A1"));
        assertFalse(result8.toString().contains("B2"));

		// Test cancel command for show(1)
        Object result9 = shell.evaluate(() -> "cancel 1 88888888");
        resultHandler.handleResult(">> " + result9);
        assertTrue(result9.toString().contains(buyerService.CANCEL));

		// Test cancel command for show(2)
        Object result10 = shell.evaluate(() -> "cancel 3 88888888");
        resultHandler.handleResult(">> " + result10);
        assertTrue(result10.toString().contains(buyerService.CANCEL));
        

		// Test availability command for show(1), seat A1 is Available
        Object result11 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result11);
        assertTrue(result11.toString().contains("A1"));
        assertFalse(result11.toString().contains("B2"));
        

		// Test availability command for show(2), seat A1 is Available
        Object result12 = shell.evaluate(() -> "availability 2");
        resultHandler.handleResult(">> " + result12);
        assertTrue(result12.toString().contains("A1"));
        assertFalse(result12.toString().contains("B2"));

		// Test book command for show(1), unable book because there is still ticket using same phone number (88888888)
        // result will contain PHONE_USED string
        Object result13 = shell.evaluate(() -> "book 1 88888888 A3");
        resultHandler.handleResult(">> " + result13);
        assertTrue(result13.toString().contains(buyerService.PHONE_USED));

		// Test book command for show(2), unable book because there is still ticket using same phone number (88888888)
        // result will contain PHONE_USED string
        Object result14 = shell.evaluate(() -> "book 2 88888888 A3");
        resultHandler.handleResult(">> " + result14);
        assertTrue(result14.toString().contains(buyerService.PHONE_USED));

        System.out.println("=====================================================");
        System.out.println(" Wait for 1min, to past the cancellation time for B2");
        System.out.println("=====================================================");

    	System.out.println("Counting down...");
        for(int i=0; i<60; i ++) {
        	System.out.print(60-i + " ");
            try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        }
    	System.out.println("");

		// Test cancel command for show(1), passed cancellation time therefore cannot cancel
        // result will contain CANNOT_CANCEL string
        Object result15 = shell.evaluate(() -> "cancel 2 88888888");
        resultHandler.handleResult(">> " + result15);
        assertTrue(result15.toString().contains(buyerService.CANNOT_CANCEL));

     // Test cancel command for show(2), passed cancellation time therefore cannot cancel
        // result will contain CANNOT_CANCEL string
        Object result16 = shell.evaluate(() -> "cancel 4 88888888");
        resultHandler.handleResult(">> " + result16);
        assertTrue(result16.toString().contains(buyerService.CANNOT_CANCEL));
        

		// Test availability command for show(1), seat B2 is still unavailable
        Object result17 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result17);
        assertFalse(result17.toString().contains("B2"));
        

		// Test availability command for show(2), seat B2 is still unavailable
        Object result18 = shell.evaluate(() -> "availability 2");
        resultHandler.handleResult(">> " + result18);
        assertFalse(result18.toString().contains("B2"));

        
	}

}
