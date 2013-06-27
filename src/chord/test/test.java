package chord.test;

import chord.utils.ChordUtils;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1: is in range
		// 2: lower bound
		// 3: upper bound
		System.out.println(ChordUtils.inRangeLeftOpenIntervall((long)71, (long)7, (long)25));
		System.out.println(ChordUtils.inRangeOpenIntervall((long)25, (long)7, (long)71));
	}

}
