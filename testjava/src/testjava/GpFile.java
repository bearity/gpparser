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
		BarChunk barChunk = new BarChunk();
		
		barChunk.barBitmask = readChar();
		String barBitmaskString = toBinary(barChunk.barBitmask,8);
		if(version < 3) {
			// end of repeat 1 byte
			// number of alternate ending 1 byte
		}
		if(version >= 3) {
			if(barBitmaskString.charAt(7) == '1') {
				barChunk.tsNumerator = readChar();
			}
			if(barBitmaskString.charAt(6) == '1') {
				barChunk.tsDenominator = readChar();
			}
			if(barBitmaskString.charAt(4) == '1') {
				barChunk.endOfRepeat = readChar();
			}
			if(version < 5) {
				// alternative ending 1 byte
				// new section(41x 4)
				// key signature change 1, 1
			}
			if(version >= 5) {
				if(barBitmaskString.charAt(2) == '1') {
					barChunk.sectionName = getContent41x();
					barChunk.sectionNameWith = readBytes(4);
					System.out.println(barChunk.sectionName.string);
				}
				if(barBitmaskString.charAt(1) == '1') {
					barChunk.key = readChar();
					barChunk.isMinor = readChar();
				}
				if(barBitmaskString.charAt(7) == '1' || barBitmaskString.charAt(6) == '1') {
					barChunk.beamEightNotesByValues = readBytes(4);
				}
				if(barBitmaskString.charAt(3) == '1') {
					barChunk.numberOfAlternateEnding = readChar();
				}
				readChar();
				barChunk.tripletFeel = readChar();
				readChar();
			}
			
		}
		
		return barChunk;
	}
	public TrackChunk readTrackChunk() {
		TrackChunk trackChunk = new TrackChunk();
		
		trackChunk.trackBitmask = readChar();
		String trackBitmaskString = toBinary(trackChunk.trackBitmask,8);
		trackChunk.trackNameLength = readChar();
		try {
			trackChunk.trackName = new String(readBytes(trackChunk.trackNameLength), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		readBytes(40-trackChunk.trackNameLength);
		trackChunk.numberOfStrings = readInt(4);
		trackChunk.stringTuningChunk = new int[7];
		for(int j=0;j<7;j++) {
			trackChunk.stringTuningChunk[j] = readInt(4);
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
			readBytes(41);
		}
		
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
		readBytes(3);
		chordDiagram1.chordRoot = readChar();
		chordDiagram1.unkonwn = readBytes(3);
		chordDiagram1.chordType = readChar();
		chordDiagram1.unknown2 = readBytes(3);
		chordDiagram1.option = readChar();
		chordDiagram1.unknown2 = readBytes(3);
		chordDiagram1.lowestNotePlayedInTheChord = readInt(4);
		chordDiagram1.plusminusOption = readChar();
		chordDiagram1.unknown3 = readBytes(4);
		chordDiagram1.chordNameStringLength = readChar();
		try {
			chordDiagram1.chordName = new String(readBytes(chordDiagram1.chordNameStringLength),encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		readBytes(20-chordDiagram1.chordNameStringLength);
		readBytes(2);
		chordDiagram1.tonalityOfTheFifth = readChar();
		readBytes(3);
		chordDiagram1.tonalityOfTheNinth = readChar();
		readBytes(3);
		chordDiagram1.tonalityOfTheEleventh = readChar();
		readBytes(3);
		chordDiagram1.chordDiagramBaseFretPosition = readInt(4);
		chordDiagram1.fretChunk = new int[7];
		for(int l=0;l<7;l++) {
			chordDiagram1.fretChunk[l] = readInt(4);
		}
		chordDiagram1.numberOfBarresInTheChord = readChar();
		chordDiagram1.barrPosition = new int[5];
		for(int l=0;l<5;l++) {
			chordDiagram1.barrPosition[l] = readChar();
		}
		chordDiagram1.barrFirstString = new int[5];
		for(int l=0;l<5;l++) {
			chordDiagram1.barrFirstString[l] = readChar();
		}
		chordDiagram1.barrLastString = new int[5];
		for(int l=0;l<5;l++) {
			chordDiagram1.barrLastString[l] = readChar();
		}
		chordDiagram1.includeFirstInterval = readChar();
		chordDiagram1.includeThirdInterval = readChar();
		chordDiagram1.includeFifthInterval = readChar();
		chordDiagram1.includeSeventhInterval = readChar();
		chordDiagram1.includeNinthInterval = readChar();
		chordDiagram1.includeEleventhInterval = readChar();
		readChar();
		
		chordDiagram1.fingeringChunk = new int[7];
		for(int l=0;l<7;l++) {
			chordDiagram1.fingeringChunk[l] = readChar();
		}
		chordDiagram1.isChordFingeringDisplayed = readChar();
		
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
		mixTableChange.unknown = readBytes(4);
		mixTableChange.newVolume = readChar();
		mixTableChange.newPanValue = readChar();
		mixTableChange.newChorusValue = readChar();
		mixTableChange.newReverbValue = readChar();
		mixTableChange.newPhaserValue = readChar();
		mixTableChange.newTremoloValue = readChar();
		mixTableChange.tempoStringData = getContent41x();
		mixTableChange.newTempo = readInt(4);
		mixTableChange.volumeChange = readChar();
		mixTableChange.panChange = readChar();
		mixTableChange.chorusChange = readChar();
		mixTableChange.reverbChange = readChar();
		mixTableChange.phaserChange = readChar();
		mixTableChange.tremoloChange = readChar();
		mixTableChange.tempoChange = readChar();
		if(mixTableChange.tempoChange != 0) {
			mixTableChange.isTempoTextStringHidden = readChar();
		}
		if(version >= 4) {
			mixTableChange.mixTableAppliedTracksBitmask = readChar();
		}
		if(version >= 5) {
			mixTableChange.unknown2 = readBytes(1);
		}
		if(version >= 5) {
			mixTableChange.rseEffect2 = getContent41x();
			mixTableChange.rseEffect1 = getContent41x();
		}
		return mixTableChange;
	}
	
	public GpFile(File f) {
		try {
			fis = new FileInputStream(f);
			
			readHeader();
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
			
			for(int i=0;i<bars;i++) {
				barChunk[i] = readBarChunk();
			}
			for(int i=0;i<tracks;i++) {
				trackChunk[i] = readTrackChunk();
			}
			if(version >= 5) {
				readChar();
			}
			
			// BEAT CHUNK
			beatChunkList = new ArrayList<BeatChunk>();
			int measureCount=0;
			while(measureCount!=49) {
				if(measureCount == 48) {
					System.out.println("48");
				}
				BeatChunk beatChunk = new BeatChunk();
				beatChunk.beatChunkTrack = new BeatChunkTrack[tracks];
				
				int voices = (version >= 5) ? 2: 1;
				for(int i=0;i<tracks;i++) {
					System.out.println("MEASURE/TRACK["+measureCount+"/"+i+"]");
					beatChunk.beatChunkTrack[i] = new BeatChunkTrack();
					beatChunk.beatChunkTrack[i].voiceChunk = new VoiceChunk[voices];
					for(int j=0;j<voices;j++) {
						beatChunk.beatChunkTrack[i].voiceChunk[j] = new VoiceChunk();
						VoiceChunk voiceChunk = beatChunk.beatChunkTrack[i].voiceChunk[j]; 
						voiceChunk.numberOfBeats = readInt(4);
						int numberOfBeats = voiceChunk.numberOfBeats;
						System.out.println(numberOfBeats+" BEATS");
						
						voiceChunk.beatSubChunk = new BeatSubChunk[numberOfBeats];
						
						for(int k=0;k<numberOfBeats;k++) {
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
							
							System.out.print("BEAT["+k+"] "+beatBitmaskString+" ");
							if(beatBitmaskString.charAt(1) == '1') {
								System.out.print("REST ");
								beatSubChunk.restType = readChar();
							}
							beatSubChunk.beatDuration = readChar();
							System.out.print(getBeatDuration(beatSubChunk.beatDuration)+" ");
							
							if(beatBitmaskString.charAt(2) == '1') {
								System.out.print("NTUPLET ");
								beatSubChunk.nTuplet = readInt(4);
							}
							if(beatBitmaskString.charAt(6) == '1') {
								beatSubChunk.chordDiagramFormat = readChar();
								// !CHORD DIAGRAM FORMAT 0 (if the format identifier was 0, ie. GP3 format):
								if(beatSubChunk.chordDiagramFormat == 0) {
									System.out.print("CHORDDIAGRAM0");
									beatSubChunk.chordDiagram0 = readChordDiagram0(trackChunk[i].numberOfStrings);
								}
								// !CHORD DIAGRAM FORMAT 1 (if the format identifier was 1, ie. GP4 format) (105 bytes):
								if(beatSubChunk.chordDiagramFormat == 1) {
									System.out.print("CHORDDIAGRAM1");
									beatSubChunk.chordDiagram1 = readChordDiagram1();
								}
							}
							if(beatBitmaskString.charAt(5) == '1') {
								System.out.print("BEATTEXT ");
								beatSubChunk.beatText = getContent41x();
							}
							if(beatBitmaskString.charAt(4) == '1') {
								System.out.print("NOTEEFFECT ");
								beatSubChunk.effectsPresent = new EffectsPresent();
								EffectsPresent effectsPresent = beatSubChunk.effectsPresent; 
								effectsPresent.beatEffectsBitmask = readChar();
								String beatEffectsBitmaskString = toBinary(effectsPresent.beatEffectsBitmask,8);
								String extendedEffectsBitmaskString = null;
								if(version >= 4) {
									effectsPresent.extendedBeatEffectsBitmask = readChar();
									extendedEffectsBitmaskString = toBinary(effectsPresent.extendedBeatEffectsBitmask,8);
								}
								if(beatEffectsBitmaskString.charAt(2) == '1') {
									effectsPresent.tappingPoppingSlappingStatus = readChar();
								}
								if(version >= 4) {
									if(extendedEffectsBitmaskString.charAt(5) == '1') {
										effectsPresent.bendChunk = new BendChunk();
										BendChunk bendChunk = effectsPresent.bendChunk; 
										bendChunk.bendType = readChar();
										bendChunk.bendHeight = readInt(4);
										bendChunk.numberOfBendPoints = readInt(4);
										
										bendChunk.bendPointChunk = new BendPointChunk[bendChunk.numberOfBendPoints];
										for(int l=0;l<bendChunk.numberOfBendPoints;l++) {
											bendChunk.bendPointChunk[l] = new BendPointChunk();
											bendChunk.bendPointChunk[l].absoluteTimePosition = readInt(4);
											bendChunk.bendPointChunk[l].verticalPosition = readInt(4);
											bendChunk.bendPointChunk[l].vibratoType = readChar();
										}
									}
								}
								if(beatEffectsBitmaskString.charAt(1) == '1') {
									beatSubChunk.effectsPresent.downstrokeSpeed = readChar();
									beatSubChunk.effectsPresent.upstrokeSpeed = readChar();
									beatSubChunk.effectsPresent.pickstrokeSpeed = readChar();
								}
							}
							if(beatBitmaskString.charAt(3) == '1') {
								System.out.print("MIXTABLECHANGE ");
								beatSubChunk.mixTableChange = readMixTableChange();
							}
							beatSubChunk.usedStringMask = readChar();
							String usedString = toBinary(beatSubChunk.usedStringMask,7);
							System.out.print(usedString+" ");
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
								System.out.print(noteBitMaskString+" ");
								if(noteBitMaskString.charAt(2) == '1') {
									stringChunk.noteType = readChar();
									System.out.print("NOTETYPE:");
									switch(stringChunk.noteType) {
									case 1:
										System.out.print("NORMAL ");
										break;
									case 2:
										System.out.print("TIE ");
										break;
									case 3:
										System.out.print("MUTED ");
										break;
									}
								}
								if(version < 5) {
									// time independent duration 2 bytes
									stringChunk.duration = readChar();
									stringChunk.nTuplet = readChar();
								}
								if(noteBitMaskString.charAt(3) == '1') {
									System.out.print("NOTEDYNAMIC ");
									stringChunk.dynamicity = readChar();
								}
								if(noteBitMaskString.charAt(2) == '1') {
									stringChunk.fretNumber = readChar();
									System.out.print("FRETNUMBER:"+stringChunk.fretNumber+" ");
								}
								if(noteBitMaskString.charAt(1) == '1') {
									
									stringChunk.leftHandFingering = readChar();
									stringChunk.rightHandFingering = readChar();
								}
								if(version >= 5) {
									if(noteBitMaskString.charAt(7) == '1') {
										System.out.print("TIMEINDEPENDENTDURATION ");
										readBytes(8);
									}
									stringChunk.unknown = readBytes(1);
								}
								if(noteBitMaskString.charAt(4) == '1') {
									stringChunk.noteEffect1Bitmask = readChar();
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
									}
									if(noteEffect1BitmaskString.charAt(7) == '1') {
										stringChunk.bendChunk = new BendChunk();
										BendChunk bendChunk = stringChunk.bendChunk; 
										bendChunk.bendType = readChar();
										bendChunk.bendHeight = readInt(4);
										bendChunk.numberOfBendPoints = readInt(4);
										
										bendChunk.bendPointChunk = new BendPointChunk[bendChunk.numberOfBendPoints];
										for(int m=0;m<bendChunk.numberOfBendPoints;m++) {
											bendChunk.bendPointChunk[m] = new BendPointChunk();
											BendPointChunk bpChunk = bendChunk.bendPointChunk[m]; 
											bpChunk.absoluteTimePosition = readInt(4);
											bpChunk.verticalPosition = readInt(4);
											bpChunk.vibratoType = readChar();
										}
									}
									if(noteEffect1BitmaskString.charAt(3) == '1') { 
										stringChunk.graceNoteFret = readChar();
										stringChunk.grateNoteDynamicity = readChar();
										stringChunk.graceNoteTransitionType = readChar();
										stringChunk.graceNoteDuration = readChar();
									}
									if(version >= 4) {
										String noteEffect2BitmaskString = toBinary(stringChunk.noteEffect2Bitmask,8);
										if(noteEffect2BitmaskString.charAt(5) == '1') {
											stringChunk.tremoloPickingSpeed = readChar();
										}
										if(noteEffect2BitmaskString.charAt(4) == '1') {
											stringChunk.slideType = readChar();
										}
										if(noteEffect2BitmaskString.charAt(3) == '1') {
											stringChunk.harmonicType = readChar();
										}
										if(noteEffect2BitmaskString.charAt(2) == '1') {
											stringChunk.trillFret = readChar();
											stringChunk.trillPeriod = readChar();
										}
									}
								
								}
							}
							if(version >= 5) {
								beatSubChunk.noteTransposeBitmask = readInt(2);
								// ADDITIONAL UNKNOWN DATA BYTE (if the note transpose bitmask declares this)
								//readChar();
							}
							System.out.println();
							
							
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
		System.out.println(version);
		System.out.println(title.fieldLength + " "+title.stringLength+" "+title.string);
		System.out.println(subtitle.fieldLength + " "+subtitle.stringLength+" "+subtitle.string);
		System.out.println(artist.fieldLength + " "+artist.stringLength+" "+artist.string);
		System.out.println(album.fieldLength + " "+album.stringLength+" "+album.string);
		if(version >= 4) {
			System.out.println(lyricist.fieldLength + " "+lyricist.stringLength+" "+lyricist.string);
		}
		System.out.println(music.fieldLength + " "+music.stringLength+" "+music.string);
		System.out.println(copyright.fieldLength + " "+copyright.stringLength+" "+copyright.string);
		System.out.println(tab.fieldLength + " "+tab.stringLength+" "+tab.string);
		System.out.println(instructions.fieldLength + " "+instructions.stringLength+" "+instructions.string);
		for(Field notice:notices) {
			System.out.println(notice.fieldLength + " "+notice.stringLength+" "+notice.string);
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
		//File f = new File("F:\\Bandscores(GP)\\이적_-_하늘을달리다-1212zxc.gp5"); // X
		//File f = new File("F:\\Bandscores(GP)\\에어맨이쓰러지지않아_원본-klklo123-miffyhth-joo017.gp5"); // X
		//File f = new File("F:\\Bandscores(GP)\\에어맨이_쓰러지지_않아-siro__yuki.gp5"); // ~TRACK
		//File f = new File("F:\\Bandscores(GP)\\ash_like_snow.gp5"); // ~TRACK
		//File f = new File("F:\\Bandscores(GP)\\avril_lavigne_-_sk8er_boi.gp5"); // ~TRACK
		File f = new File("F:\\Bandscores(GP)\\siam_shade_-_triptych-1212zxc.gp5"); // TRACK OK
		//File f = new File("F:\\test.gp5");
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

	
