package com.deere.pdp.automation.previve.CI_Tests.UI_Tests.Feature_security;

import static com.deere.pdp.automation.previve.Configuration.TestRunManager.getDriverConfiguration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.deere.pdp.automation.previve.PreViVeConstants;
import com.deere.pdp.automation.previve.Actions.CommonHelperActions;
import com.deere.pdp.automation.previve.Configuration.BrowserConfig;
import com.deere.pdp.automation.previve.Objects.Security.SystemRole;
import com.deere.pdp.automation.previve.Pages.AdminPage;
import com.deere.pdp.automation.previve.Pages.LoginPage;
import com.deere.pdp.automation.previve.Pages.Security.SystemRolePage;
import com.deere.pdp.automation.previve.TestData.Security.AdminData;
import com.deere.pdp.automation.previve.TestData.Security.SystemRoleData;



public class SystemRoleTest {

	WebDriver driver = null;
	SoftAssert sassert = null;
	AdminData adminData = new AdminData();
	SystemRoleData roleData = new SystemRoleData();
	SystemRole role = null;
	private String roleName = "";
	private String roleDesc = "";
	Boolean isCreatedRoleExist = false;
	HashMap<String,String> permissionFilesMapping;
	String accordionName ="";
	String manageUserRolelink ;
	
	@Parameters( {"browser"} )
	@BeforeClass
	public void setUp(@Optional("chrome")String browserToRun) {
		
		sassert = new SoftAssert();
		driver = getDriverConfiguration(browserToRun).getDriver();
		LoginPage.Login(driver);	
		
	}
	
	@Test(priority = 0)
	public void testCreateRole(){
		manageUserRolelink = adminData.getAdminMenuList().get(0).getMenuTitle();
		AdminPage.navigateTo(driver, manageUserRolelink);
		
		role = roleData.getSystemRole();
		permissionFilesMapping = role.getModule().get("Projects");
		roleName = role.getRoleName();
		roleDesc = role.getRoleDiscription();
		SystemRolePage.createRole(driver, roleName, roleDesc, permissionFilesMapping);
		
		accordionName = PreViVeConstants.PROJECTS_CONTACTS;
		
		AdminPage.navigateTo(driver, manageUserRolelink);
		Boolean isCreatedRoleExist = SystemRolePage.checkPresenceOfRole(driver, roleName);
		Boolean roleExist = true;
		Assert.assertEquals(isCreatedRoleExist, roleExist);
	}
	
	@Test(dependsOnMethods={"testCreateRole"})
	public void testUpdateRole(){
		SystemRolePage.clickRoleToUpdate(driver);
		
		//Update and add new permissions
		accordionName = PreViVeConstants.PROJECTS_CONTACTS;
		permissionFilesMapping.put(accordionName, PreViVeConstants.PERMISSIONS_EDIT);
		accordionName = PreViVeConstants.PROJECTS_ACCOUNTS;
		permissionFilesMapping.put(accordionName, PreViVeConstants.PERMISSIONS_NONE);
		accordionName = PreViVeConstants.PROJECTS_RELLATEDPROJECTS;
		permissionFilesMapping.put(accordionName, PreViVeConstants.PERMISSIONS_EDIT);
		accordionName = PreViVeConstants.PROJECTS_PROJECT_DETAILS;
		permissionFilesMapping.put(accordionName, PreViVeConstants.PERMISSIONS_READ);
		accordionName = PreViVeConstants.PROJECTS_PRODUCT_HIERARCHY;
		permissionFilesMapping.put(accordionName, PreViVeConstants.PERMISSIONS_CREATE_DELETE);
		
		SystemRolePage.selectPermission(driver, permissionFilesMapping);
		SystemRolePage.clickSaveButton(driver);
		SystemRolePage.navigateTo(driver, manageUserRolelink);
		SystemRolePage.clickRoleToUpdate(driver);
		
		accordionName = PreViVeConstants.PROJECTS_PROJECT_DETAILS;
		String expectedPermission = permissionFilesMapping.get(accordionName);
		String displayedPermission = SystemRolePage.getPermission(driver, accordionName);
		Assert.assertEquals(displayedPermission, expectedPermission,"Permission not updated for "+accordionName+",expectedPermission:"+expectedPermission+" but displayedPermission :"+displayedPermission);
		
		accordionName = PreViVeConstants.PROJECTS_PRODUCT_HIERARCHY;
		expectedPermission = permissionFilesMapping.get(accordionName);
		displayedPermission = SystemRolePage.getPermission(driver, accordionName);
		Assert.assertEquals(displayedPermission, expectedPermission,"Permission not updated for "+accordionName+",expectedPermission:"+expectedPermission+" but displayedPermission :"+displayedPermission);
		
		accordionName = PreViVeConstants.PROJECTS_ACCOUNTS;
		expectedPermission = permissionFilesMapping.get(accordionName);
		displayedPermission = SystemRolePage.getPermission(driver, accordionName);
		Assert.assertEquals(displayedPermission, expectedPermission,"Permission not updated for "+accordionName+",expectedPermission:"+expectedPermission+"expectedPermission :"+expectedPermission);
		
	}	
	

	@Test(dependsOnMethods={"testUpdateRole"})
	public void testSaveButtonDisableOnPermissionClear(){
		//Clear existing Permission
		accordionName = PreViVeConstants.PROJECTS_PRODUCT_HIERARCHY;
		permissionFilesMapping.put(accordionName, "");
		
		SystemRolePage.selectPermission(driver, permissionFilesMapping);
		
		sassert.assertEquals(SystemRolePage.isSaveButtonEnabled(driver), false);
	}
	
	@AfterClass
	public void tearDown() {
		SystemRolePage.deleteRolefromDB(roleName);
		driver.quit();
		sassert.assertAll();
	}
}

