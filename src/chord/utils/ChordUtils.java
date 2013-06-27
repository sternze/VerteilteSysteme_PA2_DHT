package chord.utils;

public class ChordUtils {

	/**
	 * Check to see if one identifier lies between two others on a Chord, including
	 * the upper bound.
	 * 
	 * @param number The identifier to check.
	 * @param lower The lower identifier.
	 * @param upper The upper identifier.
	 * 
	 * @return True if 'number' is in range.
	 */
	public static boolean inRangeLeftOpenIntervall(Long number, Long lower, Long upper) {
		
		if (lower == null || upper == null || number == null) {
			return false;
		}
		
		int comp = lower.compareTo(upper);
		boolean retVal = false;
		
		// lower < greater
		if (comp < 0) {
			// number > lower and number <= upper
			if (number.compareTo(lower) > 0 && number.compareTo(upper) <= 0) {
				retVal = true;
			}
		// lower > greater
		} else if (comp > 0) {
			// number > lower or number <= upper
			if (number.compareTo(lower) > 0 || number.compareTo(upper) <= 0) {
				retVal = true;
			}
		} else {
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * Check to see if one identifier lies between two others on a Chord, excluding
	 * the upper and lower bound.
	 * 
	 * @param number The identifier to check.
	 * @param lower The lower identifier.
	 * @param upper The upper identifier.
	 * 
	 * @return True if 'number' is in range.
	 */
	public static boolean inRangeOpenIntervall(Long number, Long lower, Long upper) {
		int comp = lower.compareTo(upper);
		boolean retVal = false;
		
		if (comp < 0) {
			if (number.compareTo(lower) > 0 && number.compareTo(upper) < 0) {
				retVal = true;
			}
		} else if (comp > 0) {
			if (number.compareTo(lower) > 0 || number.compareTo(upper) < 0) {
				retVal = true;
			}
		} else {
			retVal = true;
		}
		
		return retVal;	
	}
}
