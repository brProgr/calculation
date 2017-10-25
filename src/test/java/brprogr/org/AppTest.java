package brprogr.org;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTest {

	private static App app;

	@BeforeClass
	public static void init() {
		app = new App();
	}

	// Please note: tests with 'assertEquals' (not specified like e.g.
	// assertTrue) because of the clear error message retrieved.

	// Assumption: only input >= 0, that is, no 'minus' sign

	@Test
	public void testAdd() {
		assertEquals(1, app.add(0, 1));
		assertEquals(1, app.add(1, 0));
		assertEquals(1, app.add(0, 1));
		assertEquals(2, app.add(1, 1));
		assertEquals(10100, app.add(10000, 100));
	}

	@Test
	public void testMultiply() {
		assertEquals(1, app.multiply(1, 1));
		assertEquals(0, app.multiply(1, 0));
		assertEquals(0, app.multiply(0, 1));
		assertEquals(2, app.multiply(1, 2));
		assertEquals(1000000, app.multiply(10000, 100));
	}

	@Test
	public void testReadFileReturnReversedStringArray_checkIfFileExistsAndNotEmpty() throws IOException {

		File file = new File("fileEmpty.txt");
		file.createNewFile();
		assertEquals(null, app.readFileReturnStringArrayInCalculationOrder("fileEmpty.txt"));

		file.delete();
		assertEquals(null, app.readFileReturnStringArrayInCalculationOrder("fileEmpty.txt"));
	}

	// UTF-8 assumed however no checks done for now

	@Test
	public void testReadFileReturnStringArrayInCalculationOrder() throws IOException {

		List<String> lines1 = Arrays.asList("add 2", "multiply 3", "apply 4");
		Path file1 = Paths.get("file1.txt");
		Files.write(file1, lines1, Charset.forName("UTF-8"));
		String[] result1 = { "apply 4", "add 2", "multiply 3" };
		assertEquals(result1, app.readFileReturnStringArrayInCalculationOrder("file1.txt"));

		List<String> lines2 = Arrays.asList("multiply 9", "apply 5");
		Path file2 = Paths.get("file2.txt");
		Files.write(file2, lines2, Charset.forName("UTF-8"));
		String[] result2 = { "apply 5", "multiply 9" };
		assertEquals(result2, app.readFileReturnStringArrayInCalculationOrder("file2.txt"));

		List<String> lines3 = Arrays.asList("apply 1");
		Path file3 = Paths.get("file3.txt");
		Files.write(file3, lines3, Charset.forName("UTF-8"));
		String[] result3 = { "apply 1" };
		assertEquals(result3, app.readFileReturnStringArrayInCalculationOrder("file3.txt"));

		// test for empty lines, assumed that does not fail:

		List<String> lines4 = Arrays.asList("add 2", "multiply 3", "", "apply 4");
		Path file4 = Paths.get("file4.txt");
		Files.write(file4, lines4, Charset.forName("UTF-8"));
		String[] result4 = { "apply 4", "add 2", "multiply 3" };
		assertEquals(result4, app.readFileReturnStringArrayInCalculationOrder("file4.txt"));
	}

	@Test
	public void testGetValue() {
		assertEquals(0, app.getValue("0"));
		assertEquals(1, app.getValue("1"));
		assertEquals(-1, app.getValue("a"));
		assertEquals(-1, app.getValue("1.0")); // not defined whether double is
												// accepted, here: fails
	}

	@Test
	public void testGetSplits() {
		assertEquals(null, app.getSplits("")); // < 2 elements ..
		assertEquals(null, app.getSplits(" "));
		assertEquals(null, app.getSplits("a"));
		assertEquals(null, app.getSplits("a a a")); // > 2 elements
		assertEquals(null, app.getSplits("a-a")); // other separator than space

		String[] validInput = { "a", "1" };
		assertEquals(validInput, app.getSplits("a 1"));
	}

	@Test
	public void testGetAppliedValue() {
		assertEquals(-1, app.getAppliedValue("a")); // further split-tests in
													// testGetSplits
		assertEquals(-1, app.getAppliedValue("functionOtherThanApply 1"));
		assertEquals(0, app.getAppliedValue("apply 0"));
		assertEquals(1, app.getAppliedValue("apply 1"));
		assertEquals(10000, app.getAppliedValue("apply 10000")); // further
																	// conversion-tests
																	// in
																	// testGetValue
	}

	@Test
	public void testDoCalculations() {
		String[] instructions1 = { "apply 4", "add 2", "multiply 3" };
		assertEquals(18, app.doCalculations(instructions1, 4));

		String[] instructions2 = { "apply 5", "multiply 9" };
		assertEquals(45, app.doCalculations(instructions2, 5));

		String[] instructions3 = { "apply 1" };
		assertEquals(1, app.doCalculations(instructions3, 1));

		String[] instructions4 = { "apply 4", "add 2", "multiply a" }; // not
																		// numeric
		assertEquals(-1, app.doCalculations(instructions4, 4));
	}

}
