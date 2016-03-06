package hu.vesz.paintshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import junit.framework.TestCase;

public class PaintShopTest extends TestCase {

	PaintShop paintShop = new PaintShop();
	ClassLoader classLoader = getClass().getClassLoader();
	String inputFilesFolder = "input_files" + File.separator;
	
	@Test
	public void test5colors3customers_valid() throws Exception {

		File file = new File(classLoader.getResource(inputFilesFolder 
				+ "5colors3customers_valid.glt").toURI());
		String output = paintShop.mixColors(file);
		
		assertEquals("G G G G M", output);
	}

	@Test
	public void test1color2customers_invalid() throws Exception {

		File file = new File(classLoader.getResource(inputFilesFolder 
				+ "1color2customers_invalid.glt").toURI());
		String output = paintShop.mixColors(file);
		
		assertEquals("No solution exists", output);
	}

	@Test
	public void test5colors14customers_valid() throws Exception {

		File file = new File(classLoader.getResource(inputFilesFolder 
				+ "5colors14customers_valid.glt").toURI());
		String output = paintShop.mixColors(file);
		
		assertEquals("G M G M G", output);
	}

	@Test
	public void test2colors2customers_valid() throws Exception {
		
		File file = new File(classLoader.getResource(inputFilesFolder 
				+ "2colors2customers_valid.glt").toURI());
		String output = paintShop.mixColors(file);
		
		assertEquals("M M", output);
	}

	@Test
	public void testMoreColorsThanNeeded() throws Exception {

		File file = new File(classLoader.getResource(inputFilesFolder 
				+ "5colors2customers_valid.glt").toURI());
		String output = paintShop.mixColors(file);
		
		assertEquals("G G G M G", output);
	}

	@Test
	public void testNonExistingFile() {		
		try {
			paintShop.mixColors(new File("doesnotexist"));
			fail();
		} catch (FileNotFoundException e) {
			// We are expecting this
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testBadFinishFormat() {
		try {
			File file = new File(classLoader.getResource(inputFilesFolder 
					+ "badFinish_invalid.glt").toURI());
			paintShop.mixColors(file);
			fail();
		} catch (NumberFormatException | IOException | URISyntaxException e) {
			fail();
		} catch (Exception e) {
			if (!e.getMessage().startsWith("Invalid abbreviation for Finish: ")) {
				fail();
			}
		}
	}
	
	@Test
	public void testMalformedCustomers() {
		try {
			File file = new File(classLoader.getResource(inputFilesFolder 
					+ "malformedCustomers_invalid.glt").toURI());
			paintShop.mixColors(file);
			fail();
		} catch (NumberFormatException e) {
			// We are expecting this
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testMissingNumberOfColors() {
		try {
			File file = new File(classLoader.getResource(inputFilesFolder 
					+ "missingNumberOfColors_invalid.glt").toURI());
			paintShop.mixColors(file);
			fail();
		} catch (NumberFormatException e) {
			// We are expecting this
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testEmptyLineInInputFile() {
		try {
			File file = new File(classLoader.getResource(inputFilesFolder 
					+ "emptyLine_invalid.glt").toURI());
			paintShop.mixColors(file);
			fail();
		} catch (NullPointerException e) {
			// We are expecting this
		} catch (Exception e) {
			fail();
		}
	}
}
