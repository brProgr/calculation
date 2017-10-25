package brprogr.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.List;

public class App {

	public static void main(String[] args) {

		System.out.print("Please indicate the file to read: ");
		String fileName = System.console().readLine();

		if (fileName.length() == 0) {
			System.out.println("We are sorry but we cannot find the file, program ends.");
			System.exit(0);
		}

		App app = new App();

		String[] instructions = app.readFileReturnStringArrayInCalculationOrder(fileName);
		if (instructions == null || instructions.length == 0) {
			System.out.println("We are sorry but we cannot read the file, program ends.");
			System.exit(0);
		}

		int result = app.getAppliedValue(instructions[0]);
		if (result == -1) {
			System.out.println("We are sorry but the instruction file's 'apply' line is not valid, program ends.");
			System.exit(0);
		}

		result = app.doCalculations(instructions, result);

		if (result == -1) {
			System.out.println("Program ends.");
			System.exit(0);
		}
		System.out.println("Calculated output is: " + result);
	}

	protected int doCalculations(String[] instructions, int start) {
		int result = start;

		for (int i = 1; i < instructions.length; i++) {
			String[] splits = getSplits(instructions[i]);
			if (splits == null) {
				System.out.println("We are sorry but the instruction " + instructions[i] + " is not valid.");
				return -1;
			}

			int value = getValue(splits[1]);
			if (value == -1) {
				System.out.println("We are sorry but the numeric value " + splits[1] + " is not valid.");
				return -1;
			}

			switch (splits[0]) {
			case "add":
				result = add(result, value);
				break;
			case "multiply":
				result = multiply(result, value);
				break;
			default:
				System.out.println("We are sorry but the function " + splits[0] + " is, here, not valid.");
				return -1;
			}
		}
		return result;
	}

	protected int getAppliedValue(String line) {
		String[] splits = getSplits(line);
		if (splits == null || !(splits[0].equals("apply")))
			return -1;

		return getValue(splits[1]);
	}

	protected String[] getSplits(String line) {
		String[] splits = line.split(" ");
		if (splits.length != 2)
			return null;

		return splits;
	}

	// error: return -1

	protected int getValue(String value) {
		int result = -1;

		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
		return result;
	}

	/*
	 * Assumption: given input instructions in order 1, 2, 3 (f for a
	 * mathematical function):
	 *
	 * f1 y (1) f2 z (2) apply x (3 resp. last line)
	 *
	 * to be executed in order: first, the apply-line, then the other lines in
	 * the 'incoming' order, like so:
	 *
	 * apply x (last line) f1 y f2 z
	 * 
	 * The line 'apply x' is assumed to be the last line in the file.
	 *
	 * It is assumed that empty lines are to skip. The program continues to read
	 * lines until the EOF.
	 */

	protected String[] readFileReturnStringArrayInCalculationOrder(String fileName) {

		File file = new File(fileName);
		boolean empty = !file.exists() || file.length() == 0;
		if (empty)
			return null;

		List<String> lines = new ArrayList<String>();

		try {
			InputStream fis = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.trim().length() > 0) // empty lines are skipped, no
												// error assumed
					lines.add(line);
			}
			br.close();

		} catch (Exception e) {
			return null;
		}

		if (lines.size() == 0)
			return null;

		int s = lines.size();

		String[] linesArr = new String[s];
		linesArr[0] = lines.get(s - 1);

		for (int i = 1; i < s; i++) {
			linesArr[i] = lines.get(i - 1);
		}
		return linesArr;
	}

	protected int add(int input, int toAdd) {
		return input + toAdd;
	}

	protected int multiply(int input, int factor) {
		return input * factor;
	}

}
