/*
 * JSynthlib - Constants for Yamaha DX7-II
 * =======================================
 * @author  Torsten Tittmann
 * file:    DX7IIConstants.java
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
package synthdrivers.YamahaDX7II;
public class DX7IIConstants
{
  // voice patch/bank numbers of single/bank driver
  protected static final String[] PATCH_NUMBERS_VOICE = 
                                                  {"01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
                                                   "09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
                                                   "17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
                                                   "25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"};

  protected static final String[] BANK_NUMBERS_SINGLE_VOICE = 
                                                 {"Internal (01-32)", "Internal (33-64)", "Cartridge (01-32)", "Cartridge (33-64)"};

  protected static final String[] BANK_NUMBERS_BANK_VOICE = 
                                                 {"Internal (01-32)", "Internal (33-64)"};

  // Additional Voice patch/bank numbers
   protected static final String[] PATCH_NUMBERS_ADDITIONAL_VOICE = 
                                                  {"01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
                                                   "09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
                                                   "17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
                                                   "25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"};

  protected static final String[] BANK_NUMBERS_SINGLE_ADDITIONAL_VOICE = 
                                                 {"Internal (01-32)", "Internal (33-64)", "Cartridge (01-32)", "Cartridge (33-64)"};

  protected static final String[] BANK_NUMBERS_BANK_ADDITIONAL_VOICE = 
                                                 {"Internal (01-32)", "Internal (33-64)"};

  // Fractional Scaling patch/bank numbers
  protected static final String[] PATCH_NUMBERS_FRACTIONAL_SCALING = {
                                                   "01/33","02/34","03/35","04/36","05/37","06/38","07/39","08/40",
                                                   "09/41","10/42","11/43","12/44","13/45","14/46","15/47","16/48",
                                                   "17/49","18/50","19/51","20/52","21/53","22/54","23/55","24/56",
                                                   "25/57","26/58","27/59","28/60","29/61","30/62","31/63","32/64"};

  protected static final String[] BANK_NUMBERS_FRACTIONAL_SCALING  = {"Cartridge (1-32)", "Cartridge (33-64)"};

  // Micro Tuning patch/bank numbers of single/bank driver
  protected static final String[] PATCH_NUMBERS_MICRO_TUNING_SINGLE = {"Edit Buffer","User 1","User 2"};
  protected static final String[]  BANK_NUMBERS_MICRO_TUNING_SINGLE = {"Internal"};

  protected static final String[] PATCH_NUMBERS_MICRO_TUNING_BANK   = {
  "CRT   1","CRT   2","CRT   3","CRT   4","CRT   5","CRT   6","CRT   7","CRT   8",
  "CRT   9","CRT  10","CRT  11","CRT  12","CRT  13","CRT  14","CRT  15","CRT  16",
  "CRT  17","CRT  18","CRT  19","CRT  20","CRT  21","CRT  22","CRT  23","CRT  24",
  "CRT  25","CRT  26","CRT  27","CRT  28","CRT  29","CRT  30","CRT  31","CRT  32",
  "CRT  33","CRT  34","CRT  35","CRT  36","CRT  37","CRT  38","CRT  39","CRT  40",
  "CRT  41","CRT  42","CRT  43","CRT  44","CRT  45","CRT  46","CRT  47","CRT  48",
  "CRT  49","CRT  50","CRT  51","CRT  52","CRT  53","CRT  54","CRT  55","CRT  56",
  "CRT  57","CRT  58","CRT  59","CRT  60","CRT  61","CRT  62","CRT  63"};
 
  protected static final String[] BANK_NUMBERS_MICRO_TUNING_BANK    = {"Cartridge"};

  // System Setup patch/bank numbers
  protected static final String[] PATCH_NUMBERS_SYSTEM_SETUP = {"System Setup"};
  protected static final String[]  BANK_NUMBERS_SYSTEM_SETUP = {"Internal"};

  // Performance patch/bank numbers
  protected static final String[] PATCH_NUMBERS_PERFORMANCE = {
                                                   "01","02","03","04","05","06","07","08",
                                                   "09","10","11","12","13","14","15","16",
                                                   "17","18","19","20","21","22","23","24",
                                                   "25","26","27","28","29","30","31","32"};

  protected static final String[]  BANK_NUMBERS_PERFORMANCE =  {"Internal"};

  // ==============================================================================================
  // INIT PATCHES
  // ==============================================================================================

  // Init Single Voice patch ("INIT VOICE")
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

  // Init Additional Voice patch (no patchname)
  protected static final byte []INIT_ADDITIONAL_VOICE = {
  -16,67,0,5,0,49,0,0,0,0,0,0,0,0,0,0,
  0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,
  0,0,0,0,0,0,0,50,0,0,0,50,0,0,0,0,
  0,0,0,0,0,0,0,26,-9};

  // Init Micro Tuning patch (no patchname)
  protected static final byte []INIT_MICRO_TUNING = {
  -16,67,0,126,2,10,76,77,32,32,77,67,82,89,77,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  42,0,42,0,42,0,42,0,42,0,42,0,42,0,42,0,
  31,-9};

  // Init Fractional Scaling patch (no patchname)
  protected static final byte []INIT_FRACTIONAL_SCALING = {
  -16,67,0,126,3,118,76,77,32,32,70,75,83,89,69,32,
  48,48,63,63,63,50,62,50,61,52,60,52,59,52,58,54,
  57,54,56,56,55,56,54,56,53,58,52,58,51,58,50,60,
  49,60,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
  48,62,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
  48,62,48,62,48,62,48,62,48,62,48,62,48,62,48,62,
  48,62,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,50,48,50,48,50,48,50,48,52,
  48,52,48,54,48,54,48,56,48,58,48,60,48,62,49,48,
  49,50,49,50,49,50,49,50,49,50,49,50,49,50,49,50,
  49,50,49,50,48,48,56,56,56,50,55,58,55,52,54,62,
  54,54,54,48,53,56,53,50,52,58,52,52,51,60,51,54,
  51,48,50,56,50,50,49,58,49,52,48,60,48,54,48,48,
  48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,
  48,48,48,48,48,48,48,50,48,50,48,50,48,50,48,52,
  48,52,48,54,48,54,48,56,48,58,48,60,48,62,49,50,
  49,54,49,58,49,60,49,60,49,60,49,60,49,60,49,60,
  49,60,49,60,49,60,49,60,48,48,54,50,54,48,53,62,
  53,60,53,58,53,56,53,52,53,50,53,48,52,62,52,60,
  52,58,52,56,52,54,52,50,52,48,51,62,51,60,51,58,
  51,56,51,54,51,52,51,48,50,62,50,60,50,58,50,56,
  50,54,50,52,50,50,50,50,50,50,50,50,50,50,50,50,
  50,50,50,50,50,50,50,50,50,50,48,48,48,48,48,48,
  48,48,48,48,48,48,48,48,48,50,48,50,48,50,48,52,
  48,52,48,52,48,54,48,54,48,54,48,56,48,56,48,56,
  48,58,48,58,48,58,48,58,48,60,48,60,48,60,48,62,
  48,62,48,62,49,48,49,48,49,48,49,50,49,50,49,50,
  49,50,49,50,49,50,49,50,49,50,49,50,34,-9};

  // Init System Setup patch (no patchname)
  protected static final byte [] INIT_SYSTEM_SETUP = {
-16,67,0,126,0,95,76,77,32,32,56,57,55,51,83,32,
0,1,0,0,1,11,12,13,14,0,1,1,0,0,0,1,
0,2,3,3,64,0,1,2,3,4,5,6,7,8,9,10,
11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,
27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,
43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,
59,60,61,62,63,122,-9};

  // INIT Performance patch ("INIT PERFORMANCCE")
  protected static final byte []INIT_PERFORMANCE = {
-16,67,0,126,0,61,76,77,32,32,56,57,55,51,80,69,
1,0,0,0,0,0,0,60,0,3,1,3,0,24,24,50,
99,0,0,0,1,0,0,99,99,99,99,50,50,50,50,73,
78,73,84,32,80,69,82,70,79,82,77,65,78,67,69,32,
32,32,32,83,-9};
}
