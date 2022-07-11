package com.example.jpmshow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

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
import com.example.jpmshow.model.Show;
import com.example.jpmshow.repository.ShowRepository;
import com.example.jpmshow.service.AdminServiceImpl;
import com.example.jpmshow.service.BuyerServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AdminCommandTest {
	
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
	
	@BeforeEach
	void beforeTestCase() {
		// Login to admin before test case
        Object result1 = shell.evaluate(() -> "login admin");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(loginCommand.VAILD_LOGIN));

		// clear database before test case
        showRepository.deleteAll();
	}
	
	@AfterEach
	void afterTestCase() {
		// Logout after each test case
        Object result1 = shell.evaluate(() -> "logout");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(loginCommand.LOGOUT));
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
	void availabilityAdminCommandTesting() {
		System.out.println("********** availabilityAdminCommandTesting **********");

		// Test buyer command (availability), because admin cannot access to buyer command
        // result will contain CommandNotCurrentlyAvailable
        Object result1 = shell.evaluate(() -> "availability 1");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains("CommandNotCurrentlyAvailable"));

		// Test buyer command (book), because admin cannot access to buyer command
        // result will contain CommandNotCurrentlyAvailable
        Object result2 = shell.evaluate(() -> "book 1 88888888 A1");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains("CommandNotCurrentlyAvailable"));

		// Test buyer command (cancel), because admin cannot access to buyer command
        // result will contain CommandNotCurrentlyAvailable 
        Object result3 = shell.evaluate(() -> "cancel 1 88888888");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains("CommandNotCurrentlyAvailable"));
	}
	

	@Test
	void setupCommandTesting() {
		System.out.println("********** setupCommandTesting **********");

		// Check Database (Show table), result should be 0, no record yet
        assertEquals(0, showRepository.count());

		// Test setup command (without Cancellation window), result will contain CREATE_SHOW string
        Object result1 = shell.evaluate(() -> "setup 1 26 10");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(adminService.CREATE_SHOW));

		// Check Database (Show table), result should be 1
        assertEquals(1, showRepository.count());
        Iterator<Show> showItr = showRepository.findAll().iterator();
        while (showItr.hasNext()) {
        	Show show = showItr.next();
            assertEquals(show.getShow_num(), 1);
            assertEquals(show.getRow_num(), 26);
            assertEquals(show.getNum_seat_per_row(), 10);
            assertEquals(show.getCancel_window_min(), 2);
            assertEquals(show.getTickets().size(), 0);
            assertEquals(show.getAvailaleSeats().size(), 260);
            assertEquals(show.getBookedPhone().size(), 0);
        }


		// Test setup command (with Cancellation window) with the same show_num (1), result will contain UPDATE_SHOW string
        Object result2 = shell.evaluate(() -> "setup 1 26 10 3");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(adminService.UPDATE_SHOW));

		// Check Database (Show table), result should be 1, as the previous step is update record
        assertEquals(1, showRepository.count());
        showItr = showRepository.findAll().iterator();
        while (showItr.hasNext()) {
        	Show show = showItr.next();
            assertEquals(show.getShow_num(), 1);
            assertEquals(show.getRow_num(), 26);
            assertEquals(show.getNum_seat_per_row(), 10);
            assertEquals(show.getCancel_window_min(), 3);
            assertEquals(show.getTickets().size(), 0);
            assertEquals(show.getAvailaleSeats().size(), 260);
            assertEquals(show.getBookedPhone().size(), 0);
        }

		// Test setup command, with invalid row(27), max limit is 27
        // result will contain INVALID_SHOW string
        Object result3 = shell.evaluate(() -> "setup 2 27 10 3");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(adminService.INVALID_SHOW));

		// Check Database (Show table), result should be 1, as no addition record
        assertEquals(1, showRepository.count());

		// Test setup command, with invalid seat per row(11), max limit is 10
        // result will contain INVALID_SHOW string
        Object result4 = shell.evaluate(() -> "setup 2 26 11 3");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains(adminService.INVALID_SHOW));

		// Check Database (Show table), result should be 1, as no addition record
        assertEquals(1, showRepository.count());

		// Test setup command with valid parameter, result will contain CREATE_SHOW string
        Object result5 = shell.evaluate(() -> "setup 2 5 6 1");
        resultHandler.handleResult(">> " + result5);
        assertTrue(result5.toString().contains(adminService.CREATE_SHOW));

		// Check Database (Show table), result should be 2, as there is addition record
        assertEquals(2, showRepository.count());
        showItr = showRepository.findAll().iterator();
        while (showItr.hasNext()) {
        	Show show = showItr.next();
        	switch (show.getShow_num()) {
        	case 1:
                assertEquals(show.getShow_num(), 1);
                assertEquals(show.getRow_num(), 26);
                assertEquals(show.getNum_seat_per_row(), 10);
                assertEquals(show.getCancel_window_min(), 3);
                assertEquals(show.getTickets().size(), 0);
                assertEquals(show.getAvailaleSeats().size(), 260);
                assertEquals(show.getBookedPhone().size(), 0);
        		break;
        	case 2:
                assertEquals(show.getShow_num(), 2);
                assertEquals(show.getRow_num(), 5);
                assertEquals(show.getNum_seat_per_row(), 6);
                assertEquals(show.getCancel_window_min(), 1);
                assertEquals(show.getTickets().size(), 0);
                assertEquals(show.getAvailaleSeats().size(), 30);
                assertEquals(show.getBookedPhone().size(), 0);
        		break;
        	}
        }
	}
	
	@Test
	void viewCommandTesting() {
		System.out.println("********** viewCommandTesting **********");

		// Add show record (1), result will contain CREATE_SHOW string
        Object result1 = shell.evaluate(() -> "setup 1 26 10");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(adminService.CREATE_SHOW));

		// Add show record (2), result will contain CREATE_SHOW string
        Object result2 = shell.evaluate(() -> "setup 2 5 6 1");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(adminService.CREATE_SHOW));
        
		// view show record (1), result will contain NO_SOLD string
        Object result3 = shell.evaluate(() -> "view 1");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains(adminService.NO_SOLD));
        
		// view show record (2), result will contain NO_SOLD string
        Object result4 = shell.evaluate(() -> "view 2");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains(adminService.NO_SOLD));

		// view non existing show record (3), result will contain NO_DATA string
        Object result5 = shell.evaluate(() -> "view 3");
        resultHandler.handleResult(">> " + result5);
        assertTrue(result5.toString().contains(adminService.NO_DATA));
        
        logoutAdminLoginBuyer();
        
		// book show record (1), result will contain CREATE_SHOW string
        Object result6 = shell.evaluate(() -> "book 1 88888888 A1,B2");
        resultHandler.handleResult(">> " + result6);
        assertTrue(result6.toString().contains(buyerService.BOOKED));
        
		// book show record (2), result will contain CREATE_SHOW string
        Object result7 = shell.evaluate(() -> "book 2 88888889 C1,C2");
        resultHandler.handleResult(">> " + result7);
        assertTrue(result7.toString().contains((buyerService.BOOKED)));
        
        logoutBuyerLoginAdmin();
        
		// view show record (1), result will contain NO_SOLD string
        Object result8 = shell.evaluate(() -> "view 1");
        resultHandler.handleResult(">> " + result8);
        assertTrue(result8.toString().contains("Ticket number: 1 Phone number: 88888888 Seat number: A1"));
        assertTrue(result8.toString().contains("Ticket number: 2 Phone number: 88888888 Seat number: B2"));
        
		// view show record (2), result will contain NO_SOLD string
        Object result9 = shell.evaluate(() -> "view 2");
        resultHandler.handleResult(">> " + result9);
        assertTrue(result9.toString().contains("Ticket number: 3 Phone number: 88888889 Seat number: C1"));
        assertTrue(result9.toString().contains("Ticket number: 4 Phone number: 88888889 Seat number: C2"));

		// view non existing show record (3), result will contain NO_DATA string
        Object result10 = shell.evaluate(() -> "view 3");
        resultHandler.handleResult(">> " + result10);
        assertTrue(result10.toString().contains(adminService.NO_DATA));
        
        
	}

}
