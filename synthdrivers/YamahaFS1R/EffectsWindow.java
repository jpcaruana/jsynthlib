package synthdrivers.YamahaFS1R;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.KnobLookupWidget;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;



/**
	Effects panel contains Reverberation, Variation and Insertion.
*/
class EffectsWindow
{
	protected Patch p;
	
	static final String[] mReverbs = { "No effect", "Hall 1", "Hall 2", "Room 1", "Room 2", "Room 3", "Stage 1", "Stage 2", "Plate", "White room", "Tunnel", "Basement", "Canyon", "Delay LCR", "Delay L,R", "Echo", "Cross delay"
	};

	static final String[] mVariations = { "No effect", "Chorus", "Celeste", "Flanger", "Symphonic", "Phaser 1", "Phaser 2", "Ensemble detune", "Rotary speaker", "Tremolo", "Auto pan", "Auto wah", "Touch wah", "3-band EQ", "HM enhencer", "Noise gate", "Compressor", "Distortion", "Overdrive", "Amp sim", "Delay LCR", "Delay L,R", "Echo", "Cross delay", "Karaoke", "Hall", "Room", "Stage", "Plate"
	};
	
	static final String[] mInsertions = { "Thru", "Chorus", "Celeste", "Flanger", "Symphonic", "Phaser 1", "Phaser 2", "Pitch change", "Ensemble detune", "Rotary speaker", "2 way rotary", "Tremolo", "Auto pan", "Ambiance", "Auto wah+distortion", "Auto wah+overdrive", "Touch wah+distortion", "Touch wah+overdrive", "TWah+Dist+Delay", "TWah+Ovdr+Delay", "Lo-fi", "3-band EQ", "HM enhencer", "Noise gate", "Compressor", "Comp+Dist", "Comp+Dist+Delay", "Comp+Ovdr+Delay", "Distortion", "Dist+Delay", "Overdrive", "Ovdr+Delay", "Amp sim", "Delay LCR", "Delay L,R", "Echo", "Cross delay", "ER 1", "ER 2", "Gate reverb", "Reverse gate"
	};

	static final String[] mQs = { 
	"", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", 
	"1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", 
	"2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", 
	"3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9",
	"4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9",
	"5.0", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6", "5.7", "5.8", "5.9",
	"6.0", "6.1", "6.2", "6.3", "6.4", "6.5", "6.6", "6.7", "6.8", "6.9",
	"7.0", "7.1", "7.2", "7.3", "7.4", "7.5", "7.6", "7.7", "7.8", "7.9",
	"8.0", "8.1", "8.2", "8.3", "8.4", "8.5", "8.6", "8.7", "8.8", "8.9",
	"9.0", "9.1", "9.2", "9.3", "9.4", "9.5", "9.6", "9.7", "9.8", "9.9",
	"10.0", "10.1", "10.2", "10.3", "10.4", "10.5", "10.6", "10.7", "10.8", "10.9", 
	"11.0", "11.1", "11.2", "11.3", "11.4", "11.5", "11.6", "11.7", "11.8", "11.9", "12.0" 
	};

	static final String[] mFreqs = { 
	"", "", "", "", "32", "36", "40", "45", "50", "56", "63", 
	"70", "80", "90", "100", "110", "125", "140", "160", "180", "200", 
	"225", "250", "280", "315", "355", "400", "450", "500", "560", "630", 
	"700", "800", "900", "1.0k", "1.1k", "1.2k", "1.4k", "1.6k", "1.8k", "2.0k",
	"2.2k", "2.5k", "2.8k", "3.2k", "3.6k", "4.0k", "4.5k", "5.0k", "5.6k", "6.3k", "7.0k", "8.0k", "9.0k", "10.0k", "11.0k", "12.0k", "14.0k", "16.0k"
	};
	
	static final String[] mReverbTimes = {
	"0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5",
	"1.6", "1.7", "1.8", "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8",
	"2.9", "3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "4.0", "4.1",
	"4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0", "5.5", "6.0", "6.5", "7.0",
	"7.5", "8.0", "8.5", "9.0", "9.5", "10", "11", "12", "13", "14", "15", "16", "17", "18", 
	"19", "20", "25", "30"
	};
	
	static final String[] mRoomSizes = {
	"0.5", "0.8", "1.0", "1.3", "1.5", "1.8", "2.0", "2.3", "2.6", "2.8", "3.1", "3.3", "3.6",
	"3.9", "4.1", "4.4", "4.6", "4.9", "5.2", "5.4", "5.7", "5.9", "6.2", "6.5", "6.7", "7.0",
	"7.2", "7.5", "7.8", "8.0", "8.3", "8.6", "8.8", "9.1", "9.4", "9.6", "9.9", "10.2", "10.4",
	"10.7", "11.0","11.2", "11.5", "11.8", "12.1", "12.3", "12.6", "12.9", "13.1", "13.4", "13.7",
	"14.0", "14.2", "14.5", "14.8", "15.1", "15.4", "15.6", "15.9", "16.2", "16.5", "16.8", "17.1",
	"17.3", "17.6", "17.9", "18.2", "18.5", "18.8", "19.1", "19.4", "19.7", "20.0", "20.2", "20.5",
	"20.8", "21.1", "21.4", "21.7", "22.0", "22.4", "22.7", "23.0", "23.3", "23.6", "23.9", "24.2",
	"24.5", "24.9", "25.2", "25.5", "25.8", "26.1", "26.5", "26.8", "27.1", "27.5", "27.8", "28.1",
	"28.5", "28.8", "29.2", "29.5", "29.9", "30.2"
	};
		
	static String mReverbNames[][] = {
	{},
	// hall1
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// hall2
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// room1
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// room2
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// room3
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// stage1
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// stage2
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	// plate
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", null, null, null, null, null, "Rev delay", "Density", "ER/Rev", "High damp", "FB Level"},
	//white room
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", "Width", "Height", "Depth",  "Wall vary", null, "Rev delay", "Density", "ER/Rev", "High damp", "FB level"},
	// tunnel
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", "Width", "Height", "Depth",  "Wall vary", null, "Rev delay", "Density", "ER/Rev", "High damp", "FB level"},
	// basement
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", "Width", "Height", "Depth",  "Wall vary", null, "Rev delay","Density", "ER/Rev", "High damp", "FB level"},
	// canyon
	{"Reverb time", "Diffusion", "InitDelay", "HPF cutoff", "LPF cutoff", "Width", "Height", "Depth",  "Wall vary", null, "Rev delay","Density", "ER/Rev", "High damp", "FB level"},
	// delay LCR
	{"LchDelay", "RchDelay", "CchDelay", "FB delay", "FB level", "Cch level", "High damp", null, null, null, null, null, "EQ Lowfreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// delay L,R
	{"LchDelay", "RchDelay", "FB delay1", "FB delay2", "FB level", "High Damp", null, null, null, null, null, null, "EQ Lowfreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// echo
	{"LchDelay1", "Lch FB Lv", "RchDelay1", "Rch FB Lv", "High Damp", "LchDelay2", "RchDelay2", "Delay2 Lev", null, null, null, null, "EQ Lowfreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// cross delay
	{"L>R Delay", "R>L Delay", "FB level", "InputSelect", "High Damp", null, null, null, null, null, null, null, "EQ Lowfreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"}
	};
	
	static String mVariationNames[][] = {
	{},
	// chorus
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, null, null, null, null, "Mode"},
	// celeste
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, null, null, null, null, "Mode"},
	// flanger
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, null, null, null, "LFO phase"},
	// symphonic
	{"LFO freq", "LFO Depth", "Delay Offset", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// phaser1
	{"LFO freq", "LFO Depth", "Phase shift", "FB level", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, "Stage", "Diffuse"},
	// phaser2
	{"LFO freq", "LFO Depth", "Phase shift", "FB level", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, "Stage", null, "LFO phase"},
	// ens detune
	{"Detune", "InitDelayL", "InitDelayR", null, null, null, null, null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// rotary SP
	{"LFO Freq", "LFO Depth", null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// tremolo
	{"LFO Freq", "AM depth", "PM depth", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, null, null, null, "LFO phase", "Mode"},
	// auto pan
	{"LFO Freq", "L/R depth", "F/R depth", "Pan dir", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// auto wah
	{"LFO Freq", "LFO depth", "Cutoff Freq", "Resonance", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// touch wah
	{"Sensitivity", "Cutoff Freq", "Resonance", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// 3-band EQ
	{"Low Gain", "Mid Freq", "Mid Gain", "Mid Q", "High Gain", "Low Freq", "High Freq", null, null, null, null, null, null, null, "Mode"},
	// HM enhencer
	{"HPFCutoff", "Drive", "Mix level"},
	// noise gate
	{"Attack", "Release", "Threshold", "OutputLevel"},
	// compressor
	{"Attack", "Release", "Threshold", "Ratio", "OutputLevel"},
	// distortion
	{"Drive", "EQ LowFreq", "EQ LowGain", "LPFCutoff", "OutputLevel", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", null, "Edge"},
	// overdrive
	{"Drive", "EQ LowFreq", "EQ LowGain", "LPFCutoff", "OutputLevel", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", null, "Edge"},
	// amp sim
	{"Drive", "Amp type", "LPFCutoff", "OutputLevel", null, null, null, null, null, null, "Edge"},
	// delay LCR
	{"LchDelay", "RchDelay", "CchDelay", "FB Delay", "FB level", "Cch level", "High Damp", null, null, null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// delay L,R
	{"LchDelay", "RchDelay", "FBDelay1", "DBDelay2", "FB level", "High Damp", null, null, null, null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// echo
	{"LchDelay1", "Lch FB Lev", "RchDelay1", "Rch FB Lev", "High Damp", "LchDelay2", "RchDelay2", "Delay2 Lev", null, null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// crossdelay
	{"L>E delay", "R>L delay", "FB level", "InputSelect", "High Damp", null, null, null, null, null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// karaoke
	{"Delay time", "FB level", "HPF Cutoff", "LPF Cutoff"},
	// hall
	{"Reverb Time", "Diffusion", "InitDelay", "HPF Cutoff", "LPF Cutoff", null, null, null, null, null, null, "Density", "High Damp", "FB level"},
	{"Reverb Time", "Diffusion", "InitDelay", "HPF Cutoff", "LPF Cutoff", null, null, null, null, null, null, "Density", "High Damp", "FB level"},
	{"Reverb Time", "Diffusion", "InitDelay", "HPF Cutoff", "LPF Cutoff", null, null, null, null, null, null, "Density", "High Damp", "FB level"},
	{"Reverb Time", "Diffusion", "InitDelay", "HPF Cutoff", "LPF Cutoff", null, null, null, null, null, null, "Density", "High Damp", "FB level"},
	};
	
	static String mInsertionNames[][] = {
	{},
	// chorus
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "EQ MidFreq", "EQ MidGain", "EQ Mid Q", null, "Mode"},
	// celeste
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "EQ MidFreq", "EQ MidGain", "EQ Mid Q", null, "Mode"},
	// flanger
	{"LFO freq", "LFO Depth", "FB level", "Delay Offset", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "EQ MidFreq", "EQ MidGain", "EQ Mid Q", "LFO phase"},
	// symphonic
	{"LFO freq", "LFO Depth", "Delay Offset", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "EQ MidFreq", "EQ MidGain", "EQ Mid Q"},
	// phaser1
	{"LFO freq", "LFO Depth", "Phase shift", "FB level", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Stage", "Diffuse"},
	// phaser2
	{"LFO freq", "LFO Depth", "Phase shift", "FB level", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Stage", null, "LFO phase"},
	// pitch change
	{"Pitch", "Init delay", "Fine1", "Fine2", "FB level", null, null, null, null, "dry/wet", "Pan1", "Out level1", "Pan2", "Out level2"},
	// ens detune
	{"Detune", "InitDelayL", "InitDelayR", null, null, null, null, null, null, "dry/wet", "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// rotary SP
	{"LFO Freq", "LFO Depth", null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "EQ MidFreq", "EQ MidGain", "EQ Mid Q"},
	// 2 way rotary
	{"Rotor Spd", "Drive Low", "Drive High", "Low/High", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null,  "CrossFreq", "Mic Angle"},
	// tremolo
	{"LFO Freq", "AM depth", "PM depth", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", "LFO phase", "Mode"},
	// auto pan
	{"LFO Freq", "L/R depth", "F/R depth", "Pan dir", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q"},
	// ambience
	{"Delay Time", "Phase", null, null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet"},
	// a wah + dist
	{"LFO Freq", "LFO Depth", "Cutoff", "Resonance", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Drive", "DS LowGain", "DS MidGain", "LPFCutoff", "OutputLevel"},
	// a wah + overdrive
	{"LFO Freq", "LFO Depth", "Cutoff Fr", "Resonance", null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Drive", "DS LowGain", "DS MidGain", "LPFCutoff", "OutputLevel"},
	// t wah + dist
	{"Sensitivity", "Cutoff", "Resonance", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Drive", "DS LowGain", "DS MidGain", "LPFCutoff", "OutputLevel"},
	// t wah + overdrive
	{"Sensitivity", "Cutoff", "Resonance", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain", "dry/wet", "Drive", "DS LowGain", "DS MidGain", "LPFCutoff", "OutputLevel"},
	// wah + ds + delay
	{"Delay", "FB level", "Delay Mix", "Drive", "Output level", "DS LowGain", "DS MidGain", null, null, "dry/wet", "Sensitivity", "Cutoff", "Resonance", "Release"},
	// wah + od + delay
	{"Delay", "FB level", "Delay Mix", "Drive", "Output level", "DS LowGain", "DS MidGain", null, null, "dry/wet", "Sensitivity", "Cutoff", "Resonance", "Release"},
	// lofi
	{"SampleFreq", "Word Len", "OutputGain", "LPFCutoff", "Filter", "LPFReso", "Bit assign", "Emphasis", null, "dry/wet"},
	// 3-band EQ
	{"Low Gain", "Mid Freq", "Mid Gain", "Mid Q", "High Gain", "Low Freq", "High Freq", null, null, null, null, null, null, null, "Mode"},
	// HM enhencer
	{"HPFCutoff", "Drive", "Mix level"},
	// noise gate
	{"Attack", "Release", "Threshold", "OutputLevel"},
	// compressor
	{"Attack", "Release", "Threshold", "Ratio", "OutputLevel"},
	// comp + dist
	{"Drive", "EQ LowFreq", "EQ LowGain", "LPFCutoff", "OutputLevel", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", "dry/wet", "Edge", "Attack", "Release", "Threshold", "Ratio"},
	// comp + dist + delay
	{"Delay", "FB level", "Delay mix", "Drive", "OutputLevel", "DS LowGain", "DS MidGain", null, null, "dry/wet", "Attack", "Release", "Threshold", "Ratio"},
	// comp + over + delay
	{"Delay", "FB level", "Delay mix", "Drive", "OutputLevel", "DS LowGain", "DS MidGain", null, null, "dry/wet", "Attack", "Release", "Threshold", "Ratio"},
	// distortion
	{"Drive", "EQ LowFreq", "EQ LowGain", "LPFCutoff", "OutputLevel", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", "dry/wet", "Edge"},
	// dist + delay
	{"LchDelay", "RchDelay", "FB delay", "FB level", "Delay mix", "Drive", null, "DS LowGain", "DS MidGain", "dry/wet"},
	// overdrive
	{"Drive", "EQ LowFreq", "EQ LowGain", "LPFCutoff", "OutputLevel", null, "EQ MidFreq", "EQ MidGain", "EQ Mid Q", "dry/wet", "Edge"},
	// over + delay
	{"LchDelay", "RchDelay", "FB delay", "FB level", "Delay mix", "Drive", null, "DS LowGain", "DS MidGain", "dry/wet"},
	// amp sim
	{"Drive", "Amp type", "LPFCutoff", "OutputLevel", null, null, null, null, null, "dry/wet", "Edge"},
	// delay LCR
	{"LchDelay", "RchDelay", "CchDelay", "FB Delay", "FB level", "Cch level", "High Damp", null, null, "dry/wet", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// delay L,R
	{"LchDelay", "RchDelay", "FBDelay1", "DBDelay2", "FB level", "High Damp", null, null, null, "dry/wet", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// echo
	{"LchDelay1", "Lch FB Lev", "RchDelay1", "Rch FB Lev", "High Damp", "LchDelay2", "RchDelay2", "Delay2 Lev", null, "dry/wet", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// crossdelay
	{"L>E delay", "R>L delay", "FB level", "InputSelect", "High Damp", null, null, null, null, "dry/wet", null, null, "EQ LowFreq", "EQ LowGain", "EQ HiFreq", "EQ HiGain"},
	// ER1
	{"Early type", "Room size", "Diffusion", "InitDelay", "FB level", "HPFCutoff", "LPFCutoff", null, null, "dry/wet", "Liveness", "Density"},
	// ER2
	{"Early type", "Room size", "Diffusion", "InitDelay", "FB level", "HPFCutoff", "LPFCutoff", null, null, "dry/wet", "Liveness", "Density"},
	// Gate rev
	{"Gate type", "Room size", "Diffusion", "InitDelay", "FB level", "HPFCutoff", "LPFCutoff", null, null, "dry/wet", "Liveness", "Density"},
	// reverse gate
	{"Gate type", "Room size", "Diffusion", "InitDelay", "FB level", "HPFCutoff", "LPFCutoff", null, null, "dry/wet", "Liveness", "Density"},
	};
	
	static int mInsertionParams[][][] = {
	{},
	// default, min, max
	{// chorus
	{6, 0, 0x7F}, {0x3E, 0, 127}, {0x4F, 1, 0x7F}, {0x50, 0, 0x7F}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x33, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x40, 1, 0x7F}, {0x27, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {10, 10, 0x78}, {}, {1, 0, 1}
	},
	{// celeste
	{0x12, 0, 0x7F}, {0x1C, 0, 127}, {0x40, 1, 0x7F}, {10, 0, 0x7F}, {}, {0xF, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x33, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x7F, 1, 0x7F}, {0x28, 0xE, 0x36}, {0x3E, 0x34, 0x4C}, {10, 10, 0x78}, {}, {1, 0, 1}
	},
	{// flanger
	{11, 0, 0x7F}, {0x1E, 0, 127}, {0x68, 1, 0x7F}, {2, 0, 0x7F}, {}, {0x14, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x60, 1, 0x7F}, {0x27, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {10, 10, 0x78}, {4, 4, 0x7C}
	},
	{// symphonic
	{10, 0, 0x7F}, {0x28, 0, 127}, {0, 0, 0x7F}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x60, 1, 0x7F}, {0x2E, 0xE, 0x36}, {0x3D, 0x34, 0x4C}, {10, 10, 0x78} 
	},
	{// phaser1
	{0xE, 0, 0x7F}, {0x5C, 0, 0x7F}, {0x4C, 0, 0x7F}, {0x64, 1, 127}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x28, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x62, 1, 0x7F}, {6, 4, 10}, {1, 0, 1}
	},
	{// phaser2
	{2, 0, 0x7F}, {0x7F, 0, 0x7F}, {0x19, 0, 0x7F}, {0x73, 1, 127}, {}, {0x14, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x31, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x60, 1, 0x7F}, {5, 3, 5}, {}, {4, 4, 0x7C}
	},
	{// pitch change
	{0x40, 0x28, 0x58}, {1, 0, 0x7F}, {0x4F, 0xE, 0x72}, {0x30, 0xE, 0x72}, {0x40, 1, 0x7F}, {}, {}, {}, {}, {0x3D, 1, 127}, {1, 1, 0x7F}, {0x7D, 0, 0x7F}, {0x7F, 1, 0x7F}, {0x7F, 0, 0x7F}
	},
	{// ens detune
	{0x22, 0xE, 0x72}, {10, 0, 0x7F}, {0x1E, 0, 0x7F}, {}, {}, {}, {}, {}, {}, {0x40, 1, 0x7F}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// rotary SP
	{0x33, 0, 0x7F}, {0x4C, 0, 127}, {}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x40, 1, 0x7F}, {0x2B, 0xE, 0x36}, {0x34, 0x34, 0x4C}, {0x18, 10, 0x78} 
	},
	{// 2 way rotary
	{0x57, 0, 0x7F}, {0x59, 0, 127}, {62, 0, 127}, {0x11, 1, 0x7F}, {}, {15, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2C, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {0x19, 0xE, 0x36}, {0x1F, 0, 0x3C} 
	},
	{// tremolo
	{0x28, 0, 127}, {0x70, 0, 127}, {0, 0, 127}, {}, {}, {0xE, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x33, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {0x27, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {10, 10, 0x78}, {0x40, 4, 0x7C}, {1, 0, 1}
	},
	{// auto pan
	{0x29, 0, 127}, {0x50, 0, 127}, {0x20, 0, 127}, {5, 0, 5}, {}, {0x11, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {0x27, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {10, 10, 0x78}
	},
	{// ambiance
	{0x70, 0, 0x7F}, {1, 0, 1}, {}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x36, 0x34, 0x4C}, {0x4A, 1, 0x7F}
	},
	{// a wah + dist
	{0x20, 0, 0x7F}, {0x54, 0, 127}, {0x2E, 0, 127}, {0x22, 10, 0x78}, {}, {0x16, 4, 0x28}, {0x42, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x7F, 1, 0x7F}, {0x3C, 0, 0x7F}, {0x48, 0x34, 0x4C}, {0x44, 0x34, 0x4C}, {0x34, 0x22, 0x3C}, {0x40, 0, 0x7F}
	},
	{// a wah + overdr
	{0x19, 0, 0x7F}, {0x40, 0, 127}, {0x20, 0, 127}, {0x17, 10, 0x78}, {}, {0x16, 4, 0x28}, {0x42, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x7F, 1, 0x7F}, {0x10, 0, 0x7F}, {0x44, 0x34, 0x4C}, {0x48, 0x34, 0x4C}, {0x2D, 0x22, 0x3C}, {0x44, 0, 0x7F}
	},
	{// t wah + dist
	{0x50, 0, 127}, {0x12, 0, 127}, {0x2D, 10, 0x78}, {}, {}, {0x16, 4, 0x28}, {0x42, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x7F, 1, 0x7F}, {0x1E, 0, 0x7F}, {0x48, 0x34, 0x4C}, {0x4A, 0x34, 0x4C}, {0x35, 0x22, 0x3C}, {0x48, 0, 0x7F}
	},
	{// t wah + over
	{0x3D, 0, 127}, {0x1E, 0, 127}, {0x29, 10, 0x78}, {}, {}, {0x16, 4, 0x28}, {0x42, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {0x7F, 1, 0x7F}, {0xF, 0, 0x7F}, {0x44, 0x34, 0x4C}, {0x48, 0x34, 0x4C}, {0x31, 0x22, 0x3C}, {0x48, 0, 0x7F}
	},
	{// wah + ds + delay
	{0x0E6C, 1, 0x6A52}, {0x54, 1, 0x7F}, {0x1E, 0, 0x7F}, {0x3C, 0, 0x7F}, {0x35, 0, 127}, {0x44, 0x34, 0x4C}, {0x48, 0x34, 0x4C}, {}, {}, {0x7F, 1, 0x7F}, {0x66, 0, 127}, {0x14, 0, 127}, {0x17, 10, 0x78}, {0x3B, 0x34, 0x43}
	},
	{// wah + over + delay
	{0x0C40, 1, 0x6A52}, {0x54, 1, 0x7F}, {0x32, 0, 0x7F}, {0x10, 0, 0x7F}, {0x57, 0, 127}, {0x40, 0x34, 0x4C}, {0x40, 0x34, 0x4C}, {}, {}, {0x7F, 1, 0x7F}, {0x50, 0, 127}, {0x23, 0, 127}, {0x1E, 10, 0x78}, {0x40, 0x34, 0x43}
	},
	{// lofi
	{2, 0, 0x7F}, {1, 1, 127},  {3, 0, 0x2A}, {0x3C, 10, 0x3C}, {4, 0, 5}, {0x1D, 10, 0x78}, {1, 0, 6}, {0, 0, 1}, {}, {0x7F, 1, 0x7F}
	},
	{// 3 band EQ
	{0x40, 0x34, 0x4C}, {0x22, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {0x32, 10, 0x78}, {0x40, 0x34, 0x4C}, {0x13, 8, 0x28}, {0x34, 0x1C, 0x3A}, {}, {}, {}, {}, {}, {}, {}, {0, 0, 1}
	},
	{// HM enhencer
	{0x31, 0x1C, 0x3A}, {0x14, 0, 0x7F}, {0x1E, 0, 127}
	},
	{// noise gate
	{0, 0, 0x13}, {11, 0, 15}, {0x52, 0x37, 0x61}, {0x32, 0, 127}
	},
	{// compressor
	{10, 0, 0x13}, {2, 0, 15}, {0x63, 0x4F, 0x79}, {4, 0, 7}, {0x50, 0, 127}
	},
	{// comp + dist
	{0x3C, 0, 0x7F}, {0x16, 4, 0x28}, {0x45, 0x34, 0x4C}, {0x35, 0x22, 0x3C}, {0x46, 0, 0x7F}, {}, {0x2E, 0xE, 0x36}, {0x48, 0x34, 0x4C}, {10, 10, 0x78}, {0x7F, 1, 0x7F}, {0x78, 0, 127}, {6, 0, 0x13}, {2, 0, 15}, {0x64, 0x4F, 0x79}, {5, 0, 7}
	},
	{// comp + ds + delay
	{0x0E6C, 1, 0x6A52}, {0x48, 1, 127}, {0x26, 0, 127}, {0x3C, 0, 127}, {0x33, 0, 127}, {0x44, 0x34, 0x4C}, {0x48, 0x34, 0x4C}, {}, {}, {0x7F, 1, 127}, {6, 0, 0x13}, {11, 0, 15}, {0x5F, 0x4F, 0x79}, {5, 0, 7}
	},
	{// comp + over + delay
	{0x0E6C, 1, 0x6A52}, {0x4A, 1, 127}, {0x32, 0, 127}, {0x12, 0, 127}, {0x41, 0, 127}, {0x44, 0x34, 0x4C}, {0x45, 0x34, 0x4C}, {}, {}, {0x7F, 1, 127}, {6, 0, 0x13}, {2, 0, 15}, {0x5F, 0x4F, 0x79}, {4, 0, 7}
	},
	{// distortion
	{0x3C, 0, 127}, {0x13, 4, 0x28}, {0x48, 0x34, 0x4C}, {0x35, 0x22, 0x3C}, {65, 0, 127}, {}, {0x23, 0xE, 0x36}, {0x4A, 0x34, 0x4C}, {10, 10, 0x78}, {0x7F, 1, 127}, {80, 0, 127}
	},
	{// dist + delay
	{0x0374, 1, 0x6A52}, {0x768, 1, 0x6A52}, {0xF50, 1, 0x6A52}, {0x54, 1, 127}, {80, 0, 127}, {60, 0, 127}, {42, 0, 127}, {0x48, 0x34, 0x4C}, {0x4A, 0x34, 0x4C}, {0x7F, 1, 127}
	},
	{// overdrive
	{0x1D, 0, 127}, {0x18, 4, 0x28}, {0x44, 0x34, 0x4C}, {0x2E, 0x22, 0x3C}, {80, 0, 127}, {}, {0x24, 0xE, 0x36}, {0x48, 0x34, 0x4C}, {10, 10, 0x78}, {0x7F, 1, 127}, {104, 0, 127}
	},
	{// over + delay
	{0x0374, 1, 0x6A52}, {0x768, 1, 0x6A52}, {0xF50, 1, 0x6A52}, {0x54, 1, 127}, {64, 0, 127}, {25, 0, 127}, {55, 0, 127}, {0x44, 0x34, 0x4C}, {0x48, 0x34, 0x4C}, {0x7F, 1, 127}
	},
	{// amp sim
	{76, 0, 127}, {3, 0, 3}, {0x2A, 0x22, 0x3C}, {80, 0, 127}, {}, {}, {}, {}, {}, {0x7F, 1, 127}, {102, 0, 127}
	},
	{// delay LCR
	{0x1A05, 1, 0x6A52}, {0x0D03, 1, 0x6A52}, {0x2708, 1, 0x6A52}, {0x2708, 1, 0x6A52}, {0x4A, 1, 127}, {100, 0, 127}, {3, 1, 10}, {}, {}, {0x20, 1, 127}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// delay L,R
	{0x1344, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x1D28, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x57, 1, 127}, {3, 1, 10}, {}, {}, {}, {0x20, 1, 127}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// echo
	{0x1118, 1, 0x3524}, {0x56, 1, 127}, {0x1034, 1, 0x3524}, {0x55, 1, 127}, {5, 1, 10}, {0x117C, 1, 0x3524}, {0x122E, 1, 0x3524}, {62, 0, 127}, {}, {0x20, 1, 127}, {}, {}, {0x17, 4, 0x28}, {0x3A, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3F, 0x34, 0x4C}
	},
	{// cross delay
	{0x1C42, 1, 0x3524}, {0x1C42, 1, 0x3524}, {0x58, 1, 127}, {1, 0, 2}, {5, 1, 10}, {}, {}, {}, {}, {0x20, 1, 127}, {}, {}, {0x19, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3E, 0x34, 0x4C}
	},
	{// ER1
	{0, 0, 5}, {0x19, 0, 127}, {5, 0, 10}, {6, 0, 127}, {0x4A, 1, 127}, {0, 0, 0x34}, {0x2E, 0x22, 0x3C}, {}, {}, {0x1D, 1, 127}, {7, 0, 10}, {1, 0, 3}, {10, 1, 10}
	},
	{// ER2
	{2, 0, 5}, {0xC, 0, 127}, {8, 0, 10}, {4, 0, 127}, {0x43, 1, 127}, {0, 0, 0x34}, {0x38, 0x22, 0x3C}, {}, {}, {0x1D, 1, 127}, {5, 0, 10}, {3, 0, 3}, {10, 1, 10}
	},
	{// gate rev
	{0, 0, 1}, {6, 0, 127}, {10, 0, 10}, {0, 0, 127}, {0x40, 1, 127}, {0, 0, 0x34}, {0x34, 0x22, 0x3C}, {}, {}, {0x34, 1, 127}, {5, 0, 10}, {3, 0, 3}, {5, 1, 10}
	},
	{// reverse gate
	{1, 0, 1}, {15, 0, 127}, {8, 0, 10}, {3, 0, 127}, {0x40, 1, 127}, {0, 0, 0x34}, {0x2F, 0x22, 0x3C}, {}, {}, {127, 1, 127}, {6, 0, 10}, {3, 0, 3}, {10, 1, 10}
	},
	};
	
	static final int DEFAULT = 0;
	static final int MIN = 1;
	static final int MAX = 2;
	static final int BASE = 3;	// TODO
	
	static int mReverbParams[][][] = {
	{},
	// default, min, max TODO ajouter la base
	{// hall1
	{0x11, 0, 0x45}, {0xA, 0, 10}, {9, 0, 0x7F}, {0x18, 0, 0x34}, {0x31, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0x22, 0, 0x3F}, {4, 0, 4}, {0x45, 1, 0x7F}, {10, 1, 10}, {0x40, 1, 0x7F}
	},
	{// hall2
	{0x1B, 0, 0x45}, {0xA, 0, 10}, {0x1C, 0, 0x7F}, {6, 0, 0x34}, {0x2E, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0x1C, 0, 0x3F}, {3, 0, 4}, {0x64, 1, 0x7F}, {10, 1, 10}, {0x40, 1, 0x7F}
	},
	{// room1
	{0xB, 0, 0x45}, {8, 0, 10}, {0xC, 0, 0x7F}, {0x19, 0, 0x34}, {0x35, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0xF, 0, 0x3F}, {4, 0, 4}, {0x4A, 1, 0x7F}, {10, 1, 10}, {0x40, 1, 0x7F}
	},
	{// room2
	{9, 0, 0x45}, {0xA, 0, 10}, {8, 0, 0x7F}, {0xF, 0, 0x34}, {0x3C, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0xB, 0, 0x3F}, {4, 0, 4}, {0x2E, 1, 0x7F}, {7, 1, 10}, {0x40, 1, 0x7F}
	},
	{// room3
	{6, 0, 0x45}, {10, 0, 10}, {0, 0, 0x7F}, {0xF, 0, 0x34}, {0x2E, 0x22, 0x3C}, {}, {}, {}, {}, {}, {9, 0, 0x3F}, {4, 0, 4}, {0x31, 1, 0x7F}, {5, 1, 10}, {0x40, 1, 0x7F}
	},
	{// stage1
	{0xC, 0, 0x45}, {10, 0, 10}, {0x10, 0, 0x7F}, {7, 0, 0x34}, {0x33, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0x1D, 0, 0x3F}, {3, 0, 4}, {0x3D, 1, 0x7F}, {5, 1, 10}, {0x4F, 1, 0x7F}
	},
	{// stage2
	{7, 0, 0x45}, {10, 0, 10}, {0, 0, 0x7F}, {0, 0, 0x34}, {0x38, 0x22, 0x3C}, {}, {}, {}, {}, {}, {0x1D, 0, 0x3F}, {4, 0, 4}, {0x2E, 1, 0x7F}, {6, 1, 10}, {0x49, 1, 0x7F}
	},
	{// plate
	{0xF, 0, 0x45}, {5, 0, 10}, {7, 0, 0x7F}, {6, 0, 0x34}, {0x36, 0x22, 0x3C}, {}, {}, {}, {}, {}, {2, 0, 0x3F}, {3, 0, 4}, {0x40, 1, 0x7F}, {7, 1, 10}, {0x54, 1, 0x7F}
	},
	{// white room
	{1, 0, 0x45}, {5, 0, 10}, {0, 0, 0x7F}, {0xB, 0, 0x34}, {0x26, 0x22, 0x3C}, {0x10, 0, 0x25}, {0x49, 0, 0x49}, {0x68, 0, 0x68}, {6, 0, 0x1E}, {}, {8, 0, 0x3F}, {4, 0, 4}, {0x40, 1, 0x7F}, {4, 1, 10}, {0x45, 1, 0x7F}
	},
	{// tunnel
	{0x14, 0, 0x45}, {6, 0, 10}, {10, 0, 0x7F}, {0, 0, 0x34}, {0x2C, 0x22, 0x3C}, {0x21, 0, 0x25}, {0x34, 0, 0x49}, {0x46, 0, 0x68}, {10, 0, 0x1E}, {}, {0x14, 0, 0x3F}, {4, 0, 4}, {0x36, 1, 0x7F}, {10, 1, 10}, {0x6B, 1, 0x7F}
	},
	{// basement
	{5, 0, 0x45}, {6, 0, 10}, {3, 0, 0x7F}, {0, 0, 0x34}, {0x22, 0x22, 0x3C}, {0x1A, 0, 0x25}, {0, 0, 0x49}, {0x25, 0, 0x68}, {0xF, 0, 0x1E}, {}, {0x20, 0, 0x3F}, {3, 0, 4}, {0x4A, 1, 0x7F}, {0xA, 1, 10}, {0x24, 1, 0x7F}
	},
	{// canyon
	{0x3B, 0, 0x45}, {6, 0, 10}, {0x3F, 0, 0x7F}, {0, 0, 0x34}, {0x2D, 0x22, 0x3C}, {0x22, 0, 0x25}, {0x3E, 0, 0x49}, {0x5B, 0, 0x68}, {0xD, 0, 0x1E}, {}, {0xB, 0, 0x3F}, {4, 0, 4}, {0x48, 1, 0x7F}, {4, 1, 10}, {0x64, 1, 0x7F}
	},
	{// delay LCR
	{0x1A05, 1, 0x6A52}, {0x0D03, 1, 0x6A52}, {0x2708, 1, 0x6A52}, {0x2708, 1, 0x6A52}, {0x4A, 1, 0x7F}, {0x64, 0, 127}, {3, 1, 10}, {}, {}, {}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// delay L,R
	{0x1344, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x1D28, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x57, 1, 0x7F}, {3, 1, 10}, {}, {}, {}, {}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// echo
	{0x1118, 1, 0x3524}, {0x56, 1, 0x7F}, {0x1034, 1, 0x3524}, {0x55, 1, 0x7F}, {5, 1, 10}, {0x117C, 1, 0x3524}, {0x122E, 1, 0x3524}, {0x3E, 0, 127}, {}, {}, {}, {}, {0x17, 4, 0x28}, {0x3A, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3F, 0x34, 0x4C}
	},
	{// cross delay
	{0x1C42, 1, 0x3524}, {0x1C42, 1, 0x3524}, {0x58, 1, 0x7F}, {1, 0, 2}, {5, 1, 10}, {}, {}, {}, {}, {}, {}, {}, {0x19, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3E, 0x34, 0x4C}
	},
	};
	
	static int mVariationParams[][][] = {
	{},
	{// chorus
	{5, 0, 0x7F}, {0x2E, 0, 127}, {0x5C, 1, 0x7F}, {10, 0, 0x7F}, {}, {0x14, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {}, {}, {}, {}, {1, 0, 1}
	},
	{// celeste
	{0xF, 0, 0x7F}, {0x19, 0, 127}, {0x5E, 1, 0x7F}, {0x66, 0, 0x7F}, {}, {0x1C, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {}, {}, {}, {}, {1, 0, 1}
	},
	{// flanger
	{11, 0, 0x7F}, {0x1E, 0, 127}, {0x68, 1, 0x7F}, {2, 0, 0x7F}, {}, {0x14, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {}, {}, {}, {4, 4, 0x7C}
	},
	{// symphonic
	{10, 0, 0x7F}, {0x28, 0, 127}, {0, 0, 0x7F}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C} 
	},
	{// phaser1
	{0x14, 0, 0x7F}, {0x6F, 0, 0x7F}, {0x4C, 0, 0x7F}, {0x73, 1, 127}, {}, {0x17, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {6, 4, 10}, {1, 0, 1}
	},
	{// phaser2
	{2, 0, 0x7F}, {0x7F, 0, 0x7F}, {0x19, 0, 0x7F}, {0x73, 1, 127}, {}, {0x14, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x31, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {5, 3, 5}, {}, {4, 4, 0x7C}
	},
	{// ens detune
	{0x22, 0xE, 0x72}, {10, 0, 0x7F}, {0x1E, 0, 0x7F}, {}, {}, {}, {}, {}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// rotary SP
	{0x33, 0, 0x7F}, {0x4C, 0, 127}, {}, {}, {}, {0x16, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// tremolo
	{0x54, 0, 127}, {0x3C, 0, 127}, {0x14, 0, 127}, {}, {0x17, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x28, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}, {}, {}, {}, {}, {0x40, 4, 0x7C}, {0, 0, 1}
	},
	{// auto pan
	{0x4C, 0, 127}, {0x7F, 0, 127}, {0x20, 0, 127}, {5, 0, 5}, {}, {0x15, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x4C, 0x34, 0x4C}
	},
	{// auto wah
	{0x1C, 0, 127}, {0x42, 0, 127}, {0x21, 0, 127}, {0x26, 0xA, 0x78}, {}, {0x17, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// touch wah
	{0x2E, 0, 127}, {0x1C, 0, 127}, {0x17, 0xA, 0x78}, {}, {}, {0x13, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x30, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// 3 band EQ
	{0x40, 0x34, 0x4C}, {0x22, 0xE, 0x36}, {0x40, 0x34, 0x4C}, {0x32, 0xA, 0x78}, {0x40, 0x34, 0x4C}, {0x13, 8, 0x28}, {0x34, 0x1C, 0x3A}, {}, {}, {}, {}, {}, {}, {}, {0, 0, 1}
	},
	{// HM enhencer
	{0x31, 0x1C, 0x3A}, {0x14, 0, 127}, {0x1E, 0, 127}
	},
	{// noise gate
	{0, 0, 40}, {11, 0, 0xF}, {0x52, 0x37, 0x61}, {0x32, 0, 127}
	},
	{// compressor
	{10, 0, 40}, {2, 0, 0xF}, {0x63, 0x4F, 0x79}, {4, 0, 7}, {0x50, 0, 127}
	},
	{// distortion
	{0x3C, 0, 127}, {0x13, 4, 0x28}, {0x48, 0x34, 0x4C}, {0x35, 0x22, 0x3C}, {0x30, 0, 127}, {}, {0x23, 0xE, 0x36}, {0x4A, 0x34, 0x4C}, {10, 0xA, 0x78}, {}, {0x50, 0, 127}
	},
	{// overdrive
	{0x1D, 0, 127}, {0x18, 4, 0x28}, {0x44, 0x34, 0x4C}, {0x2E, 0x22, 0x3C}, {0x37, 0, 127}, {}, {0x24, 0xE, 0x36}, {0x48, 0x34, 0x4C}, {10, 0xA, 0x78}, {}, {0x68, 0, 127}
	},
	{// amp sim
	{0x4C, 0, 127}, {3, 0, 3}, {0x2A, 0x22, 0x3C}, {0x37, 0, 127}, {}, {}, {}, {}, {}, {}, {0x66, 0, 127}
	},
	{// delay LCR
	{0x1A05, 1, 0x3524}, {0x0D03, 1, 0x3524}, {0x2708, 1, 0x3524}, {0x2708, 1, 0x3524}, {0x4A, 1, 127}, {0x64, 0, 127}, {3, 1, 10}, {}, {}, {}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// delay L,R
	{0x1344, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x1D28, 1, 0x6A52}, {0x1D26, 1, 0x6A52}, {0x57, 1, 0x7F}, {3, 1, 10}, {}, {}, {}, {}, {}, {}, {0x1A, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x2E, 0x1C, 0x3A}, {0x40, 0x34, 0x4C}
	},
	{// echo
	{0x1118, 1, 0x3524}, {0x56, 1, 0x7F}, {0x1034, 1, 0x3524}, {0x55, 1, 0x7F}, {5, 1, 10}, {0x117C, 1, 0x3524}, {0x122E, 1, 0x3524}, {0x3E, 0, 127}, {}, {}, {}, {}, {0x17, 4, 0x28}, {0x3A, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3F, 0x34, 0x4C}
	},
	{// cross delay
	{0x1C42, 1, 0x3524}, {0x1C42, 1, 0x3524}, {0x58, 1, 0x7F}, {1, 0, 2}, {5, 1, 10}, {}, {}, {}, {}, {}, {}, {}, {0x19, 4, 0x28}, {0x40, 0x34, 0x4C}, {0x32, 0x1C, 0x3A}, {0x3E, 0x34, 0x4C}
	},
	{// karaoke
	{0x3F, 0, 0x7F}, {0x61, 1, 127}, {0, 0, 0x34}, {0x30, 0x22, 0x3C}
	},
	{// hall
	{0x12, 0, 0x45}, {0xA, 0, 10}, {8, 0, 0x7F}, {0xD, 0, 0x34}, {0x31, 0x22, 0x3C}, {}, {}, {}, {}, {}, {}, {2, 0, 4}, {0x32, 1, 0x7F}, {8, 1, 10}, {0x40, 1, 0x7F}
	},
	{// room
	{5, 0, 0x45}, {0xA, 0, 10}, {0x10, 0, 0x7F}, {4, 0, 0x34}, {0x31, 0x22, 0x3C}, {}, {}, {}, {}, {}, {}, {2, 0, 4}, {0x40, 1, 0x7F}, {8, 1, 10}, {0x40, 1, 0x7F}
	},
	{// stage
	{0x13, 0, 0x45}, {0xA, 0, 10}, {0x10, 0, 0x7F}, {7, 0, 0x34}, {0x36, 0x22, 0x3C}, {}, {}, {}, {}, {}, {}, {2, 0, 4}, {0x40, 1, 0x7F}, {6, 1, 10}, {0x40, 1, 0x7F}
	},
	{// plate
	{0x19, 0, 0x45}, {0xA, 0, 10}, {6, 0, 0x7F}, {8, 0, 0x34}, {0x31, 0x22, 0x3C}, {}, {}, {}, {}, {}, {}, {2, 0, 4}, {0x40, 1, 0x7F}, {5, 1, 10}, {0x40, 1, 0x7F}
	},
	};
	
	private KnobWidget mReverbWidgets[] = new KnobWidget[16];
	private KnobWidget mVariationWidgets[] = new KnobWidget[16];
	private KnobWidget mInsertionWidgets[] = new KnobWidget[16];
	
	EffectsWindow(Patch aPatch)
	{
		p = aPatch;
	}

	/** Effects parameters */
	Container buildEffectsWindow() 
	{
		JTabbedPane oPane = new JTabbedPane();
	
		// EQ
		Box oPanel4 = Box.createVerticalBox();
		JPanel oEQPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oEQPanel1.add(new KnobWidget("Low gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x0134), new YamahaFS1RPerformanceDriver.Sender(0x0134)));
		oEQPanel1.add(new KnobLookupWidget("Low freq", p, 4, 0x28, new YamahaFS1RPerformanceDriver.Model(p, 0x0135), new YamahaFS1RPerformanceDriver.Sender(0x0135), mFreqs));
		oEQPanel1.add(new KnobLookupWidget("Low Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x0136), new YamahaFS1RPerformanceDriver.Sender(0x0136), mQs));
		oEQPanel1.add(new ComboBoxWidget("Low Shape", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0137), new YamahaFS1RPerformanceDriver.Sender(0x0137), new String[] {"Shelving", "Peaking"}));
		oPanel4.add(oEQPanel1);

		JPanel oEQPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oEQPanel2.add(new KnobWidget("Mid gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x0138), new YamahaFS1RPerformanceDriver.Sender(0x0138)));
		oEQPanel2.add(new KnobLookupWidget("Mid freq", p, 0x0E, 0x36, new YamahaFS1RPerformanceDriver.Model(p, 0x0139), new YamahaFS1RPerformanceDriver.Sender(0x0139), mFreqs));
		oEQPanel2.add(new KnobLookupWidget("Mid Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x013A), new YamahaFS1RPerformanceDriver.Sender(0x013A), mQs));
		oPanel4.add(oEQPanel2);

		JPanel oEQPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oEQPanel3.add(new KnobWidget("High gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x013B), new YamahaFS1RPerformanceDriver.Sender(0x013B)));
		oEQPanel3.add(new KnobLookupWidget("High freq", p, 0x1C, 0x3A, new YamahaFS1RPerformanceDriver.Model(p, 0x013C), new YamahaFS1RPerformanceDriver.Sender(0x013C), mFreqs));
		oEQPanel3.add(new KnobLookupWidget("High Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x013D), new YamahaFS1RPerformanceDriver.Sender(0x013D), mQs));
		oEQPanel3.add(new ComboBoxWidget("High Shape", p, new YamahaFS1RPerformanceDriver.Model(p, 0x013E), new YamahaFS1RPerformanceDriver.Sender(0x013E), new String[] {"Shelving", "Peaking"}));
		oPanel4.add(oEQPanel3);
		oPane.addTab("Equalizer", oPanel4);
		
		// reverb
		oPane.addTab("Reverberation", getReverb(0x0050));
		
		// variation
		Box oVarPanel = Box.createVerticalBox(); 
		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ParamModel oVariationTypeModel = new YamahaFS1RPerformanceDriver.Model(p, 0x012B);
		ComboBoxWidget oSelVariation = new ComboBoxWidget("", p, oVariationTypeModel, new YamahaFS1RPerformanceDriver.Sender(0x012B), mVariations);
		oSelVariation.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					JComboBox oCB = (JComboBox)e.getSource();
					int oIndex = oCB.getSelectedIndex();
					selectVariationType(oIndex, true);
				}
			}
		});
		oPanel2.add(oSelVariation);
		oPanel2.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x012C), new YamahaFS1RPerformanceDriver.Sender(0x012C)));
		oPanel2.add(new KnobWidget("Return", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012D), new YamahaFS1RPerformanceDriver.Sender(0x012D)));		
		oPanel2.add(new KnobWidget("Send to Reverb", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012E), new YamahaFS1RPerformanceDriver.Sender(0x012E)));		
		oVarPanel.add(oPanel2);
		
		oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		int n = 0;
		int oMin = 0;
		int oMax = 0xFFFF;
		for (n = 0; n < 8; n++)
		{
			if (n < mVariationParams[oVariationTypeModel.get()].length && mVariationParams[oVariationTypeModel.get()][n].length > 0)
			{
				oMin = mVariationParams[oVariationTypeModel.get()][n][MIN];
				oMax = mVariationParams[oVariationTypeModel.get()][n][MAX];
			}
			mVariationWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x68+n*2), new YamahaFS1RPerformanceDriver.Sender(0x68+n*2));
			oPanel2.add((Component)mVariationWidgets[n]);
		}
		oVarPanel.add(oPanel2);
		oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (; n < 12; n++)
		{
			if (n < mVariationParams[oVariationTypeModel.get()].length && mVariationParams[oVariationTypeModel.get()][n].length > 0)
			{
				oMin = mVariationParams[oVariationTypeModel.get()][n][MIN];
				oMax = mVariationParams[oVariationTypeModel.get()][n][MAX];
			}
			mVariationWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x68+n*2), new YamahaFS1RPerformanceDriver.Sender(0x68+n*2));
			oPanel2.add((Component)mVariationWidgets[n]);
		}
		// il y a une rupture a partir de 12 car 0x100 ne suit pas 0x7E et comme
		// le model attend le numero indique dans la doc...
		for (; n < mVariationWidgets.length; n++)
		{
			if (n < mVariationParams[oVariationTypeModel.get()].length && mVariationParams[oVariationTypeModel.get()][n].length > 0)
			{
				oMin = mVariationParams[oVariationTypeModel.get()][n][MIN];
				oMax = mVariationParams[oVariationTypeModel.get()][n][MAX];
			}
			mVariationWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x100+(n-12)*2), new YamahaFS1RPerformanceDriver.Sender(0x100+(n-12)*2));
			oPanel2.add((Component)mVariationWidgets[n]);
		}
		oVarPanel.add(oPanel2);
		oPane.addTab("Variation", oVarPanel);
		selectVariationType(oSelVariation.getValue(), false);
		
		// insertion
		Box oInsPanel = Box.createVerticalBox(); 
		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ParamModel oInsertionTypeModel = new YamahaFS1RPerformanceDriver.Model(p, 0x012F);
		ComboBoxWidget oSelInsertion = new ComboBoxWidget("", p, oInsertionTypeModel, new YamahaFS1RPerformanceDriver.Sender(0x012F), mInsertions);
		oSelInsertion.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					JComboBox oCB = (JComboBox)e.getSource();
					int oIndex = oCB.getSelectedIndex();
					selectInsertionType(oIndex, true);
				}
			}
		});
		oPanel3.add(oSelInsertion);
		oPanel3.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0130), new YamahaFS1RPerformanceDriver.Sender(0x0130)));
		oPanel3.add(new KnobWidget("Send to Reverb", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0131), new YamahaFS1RPerformanceDriver.Sender(0x0131)));		
		oPanel3.add(new KnobWidget("Send to Var", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0132), new YamahaFS1RPerformanceDriver.Sender(0x0132)));		
		oPanel3.add(new KnobWidget("Level", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0133), new YamahaFS1RPerformanceDriver.Sender(0x0133)));	
		oInsPanel.add(oPanel3);	
		
		oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oMin = 0;
		oMax = 0xFFFF;
		for (n = 0; n < 8; n++)
		{
			if (n < mInsertionParams[oInsertionTypeModel.get()].length && mInsertionParams[oInsertionTypeModel.get()][n].length > 0)
			{
				oMin = mInsertionParams[oInsertionTypeModel.get()][n][MIN];
				oMax = mInsertionParams[oInsertionTypeModel.get()][n][MAX];
			}
			mInsertionWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x108+n*2), new YamahaFS1RPerformanceDriver.Sender(0x108+n*2));
			oPanel3.add((Component)mInsertionWidgets[n]);
		}
		oInsPanel.add(oPanel3);
		oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (; n < mInsertionWidgets.length; n++)
		{
			if (n < mInsertionParams[oInsertionTypeModel.get()].length && mInsertionParams[oInsertionTypeModel.get()][n].length > 0)
			{
				oMin = mInsertionParams[oInsertionTypeModel.get()][n][MIN];
				oMax = mInsertionParams[oInsertionTypeModel.get()][n][MAX];
			}
			mInsertionWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x108+n*2), new YamahaFS1RPerformanceDriver.Sender(0x108+n*2));
			oPanel3.add((Component)mInsertionWidgets[n]);
		}
		oInsPanel.add(oPanel3);	
		oPane.addTab("Insertion", oInsPanel);
		selectInsertionType(oInsertionTypeModel.get(), false);
		
		return oPane;
	}

	private void selectInsertionType(int aIndex, boolean aInitDefault)
	{
		for (int i = 0; i < 16; i++)
		{
			if (i < mInsertionNames[aIndex].length && mInsertionNames[aIndex][i] != null)
			{
				mInsertionWidgets[i].setLabel(mInsertionNames[aIndex][i]);
                KnobWidget r = mInsertionWidgets[i];
				r.setMin(mInsertionParams[aIndex][i][MIN]);
                r.setMax(mInsertionParams[aIndex][i][MAX]);
				mInsertionWidgets[i].setEnabled(true);
				if (aInitDefault)
				{
					mInsertionWidgets[i].setValue(mInsertionParams[aIndex][i][DEFAULT]);
				}
			}
			else
			{
				mInsertionWidgets[i].setLabel("");
				mInsertionWidgets[i].setEnabled(false);
			}
		}
	}
	
	private void selectVariationType(int aIndex, boolean aInitDefault)
	{
		for (int i = 0; i < 16; i++)
		{
			if (i < mVariationNames[aIndex].length && mVariationNames[aIndex][i] != null)
			{
				mVariationWidgets[i].setEnabled(true);
				mVariationWidgets[i].setLabel(mVariationNames[aIndex][i]);
                KnobWidget r = mVariationWidgets[i];
				r.setMin(mVariationParams[aIndex][i][MIN]);
                r.setMax(mVariationParams[aIndex][i][MAX]);
				if (aInitDefault)
				{
					mVariationWidgets[i].setValue(mVariationParams[aIndex][i][DEFAULT]);
				}
			}
			else
			{
				mVariationWidgets[i].setLabel("");
				mVariationWidgets[i].setEnabled(false);
			}
		}
	}
	
	/**
		Reaffecte les parametres des boutons.
	*/
	private void selectReverbType(int aIndex, boolean aInitDefault)
	{
		for (int i = 0; i < 16; i++)
		{
			if (i < mReverbNames[aIndex].length && mReverbNames[aIndex][i] != null)
			{
				mReverbWidgets[i].setEnabled(true);
				mReverbWidgets[i].setLabel(mReverbNames[aIndex][i]);
                KnobWidget r = mReverbWidgets[i];
				r.setMin(mReverbParams[aIndex][i][MIN]);
                r.setMax(mReverbParams[aIndex][i][MAX]);
				if (aInitDefault)
				{
					mReverbWidgets[i].setValue(mReverbParams[aIndex][i][DEFAULT]);
				}
			}
			else
			{
				mReverbWidgets[i].setLabel("");
				mReverbWidgets[i].setEnabled(false);
			}
		}
	}

	Container getReverb(int aBaseOffset)
	{
		Box oReverbPanel = Box.createVerticalBox(); 
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ParamModel oReverbTypeModel = new YamahaFS1RPerformanceDriver.Model(p, 0x0128);
		ComboBoxWidget oSelReverb = new ComboBoxWidget("", p, oReverbTypeModel, new YamahaFS1RPerformanceDriver.Sender(0x0128), mReverbs);
		oSelReverb.addEventListener(new ItemListener() {
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					JComboBox oCB = (JComboBox)e.getSource();
					int aIndex = oCB.getSelectedIndex();
					selectReverbType(aIndex, true);
				}
			}
		});
		oPanel1.add(oSelReverb);
		oPanel1.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0129), new YamahaFS1RPerformanceDriver.Sender(0x0129)));
		oPanel1.add(new KnobWidget("Return", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012A), new YamahaFS1RPerformanceDriver.Sender(0x012A)));
		oReverbPanel.add(oPanel1);
		
		oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		int n = 0;
		int oMin = 0;
		int oMax = 0xFFFF;
		for (n = 0; n < 8; n++)
		{
			if (n < mReverbParams[oReverbTypeModel.get()].length && mReverbParams[oReverbTypeModel.get()][n].length > 0)
			{
				oMin = mReverbParams[oReverbTypeModel.get()][n][MIN];
				oMax = mReverbParams[oReverbTypeModel.get()][n][MAX];
			}
			mReverbWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, aBaseOffset+n*2), new YamahaFS1RPerformanceDriver.Sender(aBaseOffset+n*2));
			oPanel1.add((Component)mReverbWidgets[n]);
		}
		oReverbPanel.add(oPanel1);
		oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (; n < mReverbWidgets.length; n++)
		{
			if (n < mReverbParams[oReverbTypeModel.get()].length && mReverbParams[oReverbTypeModel.get()][n].length > 0)
			{
				oMin = mReverbParams[oReverbTypeModel.get()][n][MIN];
				oMax = mReverbParams[oReverbTypeModel.get()][n][MAX];
			}
			mReverbWidgets[n] = new KnobWidget("unused", p, oMin, oMax, 0, new YamahaFS1RPerformanceDriver.Model(p, aBaseOffset+n+8), new YamahaFS1RPerformanceDriver.Sender(aBaseOffset+n+8));
			oPanel1.add((Component)mReverbWidgets[n]);
		}
		oReverbPanel.add(oPanel1);
		// reselectionner la reverb courante
		selectReverbType(oSelReverb.getValue(), false);
		return oReverbPanel;
	}
	

}
