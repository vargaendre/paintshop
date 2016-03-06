package hu.vesz.paintshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaintShop {

	private final Comparator<Customer> customerComparator = (c1, c2) ->
            c1.numOfPaintPreferences()
                .compareTo(c2.numOfPaintPreferences());

	private static final String NO_SOLUTION_EXISTS = "No solution exists";
	private static final Logger LOG = Logger.getLogger(PaintShop.class.getName());
	private int numOfColors = 0;
	private	List<Customer> customers = new ArrayList<>();
	private Map<Integer, Finish> fixedFinishes = new HashMap<>();
	
	public String mixColors(File inputFile) throws Exception, IOException, FileNotFoundException, NumberFormatException {

		readCustomersAndColorsFromFile(inputFile);

		// we want to iterate through the customers ordered by number of paint preferences
		customers.sort(customerComparator);
		
		for (Customer customer : customers) {			
			if (customer.numOfPaintPreferences() == 1) {
				// if the customer has only one paint preference, that must be in the output
				Paint fixedPaint = decidePaintForCustomer(customer, null);
				if (fixedPaint == null) {
					return NO_SOLUTION_EXISTS;
				}
				fixedFinishes.put(fixedPaint.getColor(), fixedPaint.getFinish());
			}
			else {
				// if the customer has more paint preferences, the method below will also
				// fill a list with candidate paints and return a fixed paint or null
				List<Paint> paintCandidates = new ArrayList<>();				
				Paint fixedPaint = decidePaintForCustomer(customer, paintCandidates);
				
				if (fixedPaint != null) {
					// there is a paint already fixed which is good for this customer,
					// nothing left to do, she is already satisfied
					continue;
				}
				else if (paintCandidates.isEmpty()) {
					// all the paint preferences of this customer is in conflict
					// with the ones already fixed - no solution exists
					return NO_SOLUTION_EXISTS;
				}
				
				// we need to select one paint for this customer
				// it should be gloss, since it's cheaper, but
				// if there isn't one, we'll go with the first
				Paint paintToSelect = paintCandidates.get(0);
				for (Paint paint : paintCandidates) {
					if (paint.getFinish().equals(Finish.GLOSS)) {
						paintToSelect = paint;
					}
				}
				
				fixedFinishes.put(paintToSelect.getColor(), paintToSelect.getFinish());
			}
		}
				
		return createOutput(fixedFinishes, numOfColors);
	}

	private void readCustomersAndColorsFromFile(File inputFile) throws Exception {

		boolean firstLineParsed = false;

		// first we need to read the input file and parse the colors and customers
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

			String line = null;
			while ((line = br.readLine()) != null) {
				if (!firstLineParsed) {
					numOfColors = Integer.parseInt(line.trim());
					firstLineParsed = true;
				} else {
					Customer customer = parseCustomer(line);
					customers.add(customer);
				}
			}
		}
	}

	/**
	 * Decides for a customer, whether there is one Paint which must be in the output,
	 * or gives a list of possible paints. <br/>
	 * If the customer has only one paint preference, then this method only tries to find
	 * a suitable paint and return it - or null if there's no such paint (it means there is 
	 * no solution for the input).
	 * If the customer has more paint preferences, the method can still return a paint
	 * from the already fixed ones - if there is one matching - or populates a list of
	 * paint candidates.
	 * 
	 * @param customer
	 * @param paintCandidates can be null if the customer has only one paint preference
	 * @return a paint which must be in the output if there is such, null otherwise.
	 */
	private Paint decidePaintForCustomer(Customer customer, List<Paint> paintCandidates) {
		
		for (Paint paint : customer.getPaintPreferences()) {
			Integer color = paint.getColor();
			Finish finish = paint.getFinish();
			Finish fixedFinish = fixedFinishes.get(color);
			
			if (customer.numOfPaintPreferences() == 1) {
				if (fixedFinish == null || fixedFinish.equals(finish)) {
					// there is no finish fixed for this color yet,
					// or it's the same as the preference of this customer
					return paint;
				}
				else {
					// there is another finish needed for this color already,
					// there is no solution.
					return null;
				}
			}
			else {
				if (fixedFinish == null) {
					// 'color' is not in the fixed finishes yet, 
					// let's remember it as a candidate
					paintCandidates.add(paint);
				}
				else if (fixedFinish.equals(finish)){
					// we found one of the paint preferences of this customer
					// in the already fixed paints, let's return it!
					return paint;
				}		
			}
		}
		
		// for a customer with multiple paint preferences, there were no match 
		// in the already fixed paints; that's not a problem.
		return null;
	}
	
	/**
	 * Creates the string for the output of the program.
	 * If a color is missing from the keys of the fixedFinishes map,
	 * than it is considered gloss, since it's cheaper than matte. 
	 * 
	 * @param fixedFinishes
	 * @param numOfColors
	 * @return
	 */
	private String createOutput(Map<Integer, Finish> fixedFinishes, int numOfColors) {

		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= numOfColors; i++) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			Finish finish = fixedFinishes.get(i);
			if (finish == null) {
				// If no specific need for a finish, let's make it gloss, 
				// because it's cheaper. This can happen if there are 
				// more colors than customers.
				finish = Finish.GLOSS;
			}
			sb.append(finish.getAbbreviation());
		}
		
		return sb.toString();
	}

	/**
	 * Parses a line in the input file, and returns a Customer object.<br/>
	 * E.g.: <br/>
	 * 1 M 2 G 5 M <br/>
	 * will become a customer with preference for colors 1 and 5 to be matte
	 * and color 2 to be gloss.
	 * 
	 * @param line is representing a customer in the input file
	 * @return the parsed Customer
	 * @throws Exception thrown if the finish abbreviation is bad
	 * @throws NumberFormatException thrown if number of color cannot be parsed
	 */
	private Customer parseCustomer(String line) throws Exception,
			NumberFormatException {

		if (line == null || line.equals("")) {
			return null;
		}

		Customer customer = new Customer();
		
		// 'colors' will be similar to: [1,M,2,G,5,M]
		String[] colors = line.split(" ");
		for (int i = 0; i < colors.length; i+=2) {

			Integer color = Integer.parseInt(colors[i]);
			String finishStr = colors[i+1];
			Finish finish = Finish.parseFinish(finishStr);
			
			customer.addPaintPreference(color, finish);
		}

		return customer;
	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Number of arguments is incorrect. "
					+ "Please provide the path for the input file as the first argument!");
			System.exit(1);
		}
		
		try {

			PaintShop paintShop = new PaintShop();
			File file = new File(args[0]);	
			String finishes = paintShop.mixColors(file);
			System.out.println(finishes);
			
		} catch (NumberFormatException e) {
			LOG.log(Level.SEVERE, "The program could not parse the input file.", e);
			System.exit(2);
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "The specified input file does not exist, "
					+ "or cannot be read: " + args[0], e);
			System.exit(3);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "An error occured while reading the input file.", e);
			System.exit(4);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "An error occured during runtime.", e);
			System.exit(5);
		}

		System.exit(0);
	}
}
