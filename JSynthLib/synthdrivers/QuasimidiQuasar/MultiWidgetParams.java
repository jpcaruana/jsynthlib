/*
 * Copyright 2005 Joachim Backhaus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.QuasimidiQuasar;


/**
 *
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class MultiWidgetParams {
	private String label;
	private int startBit;
	private int endBit;
	private String[] options;
	private int bitmask;
	private int index = -1;

	/**
	* Use this constructor for JCheckBox widgets
	*
	* @param label		The text of the JCheckBox
	* @param startBit	The starting bit (0 - 7)
	*/
	public MultiWidgetParams(String label, int startBit) {
		if(startBit > 7 || startBit < 0)
			throw new IllegalArgumentException("startBit is greater than 7 or lower than 0: " + startBit);

		this.label = label;
		this.startBit = startBit;
		this.endBit = startBit;

		calculateBitmask();
	}

	/*
	* Use this constructor for JComboBox widgets
	*
	* @param label		The text of the JCheckBox
	* @param startBit	The starting bit (0 - 7)
	* @param endBit		The ending bit (0 - 7)
	* @param options	The options for the JComboBox
	*/
	public MultiWidgetParams(String label, int startBit, int endBit, String[] options) {
		if (startBit < 0)
			throw new IllegalArgumentException("startBit is lower than 0!");
		if (startBit > endBit)
			throw new IllegalArgumentException("startBit is greater than endBit!");
		if(endBit > 7)
			throw new IllegalArgumentException("endBit is greater than 7!");
		if(startBit != endBit && options == null)
			throw new NullPointerException("Specify the options for the JComboBox!");

		this.label = label;
		this.startBit = startBit;
		this.endBit = endBit;
		this.options = options;

		calculateBitmask();
	}

	protected void calculateBitmask() {
		int bitmask = 0;

		for (int count = startBit; count <= endBit; count++) {
			bitmask += (int) Math.pow(2, count);
		}

		this.bitmask = bitmask;
	}

	protected String getLabel() {
		return this.label;
	}

	protected boolean isCheckBox() {
		return (this.startBit == this.endBit);
	}

	protected int getStartBit() {
		return this.startBit;
	}

	protected int getEndBit() {
		return this.endBit;
	}

	protected int getBitmask() {
		return this.bitmask;
	}

	protected String[] getOptions() {
		return this.options;
	}

	protected int getRealValue(int value) {
		return ((value & this.getBitmask() ) >> this.getStartBit() );
	}

	protected int getIndex() {
		// Index has to be set manually
		return index;
	}

	protected void setIndex(int index) {
		this.index = index;
	}
}