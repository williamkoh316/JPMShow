package com.example.jpmshow;

import static org.junit.Assert.assertTrue;

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

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoginCommandTest {
	
	@Autowired
	private Shell shell;

	@Autowired
	private DefaultResultHandler resultHandler;
	
	@Autowired
	private LoginCommand loginCommand;
	
	@Test
	void availabilityLoginCommandTesting() {
		System.out.println("********** availabilityLoginCommandTesting **********");
		
		// Test logout command, because user have not login, result will contain CommandNotCurrentlyAvailable
        Object result1 = shell.evaluate(() -> "logout");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains("CommandNotCurrentlyAvailable"));

		// Test Admin login, because user have not login, result will contain VAILD_LOGIN string
        Object result2 = shell.evaluate(() -> "login admin");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains(loginCommand.VAILD_LOGIN));
        
		// Test Admin command, because user have already login, result will contain CommandNotCurrentlyAvailable
        Object result3 = shell.evaluate(() -> "login admin");
        resultHandler.handleResult(">> " + result3);
        assertTrue(result3.toString().contains("CommandNotCurrentlyAvailable"));
        
		// Test Buyer command, because user have already login, result will contain CommandNotCurrentlyAvailable
        Object result4 = shell.evaluate(() -> "login buyer");
        resultHandler.handleResult(">> " + result4);
        assertTrue(result4.toString().contains("CommandNotCurrentlyAvailable"));
        
		// Test logout command, because user have login, result will contain LOGOUT string
        Object result5 = shell.evaluate(() -> "logout");
        resultHandler.handleResult(">> " + result5);
        assertTrue(result5.toString().contains(loginCommand.LOGOUT));

		// Test Buyer login, because user have not login, result will contain VAILD_LOGIN string
        Object result6 = shell.evaluate(() -> "login buyer");
        resultHandler.handleResult(">> " + result6);
        assertTrue(result6.toString().contains(loginCommand.VAILD_LOGIN));
        
		// Test Admin command, because user have already login, result will contain CommandNotCurrentlyAvailable
        Object result7 = shell.evaluate(() -> "login admin");
        resultHandler.handleResult(">> " + result7);
        assertTrue(result7.toString().contains("CommandNotCurrentlyAvailable"));
        
		// Test Buyer command, because user have already login, result will contain CommandNotCurrentlyAvailable
        Object result8 = shell.evaluate(() -> "login buyer");
        resultHandler.handleResult(">> " + result8);
        assertTrue(result8.toString().contains("CommandNotCurrentlyAvailable"));
        
		// Test logout command, because user have login, result will contain LOGOUT string
        Object result9 = shell.evaluate(() -> "logout");
        resultHandler.handleResult(">> " + result9);
        assertTrue(result9.toString().contains(loginCommand.LOGOUT));

	}
	
	@Test
	void incorrectLoginCommandTesting() {
		System.out.println("********** incorrectLoginCommandTesting **********");
		
		// Test incorrect user (admin1) login, currently there are only 2 users (admin & buyer)
		// result will contain INVAILD_LOGIN string
        Object result1 = shell.evaluate(() -> "login admin1");
        resultHandler.handleResult(">> " + result1);
        assertTrue(result1.toString().contains(loginCommand.INVAILD_LOGIN));
		
		// Test entered extra arg(extraArg) login, currently there is only arg required (admin or buyer)
		// result will will contain IllegalArgumentException
        Object result2 = shell.evaluate(() -> "login admin extraArg");
        resultHandler.handleResult(">> " + result2);
        assertTrue(result2.toString().contains("IllegalArgumentException"));

	}

}
