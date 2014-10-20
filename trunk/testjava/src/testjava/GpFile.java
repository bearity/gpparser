package testjava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class GpFile {
	String beatmaskdesc = "";
	String beathex = "";
	float version;

	Field title, subtitle, artist, album, lyricist, music, copyright, tab, instructions, songData;
	Field[] notices;
	Lyrics[] lyrics;
	VolumeEQ volumeEQ;
	PageSetup pageSetup;
	KeyOctaveInfo keyOctaveInfo;
	KeyInfo keyInfo;
	TrackData[] trackData;
	MusicalDirectionsDefinitions musicalDirectionsDefinitions;
	int bpm;
	int uninterestingData;
	int masterReverb;
	int bars;
	int tracks;
	BarChunk[] barChunk;
	TrackChunk[] trackChunk;
	ArrayList<BeatChunk> beatChunkList;
	
	FileInputStream fis;
	
	String encoding = "EUC-KR";
	
	int readCount;
	
	public int readChar() {
		int tmp = -1;
		try {
			tmp = fis.read();
			readCount++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tmp;
	}
	public byte[] readBytes(int characters) {
		byte[] bytes = new byte[characters];
		
		try {
			fis.read(bytes,0,characters);
			readCount+=characters;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	public int readInt(int characters) {
		return byteArrayToInt(readBytes(characters));
	}
	public Field getContent41x() {
		Field tmpField = new Field();
		StringBuilder sb = new StringBuilder();
		
		byte[] byteTmp = readBytes(4);
		
		tmpField.fieldLength = byteArrayToInt(byteTmp);
		tmpField.stringLength = readChar();
		
		byteTmp = readBytes(tmpField.stringLength);
		
		try {
			tmpField.string = new String(byteTmp, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return tmpField;
	}
	
	public void readHeader() {
		int tmpInt = 0, rythmFeel = 0;
		String tmpStr = null;
		
		tmpInt = readChar();
		try {
			tmpStr = new String(readBytes(tmpInt), encoding);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		switch(tmpStr) {
		case Version.GUITAR_PRO_510:
			version = (float)5.1;
			break;
		case Version.GUITAR_PRO_5:
			version = (float)5.0;
			break;
		case Version.GUITAR_PRO_4_06:
			version = (float)4;
			break;
		
		}
		
		readBytes(30-tmpInt);
		
		title = getContent41x();
		subtitle = getContent41x();
		artist = getContent41x();
		album = getContent41x();
		lyricist = new Field();
		if(version >= 5) {
			lyricist = getContent41x();
		}
		music = getContent41x();
		copyright = getContent41x();
		tab = getContent41x();
		instructions = getContent41x();
		tmpInt = readInt(4);
		notices = new Field[tmpInt];

		for(int i=0;i<tmpInt;i++) {
			notices[i] = getContent41x();
		}
		if(version <= 4) {
			rythmFeel = readChar();
		}
		if(version >= 4) {
			lyrics = new Lyrics[5];
			for(int i=0;i<5;i++) {
				lyrics[i] = new Lyrics();
				lyrics[i].associatedTrack = readInt(4);
				lyrics[i].startFrom = readInt(4);
				if(lyrics[i].startFrom != 0) {
					lyrics[i].lyricLength = readInt(4);
					try {
						lyrics[i].string = new String(readBytes(lyrics[i].lyricLength), encoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				else {
				}
			}
		}
		
		// !VOLUME/EQUALIZATION SETTINGS (only if the major file version is > 5)
		if(version > 5) {
			volumeEQ = new VolumeEQ();
			volumeEQ.masterVolume = readInt(4);
			volumeEQ.unknownData = readInt(4);
			volumeEQ._32Hz = readChar();
			volumeEQ._60Hz = readChar();
			volumeEQ._125Hz = readChar();
			volumeEQ._250Hz = readChar();
			volumeEQ._500Hz = readChar();
			volumeEQ._1KHz = readChar();
			volumeEQ._2KHz = readChar();
			volumeEQ._4KHz = readChar();
			volumeEQ._8KHz = readChar();
			volumeEQ._16KHz = readChar();
			volumeEQ.overallGain = readChar();
		}
		
		// !PAGE SETUP (only if the major file version is >= 5):
		if(version >= 5) {
			pageSetup = new PageSetup();
			pageSetup.pageFormatLength = readInt(4);
			pageSetup.pageFormatWidth = readInt(4);
			pageSetup.leftMargin = readInt(4);
			pageSetup.rightMargin = readInt(4);
			pageSetup.topMargin = readInt(4);
			pageSetup.bottomMargin = readInt(4);
			pageSetup.scoreSize = readInt(4);
			pageSetup.enabledHeaderFooterFieldsBitmask = byteArrayToInt(readBytes(2)); 
			pageSetup.titleHeaderFooter = getContent41x();
			pageSetup.subtitleHeaderFooter = getContent41x();
			pageSetup.artistHeaderFooter = getContent41x();
			pageSetup.albumHeaderFooter = getContent41x();
			pageSetup.wordsHeaderFooter = getContent41x();
			pageSetup.musicHeaderFooter = getContent41x();
			pageSetup.wordsNmusicHeaderFooter = getContent41x();
			pageSetup.copyrightHeaderFooter = new Field[2];
			pageSetup.copyrightHeaderFooter[0] = new Field();
			pageSetup.copyrightHeaderFooter[1] = new Field();
			pageSetup.copyrightHeaderFooter[0] = getContent41x();
			pageSetup.copyrightHeaderFooter[1] = getContent41x();
			pageSetup.pageNumberHeaderFooter = getContent41x();
		}
		
		// !Tempo string (only if the file version is >= 5.0):
		if(version >= 5) {
			songData = new Field();
			songData = getContent41x();
		}
		
		bpm = readInt(4);
		
		// !UNINTERESTING DATA/PADDING (if the file version is > 5.0)
		if(version > 5) {
			uninterestingData = readChar();
		}
		
		// !KEY/OCTAVE INFORMATION (if the major file version is >= 4)
		if(version >= 4) {
			keyOctaveInfo = new KeyOctaveInfo();
			keyOctaveInfo.key = readChar();
			
			keyOctaveInfo.unknownData = byteArrayToInt(readBytes(3));
			keyOctaveInfo.octave = readChar();
		}
		
		// !KEY INFORMATION (if the major file version is < 4)
		if(version < 4) {
			keyInfo = new KeyInfo();
			keyInfo.key = readInt(4);
		}
	}
	public void readTrackData() {
		trackData = new TrackData[64];
		for(int i=0;i<64;i++) {
			trackData[i] = new TrackData();
			trackData[i].instrumentPatchNumber = readInt(4);
			trackData[i].volume = readChar();
			trackData[i].pan = readChar();
			trackData[i].chorus = readChar();
			trackData[i].reverb = readChar();
			trackData[i].phaser = readChar();
			trackData[i].tremolo = readChar();
			readBytes(2);
		}
	}
	public void readMusicalDirectionsDefinitions() {
		musicalDirectionsDefinitions = new MusicalDirectionsDefinitions();
		musicalDirectionsDefinitions.coda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.doubleCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.segno = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.segnoSegno= byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.fine = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daCapo = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daCapoAlCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daCapoAlDoubleCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daCapoAlFine = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegno = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoAlCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoAlDoubleCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoAlFine = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoSegno = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoSegnoAlCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoSegnoAlDoubleCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daSegnoSegnoAlFine = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daCoda = byteArrayToInt(readBytes(2));
		musicalDirectionsDefinitions.daDoubleCoda = byteArrayToInt(readBytes(2));
		
	}
	public BarChunk readBarChunk() {
		String barhex = "";
		BarChunk barChunk = new BarChunk();
		
		barChunk.barBitmask = readChar();
		barhex += String.format("BARBIT %02X ",barChunk.barBitmask);
		String barBitmaskString = toBinary(barChunk.barBitmask,8);
		if(version < 3) {
			// end of repeat 1 byte
			// number of alternate ending 1 byte
		}
		if(version >= 3) {
			if(barBitmaskString.charAt(7) == '1') {
				barChunk.tsNumerator = readChar();
				barhex += String.format("TSNUM %02X ",barChunk.tsNumerator);
			}
			if(barBitmaskString.charAt(6) == '1') {
				barChunk.tsDenominator = readChar();
				barhex += String.format("TSDEN %02X ",barChunk.tsDenominator);
			}
			if(barBitmaskString.charAt(4) == '1') {
				barChunk.endOfRepeat = readChar();
				barhex += String.format("EOR %02X ",barChunk.endOfRepeat);
			}
			if(version < 5) {
				// alternative ending 1 byte
				// new section(41x 4)
				// key signature change 1, 1
				System.out.println("UNDER 5");
			}
			if(version >= 5) {
				if(barBitmaskString.charAt(2) == '1') {
					barChunk.sectionName = getContent41x();
					barChunk.sectionNameWith = readBytes(4);
					barhex += String.format("SFLEN %08X ",barChunk.sectionName.fieldLength);
					barhex += String.format("STLEN %02X ",barChunk.sectionName.stringLength);
					barhex += "STR "+barChunk.sectionName.string+" ";
					barhex += String.format("SCOLOR %02X%02X%02X%02X ",barChunk.sectionNameWith[0],barChunk.sectionNameWith[1],barChunk.sectionNameWith[2],barChunk.sectionNameWith[3]);
				}
				if(barBitmaskString.charAt(1) == '1') {
					barChunk.key = readChar();
					barhex += String.format("KEY %02X ",barChunk.key);
					barChunk.isMinor = readChar();
					barhex += String.format("ISMIN %02X ",barChunk.isMinor);
				}
				if(barBitmaskString.charAt(7) == '1' || barBitmaskString.charAt(6) == '1') {
					barChunk.beamEightNotesByValues = readBytes(4);
					barhex += String.format("EBEAM %02X%02X%02X%02X ",barChunk.beamEightNotesByValues[0],barChunk.beamEightNotesByValues[1],barChunk.beamEightNotesByValues[2],barChunk.beamEightNotesByValues[3]);
				}
				if(barBitmaskString.charAt(3) == '1') {
					barChunk.numberOfAlternateEnding = readChar();
					barhex += String.format("NALT %02X ",barChunk.numberOfAlternateEnding);
				}
				else {
					readChar();
					
				}
				barChunk.tripletFeel = readChar();
				barhex += String.format("TRF %02X ",barChunk.tripletFeel);
				barhex += String.format("PAD %02X ",readChar());
				
			}
			
		}
		System.out.println(barhex);
		return barChunk;
	}
	public TrackChunk readTrackChunk() {
		String trackhex = "";
		TrackChunk trackChunk = new TrackChunk();
		
		trackChunk.trackBitmask = readChar();
		trackhex += String.format("TRBIT %02X ",trackChunk.trackBitmask);
		String trackBitmaskString = toBinary(trackChunk.trackBitmask,8);
		trackChunk.trackNameLength = readChar();
		trackhex += String.format("TNLEN %02X ",trackChunk.trackNameLength);
		if(trackChunk.trackNameLength > 0) {
			try {
				trackChunk.trackName = new String(readBytes(trackChunk.trackNameLength), encoding);
				trackhex += "TRNAME "+trackChunk.trackName+" "; 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		readBytes(40-trackChunk.trackNameLength);
		trackChunk.numberOfStrings = readInt(4);
		trackhex += String.format("NOS %08X ",trackChunk.numberOfStrings);
		trackChunk.stringTuningChunk = new int[7];
		for(int j=0;j<7;j++) {
			trackChunk.stringTuningChunk[j] = readInt(4);
			trackhex += String.format("TSC %08X ",trackChunk.stringTuningChunk[j]);
		}
		trackChunk.midiPortUsed = readInt(4);
		trackChunk.midiChannelUsed = readInt(4);
		trackChunk.midiChannelUsedForEffects = readInt(4);
		trackChunk.numberOfFrets = readInt(4);
		trackChunk.capoFret = readInt(4);
		trackChunk.trackColor = readBytes(4);
		if(version > 5) {
			trackChunk.trackSettings = new TrackSettings();
			TrackSettings trackSettings = trackChunk.trackSettings; 
			trackSettings.bitmask1 = readChar();
			trackSettings.bitmask2 = readChar();
			trackSettings.unknown = readChar();
			trackSettings.midiBank = readChar();
			trackSettings.humanPlaying = readChar();
			trackSettings.autoAccentuationOnTheBeat = readChar();
			trackSettings.unknown2 = readBytes(31);
			trackSettings.selectedSoundBankOption = readChar();
			trackSettings.unknown3 = readBytes(7);
			trackSettings.lowFrequency = readChar();
			trackSettings.midFrequency = readChar();
			trackSettings.highFrequency = readChar();
			trackSettings.allFrequencies = readChar();
			trackSettings.instrumentEffect1 = getContent41x();
			trackSettings.instrumentEffect2 = getContent41x();
		}
		if(version == 5) {
			readBytes(45);
		}
		System.out.println(trackhex);
		return trackChunk;
	}
	public ChordDiagram0 readChordDiagram0(int numberOfStrings) {
		ChordDiagram0 chordDiagram0 = new ChordDiagram0();
		chordDiagram0.chordNameString = getContent41x();
		chordDiagram0.beginFret = readInt(4);
		chordDiagram0.pressedFret = new int[numberOfStrings];
		for(int l=0;l<numberOfStrings;l++) {
			chordDiagram0.pressedFret[l] = readInt(4);
		}
		return chordDiagram0;
	}
	public ChordDiagram1 readChordDiagram1() {
		ChordDiagram1 chordDiagram1 = new ChordDiagram1();
		chordDiagram1.displaySharpInsteadFlat = readChar();
		beathex += String.format("CD{DSIF %02X ",chordDiagram1.displaySharpInsteadFlat);
		
		beathex += String.format("PAD %02X%02X%02X ",readChar(),readChar(),readChar());
		chordDiagram1.chordRoot = readChar();
		beathex += String.format("CROOT %02X ",chordDiagram1.chordRoot);
		//chordDiagram1.unkonwn = readBytes(3);
		chordDiagram1.chordType = readChar();
		beathex += String.format("CTYPE %02X ",chordDiagram1.chordType);
		//chordDiagram1.unknown2 = readBytes(3);
		chordDiagram1.option = readChar();
		beathex += String.format("OPT %02X ",chordDiagram1.option);
		//chordDiagram1.unknown3 = readBytes(3);
		chordDiagram1.lowestNotePlayedInTheChord = readInt(4);
		beathex += String.format("LPIC %08X ",chordDiagram1.lowestNotePlayedInTheChord);
		chordDiagram1.plusminusOption = readChar();
		beathex += String.format("PMOP %02X ",chordDiagram1.plusminusOption);
		chordDiagram1.unknown4 = readBytes(4);
		beathex += String.format("UNK %02X%02X%02X%02X ",chordDiagram1.unknown4[0],chordDiagram1.unknown4[1],chordDiagram1.unknown4[2],chordDiagram1.unknown4[3]);
		chordDiagram1.chordNameStringLength = readChar();
		beathex += String.format("CNSLEN %02X ",chordDiagram1.chordNameStringLength);
		try {
			chordDiagram1.chordName = new String(readBytes(chordDiagram1.chordNameStringLength),encoding);
			beathex += "CNAME "+chordDiagram1.chordName+" ";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		readBytes(20-chordDiagram1.chordNameStringLength);
		beathex += String.format("PAD %02X%02X ",readChar(), readChar());
		
		chordDiagram1.tonalityOfTheFifth = readChar();
		beathex += String.format("TOF %02X ",chordDiagram1.tonalityOfTheFifth);
		//readBytes(3);
		chordDiagram1.tonalityOfTheNinth = readChar();
		beathex += String.format("TON %02X ",chordDiagram1.tonalityOfTheNinth);
		//readBytes(3);
		chordDiagram1.tonalityOfTheEleventh = readChar();
		beathex += String.format("TOE %02X ",chordDiagram1.tonalityOfTheEleventh);
		//readBytes(3);
		chordDiagram1.chordDiagramBaseFretPosition = readInt(4);
		beathex += String.format("CDBFP %08X ",chordDiagram1.chordDiagramBaseFretPosition);
		chordDiagram1.fretChunk = new int[7];
		beathex += "\n  FC ";
		for(int l=0;l<7;l++) {
			chordDiagram1.fretChunk[l] = readInt(4);
			beathex += String.format("%08X ",chordDiagram1.fretChunk[l]);
		}
		chordDiagram1.numberOfBarresInTheChord = readChar();
		beathex += String.format("NOB %02X ",chordDiagram1.numberOfBarresInTheChord);
		chordDiagram1.barrPosition = new int[5];
		beathex += "BPOS ";
		for(int l=0;l<5;l++) {
			chordDiagram1.barrPosition[l] = readChar();
			beathex += String.format("%02X ",chordDiagram1.barrPosition[l]);
		}
		chordDiagram1.barrFirstString = new int[5];
		beathex += "BFS ";
		for(int l=0;l<5;l++) {
			chordDiagram1.barrFirstString[l] = readChar();
			beathex += String.format("%02X ",chordDiagram1.barrFirstString[l]);
		}
		chordDiagram1.barrLastString = new int[5];
		beathex += "BLS ";
		for(int l=0;l<5;l++) {
			chordDiagram1.barrLastString[l] = readChar();
			beathex += String.format("%02X ",chordDiagram1.barrLastString[l]);
		}
		chordDiagram1.includeFirstInterval = readChar();
		beathex += String.format("IFI %02X ",chordDiagram1.includeFirstInterval);
		chordDiagram1.includeThirdInterval = readChar();
		beathex += String.format("ITI %02X ",chordDiagram1.includeThirdInterval);
		chordDiagram1.includeFifthInterval = readChar();
		beathex += String.format("IFI %02X ",chordDiagram1.includeFifthInterval);
		chordDiagram1.includeSeventhInterval = readChar();
		beathex += String.format("ISI %02X ",chordDiagram1.includeSeventhInterval);
		chordDiagram1.includeNinthInterval = readChar();
		beathex += String.format("INI %02X ",chordDiagram1.includeNinthInterval);
		chordDiagram1.includeEleventhInterval = readChar();
		beathex += String.format("IEI %02X ",chordDiagram1.includeEleventhInterval);
		chordDiagram1.includeThirteenthInterval = readChar();
		beathex += String.format("ITTI %02X ",chordDiagram1.includeThirteenthInterval);
		beathex += String.format("PAD %02X\n",readChar());
		
		chordDiagram1.fingeringChunk = new int[7];
		beathex += "  FINC ";
		for(int l=0;l<7;l++) {
			chordDiagram1.fingeringChunk[l] = readChar();
			beathex += String.format("%02X ",chordDiagram1.fingeringChunk[l]);
		}
		chordDiagram1.isChordFingeringDisplayed = readChar();
		beathex += String.format("ISCFD %02X ",chordDiagram1.isChordFingeringDisplayed);
		
		beathex += "}\n";
		return chordDiagram1;
	}
	public String getBeatDuration(int beatDuration) {
		switch(beatDuration) {
		case -2:return "WHOLE";
		case -1:return "HALF ";
		case 0:return "QUATER ";
		case 1:return "EIGHTH ";
		case 2:return "SIXTEENTH ";
		case 3:return "THIRTYSECOND ";
		case 4:return "SIXTYFOURTH ";
		default: return "";
		}
	}
	public MixTableChange readMixTableChange() {
		MixTableChange mixTableChange = new MixTableChange();  
		mixTableChange.numberOfNewInstrument = readChar();
		mixTableChange.rseNumber1 = readInt(4);
		mixTableChange.rseNumber2 = readInt(4);
		mixTableChange.rseNumber3 = readInt(4);
		mixTableChange.unknown = readInt(4);
		mixTableChange.newVolume = readChar();
		mixTableChange.newPanValue = readChar();
		mixTableChange.newChorusValue = readChar();
		mixTableChange.newReverbValue = readChar();
		mixTableChange.newPhaserValue = readChar();
		mixTableChange.newTremoloValue = readChar();
		mixTableChange.tempoStringData = getContent41x();
		mixTableChange.newTempo = readInt(4);
		beathex += String.format("  MIX{NOI %02X ",mixTableChange.numberOfNewInstrument);
		beathex += String.format("RSE1 %08X ",mixTableChange.rseNumber1);
		beathex += String.format("RSE2 %08X ",mixTableChange.rseNumber2);
		beathex += String.format("RSE3 %08X ",mixTableChange.rseNumber3);
		beathex += String.format("UNK %08X ",mixTableChange.unknown);
		beathex += String.format("NVOL %02X ",mixTableChange.newVolume);
		beathex += String.format("NPAN %02X ",mixTableChange.newPanValue);
		beathex += String.format("NCHO %02X ",mixTableChange.newChorusValue);
		beathex += String.format("NREV %02X ",mixTableChange.newReverbValue);
		beathex += String.format("NPHS %02X ",mixTableChange.newPhaserValue);
		beathex += String.format("NTRE %02X ",mixTableChange.newTremoloValue);
		beathex += String.format("TPFLEN %02X ",mixTableChange.tempoStringData.fieldLength);
		beathex += String.format("TPSLEN %02X ",mixTableChange.tempoStringData.stringLength);
		beathex += "TPSTR "+mixTableChange.tempoStringData.string+" ";
		beathex += String.format("NTEM %02X ",mixTableChange.newTempo);
		if(mixTableChange.newVolume != 255) {
			mixTableChange.volumeChange = readChar();
			beathex += String.format("VOLCH %02X ",mixTableChange.volumeChange);
		}
		if(mixTableChange.newPanValue != 255) {
			mixTableChange.panChange = readChar();
			beathex += String.format("PANCH %02X ",mixTableChange.panChange);
		}
		if(mixTableChange.newChorusValue != 255) {
			mixTableChange.chorusChange = readChar();
			beathex += String.format("CHOCH %02X ",mixTableChange.chorusChange);
		}
		if(mixTableChange.newReverbValue != 255) {
			mixTableChange.reverbChange = readChar();
			beathex += String.format("REVCH %02X ",mixTableChange.reverbChange);
		}
		if(mixTableChange.newPhaserValue != 255) {
			mixTableChange.phaserChange = readChar();
			beathex += String.format("PHACH %02X ",mixTableChange.phaserChange);
		}
		if(mixTableChange.newTremoloValue != 255) {
			mixTableChange.tremoloChange = readChar();
			beathex += String.format("TRECH %02X ",mixTableChange.tremoloChange);
		}
		if(mixTableChange.newTempo != -1) {
			mixTableChange.tempoChange = readChar();
			beathex += String.format("TPCH %02X ",mixTableChange.tempoChange);
			if(version > 5) {
				mixTableChange.isTempoTextStringHidden = readChar();
				beathex += String.format("ISTSH %02X ",mixTableChange.isTempoTextStringHidden);
			}
		}
		if(version >= 4) {
			mixTableChange.mixTableAppliedTracksBitmask = readChar();
			beathex += String.format("MTATB %02X ",mixTableChange.mixTableAppliedTracksBitmask);
		}
		if(version >= 5) {
			mixTableChange.unknown2 = readChar();
			beathex += String.format("PAD %02X ",mixTableChange.unknown2);
		}
		if(version > 5) {
			mixTableChange.rseEffect2 = getContent41x();
			mixTableChange.rseEffect1 = getContent41x();
		}
		beathex += "}\n";
		return mixTableChange;
	}
	
	public GpFile(File f) {
		try {
			fis = new FileInputStream(f);
			
			readHeader();
			printHeader();
			readTrackData();
			if(version >= 5) {
				readMusicalDirectionsDefinitions();
			}
			
			if(version >= 5) {
				masterReverb = readInt(4);
			}
			bars = readInt(4);
			tracks = readInt(4);

			barChunk = new BarChunk[bars];
			trackChunk = new TrackChunk[tracks];
			
			System.out.println(bars+" bars");
			System.out.println(tracks+" tracks");
			
			System.exit(1);
			for(int i=0;i<bars;i++) {
				System.out.printf("[BAR %d(%04x)]",i+1,readCount);
				barChunk[i] = readBarChunk();
			}
			for(int i=0;i<tracks;i++) {
				System.out.printf("[TRACK %d(%04x)]",i+1,readCount);
				trackChunk[i] = readTrackChunk();
			}
			if(version >= 5) {
				readChar();
			}
			
			// BEAT CHUNK
			beatChunkList = new ArrayList<BeatChunk>();
			int measureCount=1;
			int limit = bars;
			while(measureCount<=limit) {
				BeatChunk beatChunk = new BeatChunk();
				beatChunk.beatChunkTrack = new BeatChunkTrack[tracks];
				
				int voices = (version >= 5) ? 2: 1;
				for(int i=0;i<tracks;i++) {
					System.out.print("[Measure "+measureCount+"/Track "+(i+1)+"]\n");
					beatChunk.beatChunkTrack[i] = new BeatChunkTrack();
					beatChunk.beatChunkTrack[i].voiceChunk = new VoiceChunk[voices];
					for(int j=0;j<voices;j++) {
						beatChunk.beatChunkTrack[i].voiceChunk[j] = new VoiceChunk();
						VoiceChunk voiceChunk = beatChunk.beatChunkTrack[i].voiceChunk[j]; 
						voiceChunk.numberOfBeats = readInt(4);
						int numberOfBeats = voiceChunk.numberOfBeats;
						System.out.print(numberOfBeats+" BEATS ");
						System.out.printf("%x\n",readCount);
						
						voiceChunk.beatSubChunk = new BeatSubChunk[numberOfBeats];
						
						for(int k=0;k<numberOfBeats;k++) {
							if(numberOfBeats > 128) {
								System.out.print("BEAT OVERFLOW");
								System.exit(1);
							}
							voiceChunk.beatSubChunk[k] = new BeatSubChunk();
							
							BeatSubChunk beatSubChunk = voiceChunk.beatSubChunk[k];
							/*
								Bit 0 (LSB):	Dotted notes?
								Bit 1:		Chord diagram present
								Bit 2:		Text present
								Bit 3:		Beat effects present
								Bit 4:		Mix table change present
								Bit 5:		This beat is an N-tuplet
								Bit 6:		Is a rest beat
								Bit 7 (MSB):	Unused (set to 0)
							*/ 
							beatSubChunk.beatBitmask = readChar();
							String beatBitmaskString = toBinary(beatSubChunk.beatBitmask, 8);
							
							System.out.print("BEAT["+k+"] ");
							beatmaskdesc = "";
							beathex = "";
							beathex += String.format("BBIT %02X ",beatSubChunk.beatBitmask);
							if(beatBitmaskString.charAt(1) == '1') {
								beatSubChunk.restType = readChar();
								beathex += String.format("RTYPE %02X ",beatSubChunk.restType);
								
							}
							beatSubChunk.beatDuration = readChar();
							beathex += String.format("DUR %02X ",beatSubChunk.beatDuration);
							//System.out.print(getBeatDuration(beatSubChunk.beatDuration)+" ");
							
							if(beatBitmaskString.charAt(2) == '1') {
								beatSubChunk.nTuplet = readBytes(4);
								beathex += String.format("NTPL %02X%02X%02X%02X ",beatSubChunk.nTuplet[0],beatSubChunk.nTuplet[1],beatSubChunk.nTuplet[2],beatSubChunk.nTuplet[3]);
							}
							if(beatBitmaskString.charAt(6) == '1') {
								beatSubChunk.chordDiagramFormat = readChar();
								// !CHORD DIAGRAM FORMAT 0 (if the format identifier was 0, ie. GP3 format):
								if(beatSubChunk.chordDiagramFormat == 0) {
									System.out.print("CHORDDIAGRAM0 ");
									beatSubChunk.chordDiagram0 = readChordDiagram0(trackChunk[i].numberOfStrings);
								}
								// !CHORD DIAGRAM FORMAT 1 (if the format identifier was 1, ie. GP4 format) (105 bytes):
								if(beatSubChunk.chordDiagramFormat == 1) {
									beatSubChunk.chordDiagram1 = readChordDiagram1();
								}
							}
							if(beatBitmaskString.charAt(5) == '1') {
								System.out.print("BEATTEXT ");
								beatSubChunk.beatText = getContent41x();
							}
							if(beatBitmaskString.charAt(4) == '1') {
								beatSubChunk.effectsPresent = new EffectsPresent();
								EffectsPresent effectsPresent = beatSubChunk.effectsPresent; 
								effectsPresent.beatEffectsBitmask = readChar();
								beathex += String.format("BEBIT %02X ", effectsPresent.beatEffectsBitmask); 
								String beatEffectsBitmaskString = toBinary(effectsPresent.beatEffectsBitmask,8);
								String extendedEffectsBitmaskString = null;
								if(version >= 4) {
									effectsPresent.extendedBeatEffectsBitmask = readChar();
									beathex += String.format("EXBEBIT %02X ", effectsPresent.extendedBeatEffectsBitmask);
									extendedEffectsBitmaskString = toBinary(effectsPresent.extendedBeatEffectsBitmask,8);
								}
								if(beatEffectsBitmaskString.charAt(2) == '1') {
									effectsPresent.stringEffect = readChar();
									beathex += String.format("TPS %02X ", effectsPresent.stringEffect);
								}
								if(version >= 4) {
									if(extendedEffectsBitmaskString.charAt(5) == '1') {
										effectsPresent.bendChunk = new BendChunk();
										BendChunk bendChunk = effectsPresent.bendChunk; 
										bendChunk.bendType = readChar();
										beathex += String.format("{BTYPE %02X ",bendChunk.bendType);
										bendChunk.bendHeight = readInt(4);
										beathex += String.format("BHGT %02X ",bendChunk.bendHeight);
										bendChunk.numberOfBendPoints = readInt(4);
										beathex += String.format("NBP %08X ",bendChunk.numberOfBendPoints);
										
										bendChunk.bendPointChunk = new BendPointChunk[bendChunk.numberOfBendPoints];
										for(int l=0;l<bendChunk.numberOfBendPoints;l++) {
											bendChunk.bendPointChunk[l] = new BendPointChunk();
											bendChunk.bendPointChunk[l].absoluteTimePosition = readInt(4);
											beathex += String.format("ATP %08X ",bendChunk.bendPointChunk[l].absoluteTimePosition);
											bendChunk.bendPointChunk[l].verticalPosition = readInt(4);
											beathex += String.format("VP %08X ",bendChunk.bendPointChunk[l].verticalPosition);
											bendChunk.bendPointChunk[l].vibratoType = readChar();
											beathex += String.format("VTYPE %02X} ",bendChunk.bendPointChunk[l].vibratoType);
										}
									}
								}
								if(beatEffectsBitmaskString.charAt(1) == '1') {
									beatSubChunk.effectsPresent.downstrokeSpeed = readChar();
									beathex += String.format("DSSPD %02X} ",beatSubChunk.effectsPresent.downstrokeSpeed);
									beatSubChunk.effectsPresent.upstrokeSpeed = readChar();
									beathex += String.format("USSPD %02X} ",beatSubChunk.effectsPresent.upstrokeSpeed);
								}
								if(extendedEffectsBitmaskString.charAt(6) == '1') {
									beatSubChunk.effectsPresent.pickstrokeSpeed = readChar();
									beathex += String.format("PSSPD %02X} ",beatSubChunk.effectsPresent.pickstrokeSpeed);
								}
							}
							if(beatBitmaskString.charAt(3) == '1') {
								beatSubChunk.mixTableChange = readMixTableChange();
							}
							beatSubChunk.usedStringMask = readChar();
							String usedString = toBinary(beatSubChunk.usedStringMask,7);
							beathex += String.format("USE %02X ", beatSubChunk.usedStringMask);
							
							ArrayList<Integer> stringOrder = new ArrayList<Integer>();
							int strings = 0;
							for(int l=0;l<usedString.length();l++) {
								if(usedString.charAt(l) == '1') {
									stringOrder.add(l);
									strings++;
								}
							}
							beatSubChunk.stringChunk = new StringChunk[strings];
							for(int l=0;l<strings;l++) {
								int thisString = stringOrder.get(l);
								beatSubChunk.stringChunk[l] = new StringChunk();
								StringChunk stringChunk = beatSubChunk.stringChunk[l]; 
								stringChunk.noteBitmask = readChar();
								beathex += String.format("[String"+l+"]NBIT %02X ",stringChunk.noteBitmask);
								/*
								Bit 0 (LSB):	Time-independent duration
								Bit 1:		Heavy Accentuated note (GP5 or higher only, usage in older formats is unknown)
								Bit 2:		Ghost note
								Bit 3:		Note effects present
								Bit 4:		Note dynamic
								Bit 5:		Note type
								Bit 6:		Accentuated note
								Bit 7:		Right/Left hand fingering
								*/
								String noteBitMaskString = toBinary(stringChunk.noteBitmask,8);
								if(noteBitMaskString.charAt(2) == '1') {
									stringChunk.noteType = readChar();
									beathex += String.format("NTYPE %02X ",stringChunk.noteType);
//									System.out.print("NOTETYPE:");
//									switch(stringChunk.noteType) {
//									case 1:
//										System.out.print("NORMAL ");
//										break;
//									case 2:
//										System.out.print("TIE ");
//										break;
//									case 3:
//										System.out.print("MUTED ");
//										break;
//									}
								}
								if(version < 5) {
									// time independent duration 2 bytes
									stringChunk.duration = readChar();
									stringChunk.nTuplet = readChar();
								}
								if(noteBitMaskString.charAt(3) == '1') {
									stringChunk.dynamicity = readChar();
									beathex += String.format("DYN %02X ", stringChunk.dynamicity);
								}
								if(noteBitMaskString.charAt(2) == '1') {
									stringChunk.fretNumber = readChar();
									beathex += String.format("FRET %02X ", stringChunk.fretNumber);
								}
								if(noteBitMaskString.charAt(0) == '1') {
									stringChunk.leftHandFingering = readChar();
									stringChunk.rightHandFingering = readChar();
									beathex += String.format("LRFI ");
								}
								if(version >= 5) {
									if(noteBitMaskString.charAt(7) == '1') {
										byte[] tid = readBytes(8);
										beathex += String.format("TID %02X%02X%02X%02X ",tid[0],tid[1],tid[2],tid[3],tid[4],tid[5],tid[6],tid[7]);
									}
									stringChunk.unknown = readChar();
									beathex += String.format("UNK %02X ",stringChunk.unknown);
								}
								if(noteBitMaskString.charAt(4) == '1') {
									stringChunk.noteEffect1Bitmask = readChar();
									beathex += String.format("NE1 %02X ",stringChunk.noteEffect1Bitmask);
									String noteEffect1BitmaskString = toBinary(stringChunk.noteEffect1Bitmask,8);
									/*
									 The note effect 1 bitmask declares which effects are defined for the note:
									Bit 0 (LSB):	Bend present
									Bit 1:		Hammer on/Pull off from the current note
									Bit 2:		Slide from the current note (GP3 format version)
									Bit 3:		Let ring
									Bit 4:		Grace note
									Bits 5-7:	Unused (set to 0)
									
									 The note effect 2 bitmask declares more effects for the note:
									Bit 0 (LSB):	Note played staccato
									Bit 1:		Palm Mute
									Bit 2:		Tremolo Picking
									Bit 3:		Slide from the current note
									Bit 4:		Harmonic note
									Bit 5:		Trill
									Bit 6:		Vibrato
									Bit 7 (MSB):	Unused (set to 0) 
									 */
									
									if(version >= 4) {
										stringChunk.noteEffect2Bitmask = readChar();
										beathex += String.format("NE2 %02X ",stringChunk.noteEffect2Bitmask);
									}
									if(noteEffect1BitmaskString.charAt(7) == '1') {
										stringChunk.bendChunk = new BendChunk();
										BendChunk bendChunk = stringChunk.bendChunk; 
										bendChunk.bendType = readChar();
										beathex += String.format("BTYPE %02X ",bendChunk.bendType);
										bendChunk.bendHeight = readInt(4);
										beathex += String.format("BHGT %08X ",bendChunk.bendHeight);
										bendChunk.numberOfBendPoints = readInt(4);
										beathex += String.format("NBP %08X ",bendChunk.numberOfBendPoints);
										
										bendChunk.bendPointChunk = new BendPointChunk[bendChunk.numberOfBendPoints];
										for(int m=0;m<bendChunk.numberOfBendPoints;m++) {
											bendChunk.bendPointChunk[m] = new BendPointChunk();
											BendPointChunk bpChunk = bendChunk.bendPointChunk[m]; 
											bpChunk.absoluteTimePosition = readInt(4);
											beathex += String.format("ATP %08X ",bpChunk.absoluteTimePosition);
											bpChunk.verticalPosition = readInt(4);
											beathex += String.format("VP %08X ",bpChunk.verticalPosition);
											bpChunk.vibratoType = readChar();
											beathex += String.format("VTYPE %02X ",bpChunk.vibratoType);
										}
									}
									if(noteEffect1BitmaskString.charAt(3) == '1') { 
										stringChunk.graceNoteFret = readChar();
										beathex += String.format("GNFRET %02X ",stringChunk.graceNoteFret);
										stringChunk.grateNoteDynamicity = readChar();
										beathex += String.format("GNDYN %02X ",stringChunk.grateNoteDynamicity);
										if(version >= 5) {
											stringChunk.graceNoteTransitionType = readChar();
											beathex += String.format("GNTTYPE %02X ",stringChunk.graceNoteTransitionType);
										}
										if(version < 5) {
											stringChunk.unknown = readChar();
											beathex += String.format("UNK %02X ",stringChunk.unknown);
										}
										stringChunk.graceNoteDuration = readChar();
										beathex += String.format("GNDUR %02X ",stringChunk.graceNoteDuration);
										
										if(version >= 5) {
											stringChunk.graceNotePosition = readChar();
											beathex += String.format("GNPOS %02X ",stringChunk.graceNotePosition);
										}
									}
									if(version >= 4) {
										String noteEffect2BitmaskString = toBinary(stringChunk.noteEffect2Bitmask,8);
										if(noteEffect2BitmaskString.charAt(5) == '1') {
											stringChunk.tremoloPickingSpeed = readChar();
											beathex += String.format("TPS %02X ",stringChunk.tremoloPickingSpeed);
										}
										if(noteEffect2BitmaskString.charAt(4) == '1') {
											stringChunk.slideType = readChar();
											beathex += String.format("STYPE %02X ",stringChunk.slideType);
										}
										if(noteEffect2BitmaskString.charAt(3) == '1') {
											stringChunk.harmonicType = readChar();
											beathex += String.format("HTYPE %02X ",stringChunk.harmonicType);
										}
										if(noteEffect2BitmaskString.charAt(2) == '1') {
											stringChunk.trillFret = readChar();
											beathex += String.format("TLFRET %02X ",stringChunk.trillFret);
											stringChunk.trillPeriod = readChar();
											beathex += String.format("TLPER %02X ",stringChunk.trillPeriod);
										}
									}
								
								}
							}
							if(version >= 5) {
								beatSubChunk.noteTransposeBitmask = readBytes(2);
								beathex += String.format("TBIT %02X%02X ",beatSubChunk.noteTransposeBitmask[0],beatSubChunk.noteTransposeBitmask[1]);
								// ADDITIONAL UNKNOWN DATA BYTE (if the note transpose bitmask declares this)
								if(beatSubChunk.noteTransposeBitmask[1] == 8 || 
										beatSubChunk.noteTransposeBitmask[1] == 10 || 
										beatSubChunk.noteTransposeBitmask[1] == 11) {
									readChar();
								}
							}
							
							System.out.println(beathex);
							
							
						}
					}
					readChar();
				}
				beatChunkList.add(beatChunk);
				measureCount++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public String toBinary(int b, int length) {
		StringBuilder sb = new StringBuilder();
		String binaryStr = Integer.toBinaryString(b);
		if(binaryStr.length() < length) {
			for(int i=0;i<length-binaryStr.length();i++) {
				sb.append("0");
			}
			sb.append(binaryStr);
			return sb.toString();
		}
		else {
			return binaryStr;
		}
	}
	public void printHeader() {
		System.out.println("VERSION : "+version);
		printField(title);
		printField(subtitle);
		printField(artist);
		printField(album);
		if(version >= 5) {
			printField(lyricist);
		}
		printField(music);
		printField(copyright);
		printField(tab);
		printField(instructions);
		for(Field notice:notices) {
			printField(notice);
		}
		if(version >= 4) {
			for(int i=0;i<5;i++) {
				System.out.println(lyrics[i].associatedTrack+" "+lyrics[i].startFrom+" "+lyrics[i].lyricLength+" "+lyrics[i].string);
			}
		}
	}
	public static void main(String[] args) {
		//File f = new File("d:\\testtest2.gp5");
		//File f = new File("d:\\ehehflrkd.gp5");
		//File f = new File("d:\\Jason Mraz - I'm Yours.gp5");
		//File f = new File("d:\\yourtest.gp5");
		//File f = new File("F:\\Bandscores(GP)\\이적_-_하늘을달리다-1212zxc.gp5"); // O
		File f = new File("F:\\Bandscores(GP)\\05Lavigne,_Avril_-_Sk8er_Boi.gp4"); // O
		//File f = new File("F:\\Bandscores(GP)\\에어맨이_쓰러지지_않아-siro__yuki.gp5"); // ~TRACK
		//File f = new File("F:\\Bandscores(GP)\\ash_like_snow.gp5"); // ~TRACK
		//File f = new File("F:\\Bandscores(GP)\\avril_lavigne_-_sk8er_boi.gp5"); // ~TRACK
		GpFile gp = new GpFile(f);
		//gp.printHeader();
	}
	
	public static int byteArrayToInt(byte[] b) 
	{
		int value = 0;
		byte[] reversed = new byte[b.length];
		
		// reverse
		
		for(int i=b.length-1;i>=0;i--) {
			reversed[b.length - i - 1] = b[i];
		}
	    
	    for (int i = 0; i < reversed.length; i++) {
	        int shift = (reversed.length - 1 - i) * 8;
	        value += (reversed[i] & 0x000000FF) << shift;
	    }
	    return value;
	}
	private byte[] toBytes(char[] chars) {
	    CharBuffer charBuffer = CharBuffer.wrap(chars);
	    ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
	    byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
	            byteBuffer.position(), byteBuffer.limit());
	    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
	    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
	    return bytes;
	}
	public void printField(Field f) {
		System.out.println(f.fieldLength + " " + f.stringLength + " " +f.string);
	}
}

	
