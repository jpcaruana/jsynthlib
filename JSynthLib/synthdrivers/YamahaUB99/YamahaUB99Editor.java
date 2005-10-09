/*
 * Copyright 2005 Ton Holsink
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
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

package synthdrivers.YamahaUB99;
import core.*;
import synthdrivers.YamahaUB99.format.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;
import javax.swing.event.*;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Vector;

class YamahaUB99Editor extends PatchEditorFrame {

//     private final String[] fxTypeString = {"Acoustic Multi ", "8 Band Parallel Delay", "8 Band Series Delay", "4 Band 2 Tap Mod Delay", "2 Band 4 Tap Mod Delay", "8 Multi Tap Mod Delay", "2 Band Long + 4 Short Mod Delay", "Short + Medium + Long Mod Delay", "Amp Simulator", "Reverb", "Early Ref.", "Gate Reverb", "Reverse Gate", "Mono Delay", "Stereo Delay", "Mod Delay", "Delay LCR", "Echo", "Chorus", "Flange", "Symphonic", "Phaser", "Auto Pan", "Tremolo", "HQ. Pitch", "Dual Pitch", "Rotary", "Ring Mod.", "Mod. Filter", "Digital Distortion", "Dyna. Filter", "Dyna. Flange", "Dyna. Phaser", "Reverb + Chorus", "Reverb -> Chorus", "Reverb + Flange", "Reverb -> Flange", "Reverb + Symphonic", "Reverb -> Symphonic", "Reverb -> Pan", "Delay + Early Ref.", "Delay -> Early Ref.", "Delay + Reverb", "Delay -> Reverb", "Distortion -> Delay", "Multi Filter", "M.Band Dyna.", "Distortion", "Vintage Flange", "Mono Vintage Phaser", "Stereo Vintage Phaser", "3 Band Parametric EQ", "Spring Reverb", "Tape Echo", "Compressor", "Amp Multi (Chorus)", "Amp Multi (Flange)", "Amp Multi (Tremolo)", "Amp Multi (Phaser)", "Distortion Multi (Chorus)", "Distortion Multi (Flange)", "Distortion Multi (Tremolo)", "Distortion Multi (Phaser)"};

    private final String[] eq1FreqString = {"50.0", "50.8", "51.7", "52.5", "53.4", "54.3", "55.2", "56.1", "57.0", "57.9", "58.9", "59.9", "60.9", "61.9", "62.9", "63.9", "65.0", "66.0", "67.1", "68.2", "69.4", "70.5", "71.7", "72.9", "74.1", "75.3", "76.5", "77.8", "79.1", "80.4", "81.7", "83.1", "84.4", "85.8", "87.2", "88.7", "90.1", "91.6", "93.2", "94.7", "96.3", "97.8", "99.5", "101", "103", "104", "106", "108", "110", "112", "113", "115", "117", "119", "121", "123", "125", "127", "129", "131", "134", "136", "138", "140", "143", "145", "147", "150", "152", "155", "157", "160", "163", "165", "168", "171", "174", "176", "179", "182", "185", "188", "191", "195", "198", "201", "204", "208", "211", "215", "218", "222", "226", "229", "233", "237", "241", "245", "249", "253", "257", "261", "266", "270", "274", "279", "284", "288", "293", "298", "303", "308", "313", "318", "323", "329", "334", "340", "345", "351", "357", "363", "369", "375", "381", "387", "394", "400"};

    private final String[] eq2FreqString = {"200", "203", "207", "210", "214", "217", "221", "224", "228", "232", "236", "239", "243", "247", "252", "256", "260", "264", "269", "273", "277", "282", "287", "291", "296", "301", "306", "311", "316", "322", "327", "332", "338", "343", "349", "355", "361", "367", "373", "379", "385", "391", "398", "404", "411", "418", "425", "432", "439", "446", "453", "461", "469", "476", "484", "492", "500", "509", "517", "526", "534", "543", "552", "561", "570", "580", "589", "599", "609", "619", "629", "640", "650", "661", "672", "683", "694", "706", "717", "729", "741", "753", "766", "778", "791", "804", "818", "831", "845", "859", "873", "887", "902", "917", "932", "947", "963", "979", "995", "1.01k", "1.03k", "1.05k", "1.06k", "1.08k", "1.10k", "1.12k", "1.13k", "1.15k", "1.17k", "1.19k", "1.21k", "1.23k", "1.25k", "1.27k", "1.29k", "1.31k", "1.34k", "1.36k", "1.38k", "1.40k", "1.43k", "1.45k", "1.47k", "1.50k", "1.52k", "1.55k", "1.57k", "1.60k"};

    private final String[] eq3FreqString = {"600", "610", "620", "630", "641", "651", "662", "673", "684", "695", "707", "718", "730", "742", "755", "767", "780", "793", "806", "819", "832", "846", "860", "874", "889", "903", "918", "934", "949", "965", "981", "997", "1.01k", "1.03k", "1.05k", "1.06k", "1.08k", "1.10k", "1.12k", "1.14k", "1.16k", "1.17k", "1.19k", "1.21k", "1.23k", "1.25k", "1.27k", "1.30k", "1.32k", "1.34k", "1.36k", "1.38k", "1.41k", "1.43k", "1.45k", "1.48k", "1.50k", "1.53k", "1.55k", "1.58k", "1.60k", "1.63k", "1.66k", "1.68k", "1.71k", "1.74k", "1.77k", "1.80k", "1.83k", "1.86k", "1.89k", "1.92k", "1.95k", "1.98k", "2.02k", "2.05k", "2.08k", "2.12k", "2.15k", "2.19k", "2.22k", "2.26k", "2.30k", "2.34k", "2.37k", "2.41k", "2.45k", "2.49k", "2.53k", "2.58k", "2.62k", "2.66k", "2.71k", "2.75k", "2.80k", "2.84k", "2.89k", "2.94k", "2.99k", "3.03k", "3.08k", "3.14k", "3.19k", "3.24k", "3.29k", "3.35k", "3.40k", "3.46k", "3.52k", "3.57k", "3.63k", "3.69k", "3.75k", "3.82k", "3.88k", "3.94k", "4.01k", "4.08k", "4.14k", "4.21k", "4.28k", "4.35k", "4.42k", "4.50k", "4.57k", "4.65k", "4.72k", "4.80k"};

    private final String[] eq4FreqString = {"4.80k", "2.03k", "2.07k", "2.10k", "2.14k", "2.17k", "2.21k", "2.24k", "2.28k", "2.32k", "2.36k", "2.39k", "2.43k", "2.47k", "2.52k", "2.56k", "2.60k", "2.64k", "2.69k", "2.73k", "2.77k", "2.82k", "2.87k", "2.91k", "2.96k", "3.01k", "3.06k", "3.11k", "3.16k", "3.22k", "3.27k", "3.32k", "3.38k", "3.43k", "3.49k", "3.55k", "3.61k", "3.67k", "3.73k", "3.79k", "3.85k", "3.91k", "3.98k", "4.04k", "4.11k", "4.18k", "4.25k", "4.32k", "4.39k", "4.46k", "4.53k", "4.61k", "4.69k", "4.76k", "4.84k", "4.92k", "5.00k", "5.09k", "5.17k", "5.26k", "5.34k", "5.43k", "5.52k", "5.61k", "5.70k", "5.80k", "5.89k", "5.99k", "6.09k", "6.19k", "6.29k", "6.40k", "6.50k", "6.61k", "6.72k", "6.83k", "6.94k", "7.06k", "7.17k", "7.29k", "7.41k", "7.53k", "7.66k", "7.78k", "7.91k", "8.04k", "8.18k", "8.31k", "8.45k", "8.59k", "8.73k", "8.87k", "9.02k", "9.17k", "9.32k", "9.47k", "9.63k", "9.79k", "9.95k", "10.1k", "10.3k", "10.5k", "10.6k", "10.8k", "11.0k", "11.2k", "11.3k", "11.5k", "11.7k", "11.9k", "12.1k", "12.3k", "12.5k", "12.7k", "12.9k", "13.1k", "13.4k", "13.6k", "13.8k", "14.0k", "14.3k", "14.5k", "14.7k", "15.0k", "15.2k", "15.5k", "15.7k", "16.0k"};

    private final String[] eq5FreqString = {"20.0", "20.5", "21.1", "21.7", "22.3", "22.9", "23.5", "24.2", "24.8", "25.5", "26.2", "26.9", "27.7", "28.4", "29.2", "30.0", "30.9", "31.7", "32.6", "33.5", "34.4", "35.3", "36.3", "37.3", "38.3", "39.4", "40.4", "41.6", "42.7", "43.9", "45.1", "46.3", "47.6", "48.9", "50.2", "51.6", "53.0", "54.5", "56.0", "57.5", "59.1", "60.7", "62.4", "64.1", "65.9", "67.7", "69.5", "71.4", "73.4", "75.4", "77.5", "79.6", "81.8", "84.1", "86.4", "88.7", "91.2", "93.7", "96.2", "98.9", "102", "104", "107", "110", "113", "116", "120", "123", "126", "130", "133", "137", "141", "144", "148", "153", "157", "161", "165", "170", "175", "179", "184", "189", "195", "200", "205", "211", "217", "223", "229", "235", "242", "248", "255", "262", "269", "277", "284", "292", "300", "309", "317", "326", "335", "344", "353", "363", "373", "383", "394", "404", "416", "427", "439", "451", "463", "476", "489", "502", "516", "530", "545", "560", "575", "591", "607", "624", "641", "659", "677", "695", "714", "734", "754", "775", "796", "818", "841", "864", "887", "912", "937", "962", "989", "1.02k", "1.04k", "1.07k", "1.10k", "1.13k", "1.16k", "1.20k", "1.23k", "1.26k", "1.30k", "1.33k", "1.37k", "1.41k", "1.44k", "1.48k", "1.53k", "1.57k", "1.61k", "1.65k", "1.70k", "1.75k", "1.79k", "1.84k", "1.89k", "1.95k", "2.00k", "2.05k", "2.11k", "2.17k", "2.23k", "2.29k", "2.35k", "2.42k", "2.48k", "2.55k", "2.62k", "2.69k", "2.77k", "2.84k", "2.92k", "3.00k", "3.09k", "3.17k", "3.26k", "3.35k", "3.44k", "3.53k", "3.63k", "3.73k", "3.83k", "3.94k", "4.04k", "4.16k", "4.27k", "4.39k", "4.51k", "4.63k", "4.76k", "4.89k", "5.02k", "5.16k", "5.30k", "5.45k", "5.60k", "5.75k", "5.91k", "6.07k", "6.24k", "6.41k", "6.59k", "6.77k", "6.95k", "7.14k", "7.34k", "7.54k", "7.75k", "7.96k", "8.18k", "8.41k", "8.64k", "8.87k", "9.12k", "9.37k", "9.62k", "9.89k", "10.2k", "10.4k", "10.7k", "11.0k", "11.3k", "11.6k", "12.0k", "12.3k", "12.6k", "13.0k", "13.3k", "13.7k", "14.1k", "14.4k", "14.8k", "15.3k", "15.7k", "16.1k", "16.5k", "17.0k", "17.5k", "17.9k", "18.4k", "18.9k", "19.5k", "20.0k"};

    private final String[] eq6FreqString = {"28.0", "30.0", "31.5", "33.5", "35.5", "37.5", "40.0", "42.5", "45.0", "47.5", "50.0", "53.0", "56.0", "60.0", "63.0", "67.0", "71.0", "75.0", "80.0", "85.0", "90.0", "95.0", "100", "106", "112", "118", "125", "132", "140", "150", "160", "170", "180", "190", "200", "212", "224", "236", "250", "265", "280", "300", "315", "335", "355", "375", "400", "425", "450", "475", "500", "530", "560", "600", "630", "670", "710", "750", "800", "850", "900", "950", "1.00k", "1.06k", "1.12k", "1.18k", "1.25k", "1.32k", "1.40k", "1.50k", "1.60k", "1.70k", "1.80k", "1.90k", "2.00k", "2.12k", "2.24k", "2.36k", "2.50k", "2.65k", "2.80k", "3.00k", "3.15k", "3.35k", "3.55k", "3.75k", "4.00k", "4.25k", "4.50k", "4.75k", "5.00k", "5.30k", "5.60k", "6.00k", "6.30k", "6.70k", "7.10k", "7.50k", "8.00k", "8.50k", "9.00k", "9.50k", "10.0k", "10.6k", "11.2k", "11.8k", "12.5k", "13.2k", "14.0k", "15.0k", "16.0k"};

    private final String[] qString = {"0.100", "0.104", "0.109", "0.113", "0.118", "0.123", "0.128", "0.134", "0.140", "0.146", "0.152", "0.158", "0.165", "0.172", "0.179", "0.187", "0.195", "0.203", "0.212", "0.221", "0.230", "0.240", "0.250", "0.261", "0.272", "0.284", "0.296", "0.308", "0.322", "0.335", "0.350", "0.364", "0.380", "0.396", "0.413", "0.431", "0.449", "0.468", "0.488", "0.509", "0.531", "0.553", "0.577", "0.601", "0.627", "0.654", "0.681", "0.710", "0.741", "0.772", "0.805", "0.840", "0.875", "0.913", "0.951", "0.992", "1.03", "1.08", "1.12", "1.17", "1.22", "1.27", "1.33", "1.39", "1.44", "1.51", "1.57", "1.64", "1.71", "1.78", "1.85", "1.93", "2.02", "2.10", "2.19", "2.28", "2.38", "2.48", "2.59", "2.70", "2.81", "2.93", "3.06", "3.19", "3.33", "3.47", "3.62", "3.77", "3.93", "4.10", "4.27", "4.45", "4.64", "4.84", "5.05", "5.26", "5.49", "5.72", "5.96", "6.22", "6.48", "6.76", "7.05", "7.35", "7.66", "7.99", "8.33", "8.68", "9.05", "9.44", "9.84", "10.3", "10.7", "11.2", "11.6", "12.1", "12.6", "13.2", "13.7", "14.3", "14.9", "15.6", "16.2", "16.9", "17.6", "18.4", "19.2", "20.0"};

    private final String[] preEQ1FreqString = {"50.0", "50.9", "51.8", "52.8", "53.8", "54.7", "55.7", "56.8", "57.8", "58.9", "59.9", "61.0", "62.2", "63.3", "64.4", "65.6", "66.8", "68.1", "69.3", "70.6", "71.9", "73.2", "74.5", "75.9", "77.3", "78.7", "80.1", "81.6", "83.1", "84.6", "86.1", "87.7", "89.3", "91.0", "92.6", "94.3", "96.0", "97.8", "99.6", "101", "103", "105", "107", "109", "111", "113", "115", "117", "119", "122", "124", "126", "128", "131", "133", "136", "138", "141", "143", "146", "148", "151", "154", "157", "160", "162", "165", "168", "172", "175", "178", "181", "184", "188", "191", "195", "198", "202", "206", "209", "213", "217", "221", "225", "229", "233", "238", "242", "247", "251", "256", "260", "265", "270", "275", "280", "285", "290", "296", "301", "306", "312", "318", "324", "330", "336", "342", "348", "354", "361", "367", "374", "381", "388", "395", "402", "410", "417", "425", "432", "440", "448", "457", "465", "474", "482", "491", "500"};

    private final String[] preEQ2FreqString = {"200", "204", "207", "211", "215", "219", "223", "227", "231", "235", "240", "244", "249", "253", "258", "263", "267", "272", "277", "282", "287", "293", "298", "303", "309", "315", "320", "326", "332", "338", "345", "351", "357", "364", "370", "377", "384", "391", "398", "406", "413", "421", "428", "436", "444", "452", "461", "469", "478", "486", "495", "504", "513", "523", "532", "542", "552", "562", "572", "583", "594", "604", "615", "627", "638", "650", "662", "674", "686", "699", "712", "725", "738", "751", "765", "779", "793", "808", "823", "838", "853", "869", "885", "901", "917", "934", "951", "968", "986", "1.00k", "1.02k", "1.04k", "1.06k", "1.08k", "1.10k", "1.12k", "1.14k", "1.16k", "1.18k", "1.20k", "1.23k", "1.25k", "1.27k", "1.29k", "1.32k", "1.34k", "1.37k", "1.39k", "1.42k", "1.44k", "1.47k", "1.50k", "1.52k", "1.55k", "1.58k", "1.61k", "1.64k", "1.67k", "1.70k", "1.73k", "1.76k", "1.79k", "1.83k", "1.86k", "1.89k", "1.93k", "1.96k", "2.00k"};

    private final String[] preEQ3FreqString = {"1.00k", "1.02k", "1.04k", "1.06k", "1.08k", "1.09k", "1.11k", "1.14k", "1.16k", "1.18k", "1.20k", "1.22k", "1.24k", "1.27k", "1.29k", "1.31k", "1.34k", "1.36k", "1.39k", "1.41k", "1.44k", "1.46k", "1.49k", "1.52k", "1.55k", "1.57k", "1.60k", "1.63k", "1.66k", "1.69k", "1.72k", "1.75k", "1.79k", "1.82k", "1.85k", "1.89k", "1.92k", "1.96k", "1.99k", "2.03k", "2.07k", "2.10k", "2.14k", "2.18k", "2.22k", "2.26k", "2.30k", "2.34k", "2.39k", "2.43k", "2.48k", "2.52k", "2.57k", "2.61k", "2.66k", "2.71k", "2.76k", "2.81k", "2.86k", "2.91k", "2.97k", "3.02k", "3.08k", "3.13k", "3.19k", "3.25k", "3.31k", "3.37k", "3.43k", "3.49k", "3.56k", "3.62k", "3.69k", "3.76k", "3.83k", "3.90k", "3.97k", "4.04k", "4.11k", "4.19k", "4.27k", "4.34k", "4.42k", "4.50k", "4.59k", "4.67k", "4.76k", "4.84k", "4.93k", "5.02k", "5.11k", "5.21k", "5.30k", "5.40k", "5.50k", "5.60k", "5.70k", "5.80k", "5.91k", "6.02k", "6.13k", "6.24k", "6.36k", "6.47k", "6.59k", "6.71k", "6.83k", "6.96k", "7.09k", "7.22k", "7.35k", "7.48k", "7.62k", "7.76k", "7.90k", "8.04k", "8.19k", "8.34k", "8.49k", "8.65k", "8.81k", "8.97k", "9.13k", "9.30k", "9.47k", "9.64k", "9.82k", "10.0k"};

    private final String[] ngHoldString = {"0.02", "0.07", "0.11", "0.16", "0.20", "0.25", "0.29", "0.34", "0.39", "0.43", "0.48", "0.52", "0.57", "0.61", "0.66", "0.70", "0.75", "0.84", "0.93", "1.02", "1.11", "1.20", "1.29", "1.38", "1.47", "1.66", "1.84", "2.02", "2.20", "2.38", "2.56", "2.74", "2.93", "3.29", "3.65", "4.01", "4.38", "4.74", "5.10", "5.46", "5.83", "6.55", "7.28", "8.00", "8.73", "9.46", "10.1", "10.9", "11.6", "13.0", "14.5", "15.9", "17.4", "18.8", "20.3", "21.7", "23.2", "26.1", "29.0", "31.9", "34.8", "37.7", "40.6", "43.5", "46.4", "52.2", "58.0", "63.8", "69.6", "75.4", "81.2", "87.1", "92.9", "104", "116", "127", "139", "150", "162", "174", "185", "209", "232", "255", "278", "301", "325", "348", "371", "417", "464", "510", "557", "603", "650", "696", "743", "835", "928", "1020", "1110", "1200", "1300", "1390", "1480", "1670", "1850", "2040"};

    private final String[] ngDecayString = {"6", "17", "29", "41", "52", "64", "75", "87", "99", "110", "122", "133", "145", "157", "168", "180", "192", "215", "238", "261", "284", "308", "331", "354", "377", "424", "470", "517", "563", "609", "656", "702", "749", "842", "934", "1020", "1120", "1210", "1300", "1390", "1490", "1670", "1860", "2040", "2230", "2420", "2600", "2790", "2970", "3340", "3720", "4090", "4460", "4830", "5200", "5570", "5940", "6690", "7430", "8170", "8920", "9660", "10400", "11100", "11800", "13300", "14800", "16300", "17800", "19300", "20800", "22200", "23700", "26700", "29700", "32600", "35600", "38600", "41600", "44500"};

    private final String[] spSimString = {"Off", "American 412", "British 412", "Modern 412", "YAMAHA 412", "Hybrid 412", "American 212", "British 212", "Modern 212", "YAMAHA 212", "Hybrid 212", "American 112", "Modern 112", "YAMAHA 112", "Hybrid 112", "410", "210"};

    private final String[] waveString = {"Sine", "Triangle"};

    private final String[] wave2String = {"Sine", "Triangle", "Square"};

    private final String[] wave3String = {"Triangle", "Saw Up", "Saw Down"};

    private final String[] hipassString = {"Thru", "21.2", "22.4", "23.6", "25.0", "26.5", "28.0", "30.0", "31.5", "33.5", "35.5", "37.5", "40.0", "42.5", "45.0", "47.5", "50.0", "53.0", "56.0", "60.0", "63.0", "67.0", "71.0", "75.0", "80.0", "85.0", "90.0", "95.0", "100", "106", "112", "118", "125", "132", "140", "150", "160", "170", "180", "190", "200", "212", "224", "236", "250", "265", "280", "300", "315", "335", "355", "375", "400", "425", "450", "475", "500", "530", "560", "600", "630", "670", "710", "750", "800", "850", "900", "950", "1.00k", "1.06k", "1.12k", "1.18k", "1.25k", "1.32k", "1.40k", "1.50k", "1.60k", "1.70k", "1.80k", "1.90k", "2.00k", "2.12k", "2.24k", "2.36k", "2.50k", "2.65k", "2.80k", "3.00k", "3.15k", "3.35k", "3.55k", "3.75k", "4.00k", "4.25k", "4.50k", "4.75k", "5.00k", "5.30k", "5.60k", "6.00k", "6.30k", "6.70k", "7.10k", "7.50k", "8.00k"};

    private String[] lshFreqString = new String[104];

    private String[] eqFreqString = new String[77];

    private final String[] eqQString = {"10.0", "9.0", "8.0", "7.0", "6.3", "5.6", "5.0", "4.5", "4.0", "3.5", "3.2", "2.8", "2.5", "2.2", "2.0", "1.8", "1.6", "1.4", "1.2", "1.1", "1.0", "0.90", "0.80", "0.70", "0.63", "0.56", "0.50", "0.45", "0.40", "0.35", "0.32", "0.28", "0.25", "0.22", "0.20", "0.18", "0.16", "0.14", "0.12", "0.11", "0.10"};

    private final String[] lopassString = {"50.0", "53.0", "56.0", "60.0", "63.0", "67.0", "71.0", "75.0", "80.0", "85.0", "90.0", "95.0", "100", "106", "112", "118", "125", "132", "140", "150", "160", "170", "180", "190", "200", "212", "224", "236", "250", "265", "280", "300", "315", "335", "355", "375", "400", "425", "450", "475", "500", "530", "560", "600", "630", "670", "710", "750", "800", "850", "900", "950", "1.00k", "1.06k", "1.12k", "1.18k", "1.25k", "1.32k", "1.40k", "1.50k", "1.60k", "1.70k", "1.80k", "1.90k", "2.00k", "2.12k", "2.24k", "2.36k", "2.50k", "2.65k", "2.80k", "3.00k", "3.15k", "3.35k", "3.55k", "3.75k", "4.00k", "4.25k", "4.50k", "4.75k", "5.00k", "5.30k", "5.60k", "6.00k", "6.30k", "6.70k", "7.10k", "7.50k", "8.00k", "8.50k", "9.00k", "9.50k", "10.0k", "10.6k", "11.2k", "11.8k", "12.5k", "13.2k", "14.0k", "15.0k", "16.0k", "Thru"};

    private String[] hshFreqString = new String[101];

    private final String[] phaseString = {"0.00", "5.63", "11.25", "16.88", "22.50", "28.13", "33.75", "39.38", "45.00", "50.63", "56.25", "61.88", "67.50", "73.13", "78.75", "84.38", "90.00", "95.63", "101.25", "106.88", "112.50", "118.13", "123.75", "129.38", "135.00", "140.63", "146.25", "151.88", "157.50", "163.13", "168.75", "174.38", "180.00", "185.63", "191.25", "196.88", "202.50", "208.13", "213.75", "219.38", "225.00", "230.63", "236.25", "241.88", "247.50", "253.13", "258.75", "264.38", "270.00", "275.63", "281.25", "286.88", "292.50", "298.13", "303.75", "309.38", "315.00", "320.63", "326.25", "331.88", "337.50", "343.13", "348.75", "354.38"};

    private final String[] directionString = {"L<--->R", "L--->R", "L<---R", "Turn L", "Turn R"};

    private final String[] filtdirString = {"Up", "Down"};

    private final String[] filterString = {"Low Pass", "High Pass", "Band Pass"};

    private final String[] cmpRatioString = {"1:1", "1.1:1", "1.3:1", "1.5:1", "1.7:1", "2:1", "2.5:1", "3:1", "3.5:1", "4:1", "5:1", "6:1", "8:1", "10:1", "20:1", "inf:1"};

    private String[] cmpRatio2String = new String[15];

    private String[] cmpReleaseString = new String[128];

    private final String[] cmpKneeString = {"Hard", "1", "2", "3", "4", "5"};

    private final String[] decayString = {"6", "12", "17", "23", "29", "35", "41", "46", "52", "58", "64", "70", "75", "81", "87", "93", "99", "104", "110", "116", "122", "128", "133", "139", "145", "151", "157", "163", "168", "174", "180", "186", "192", "203", "215", "226", "238", "250", "261", "273", "284", "296", "308", "319", "331", "342", "354", "366", "377", "400", "424", "447", "470", "493", "517", "540", "563", "586", "609", "633", "656", "679", "702", "725", "749", "795", "842", "888", "934", "981", "1020", "1070", "1120", "1160", "1210", "1250", "1300", "1350", "1390", "1440", "1490", "1580", "1670", "1770", "1860", "1950", "2040", "2140", "2230", "2320", "2420", "2510", "2600", "2690", "2790", "2880", "2970", "3160", "3340", "3530", "3720", "3900", "4090", "4270", "4460", "4640", "4830", "5020", "5200", "5390", "5570", "5760", "5940", "6320", "6690", "7060", "7430", "7800", "8170", "8540", "8920", "9290", "9660", "10000", "10400", "10700", "11100", "11500", "11800", "12600", "13300", "14100", "14800", "15600", "16300", "17000", "17800", "18500", "19300", "20000", "20800", "21500", "22200", "23000", "23700", "25200", "26700", "28200", "29700", "31200", "32600", "34100", "35600", "37100", "38600", "40100", "41600", "43000", "44500", "46000"};

    private final String[] offOnString = {"Off", "On"};

    private final String[] panString = {"L10.0", "L9.8", "L9.6", "L9.4", "L9.2", "L9.0", "L8.8", "L8.6", "L8.4", "L8.2", "L8.0", "L7.8", "L7.6", "L7.4", "L7.2", "L7.0", "L6.8", "L6.6", "L6.4", "L6.2", "L6.0", "L5.8", "L5.6", "L5.4", "L5.2", "L5.0", "L4.8", "L4.6", "L4.4", "L4.2", "L4.0", "L3.8", "L3.6", "L3.4", "L3.2", "L3.0", "L2.8", "L2.6", "L2.4", "L2.2", "L2.0", "L1.8", "L1.6", "L1.4", "L1.2", "L1.0", "L0.8", "L0.6", "L0.4", "L0.2", "Center", "R0.2", "R0.4", "R0.6", "R0.8", "R1.0", "R1.2", "R1.4", "R1.6", "R1.8", "R2.0", "R2.2", "R2.4", "R2.6", "R2.8", "R3.0", "R3.2", "R3.4", "R3.6", "R3.8", "R4.0", "R4.2", "R4.4", "R4.6", "R4.8", "R5.0", "R5.2", "R5.4", "R5.6", "R5.8", "R6.0", "R6.2", "R6.4", "R6.6", "R6.8", "R7.0", "R7.2", "R7.4", "R7.6", "R7.8", "R8.0", "R8.2", "R8.4", "R8.6", "R8.8", "R9.0", "R9.2", "R9.4", "R9.6", "R9.8", "R10.0"};

    private final String[] pan1String = {"L63", "L62", "L61", "L60", "L59", "L58", "L57", "L56", "L55", "L54", "L53", "L52", "L51", "L50", "L49", "L48", "L47", "L46", "L45", "L44", "L43", "L42", "L41", "L40", "L39", "L38", "L37", "L36", "L35", "L34", "L33", "L32", "L31", "L30", "L29", "L28", "L27", "L26", "L25", "L24", "L23", "L22", "L21", "L20", "L19", "L18", "L17", "L16", "L15", "L14", "L13", "L12", "L11", "L10", "L9", "L8", "L7", "L6", "L5", "L4", "L3", "L2", "L1", "Center", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16", "R17", "R18", "R19", "R20", "R21", "R22", "R23", "R24", "R25", "R26", "R27", "R28", "R29", "R30", "R31", "R32", "R33", "R34", "R35", "R36", "R37", "R38", "R39", "R40", "R41", "R42", "R43", "R44", "R45", "R46", "R47", "R48", "R49", "R50", "R51", "R52", "R53", "R54", "R55", "R56", "R57", "R58", "R59", "R60", "R61", "R62", "R63"};

    private final String[] reverbTimeString = {"0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5", "9.0", "9.5", "10.0", "11.0", "12.0", "13.0", "14.0", "15.0", "16.0", "17.0", "18.0", "19.0", "20.0", "25.0", "30.0", "35.0", "40.0", "45.0", "50.0", "55.0", "60.0", "65.0", "70.0", "75.0", "80.0", "85.0", "90.0", "95.0", "99.0"};

    private final String[] erTypeString = {"Small Hall", "Large Hall", "Random", "Reverse", "Plate", "Spring"};

    private final String[] gateTypeString = {"Type-A", "Type-B"};

    private final String[] distString = {"Distortion1", "Distortion2", "Overdrive1", "Overdrive2", "Crunch"};

    private final String[] dist2String = {"Heavy1", "Heavy2", "Lead1", "Lead2", "Drive1", "Drive2", "Crunch1", "Crunch2", "Clean1", "Clean2", "Solid"};

    private final String[] dist3String = {"Lead1", "Lead2", "Drive1", "Drive2", "Crunch1", "Crunch2", "Fuzz1", "Fuzz2", "Distortion1", "Distortion2", "Overdrive1", "Overdrive2", "Tube", "Solid State", "Bypass"};

    private String[] fbGainString;

    private String[] cutFilterString;

    private Patch patch;
    private JTabbedPane oTabs;
    private JPanel knobPnl;

    private IdComboWidget cmbFXType;

    private Vector RegisterVector = new Vector(128);
    private Vector KnobVector = new Vector(128);

    public YamahaUB99Editor(Patch iPatch) {
        super ("Yamaha UB99 Editor", iPatch);

        patch = (Patch) iPatch;

        System.arraycopy(hipassString, 1, lshFreqString, 0, 104);
        System.arraycopy(lopassString, 0, hshFreqString, 0, 101);
        System.arraycopy(hipassString, 28, eqFreqString, 0, 77);
        System.arraycopy(decayString, 0, cmpReleaseString, 0, 128);
        System.arraycopy(cmpRatioString, 0, cmpRatio2String, 0, 15);
        fbGainString = YamahaUB99Util.genString(-100, 100, 2);
        fbGainString[0] = "-99";
        fbGainString[fbGainString.length-1] = "99";

        cutFilterString = YamahaUB99Util.genString(0.0, 10.0, 0.1, "0.0");
        cutFilterString[0] = "Off";

        Box box = Box.createVerticalBox();
        box.add(buildTopPanel());

        knobPnl = buildKnobPanel();
        box.add(knobPnl);

        oTabs = new JTabbedPane();
        oTabs.setPreferredSize(new Dimension(520, 360));
        box.add(oTabs);

        initTabbedPane(cmbFXType.getValue());

        scrollPane.add(box);

        pack();

    }

    /**
     * This is addWidget from PatchEditorFrame, without the slider bank functionality.
     * All the widgets in the filter-, pitch-, chorus,- delay,- and reverb panel are created dynamically depending on the effect type.
     * This means that the widgetList doesn't contain all available widgets or becomes very crowded with double registered widgets.
     * A lot of widgets share the same offset but have different values to choose from and different min and max values.
     * I could create all available widgets at once, register them in the widget list and change the visibility, comboboxlist, min and max values
     * depending on the effect type chosen. Maybe I will do that some time.
     * Creating widgets dynamically is much more lightweight, but I have to sacrifice the slider bank functionality.
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int anchor, int fill,
                             int slidernum) {
        try {
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = gridwidth;
            gbc.gridheight = gridheight;
            gbc.anchor = anchor;
            gbc.fill = fill;
            parent.add(widget, gbc);
        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
        }
    }

    protected void addRegisterWidget(JComponent parent, SysexWidget widget,
                             int gridx, int gridy, int gridwidth, int gridheight,
                             int anchor, int fill,
                             int slidernum) {
        this.addWidget(parent, widget, gridx, gridy, gridwidth, gridheight, anchor, fill, slidernum);
        RegisterVector.add(widget);
    }

    private Container buildTopPanel() {
        // Top panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel pnl = new JPanel();
        pnl.setLayout(gridbag);
        gbc.gridx = 0;
        gbc.gridy = 0;

        PatchNameWidget pnw = new PatchNameWidget("Patch Name ", patch);
        pnl.add(pnw, gbc);
        pnw.setEnabled(false);

        Vector v = new Vector(63);
        v.add(new IdItem(4, "2 Band 4 Tap Mod Delay"));
        v.add(new IdItem(6, "2 Band Long + 4 Short Mod Delay"));
        v.add(new IdItem(51, "3 Band Parametric EQ"));
        v.add(new IdItem(3, "4 Band 2 Tap Mod Delay"));
        v.add(new IdItem(1, "8 Band Parallel Delay"));
        v.add(new IdItem(2, "8 Band Series Delay"));
        v.add(new IdItem(5, "8 Multi Tap Mod Delay"));
        v.add(new IdItem(0, "Acoustic Multi "));
        v.add(new IdItem(55, "Amp Multi (Chorus)"));
        v.add(new IdItem(56, "Amp Multi (Flange)"));
        v.add(new IdItem(58, "Amp Multi (Phaser)"));
        v.add(new IdItem(57, "Amp Multi (Tremolo)"));
        v.add(new IdItem(8, "Amp Simulator"));
        v.add(new IdItem(22, "Auto Pan"));
        v.add(new IdItem(18, "Chorus"));
        v.add(new IdItem(54, "Compressor"));
        v.add(new IdItem(41, "Delay -> Early Ref."));
        v.add(new IdItem(43, "Delay -> Reverb"));
        v.add(new IdItem(40, "Delay + Early Ref."));
        v.add(new IdItem(42, "Delay + Reverb"));
        v.add(new IdItem(16, "Delay LCR"));
        v.add(new IdItem(29, "Digital Distortion"));
        v.add(new IdItem(47, "Distortion"));
        v.add(new IdItem(44, "Distortion -> Delay"));
        v.add(new IdItem(59, "Distortion Multi (Chorus)"));
        v.add(new IdItem(60, "Distortion Multi (Flange)"));
        v.add(new IdItem(62, "Distortion Multi (Phaser)"));
        v.add(new IdItem(61, "Distortion Multi (Tremolo)"));
        v.add(new IdItem(25, "Dual Pitch"));
        v.add(new IdItem(30, "Dyna. Filter"));
        v.add(new IdItem(31, "Dyna. Flange"));
        v.add(new IdItem(32, "Dyna. Phaser"));
        v.add(new IdItem(10, "Early Ref."));
        v.add(new IdItem(17, "Echo"));
        v.add(new IdItem(19, "Flange"));
        v.add(new IdItem(11, "Gate Reverb"));
        v.add(new IdItem(24, "HQ. Pitch"));
        v.add(new IdItem(46, "M.Band Dyna."));
        v.add(new IdItem(15, "Mod Delay"));
        v.add(new IdItem(28, "Mod. Filter"));
        v.add(new IdItem(13, "Mono Delay"));
        v.add(new IdItem(49, "Mono Vintage Phaser"));
        v.add(new IdItem(45, "Multi Filter"));
        v.add(new IdItem(21, "Phaser"));
        v.add(new IdItem(9, "Reverb"));
        v.add(new IdItem(34, "Reverb -> Chorus"));
        v.add(new IdItem(36, "Reverb -> Flange"));
        v.add(new IdItem(39, "Reverb -> Pan"));
        v.add(new IdItem(38, "Reverb -> Symphonic"));
        v.add(new IdItem(33, "Reverb + Chorus"));
        v.add(new IdItem(35, "Reverb + Flange"));
        v.add(new IdItem(37, "Reverb + Symphonic"));
        v.add(new IdItem(12, "Reverse Gate"));
        v.add(new IdItem(27, "Ring Mod."));
        v.add(new IdItem(26, "Rotary"));
        v.add(new IdItem(7, "Short + Medium + Long Mod Delay"));
        v.add(new IdItem(52, "Spring Reverb"));
        v.add(new IdItem(14, "Stereo Delay"));
        v.add(new IdItem(50, "Stereo Vintage Phaser"));
        v.add(new IdItem(20, "Symphonic"));
        v.add(new IdItem(53, "Tape Echo"));
        v.add(new IdItem(23, "Tremolo"));
        v.add(new IdItem(48, "Vintage Flange"));

        cmbFXType = new IdComboWidget("FX Type", patch,
            new YamahaUB99Model(patch, 0x200000),
            new YamahaUB99Sender(0x200000),
            v);
        addWidget(pnl, cmbFXType, 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);

        cmbFXType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    int idx = ((IdItem)((JComboBox) e.getSource()).getSelectedItem()).getID();
                    initTabbedPane(idx);
                    setDefaultValues(idx);

                }
            }});

        return pnl;
    }

    private JPanel buildKnobPanel() {
        // Knob panel
        GridBagLayout gridbag = new GridBagLayout();

        JPanel pnl = new JPanel();
        pnl.setLayout(gridbag);

        return pnl;
    }

    private void initKnobPane() {
        knobPnl.setVisible(false);
        knobPnl.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRegisterWidget(knobPnl, new IdComboWidget("Knob 1", patch, new YamahaUB99Model(patch, 0x200002), new YamahaUB99Sender(0x200002), KnobVector), 0, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        addRegisterWidget(knobPnl, new IdComboWidget("Knob 2", patch, new YamahaUB99Model(patch, 0x200004), new YamahaUB99Sender(0x200004), KnobVector), 1, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        addRegisterWidget(knobPnl, new IdComboWidget("Knob 3", patch, new YamahaUB99Model(patch, 0x200006), new YamahaUB99Sender(0x200006), KnobVector), 2, 0, 1, 1, gbc.anchor, gbc.fill, 0);


        knobPnl.setVisible(true);
    }

    private void initTabbedPane(int index) {
        RegisterVector.clear();
        KnobVector.clear();
        oTabs.setVisible(false);
        oTabs.removeAll();
        switch (index) {
            //Acoustic Multi
            case 0: buildAcousticMulti(); break;
            //8 Band Parallel Delay
            case 1: build8BandParDelay(); break;
            //8 Band Series Delay
            case 2: build8BandSerDelay(); break;
            //4 Band 2 Tap Mod Delay
            case 3: build4Band2TapModDelay(); break;
            //2 Band 4 Tap Mod Delay
            case 4: build2Band4TapModDelay(); break;
            //8 Multi Tap Mod Delay
            case 5: build8MultiTapModDelay(); break;
            //2 Band Long + 4 Short Mod Delay
            case 6: build2BandLong4ShortModDelay(); break;
            //Short + Medium + Long Mod Delay
            case 7: buildShortMediumLongModDelay(); break;
            //Amp Simulator
            case 8: buildAmpSim(); break;
            //Reverb
            case 9: buildReverb(); break;
            //Early Ref.
            case 10: buildEarlyRef(); break;
            //Gate Reverb
            case 11: buildGateReverb(); break;
            //Reverse Gate
            case 12: buildReverseGate(); break;
            //Mono Delay
            case 13: buildMonoDelay(); break;
            //Stereo Delay
            case 14: buildStereoDelay(); break;
            //Mod Delay
            case 15: buildModDelay(); break;
            //Delay LCR
            case 16: buildDelayLCR(); break;
            //Echo
            case 17: buildEcho(); break;
            //Chorus
            case 18: buildChorus(); break;
            //Flange
            case 19: buildFlange(); break;
            //Symphonic
            case 20: buildSymphonic(); break;
            //Phaser
            case 21: buildPhaser(); break;
            //Auto Pan
            case 22: buildAutoPan(); break;
            //Tremolo
            case 23: buildTremolo(); break;
            //HQ. Pitch
            case 24: buildHQPitch(); break;
            //Dual Pitch
            case 25: buildDualPitch(); break;
            //Rotary
            case 26: buildRotary(); break;
            ///Ring Mod.
            case 27: buildRingMod(); break;
            //Mod. Filter
            case 28: buildModFilter(); break;
            //Digital Distortion
            case 29: buildDigDist(); break;
            //Dyna. Filter
            case 30: buildDynaFilter(); break;
            //Dyna. Flange
            case 31: buildDynaFlange(); break;
            //Dyna. Phaser
            case 32: buildDynaPhaser(); break;
            //Reverb + Chorus
            case 33: buildReverbChorusPar(); break;
            //Reverb -> Chorus
            case 34: buildReverbChorusSer(); break;
            //Reverb + Flange
            case 35: buildReverbFlangePar(); break;
            //Reverb -> Flange
            case 36: buildReverbFlangeSer(); break;
            //Reverb + Symphonic
            case 37: buildReverbSymphonicPar(); break;
            //Reverb -> Symphonic
            case 38: buildReverbSymphonicSer(); break;
            //Reverb -> Pan
            case 39: buildReverbPanSer(); break;
            //Delay + Early Ref.
            case 40: buildDelayEarlyRefPar(); break;
            //Delay -> Early Ref.
            case 41: buildDelayEarlyRefSer(); break;
            //Delay + Reverb
            case 42: buildDelayReverbPar(); break;
            //Delay -> Reverb
            case 43: buildDelayReverbSer(); break;
            //Distortion -> Delay
            case 44: buildDistortionDelaySer(); break;
            //Multi Filter
            case 45: buildMultiFilter(); break;
            //M.Band Dyna.
            case 46: buildMBandDyna(); break;
            //Distortion
            case 47: buildDist(); break;
            //Vintage Flange
            case 48: buildVinFlange(); break;
            //Mono Vintage Phaser
            case 49: buildMonoVinPhaser(); break;
            //Stereo Vintage Phaser
            case 50: buildStereoVinPhaser(); break;
            //3 Band Parametric EQ
            case 51: build3BandParEQ(); break;
            //Spring Reverb
            case 52: buildSpringReverb(); break;
            //Tape Echo
            case 53: buildTapeEcho(); break;
            //Compressor
            case 54: buildCompressor(); break;
            //Amp Multi (Chorus)
            case 55: buildAmpMultiChorus(); break;
            //Amp Multi (Flange)
            case 56: buildAmpMultiFlange(); break;
            //Amp Multi (Tremolo)
            case 57: buildAmpMultiTremolo(); break;
            //Amp Multi (Phaser)
            case 58: buildAmpMultiPhaser(); break;
            //Distortion Multi (Chorus)
            case 59: buildDistMultiChorus(); break;
            //Distortion Multi (Flange)
            case 60: buildDistMultiFlange(); break;
            //Distortion Multi (Tremolo)
            case 61: buildDistMultiTremolo(); break;
            //Distortion Multi (Phaser)
            case 62: buildDistMultiPhaser(); break;

            default: ErrorMsg.reportStatus("Switch value " + index + " not handled! ");
        }
       oTabs.setVisible(true);
       KnobVector.add(new IdItem(107, "No Assign"));
       initKnobPane();
    }

    private void setDefaultValues(int index) {
        Patch p;
        p = ((YamahaUB99Driver)patch.getDriver()).getDefaultValues(patch, index);
        if (p != null) {
            patch = p;
            for (int i = 0; i < RegisterVector.size(); i++)
                ((SysexWidget)RegisterVector.elementAt(i)).setValue();
        }
    }

    private Container buildPanel(String label, Object[] widgets, int[] rowcount) {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            label,TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);
        int tot = 0;
        for (int i = 0; i < rowcount.length; i++)
            for (int j = 0; j < rowcount[i]; j++) {
                addRegisterWidget(pnl, (SysexWidget)widgets[tot++], j, i, 1, 1, gbc.anchor, gbc.fill, 0);
            }

        return pnl;
    }

    private Container buildPanel(String label, Object[] widgets) {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            label,TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        for (int i = 0; i < widgets.length; i++) {
            addRegisterWidget(pnl, (SysexWidget)widgets[i], i, 0, 1, 1, gbc.anchor, gbc.fill, 0);
        }

        return pnl;
    }

    private void addKnobItem(int adr, String label) {
        if (adr >= 0x200100) {
            int id = adr - 0x200100;
            if (id < 0x16)
                id = id / 2;
            else
                id = 11 + (id - 0x16);
            KnobVector.add(new IdItem(id, label));
        }
    }

    private String strip(String s) {
        return s.substring(s.indexOf(": ") + 1);
    }

    private Knob newKnob(String label, int adr, int min, int max, int base) {
        addKnobItem(adr, label);
        return new Knob(strip(label), patch, min, max, base, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(String label, int adr, int min, int max) {
        addKnobItem(adr, label);
        return new Knob(strip(label), patch, min, max, 0, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(int adr, int min, int max, String label) {
        addKnobItem(adr, label);
        return new Knob("", patch, min, max, 0, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(String label, int adr, int min, int max, IFormat fmt) {
        addKnobItem(adr, label);
        return new Knob(strip(label), patch, min, max, fmt, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(int adr, int min, int max, IFormat fmt, String label) {
        addKnobItem(adr, label);
        return new Knob("", patch, min, max, fmt, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(String label, int adr, String[] s) {
        addKnobItem(adr, label);
        return new Knob(strip(label), patch, 0, s.length-1, new ListFormat(s), new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private Knob newKnob(int adr, String[] s, String label) {
        addKnobItem(adr, label);
        return new Knob("", patch, 0, s.length-1, new ListFormat(s), new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    private ComboBoxWidget newCombo(String label, int adr, Object[] options) {
        addKnobItem(adr, label);
        return new ComboBoxWidget(strip(label), patch, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr), options);
    }

    private ComboBoxWidget newCombo(int adr, Object[] options, String label) {
        addKnobItem(adr, label);
        return new ComboBoxWidget("", patch, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr), options);
    }

    private CheckBoxWidget newCheck(String label, int adr) {
        addKnobItem(adr, label);
        return new CheckBoxWidget(strip(label), patch, new YamahaUB99Model(patch, adr), new YamahaUB99Sender(adr));
    }

    //Distortion
    private void buildDist() {
        oTabs.add(buildDistortionPanel(), "Distortion");
        oTabs.add(buildEQPanel(), "EQ");
        oTabs.add(buildPreEQPanel(), "Pre EQ");
        oTabs.add(buildNGPanel(0x200135, 0x200136, 0x200137, 0x200138), "Noise Gate");
    }

    private Container buildDistortionPanel() {
        SysexWidget[] widgets = {
            newCombo("dst: Type", 0x200116,
                        new String [] {"Lead1", "Lead2", "Drive1", "Drive2", "Crunch1", "Crunch2", "Fuzz1", "Fuzz2", "Distortion1", "Distortion2", "Overdrive1", "Overdrive2", "Tube", "Solid State"}),
            newKnob("dst: Gain", 0x200110, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Master", 0x200112, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Tone", 0x200114, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Distortion", widgets);
    }

    private Container buildEQPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "EQ",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        gbc.gridx = 1;
        gbc.gridy = 0;

        JLabel lbl = new JLabel("   EQ1");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   EQ2");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   EQ3");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   EQ4");
        pnl.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnl.add(new JLabel("Freq"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Gain"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Q"), gbc);

        //EQ1 Freq
        addRegisterWidget(pnl, newKnob(0x200100, eq1FreqString, "eq: Freq1"),
            1, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        //EQ2 Freq
        addRegisterWidget(pnl, newKnob(0x200120, eq2FreqString, "eq: Freq2"),
            2, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        //EQ3 Freq
        addRegisterWidget(pnl, newKnob(0x200123, eq3FreqString, "eq: Freq3"),
            3, 1, 1, 1, gbc.anchor, gbc.fill, 0);
        //EQ4 Freq
        addRegisterWidget(pnl, newKnob(0x200126, eq2FreqString, "eq: Freq4"),
            4, 1, 1, 1, gbc.anchor, gbc.fill, 0);

        //EQ Gain
        for (int i = 0; i < 4; i++)
            addRegisterWidget(pnl, newKnob(0x20011E + i*3, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0"), "eq: Gain" + (i+1)),
                1 + i, 2, 1, 1, gbc.anchor, gbc.fill, 0);

        //EQ Q
        for (int i = 0; i < 4; i++)
            addRegisterWidget(pnl, newKnob(0x20011F + i*3, qString, "eq: Q" + (i+1)),
                1 + i, 3, 1, 1, gbc.anchor, gbc.fill, 0);

        return pnl;
    }

    private Container buildPreEQPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Pre EQ",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        addRegisterWidget(pnl, newKnob("peq: Level", 0x200102, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            0, 0, 1, 1, gbc.anchor, gbc.fill, 0);

        gbc.gridx = 1;
        gbc.gridy = 1;

        JLabel lbl = new JLabel("   Pre EQ1");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   Pre EQ2");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   Pre EQ3");
        pnl.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnl.add(new JLabel("Freq"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Gain"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Q"), gbc);

        //Pre EQ1 Freq
        addRegisterWidget(pnl, newKnob(0x20012B, preEQ1FreqString, "peq: Freq1"),
            1, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        //Pre EQ2 Freq
        addRegisterWidget(pnl, newKnob(0x20012E, preEQ2FreqString, "peq: Freq2"),
            2, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        //Pre EQ3 Freq
        addRegisterWidget(pnl, newKnob(0x200131, preEQ3FreqString, "peq: Freq3"),
            3, 2, 1, 1, gbc.anchor, gbc.fill, 0);

        //Pre EQ Gain
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x20012C + i*3, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0"), "peq: Gain" + (i+1)),
                1 + i, 3, 1, 1, gbc.anchor, gbc.fill, 0);

        //Pre EQ Q
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x20012D + i*3, qString, "peq: Q" + (i+1)),
                1 + i, 4, 1, 1, gbc.anchor, gbc.fill, 0);

        return pnl;
    }

    private Container buildNGPanel(int iThr, int iAtt, int iHold, int iDec) {
        SysexWidget[] widgets = {
            newKnob("ng: Tresh", iThr, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ng: Attack", iAtt, 0, 120),
            newKnob("ng: Hold", iHold, ngHoldString),
            newKnob("ng: Decay", iDec, ngDecayString)
        };

        return buildPanel("Noise Gate", widgets);
    }

    //Digital Distortion
    private void buildDigDist() {
        oTabs.add(buildDigDistPanel(), "Distortion");
    }

    private Container buildDigDistPanel() {
        SysexWidget[] widgets = {
            newCombo("dst: Type", 0x200116, distString),
            newKnob("dst: Drive", 0x20011E, 0, 100),
            newKnob("dst: Master", 0x20011F, 0, 100),
            newKnob("dst: Tone", 0x200120, 0, 20, -10),
            newKnob("dst: Noise Gate", 0x200121, 0, 20)
        };

        return buildPanel("Digital Distortion", widgets, new int[] {1, 4});
    }

    //Amp Simulator
    private void buildAmpSim() {
        oTabs.add(buildAmpSimPanel(), "Amp Sim");
        oTabs.add(buildNGPanel(0x20012A, 0x20012B, 0x20012C, 0x20012D), "Noise Gate");
    }

    private Container buildAmpSimPanel() {
        SysexWidget[] widgets = {
            newCombo("amp: Type", 0x200116, dist2String),
            newKnob("amp: Gain", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Master", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Tone", 0x200122, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newCombo("amp: Sp Sim", 0x200117, spSimString),
            newKnob("amp: Treble", 0x200124, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Hi Mid", 0x200125, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Lo Mid", 0x200126, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Bass", 0x200127, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("amp: Pres", 0x200128, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Amp Simulator", widgets, new int[] {1, 3, 1, 5});
    }

    //Chorus
    private void buildChorus() {
        oTabs.add(buildChorusPanel(), "Chorus");
    }

    private Container buildChorusPanel() {
        SysexWidget[] widgets = {
            newCombo("ch: Wave", 0x200116,waveString),
            newKnob("ch: Freq", 0x200110, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("ch: AM Depth", 0x200112, 0, 100),
            newKnob("ch: PM Depth", 0x200114, 0, 100),
            newKnob("ch: Mod Delay", 0x200100, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ch: LSH Freq", 0x20011E, lshFreqString),
            newKnob("ch: LSH Gain", 0x20011F, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ch: EQ Freq", 0x200120, eqFreqString),
            newKnob("ch: EQ Gain", 0x200121, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ch: EQ Q", 0x200122, eqQString),
            newKnob("ch: HSH Freq", 0x200123, hshFreqString),
            newKnob("ch: HSH Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ch: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Chorus", widgets, new int[] {1, 6, 6});
    }

    //Symphonic
    private void buildSymphonic() {
        oTabs.add(buildSymphonicPanel(), "Symphonic");
    }

    private Container buildSymphonicPanel() {
        SysexWidget[] widgets = {
            newCombo("sym: Wave", 0x200116,waveString),
            newKnob("sym: Freq", 0x200110, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("sym: Depth", 0x200112, 0, 100),
            newKnob("sym: Mod Delay", 0x200114, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("sym: LSH Freq", 0x20011E, lshFreqString),
            newKnob("sym: LSH Gain", 0x20011F, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("sym: EQ Freq", 0x200120, eqFreqString),
            newKnob("sym: EQ Gain", 0x200121, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("sym: EQ Q", 0x200122, eqQString),
            newKnob("sym: HSH Freq", 0x200124, hshFreqString),
            newKnob("sym: HSH Gain", 0x200125, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("sym: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Symphonic", widgets, new int[] {1, 5, 6});
    }

    //Flange
    private void buildFlange() {
        oTabs.add(buildFlangePanel(), "Flange");
    }

    private Container buildFlangePanel() {
        SysexWidget[] widgets = {
            newCombo("fl: Wave", 0x200116,waveString),
            newKnob("fl: Freq", 0x200110, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("fl: Depth", 0x200112, 0, 100),
            newKnob("fl: FB Gain", 0x200114, 0, 198, -99),
            newKnob("fl: Mod Delay", 0x200100, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: LSH Freq", 0x20011E, lshFreqString),
            newKnob("fl: LSH Gain", 0x20011F, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: EQ Freq", 0x200120, eqFreqString),
            newKnob("fl: EQ Gain", 0x200121, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: EQ Q", 0x200122, eqQString),
            newKnob("fl: HSH Freq", 0x200123, hshFreqString),
            newKnob("fl: HSH Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Flange", widgets, new int[] {1, 6, 6});
    }

    //Vintage Flange
    private void buildVinFlange() {
        oTabs.add(buildVinFlangePanel(), "Flange");
    }

    private Container buildVinFlangePanel() {
        SysexWidget[] widgets = {
            newCombo("fl: Type", 0x200116, new String [] {"1", "2", "3"}),
            newKnob("fl: Speed", 0x200100, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: Depth", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: Manual", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: FB", 0x200120, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: Spread", 0x200121, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("fl: Mix", 0x200122, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
        };

        return buildPanel("Vintage Flange", widgets, new int[] {1, 6});
    }

    //Phaser
    private void buildPhaser() {
        oTabs.add(buildPhaserPanel(), "Phaser");
    }

    private Container buildPhaserPanel() {
        SysexWidget[] widgets = {
            newCombo("ph: Stage", 0x200116, YamahaUB99Util.genString(2, 16, 2)),
            newKnob("ph: Freq", 0x200110, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("ph: Depth", 0x200112, 0, 100),
            newKnob("ph: FB Gain", 0x200114, 0, 198, -99),
            newKnob("ph: Offset", 0x200100, 0, 100),
            newKnob("ph: Phase", 0x20011E, phaseString),
            newKnob("ph: LSH Freq", 0x20011F, lshFreqString),
            newKnob("ph: LSH Gain", 0x200120, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ph: HSH Freq", 0x200121, hshFreqString),
            newKnob("ph: HSH Gain", 0x200122, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ph: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Phaser", widgets, new int[] {1, 5, 5});
    }

    //Mono Vintage Phaser
    private void buildMonoVinPhaser() {
        oTabs.add(buildMonoVinPhaserPanel(), "Phaser");
    }

    private Container buildMonoVinPhaserPanel() {
        SysexWidget[] widgets = {
            newCombo("ph: Stage", 0x200116, new String[] {"2", "4", "6", "8", "10", "12", "16"}),
            newCombo("ph: Mode", 0x200100, new String[] {"1", "2"}),
            newKnob("ph: Speed", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Depth", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Manual", 0x200120, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: FB", 0x200121, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Color", 0x200122, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Mono Vintage Phaser", widgets, new int[] {2, 5});
    }

    //Stereo Vintage Phaser
    private void buildStereoVinPhaser() {
        oTabs.add(buildStereoVinPhaserPanel(), "Phaser");
    }

    private Container buildStereoVinPhaserPanel() {
        SysexWidget[] widgets = {
            newCombo("ph: Stage", 0x200116, new String[] {"4", "6", "8", "10"}),
            newCombo("ph: Mode", 0x200100, new String[] {"1", "2"}),
            newKnob("ph: Speed", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Depth", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Manual", 0x200120, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: FB", 0x200121, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Color", 0x200122, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ph: Spread", 0x200123, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Stereo Vintage Phaser", widgets, new int[] {2, 6});
    }

    //Tremolo
    private void buildTremolo() {
        oTabs.add(buildTremoloPanel(), "Tremolo");
    }

    private Container buildTremoloPanel() {
        SysexWidget[] widgets = {
            newCombo("trm: Wave", 0x200116,wave2String),
            newKnob("trm: Freq", 0x200100, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("trm: Depth", 0x20011E, 0, 100),
            newKnob("trm: LSH Freq", 0x20011F, lshFreqString),
            newKnob("trm: LSH Gain", 0x200120, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("trm: EQ Freq", 0x200123, eqFreqString),
            newKnob("trm: EQ Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("trm: EQ Q", 0x200125, eqQString),
            newKnob("trm: HSH Freq", 0x200126, hshFreqString),
            newKnob("trm: HSH Gain", 0x200127, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0"))
        };

        return buildPanel("Tremolo", widgets, new int[] {1, 4, 5});
    }

    //Auto Pan
    private void buildAutoPan() {
        oTabs.add(buildAutoPanPanel(), "Auto Pan");
    }

    private Container buildAutoPanPanel() {
        SysexWidget[] widgets = {
            newCombo("pan: Wave", 0x200116, wave2String),
            newCombo("pan: Dir", 0x20011F, directionString),
            newKnob("pan: Freq", 0x200100, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("pan: Depth", 0x20011E, 0, 100),
            newKnob("pan: LSH Freq", 0x200120, lshFreqString),
            newKnob("pan: LSH Gain", 0x200121, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("pan: EQ Freq", 0x200123, eqFreqString),
            newKnob("pan: EQ Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("pan: EQ Q", 0x200125, eqQString),
            newKnob("pan: HSH Freq", 0x200126, hshFreqString),
            newKnob("pan: HSH Gain", 0x200127, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0"))
        };

        return buildPanel("Auto Pan", widgets, new int[] {1, 1, 4, 5});
    }

    //Rotary
    private void buildRotary() {
        oTabs.add(buildRotaryPanel(), "Rotary");
    }

    private Container buildRotaryPanel() {
        SysexWidget[] widgets = {
            newCombo("rot: Rotate", 0x200116, new String [] {"Stop", "Start"}),
            newCombo("rot: Speed", 0x20011E, new String [] {"Slow", "Fast"}),
            newKnob("rot: Slow", 0x200110, 0, 199, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("rot: Fast", 0x200112, 0, 199, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("rot: Drive", 0x20011F, 0, 100),
            newKnob("rot: Accel", 0x200120, 0, 10),
            newKnob("rot: Low", 0x200121, 0, 100),
            newKnob("rot: High", 0x200122, 0, 100),
            newKnob("rot: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Rotary", widgets, new int[] {1, 1, 4, 3});
    }

    ///Ring Mod.
    private void buildRingMod() {
        oTabs.add(buildRingModPanel(), "Ring Mod");
    }

    private Container buildRingModPanel() {
        SysexWidget[] widgets = {
            newCombo("mod: Source", 0x200116, new String [] {"Osc", "Self"}),
            newKnob("mod: Osc Freq", 0x200110, 0, 10000, new DoubleFormat(0.0, 0.5, "0.0")),
            newKnob("mod: FM Freq", 0x200112, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("mod: FM Depth", 0x200121, 0, 100),
            newKnob("mod: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Ring Mod", widgets, new int[] {1, 4});
    }

    //Mod. Filter
    private void buildModFilter() {
        oTabs.add(buildModFilterPanel(), "Mod Filter");
    }

    private Container buildModFilterPanel() {
        SysexWidget[] widgets = {
            newCombo("flt: Type", 0x200116, filterString),
            newKnob("flt: Freq", 0x200100, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("flt: Depth", 0x20011E, 0, 100),
            newKnob("flt: Phase", 0x20011F, phaseString),
            newKnob("flt: Offset", 0x200120, 0, 100),
            newKnob("flt: Res", 0x200121, 0, 20),
            newKnob("flt: Level", 0x200122, 0, 100),
            newKnob("flt: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Mod Filter", widgets, new int[] {1, 3, 4});
    }

    //Compressor
    private void buildCompressor() {
        oTabs.add(buildCompressorPanel(0x200100, 0x20011E, 0x20011F, 0x200120, 0x200121, 0x200122), "Comp");
    }

    private Container buildCompressorPanel(int iThr, int iRat, int iAtt, int iRel, int iKnee, int iGain) {
        SysexWidget[] widgets = {
            newKnob("cmp: Tresh", iThr, 0, 540, new DoubleFormat(-54.0, 0.1, "0.0")),
            newKnob("cmp: Ratio", iRat, cmpRatioString),
            newKnob("cmp: Attack", iAtt, 0, 120),
            newKnob("cmp: Release", iRel, cmpReleaseString),
            newKnob("cmp: Knee", iKnee, cmpKneeString),
            newKnob("cmp: Gain", iGain, 0, 36, new DoubleFormat(0.0, 0.5, "0.0"))
        };

        return buildPanel("Compressor", widgets);
    }

    //M.Band Dyna.
    private void buildMBandDyna() {
        oTabs.add(buildFilterPanel(), "Filter");
        oTabs.add(buildCompressorPanel(), "Compressor");
        oTabs.add(buildExpanderPanel(), "Expander");
        oTabs.add(buildLimiterPanel(), "Limiter");
    }

    private Container buildFilterPanel() {
        String[] ceilingString = YamahaUB99Util.genString(-6.0, 0.1, 0.1, "0.0");
        ceilingString[ceilingString.length-1] = "Off";
        SysexWidget[] widgets = {
            newCombo("flt: Slope", 0x200116, new String[] {"-6", "-12"}),
            newKnob("flt: Lo Gain", 0x200110, 0, 1080, new DoubleFormat(-96.0, 0.1, "0.0")),
            newKnob("flt: Mid Gain", 0x200112, 0, 1080, new DoubleFormat(-96.0, 0.1, "0.0")),
            newKnob("flt: Hi Gain", 0x200114, 0, 1080, new DoubleFormat(-96.0, 0.1, "0.0")),
            newKnob("flt: Lookup", 0x200100, 0, 1000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("flt: Ceiling", 0x20011F, ceilingString),
            newKnob("flt: L-M Xover", 0x200120, lshFreqString),
            newKnob("flt: M-H Xover", 0x200121, lshFreqString),
            newKnob("flt: Pres", 0x200122, 0, 20, -10)
        };

        return buildPanel("Filter", widgets, new int[] {1, 4, 4});
    }

    private Container buildCompressorPanel() {
        SysexWidget[] widgets = {
            newCombo("cmp: Bypass", 0x200117, offOnString),
            newKnob("cmp: Tresh", 0x200102, 0, 240, new DoubleFormat(-24.0, 0.1, "0.0")),
            newKnob("cmp: Ratio", 0x200129, cmpRatio2String),
            newKnob("cmp: Attack", 0x20012A, 0, 120),
            newKnob("cmp: Release", 0x20012B, cmpReleaseString),
            newKnob("cmp: Knee", 0x20012C, 0, 5)
        };

        return buildPanel("Compressor", widgets);
    }

    private Container buildExpanderPanel() {
        SysexWidget[] widgets = {
            newCombo("exp: Bypass", 0x200118, offOnString),
            newKnob("exp: Tresh", 0x200104, 0, 300, new DoubleFormat(-54.0, 0.1, "0.0")),
            newKnob("exp: Ratio", 0x200134, cmpRatioString),
            newKnob("exp: Release", 0x200136, cmpReleaseString)
        };

        return buildPanel("Expander", widgets);
    }

    private Container buildLimiterPanel() {
        SysexWidget[] widgets = {
            newCombo("lim: Bypass", 0x200119, offOnString),
            newKnob("lim: Tresh", 0x200106, 0, 120, new DoubleFormat(-12.0, 0.1, "0.0")),
            newKnob("lim: Attack", 0x200140, 0, 120),
            newKnob("lim: Release", 0x200141, cmpReleaseString),
            newKnob("lim: Knee", 0x200142, 0, 5)
        };

        return buildPanel("Limiter", widgets);
    }

    //Dyna. Filter
    private void buildDynaFilter() {
        oTabs.add(buildDynaFilterPanel(), "Filter");
    }

    private Container buildDynaFilterPanel() {
        SysexWidget[] widgets = {
            newCombo("flt: Type", 0x200116, filterString),
            newCombo("flt: Dir", 0x20011E, filtdirString),
            newKnob("flt: Decay", 0x200114, decayString),
            newKnob("flt: Sense", 0x20011F, 0, 100),
            newKnob("flt: Offset", 0x200120, 0, 100),
            newKnob("flt: Res", 0x200121, 0, 20),
            newKnob("flt: Level", 0x200122, 0, 100),
            newKnob("flt: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Dyna Filter", widgets, new int[] {1, 1, 6});
    }

    //Dyna. Flange
    private void buildDynaFlange() {
        oTabs.add(buildDynaFlangePanel(), "Flange");
    }

    private Container buildDynaFlangePanel() {
        SysexWidget[] widgets = {
            newCombo("fl: Dir", 0x20011E, filtdirString),
            newKnob("fl: Decay", 0x200114, decayString),
            newKnob("fl: FB Gain", 0x200100, 0, 198, -99),
            newKnob("fl: Sense", 0x20011F, 0, 100),
            newKnob("fl: Offset", 0x200120, 0, 100),
            newKnob("fl: LSH Freq", 0x200121, lshFreqString),
            newKnob("fl: LSH Gain", 0x200122, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: EQ Freq", 0x200123, eqFreqString),
            newKnob("fl: EQ Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: EQ Q", 0x200125, eqQString),
            newKnob("fl: HSH Freq", 0x200126, hshFreqString),
            newKnob("fl: HSH Gain", 0x200127, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("fl: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Dyna Flange", widgets, new int[] {1, 6, 6});
    }

    //Dyna. Phaser
    private void buildDynaPhaser() {
        oTabs.add(buildDynaPhaserPanel(), "Phaser");
    }

    private Container buildDynaPhaserPanel() {
        SysexWidget[] widgets = {
            newCombo("ph: Dir", 0x20011E, filtdirString),
            newCombo("ph: Stage", 0x200121, YamahaUB99Util.genString(2, 16, 2)),
            newKnob("ph: Decay", 0x200114, decayString),
            newKnob("ph: FB Gain", 0x200100, 0, 198, -99),
            newKnob("ph: Sense", 0x20011F, 0, 100),
            newKnob("ph: Offset", 0x200120, 0, 100),
            newKnob("ph: LSH Freq", 0x200123, lshFreqString),
            newKnob("ph: LSH Gain", 0x200124, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ph: HSH Freq", 0x200125, hshFreqString),
            newKnob("ph: HSH Gain", 0x200126, 0, 48, new DoubleFormat(-12.0, 0.5, "0.0")),
            newKnob("ph: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Dyna Phaser", widgets, new int[] {1, 1, 4, 5});
    }

    //Tape Echo
    private void buildTapeEcho() {
        oTabs.add(buildTapeEchoPanel(), "Echo");
    }

    private Container buildTapeEchoPanel() {
        SysexWidget[] widgets = {
            newKnob("ech: Time", 0x200100, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: FB", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: Level", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Tape Echo", widgets);
    }

    //Mono Delay
    private void buildMonoDelay() {
        oTabs.add(buildMonoDelayPanel(), "Delay");
    }

    private Container buildMonoDelayPanel() {
        SysexWidget[] widgets = {
            newKnob("dly: Delay", 0x200110, 0, 13650, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: FB Gain", 0x200112, 0, 198, -99),
            newKnob("dly: Hi Ratio", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: Hi Pass", 0x200120, hipassString),
            newKnob("dly: Lo Pass", 0x200121, lopassString),
            newKnob("dly: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Mono Delay", widgets);
    }

    //Stereo Delay
    private void buildStereoDelay() {
        oTabs.add(buildStereoDelayPanel(), "Delay");
    }

    private Container buildStereoDelayPanel() {
        SysexWidget[] widgets = {
            newKnob("dly: Delay L", 0x200110, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: Delay R", 0x200112, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: FB Gain L", 0x200114, 0, 198, -99),
            newKnob("dly: FB Gain R", 0x200100, 0, 198, -99),
            newKnob("dly: Hi Ratio", 0x20011E, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("dly: Hi Pass", 0x20011F, hipassString),
            newKnob("dly: Lo Pass", 0x200120, lopassString),
            newKnob("dly: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Stereo Delay", widgets, new int[] {4, 4});
    }

    //Mod Delay
    private void buildModDelay() {
        oTabs.add(buildModDelayPanel(), "Delay");
    }

    private Container buildModDelayPanel() {
        SysexWidget[] widgets = {
            newCombo("dly: Wave", 0x200116, waveString),
            newKnob("dly: Delay", 0x200110, 0, 13625, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: Freq", 0x200114, 0, 800, new DoubleFormat(0.00, 0.05, "0.00")),
            newKnob("dly: FB Gain", 0x200112, 0, 198, -99),
            newKnob("dly: Hi Pass", 0x20011F, hipassString),
            newKnob("dly: Lo Pass", 0x200120, lopassString),
            newKnob("dly: Hi Ratio", 0x200121, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("dly: Depth", 0x200122, 0, 100),
            newKnob("dly: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Mod Delay", widgets, new int[] {1, 5, 3});
    }

    //Delay LCR
    private void buildDelayLCR() {
        oTabs.add(buildDelayLCRPanel(), "Delay");
    }

    private Container buildDelayLCRPanel() {
        SysexWidget[] widgets = {
            newKnob("dly: Delay L", 0x200110, 0, 13650, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: Delay C", 0x200112, 0, 13650, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: Delay R", 0x200114, 0, 13650, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: Level L", 0x20011E, 0, 100, new IntFormat(-100, 2)),
            newKnob("dly: Level C", 0x20011F, 0, 100, new IntFormat(-100, 2)),
            newKnob("dly: Level R", 0x200120, 0, 100, new IntFormat(-100, 2)),
            newKnob("dly: FB", 0x200100, 0, 13650, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("dly: FB Gain", 0x200121, fbGainString),
            newKnob("dly: Hi Ratio", 0x200122, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("dly: Hi Pass", 0x200123, hipassString),
            newKnob("dly: Lo Pass", 0x200124, lopassString),
            newKnob("dly: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Delay LCR", widgets, new int[] {6, 6});
    }

    //Echo
    private void buildEcho() {
        oTabs.add(buildEchoPanel(), "Echo");
    }

    private Container buildEchoPanel() {
        SysexWidget[] widgets = {
            newKnob("ech: Delay L", 0x200110, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: Delay R", 0x200112, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: FB Dly L", 0x200114, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: FB Dly R", 0x200100, 0, 13500, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("ech: FB Gn L", 0x20011E, fbGainString),
            newKnob("ech: FB Gn R", 0x20011F, fbGainString),
            newKnob("ech: LR FB Gn", 0x200120, fbGainString),
            newKnob("ech: RL FB Gn", 0x200121, fbGainString),
            newKnob("ech: Hi Ratio", 0x200122, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("ech: Hi Pass", 0x200123, hipassString),
            newKnob("ech: Lo Pass", 0x200124, lopassString),
            newKnob("ech: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Echo", widgets, new int[] {6, 6});
    }


    //8 Band Parallel Delay
    private void build8BandParDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        for (int i = 0; i < 8; i++)
            oTabs.add(build8BandTimePanel(i, 6959, 0.1), "Band " + (i+1));
    }

    private Container build8BandParDelayPanel() {
        SysexWidget[] widgets = {
            newCombo("dly: Wave", 0x200116, wave3String),
            newKnob("dly: FX Lvl", 0x200110, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: Dir Lvl", 0x200112, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: Dir Pan", 0x200114, panString)
        };

        return buildPanel("Main", widgets);
    }

    private Vector TimeVector(int i, int delay, double inc) {
        Vector widgets = new Vector(4);
        widgets.add(newKnob((i+1)+": "+"Time", 0x200100 + i*2, 0, delay, new DoubleFormat(inc, inc, "0.0")));
        widgets.add(newKnob((i+1)+": "+"LoCut Flt", 0x20011E + i*11, cutFilterString));
        widgets.add(newKnob((i+1)+": "+"HiCut Flt", 0x20011F + i*11, cutFilterString));
        widgets.add(newKnob((i+1)+": "+"FB", 0x200120 + i*11, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        return widgets;
    }

    private Vector WaveVector(int i) {
        Vector widgets = new Vector(8);
        widgets.add(newCombo((i+1)+": "+"Wave", 0x200121 + i*11, new String[] {"Sine", "Other"}));
        widgets.add(newCombo((i+1)+": "+"Phase", 0x200122 + i*11, new String[] {"Normal", "Reverse"}));
        widgets.add(newKnob((i+1)+": "+"Tap", 0x200123 + i*11, 0, 100));
        widgets.add(newKnob((i+1)+": "+"Speed", 0x200124 + i*11, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob((i+1)+": "+"Depth", 0x200125 + i*11, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob((i+1)+": "+"Pan", 0x200126 + i*11, panString));
        widgets.add(newKnob((i+1)+": "+"Level", 0x200127 + i*11, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob((i+1)+": "+"Sync", 0x200128 + i*11, 0, 7, 1));
        return widgets;
    }


    private Container build8BandTimePanel(int i, int delay, double inc) {
        Vector widgets = new Vector(12);
        widgets.addAll(TimeVector(i, delay, inc));
        widgets.addAll(WaveVector(i));
        return buildPanel("Band " + (i+1), widgets.toArray(), new int[] {4, 1, 1, 6});
    }


    private Container build8BandWavePanel(int i) {
        Vector widgets = new Vector(8);
        widgets.addAll(WaveVector(i));
        return buildPanel("Band " + (i+1), widgets.toArray(), new int[] {1, 1, 6});
    }

    //8 Band Series Delay
    private void build8BandSerDelay() {
        build8BandParDelay();
    }

    //4 Band 2 Tap Mod Delay
    private void build4Band2TapModDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        oTabs.add(build8BandTimePanel(0, 14299, 0.1), "Band 1");
        oTabs.add(build8BandWavePanel(1), "Band 2");
        oTabs.add(build8BandTimePanel(2, 14299, 0.1), "Band 3");
        oTabs.add(build8BandWavePanel(3), "Band 4");
        oTabs.add(build8BandTimePanel(4, 14299, 0.1), "Band 5");
        oTabs.add(build8BandWavePanel(5), "Band 6");
        oTabs.add(build8BandTimePanel(6, 14299, 0.1), "Band 7");
        oTabs.add(build8BandWavePanel(7), "Band 8");
    }

    //2 Band 4 Tap Mod Delay
    private void build2Band4TapModDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        oTabs.add(build8BandTimePanel(0, 14599, 0.2), "Band 1");
        oTabs.add(build8BandWavePanel(1), "Band 2");
        oTabs.add(build8BandWavePanel(2), "Band 3");
        oTabs.add(build8BandWavePanel(3), "Band 4");
        oTabs.add(build8BandTimePanel(4, 14299, 0.1), "Band 5");
        oTabs.add(build8BandWavePanel(5), "Band 6");
        oTabs.add(build8BandWavePanel(6), "Band 7");
        oTabs.add(build8BandWavePanel(7), "Band 8");
    }

    //8 Multi Tap Mod Delay
    private void build8MultiTapModDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        oTabs.add(build8BandTimePanel(0, 11779, 0.5), "Band 1");
        oTabs.add(build8BandWavePanel(1), "Band 2");
        oTabs.add(build8BandWavePanel(2), "Band 3");
        oTabs.add(build8BandWavePanel(3), "Band 4");
        oTabs.add(build8BandWavePanel(4), "Band 5");
        oTabs.add(build8BandWavePanel(5), "Band 6");
        oTabs.add(build8BandWavePanel(6), "Band 7");
        oTabs.add(build8BandWavePanel(7), "Band 8");
    }

    //2 Band Long + 4 Short Mod Delay
    private void build2BandLong4ShortModDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        oTabs.add(build8BandTimePanel(0, 14299, 0.1), "Band 1");
        oTabs.add(build8BandWavePanel(1), "Band 2");
        oTabs.add(build8BandTimePanel(2, 14299, 0.1), "Band 3");
        oTabs.add(build8BandWavePanel(3), "Band 4");
        oTabs.add(build8BandTimePanel(4, 6959, 0.1), "Band 5");
        oTabs.add(build8BandTimePanel(5, 6959, 0.1), "Band 6");
        oTabs.add(build8BandTimePanel(6, 6959, 0.1), "Band 7");
        oTabs.add(build8BandTimePanel(7, 6959, 0.1), "Band 8");
    }

    //Short + Medium + Long Mod Delay
    private void buildShortMediumLongModDelay() {
        oTabs.add(build8BandParDelayPanel(), "Main");
        oTabs.add(build8BandTimePanel(0, 6959, 0.1), "Band 1");
        oTabs.add(build8BandTimePanel(1, 10899, 0.2), "Band 2");
        oTabs.add(build8BandWavePanel(2), "Band 3");
        oTabs.add(build8BandWavePanel(3), "Band 4");
        oTabs.add(build8BandTimePanel(4, 14599, 0.2), "Band 5");
        oTabs.add(build8BandWavePanel(5), "Band 6");
        oTabs.add(build8BandWavePanel(6), "Band 7");
        oTabs.add(build8BandWavePanel(7), "Band 8");
    }

    //Reverb
    private void buildReverb() {
        oTabs.add(buildReverbPanel(), "Reverb");
    }

    private Container buildReverbPanel() {
        String[] gateLevelString = YamahaUB99Util.genString(-61, 0);
        gateLevelString[0] = "Off";
        SysexWidget[] widgets = {
            newCombo("rv: Type", 0x200116, new String[] {"Hall", "Room", "Stage", "Plate"}),
            newKnob("rv: Init Dly", 0x200110, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("rv: ER/Rev Dly", 0x200112, 0, 1000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("rv: Time", 0x200114, reverbTimeString),
            newKnob("rv: Hi Ratio", 0x200100, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("rv: Lo Ratio", 0x20011E, 0, 23, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("rv: Diff", 0x20011F, 0, 10),
            newKnob("rv: Dens", 0x200120, 0, 100),
            newKnob("rv: Balance", 0x200121, 0, 100),
            newKnob("rv: Hi Pass", 0x200122, hipassString),
            newKnob("rv: Lo Pass", 0x200123, lopassString),
            newKnob("rv: Gate Lvl", 0x200124, gateLevelString),
            newKnob("rv: Attack", 0x200125, 0, 120),
            newKnob("rv: Hold", 0x200126, ngHoldString),
            newKnob("rv: Decay", 0x200127, ngDecayString),
            newKnob("rv: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Reverb", widgets, new int[] {1, 5, 5, 5});
    }

    //Early Ref.
    private void buildEarlyRef() {
        oTabs.add(buildEarlyRefPanel("Early Reflection", erTypeString), "Early Ref");
    }

    private Container buildEarlyRefPanel(String caption, String[] type) {
        SysexWidget[] widgets = {
            newCombo("er: Type", 0x200116, type),
            newKnob("er: Init Dly", 0x200110, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("er: FB Gain", 0x200112, 0, 198, -99),
            newKnob("er: Room size", 0x200114, 0, 199, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("er: Liveness", 0x20011F, 0, 10),
            newKnob("er: Diff", 0x200120, 0, 10),
            newKnob("er: Dens", 0x200121, 0, 100),
            newKnob("er: ER Nr", 0x200122, 0, 18, 1),
            newKnob("er: Hi Ratio", 0x200125, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("er: Hi Pass", 0x200126, hipassString),
            newKnob("er: Lo Pass", 0x200127, lopassString),
            newKnob("er: Mix", 0x200128, 0, 100)
        };

        return buildPanel(caption, widgets, new int[] {1, 6, 5});
    }

    //Gate Reverb
    private void buildGateReverb() {
        oTabs.add(buildEarlyRefPanel("Gate Reverb", gateTypeString), "Reverb");
    }

    //Reverse Gate
    private void buildReverseGate() {
        oTabs.add(buildEarlyRefPanel("Reverse Gate", gateTypeString), "Reverb");
    }

    //Spring Reverb
    private void buildSpringReverb() {
        oTabs.add(buildSpringReverbPanel(), "Reverb");
    }

    private Container buildSpringReverbPanel() {
        SysexWidget[] widgets = {
            newKnob("rv: Reverb", 0x200100, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Spring Reverb", widgets);
    }

    //HQ. Pitch
    private void buildHQPitch() {
        oTabs.add(buildHQPitchPanel(), "Pitch");
    }

    private Container buildHQPitchPanel() {
        SysexWidget[] widgets = {
            newKnob("pit: Mode", 0x200116, 0, 9, 1),
            newKnob("pit: Delay", 0x200110, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("pit: FB Gain", 0x200112, 0, 198, -99),
            newKnob("pit: Pitch", 0x20011E, 0, 24, -12),
            newKnob("pit: Fine", 0x20011F, 0, 100, -50),
            newKnob("pit: Mix", 0x200128, 0, 100)
        };

        return buildPanel("HQ Pitch", widgets);
    }

    //Dual Pitch
    private void buildDualPitch() {
        oTabs.add(buildDualPitchPanel(), "Pitch");
    }

    private Container buildDualPitchPanel() {
        SysexWidget[] widgets = {
            newKnob("pit: Mode", 0x200116, 0, 9, 1),
            newKnob("pit: Dly 1", 0x200110, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("pit: FB Gn 1", 0x200112, 0, 198, -99),
            newKnob("pit: Dly 2", 0x200114, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("pit: FB Gn 2", 0x200100, 0, 198, -99),
            newKnob("pit: Pitch 1", 0x20011E, 0, 48, -24),
            newKnob("pit: Fine 1", 0x20011F, 0, 100, -50),
            newKnob("pit: Lvl 1", 0x200120, 0, 100, new IntFormat(-100, 2)),
            newKnob("pit: Pan 1", 0x200121, pan1String),
            newKnob("pit: Pitch 2", 0x200122, 0, 48, -24),
            newKnob("pit: Fine 2", 0x200123, 0, 100, -50),
            newKnob("pit: Lvl 2", 0x200124, 0, 100, new IntFormat(-100, 2)),
            newKnob("pit: Pan 2", 0x200125, pan1String),
            newKnob("pit: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Dual Pitch", widgets, new int[] {5, 4, 5});
    }

    //3 Band Parametric EQ
    private void build3BandParEQ() {
        oTabs.add(build3BandParEQPanel(), "EQ");
    }

    private Container build3BandParEQPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "3 Band Parametric EQ",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        //EQ Level
        addRegisterWidget(pnl, newKnob("Level", 0x200100, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            0, 0, 1, 1, gbc.anchor, gbc.fill, 0);

        gbc.gridx = 1;
        gbc.gridy = 1;

        JLabel lbl = new JLabel("   EQ1");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   EQ2");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("   EQ3");
        pnl.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnl.add(new JLabel("Freq"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Gain"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Q"), gbc);

        //Freq
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x200110 + i*2, eq5FreqString, "eq: Freq" + (i+1)),
                1 + i, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        //Gain
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x200120 + i, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0"), "eq: Gain" + (i+1)),
                1 + i, 3, 1, 1, gbc.anchor, gbc.fill, 0);
        //Q
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x200126 + i, qString, "eq: Q" + (i+1)),
                1 + i, 4, 1, 1, gbc.anchor, gbc.fill, 0);

        return pnl;
    }

    //Multi Filter
    private void buildMultiFilter() {
        oTabs.add(buildMultiFilterPanel(), "Filter");
    }

    private Container buildMultiFilterPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel pnl = new JPanel();
        pnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
            "Multi Filter",TitledBorder.CENTER,TitledBorder.CENTER));
        pnl.setLayout(gridbag);

        //Mix
        addRegisterWidget(pnl, newKnob("flt: Mix", 0x200128, 0, 100),
            0, 0, 1, 1, gbc.anchor, gbc.fill, 0);

        gbc.gridx = 1;
        gbc.gridy = 1;

        JLabel lbl = new JLabel("     1");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("     2");
        pnl.add(lbl, gbc);
        gbc.gridx++;
        lbl = new JLabel("     3");
        pnl.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnl.add(new JLabel("Type"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Freq"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Level"), gbc);
        gbc.gridy++;
        pnl.add(new JLabel("Res"), gbc);

        //Type
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newCombo(0x200116 + i, filterString, "flt: Type" + (i+1)),
                1 + i, 2, 1, 1, gbc.anchor, gbc.fill, 0);
        //Freq
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x20011E + i*11, eq6FreqString, "flt: Freq" + (i+1)),
                1 + i, 3, 1, 1, gbc.anchor, gbc.fill, 0);
        //Level
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x20011F + i*11, 0, 100, "flt: Level" + (i+1)),
                1 + i, 4, 1, 1, gbc.anchor, gbc.fill, 0);

        //Res
        for (int i = 0; i < 3; i++)
            addRegisterWidget(pnl, newKnob(0x200120 + i*11, 0, 20, "flt: Res" + (i+1)),
                1 + i, 5, 1, 1, gbc.anchor, gbc.fill, 0);

        return pnl;
    }

    //Reverb + Chorus
    private void buildReverbChorusPar() {
        oTabs.add(buildChPanel(0, "Chorus"), "Chorus");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    private Container buildRvPanel(int iTime, int iHi, int iInit, int iDiff, int iDens) {
        SysexWidget[] widgets = {
            newKnob("rv: Time", iTime, reverbTimeString),
            newKnob("rv: Hi Ratio", iHi, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("rv: Init Dly", iInit, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("rv: Diff", iDiff, 0, 10),
            newKnob("rv: Dens", iDens, 0, 100)
        };

        return buildPanel("Reverb", widgets);
    }

    private Container buildChPanel(int type, String caption) {
        Vector widgets = new Vector();

        if (type == 3) { //Pan
            widgets.add(newCombo("ch: Wave", 0x200116,wave2String));
            widgets.add(newCombo("ch: Dir", 0x200127, directionString));
        } else { //Chorus, Flange, Symphonic
            widgets.add(newCombo("ch: Wave", 0x200116,waveString));
        }
        widgets.add(newKnob("ch: Freq", 0x200112, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")));

        if (type == 0) { //Chorus
            widgets.add(newKnob("ch: AM Depth", 0x200126, 0, 100));
            widgets.add(newKnob("ch: PM Depth", 0x200127, 0, 100));
        } else { //Flange, Symphonic, Pan
            widgets.add(newKnob("ch: Depth", 0x200126, 0, 100));
        }

        if (type == 1) { //Flange
            widgets.add(newKnob("ch: FB Gain", 0x200100, 0, 198, -99));
        }
        widgets.add(newKnob("ch: Mod Delay", 0x200114, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")));

        return buildPanel(caption, widgets.toArray());
    }

    private Container buildBalPanel(int iBal) {
        SysexWidget[] widgets = {
            newKnob("bal: Balance", iBal, 0, 100)
        };

        return buildPanel("Balance", widgets);
    }

    private Container buildFltrPanel(int iHi, int iLo) {
        SysexWidget[] widgets = {
            newKnob("flt: Hi Pass", iHi, hipassString),
            newKnob("flt: Lo Pass", iLo, lopassString)
        };

        return buildPanel("Filter", widgets);
    }

    private Container buildOutPanel() {
        SysexWidget[] widgets = {
            newKnob("out: Mix", 0x200128, 0, 100)
        };

        return buildPanel("Output", widgets);
    }

    //Reverb -> Chorus
    private void buildReverbChorusSer() {
        oTabs.add(buildChPanel(0, "Chorus"), "Chorus");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Reverb + Flange
    private void buildReverbFlangePar() {
        oTabs.add(buildChPanel(1, "Flange"), "Flange");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Reverb -> Flange
    private void buildReverbFlangeSer() {
        oTabs.add(buildChPanel(1, "Flange"), "Flange");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Reverb + Symphonic
    private void buildReverbSymphonicPar() {
        oTabs.add(buildChPanel(2, "Symphonic"), "Symphonic");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Reverb -> Symphonic
    private void buildReverbSymphonicSer() {
        oTabs.add(buildChPanel(2, "Symphonic"), "Symphonic");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Reverb -> Pan
    private void buildReverbPanSer() {
        oTabs.add(buildChPanel(3, "Pan"), "Pan");
        oTabs.add(buildRvPanel(0x20011E, 0x20011F, 0x200110, 0x200120, 0x200121), "Reverb");
        oTabs.add(buildBalPanel(0x200125), "Balance");
        oTabs.add(buildFltrPanel(0x200122, 0x200124), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Delay + Early Ref.
    private void buildDelayEarlyRefPar() {
        oTabs.add(buildDlyPanel(), "Delay");
        oTabs.add(buildERPanel(), "ER");
        oTabs.add(buildBalPanel(0x200121), "Balance");
        oTabs.add(buildFltrPanel(0x20011F, 0x200120), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    private Container buildDlyPanel() {
        SysexWidget[] widgets = {
            newKnob("dly: Delay L", 0x200110, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: Delay R", 0x200112, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: FB Dly", 0x200114, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: FB Gain", 0x200100, 0, 198, -99),
            newKnob("dly: Hi Ratio", 0x20011E, 0, 9, new DoubleFormat(0.1, 0.1, "0.0"))
        };

        return buildPanel("Delay", widgets);
    }

    private Container buildERPanel() {
        SysexWidget[] widgets = {
            newCombo("er: Type", 0x200117, erTypeString),
            newKnob("er: Room size", 0x200129, 0, 99, new DoubleFormat(0.2, 0.2, "0.0")),
            newKnob("er: Liveness", 0x20012A, 0, 10),
            newKnob("er: Init Dly", 0x200102, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("er: Diff", 0x20012B, 0, 10),
            newKnob("er: Dens", 0x20012C, 0, 100),
            newKnob("er: ER Nr", 0x20012D, 0, 18, 1),
        };

        return buildPanel("Early Reflections", widgets);
    }

    //Delay -> Early Ref.
    private void buildDelayEarlyRefSer() {
        oTabs.add(buildDlyPanel(), "Delay");
        oTabs.add(buildERPanel(), "ER");
        oTabs.add(buildBalPanel(0x200121), "Balance");
        oTabs.add(buildFltrPanel(0x20011F, 0x200120), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Delay + Reverb
    private void buildDelayReverbPar() {
        oTabs.add(buildDlyPanel(), "Delay");
        oTabs.add(buildRvPanel(0x200129, 0x20012A, 0x200102, 0x20012B, 0x20012C), "Reverb");
        oTabs.add(buildBalPanel(0x200121), "Balance");
        oTabs.add(buildFltrPanel(0x20011F, 0x200120), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Delay -> Reverb
    private void buildDelayReverbSer() {
        oTabs.add(buildDlyPanel(), "Delay");
        oTabs.add(buildRvPanel(0x200129, 0x20012A, 0x200102, 0x20012B, 0x20012C), "Reverb");
        oTabs.add(buildBalPanel(0x200121), "Balance");
        oTabs.add(buildFltrPanel(0x20011F, 0x200120), "Filter");
        oTabs.add(buildOutPanel(), "Output");
    }

    //Distortion -> Delay
    private void buildDistortionDelaySer() {
        oTabs.add(buildDistPanel(), "Dist");
        oTabs.add(buildNGPanel(), "NG");
        oTabs.add(buildModPanel(), "Mod");
        oTabs.add(buildBalPanel(0x200126), "Balance");
    }

    private Container buildDistPanel() {
        SysexWidget[] widgets = {
            newCombo("dst: Type", 0x200116, distString),
            newKnob("dst: Drive", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Master", 0x20011F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Tone", 0x200120, 0, 20, -10)
        };

        return buildPanel("Distortion", widgets);
    }

    private Container buildModPanel() {
        SysexWidget[] widgets = {
            newKnob("mod: Freq", 0x200114, 0, 799, new DoubleFormat(0.05, 0.05, "0.00")),
            newKnob("mod: Depth", 0x200125, 0, 100),
            newKnob("mod: Delay", 0x200110, 0, 13625, new DoubleFormat(0.0, 0.2, "0.0")),
            newKnob("mod: FB Gain", 0x200112, 0, 198, -99),
            newKnob("mod: Hi Ratio", 0x200124, 0, 9, new DoubleFormat(0.1, 0.1, "0.0"))
        };

        return buildPanel("Modulation", widgets);
    }

    private Container buildNGPanel() {
        SysexWidget[] widgets = {
            newKnob("ng: NG", 0x200121, 0, 20)
        };

        return buildPanel("Noise Gate", widgets);
    }

    //Amp Multi (Chorus)
    private void buildAmpMultiChorus() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildAmpSimPanel(), "Amp Sim");
        oTabs.add(buildNGPanel(0x20012A, 0x20012B, 0x20012C, 0x20012D), "NG");
        oTabs.add(buildMultiChorusPanel(), "Chorus");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    private Container buildMultiChorusPanel() {
        Vector widgets = new Vector();

        widgets.add(newCombo("ch: Wave", 0x200119,waveString));
        widgets.add(newKnob("ch: Delay", 0x200106, 0, 300, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("ch: Speed", 0x20013F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("ch: Depth", 0x200140, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("ch: Level", 0x200141, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));

        return buildPanel("Chorus", widgets.toArray());
    }

    private Container buildMultiDlyPanel() {
        SysexWidget[] widgets = {
            newKnob("dly: Tap L", 0x20014A, 0, 100),
            newKnob("dly: Tap R", 0x20014B, 0, 100),
            newKnob("dly: FB Dly", 0x200108, 0, 10000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dly: FB Gain", 0x20014C, fbGainString),
            newKnob("dly: Hi Ratio", 0x20014D, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("dly: Level", 0x20014E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Delay", widgets);
    }

    private Container buildMultiRvPanel() {
        SysexWidget[] widgets = {
            newKnob("rv: Time", 0x200155, reverbTimeString),
            newKnob("rv: Init Dly", 0x20010A, 0, 5000, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("rv: Hi Ratio", 0x200156, 0, 9, new DoubleFormat(0.1, 0.1, "0.0")),
            newKnob("rv: Diff", 0x200157, 0, 10),
            newKnob("rv: Dens", 0x200158, 0, 100),
            newKnob("rv: Level", 0x200159, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Reverb", widgets);
    }

    //Amp Multi (Flange)
    private void buildAmpMultiFlange() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildAmpSimPanel(), "Amp Sim");
        oTabs.add(buildNGPanel(0x20012A, 0x20012B, 0x20012C, 0x20012D), "NG");
        oTabs.add(buildMultiFlangePanel(), "Flange");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    private Container buildMultiFlangePanel() {
        Vector widgets = new Vector();

        widgets.add(newCombo("fl: Wave", 0x200119,waveString));
        widgets.add(newKnob("fl: Delay", 0x200106, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("fl: Speed", 0x20013F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("fl: Depth", 0x200140, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("fl: FB", 0x200141, fbGainString));
        widgets.add(newKnob("fl: Level", 0x200142, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));

        return buildPanel("Flange", widgets.toArray());
    }

    //Amp Multi (Tremolo)
    private void buildAmpMultiTremolo() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildAmpSimPanel(), "Amp Sim");
        oTabs.add(buildNGPanel(0x20012A, 0x20012B, 0x20012C, 0x20012D), "NG");
        oTabs.add(buildMultiTremoloPanel(), "Tremolo");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    private Container buildMultiTremoloPanel() {
        Vector widgets = new Vector();

        widgets.add(newCombo("trm: Wave", 0x200119,wave2String));
        widgets.add(newKnob("trm: Speed", 0x20013F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("trm: Depth", 0x200140, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));

        return buildPanel("Tremolo", widgets.toArray());
    }

    //Amp Multi (Phaser)
    private void buildAmpMultiPhaser() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildAmpSimPanel(), "Amp Sim");
        oTabs.add(buildNGPanel(0x20012A, 0x20012B, 0x20012C, 0x20012D), "NG");
        oTabs.add(buildMultiPhaserPanel(), "Phaser");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    private Container buildMultiPhaserPanel() {
        Vector widgets = new Vector();

        widgets.add(newCombo("ph: Wave", 0x200119,waveString));
        widgets.add(newKnob("ph: Speed", 0x20013F, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("ph: Depth", 0x200140, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));
        widgets.add(newKnob("ph: FB", 0x200141, fbGainString));
        widgets.add(newKnob("ph: Level", 0x200142, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")));

        return buildPanel("Phaser", widgets.toArray());
    }

    //Distortion Multi (Chorus)
    private void buildDistMultiChorus() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildMultiDistPanel(), "Distortion");
        oTabs.add(buildEQPanel(), "EQ");
        oTabs.add(buildPreEQPanel(), "Pre EQ");
        oTabs.add(buildNGPanel(0x20013B, 0x20013C, 0x20013D, 0x20013E), "NG");
        oTabs.add(buildMultiChorusPanel(), "Chorus");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    private Container buildMultiDistPanel() {
        SysexWidget[] widgets = {
            newCombo("dst: Type", 0x200116, dist3String),
            newKnob("dst: Gain", 0x200110, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Master", 0x200112, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("dst: Tone", 0x200114, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Distortion", widgets);
    }

    //Distortion Multi (Flange)
    private void buildDistMultiFlange() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildMultiDistPanel(), "Distortion");
        oTabs.add(buildEQPanel(), "EQ");
        oTabs.add(buildPreEQPanel(), "Pre EQ");
        oTabs.add(buildNGPanel(0x20013B, 0x20013C, 0x20013D, 0x20013E), "NG");
        oTabs.add(buildMultiFlangePanel(), "Flange");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    //Distortion Multi (Tremolo)
    private void buildDistMultiTremolo() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildMultiDistPanel(), "Distortion");
        oTabs.add(buildEQPanel(), "EQ");
        oTabs.add(buildPreEQPanel(), "Pre EQ");
        oTabs.add(buildNGPanel(0x20013B, 0x20013C, 0x20013D, 0x20013E), "NG");
        oTabs.add(buildMultiTremoloPanel(), "Tremolo");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    //Distortion Multi (Phaser)
    private void buildDistMultiPhaser() {
        oTabs.add(buildCompressorPanel(0x200104, 0x200134, 0x200135, 0x200136, 0x200137, 0x200138), "Comp");
        oTabs.add(buildMultiDistPanel(), "Distortion");
        oTabs.add(buildEQPanel(), "EQ");
        oTabs.add(buildPreEQPanel(), "Pre EQ");
        oTabs.add(buildNGPanel(0x20013B, 0x20013C, 0x20013D, 0x20013E), "NG");
        oTabs.add(buildMultiPhaserPanel(), "Phaser");
        oTabs.add(buildFltrPanel(0x200152, 0x200153), "Filter");
        oTabs.add(buildMultiDlyPanel(), "Delay");
        oTabs.add(buildMultiRvPanel(), "Reverb");
    }

    //Acoustic Multi
    private void buildAcousticMulti() {
        oTabs.add(buildAcLimPanel(), "Limiter");
        oTabs.add(buildAcMicPanel(), "Mic");
        oTabs.add(buildAcTonePanel(), "Tone");
        oTabs.add(buildAcEQPanel(), "EQ");
        oTabs.add(buildAcFXPanel(), "FX");
        oTabs.add(buildAcRvPanel(), "Reverb");
    }

    private Container buildAcMicPanel() {
        SysexWidget[] widgets = {
            newCombo("mic: Type", 0x200116, new String[] {"Condenser 1", "Condenser 2", "Dynamic 1", "Dynamic 2", "Tube 1", "Tube 2", "Nylon String 1", "Nylon String 2"}),
            newKnob("mic: Blend", 0x20011E, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("mic: Volume", 0x200123, 0, 100, new DoubleFormat(0.0, 0.1, "0.0")),
            newKnob("mic: Stereo", 0x200124, 0, 100, new DoubleFormat(0.0, 0.1, "0.0"))
        };

        return buildPanel("Microphone", widgets);
    }

    private Container buildAcTonePanel() {
        SysexWidget[] widgets = {
            newKnob("ton: Bass", 0x20011F, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0")),
            newKnob("ton: Middle", 0x200120, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0")),
            newKnob("ton: Treble", 0x200121, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0")),
            newKnob("ton: Pres", 0x200122, 0, 120, new DoubleFormat(-12.0, 0.2, "0.0"))
        };

        return buildPanel("Tone", widgets);
    }

    private Container buildAcEQPanel() {
        String[] bassFreqString = {"50.0", "50.8", "51.7", "52.5", "53.4", "54.3", "55.2", "56.1", "57.0", "57.9", "58.9", "59.9", "60.9", "61.9", "62.9", "63.9", "65.0", "66.0", "67.1", "68.2", "69.4", "70.5", "71.7", "72.9", "74.1", "75.3", "76.5", "77.8", "79.1", "80.4", "81.7", "83.1", "84.4", "85.8", "87.2", "88.7", "90.1", "91.6", "93.2", "94.7", "96.3", "97.8", "99.5", "101", "103", "104", "106", "108", "110", "112", "113", "115", "117", "119", "121", "123", "125", "127", "129", "131", "134", "136", "138", "140", "143", "145", "147", "150", "152", "155", "157", "160", "163", "165", "168", "171", "174", "176", "179", "182", "185", "188", "191", "195", "198", "201", "204", "208", "211", "215", "218", "222", "226", "229", "233", "237", "241", "245", "249", "253", "257", "261", "266", "270", "274", "279", "284", "288", "293", "298", "303", "308", "313", "318", "323", "329", "334", "340", "345", "351", "357", "363", "369", "375", "381", "387", "394", "400"};

        String[] midFreqString = {"200", "203", "207", "210", "214", "217", "221", "224", "228", "232", "236", "239", "243", "247", "252", "256", "260", "264", "269", "273", "277", "282", "287", "291", "296", "301", "306", "311", "316", "322", "327", "332", "338", "343", "349", "355", "361", "367", "373", "379", "385", "391", "398", "404", "411", "418", "425", "432", "439", "446", "453", "461", "469", "476", "484", "492", "500", "509", "517", "526", "534", "543", "552", "561", "570", "580", "589", "599", "609", "619", "629", "640", "650", "661", "672", "683", "694", "706", "717", "729", "741", "753", "766", "778", "791", "804", "818", "831", "845", "859", "873", "887", "902", "917", "932", "947", "963", "979", "995", "1.01k", "1.03k", "1.05k", "1.06k", "1.08k", "1.10k", "1.12k", "1.13k", "1.15k", "1.17k", "1.19k", "1.21k", "1.23k", "1.25k", "1.27k", "1.29k", "1.31k", "1.34k", "1.36k", "1.38k", "1.40k", "1.43k", "1.45k", "1.47k", "1.50k", "1.52k", "1.55k", "1.57k", "1.60k"};

        String[] trebFreqString = {"600", "610", "620", "630", "641", "651", "662", "673", "684", "695", "707", "718", "730", "742", "755", "767", "780", "793", "806", "819", "832", "846", "860", "874", "889", "903", "918", "934", "949", "965", "981", "997", "1.01k", "1.03k", "1.05k", "1.06k", "1.08k", "1.10k", "1.12k", "1.14k", "1.16k", "1.17k", "1.19k", "1.21k", "1.23k", "1.25k", "1.27k", "1.30k", "1.32k", "1.34k", "1.36k", "1.38k", "1.41k", "1.43k", "1.45k", "1.48k", "1.50k", "1.53k", "1.55k", "1.58k", "1.60k", "1.63k", "1.66k", "1.68k", "1.71k", "1.74k", "1.77k", "1.80k", "1.83k", "1.86k", "1.89k", "1.92k", "1.95k", "1.98k", "2.02k", "2.05k", "2.08k", "2.12k", "2.15k", "2.19k", "2.22k", "2.26k", "2.30k", "2.34k", "2.37k", "2.41k", "2.45k", "2.49k", "2.53k", "2.58k", "2.62k", "2.66k", "2.71k", "2.75k", "2.80k", "2.84k", "2.89k", "2.94k", "2.99k", "3.03k", "3.08k", "3.14k", "3.19k", "3.24k", "3.29k", "3.35k", "3.40k", "3.46k", "3.52k", "3.57k", "3.63k", "3.69k", "3.75k", "3.82k", "3.88k", "3.94k", "4.01k", "4.08k", "4.14k", "4.21k", "4.28k", "4.35k", "4.42k", "4.50k", "4.57k", "4.65k", "4.72k", "4.80k"};

        String[] presFreqString = {"2.00k", "2.03k", "2.07k", "2.10k", "2.14k", "2.17k", "2.21k", "2.24k", "2.28k", "2.32k", "2.36k", "2.39k", "2.43k", "2.47k", "2.52k", "2.56k", "2.60k", "2.64k", "2.69k", "2.73k", "2.77k", "2.82k", "2.87k", "2.91k", "2.96k", "3.01k", "3.06k", "3.11k", "3.16k", "3.22k", "3.27k", "3.32k", "3.38k", "3.43k", "3.49k", "3.55k", "3.61k", "3.67k", "3.73k", "3.79k", "3.85k", "3.91k", "3.98k", "4.04k", "4.11k", "4.18k", "4.25k", "4.32k", "4.39k", "4.46k", "4.53k", "4.61k", "4.69k", "4.76k", "4.84k", "4.92k", "5.00k", "5.09k", "5.17k", "5.26k", "5.34k", "5.43k", "5.52k", "5.61k", "5.70k", "5.80k", "5.89k", "5.99k", "6.09k", "6.19k", "6.29k", "6.40k", "6.50k", "6.61k", "6.72k", "6.83k", "6.94k", "7.06k", "7.17k", "7.29k", "7.41k", "7.53k", "7.66k", "7.78k", "7.91k", "8.04k", "8.18k", "8.31k", "8.45k", "8.59k", "8.73k", "8.87k", "9.02k", "9.17k", "9.32k", "9.47k", "9.63k", "9.79k", "9.95k", "10.1k", "10.3k", "10.5k", "10.6k", "10.8k", "11.0k", "11.2k", "11.3k", "11.5k", "11.7k", "11.9k", "12.1k", "12.3k", "12.5k", "12.7k", "12.9k", "13.1k", "13.4k", "13.6k", "13.8k", "14.0k", "14.3k", "14.5k", "14.7k", "15.0k", "15.2k", "15.5k", "15.7k", "16.0k"};

        SysexWidget[] widgets = {
            newKnob("eq: Bass F", 0x200125, bassFreqString),
            newKnob("eq: Mid F", 0x200126, midFreqString),
            newKnob("eq: Treb F", 0x200127, trebFreqString),
            newKnob("eq: Pres F", 0x200128, presFreqString)
        };

        return buildPanel("EQ", widgets);
    }

    private Container buildAcLimPanel() {
        SysexWidget[] widgets = {
            newCheck("lim: Limiter", 0x200129),
            newKnob("lim: Level", 0x20012F, 0, 100)
        };

        return buildPanel("Limiter", widgets);
    }

    private Container buildAcFXPanel() {
        SysexWidget[] widgets = {
            newCombo("fx: Type", 0x20012B, new String[] {"Off", "Chorus", "Delay"}),
            newKnob("fx: Speed/Time", 0x200130, 0, 100),
            newKnob("fx: Depth/FB", 0x200131, 0, 100),
            newKnob("fx: Level", 0x200132, 0, 100)
        };

        return buildPanel("Effect", widgets);
    }

    private Container buildAcRvPanel() {
        SysexWidget[] widgets = {
            newCombo("rv: Type", 0x20012D, new String[] {"Off", "Hall", "Room", "Plate"}),
            newKnob("rv: Level", 0x200133, 0, 100)
        };

        return buildPanel("Reverb", widgets);
    }

}
