/*
 * JSynthlib - Constants for Yamaha TX7
 * ====================================
 * @author  Torsten Tittmann
 * file:    TX7Constants.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *
 */
package synthdrivers.YamahaTX7;
public class TX7Constants
{
  // voice patch/bank numbers of single/bank driver
  protected static final String[] PATCH_NUMBERS_VOICE = 
                                                  {"01","02","03","04","05","06","07","08",
                                                   "09","10","11","12","13","14","15","16",
                                                   "17","18","19","20","21","22","23","24",
                                                   "25","26","27","28","29","30","31","32"};

  protected static final String[] BANK_NUMBERS_SINGLE_VOICE = 
                                                 {"Internal"};

  protected static final String[] BANK_NUMBERS_BANK_VOICE = 
                                                 {"Internal"};

  // TX7 performance patch/bank numbers
  protected static final String[] PATCH_NUMBERS_PERFORMANCE = {
                                                   "01","02","03","04","05","06","07","08",
                                                   "09","10","11","12","13","14","15","16",
                                                   "17","18","19","20","21","22","23","24",
                                                   "25","26","27","28","29","30","31","32",
                                                   "33","34","35","36","37","38","39","40",
                                                   "41","42","43","44","45","46","47","48",
                                                   "49","50","51","52","53","54","55","56",
                                                   "57","58","59","60","61","62","63","64"};

  protected static final String[] BANK_NUMBERS_PERFORMANCE  = {"Internal"};

  // ==============================================================================================
  // INIT PATCHES
  // ==============================================================================================

  // Init Single Voice patch ("init voice")
  protected static final byte [] INIT_VOICE = {
  -16,67,0,0,1,27,99,99,99,99,99,99,99,0,0,0,0,0,0,0,
  0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,0,
  0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,
  0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,
  0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,
  0,0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,
  0,0,0,0,0,0,0,99,0,1,0,7,99,99,99,99,50,50,50,50,
  0,0,1,35,0,0,0,1,0,3,24,73,78,73,84,32,86,79,73,67,
  69,81,-9};

  // Init TX7 Single Performance patch (" YAMAHA TX7 FUNCTION DATA  ")
  protected static final byte [] INIT_PERFORMANCE = {
  -16,67,0,1,0,94,0,0,0,7,0,0,0,0,1,8,
  1,8,0,8,0,15,0,0,0,0,0,0,0,0,99,99,
  7,0,1,24,0,0,0,7,0,0,0,0,1,8,1,8,
  0,8,0,15,0,0,0,0,0,0,0,0,99,99,7,0,
  1,24,0,0,0,39,32,89,65,77,65,72,65,32,32,84,
  88,55,32,32,70,85,78,67,84,73,79,78,32,32,68,65,
  84,65,32,32,121,-9};
}
