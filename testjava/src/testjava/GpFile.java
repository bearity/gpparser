package testjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

class Version {
	final static String GUITAR_PRO_510 = "FICHIER GUITAR PRO v5.10";
}

class Field {
	int fieldLength;
	int stringLength;
	String string;
}

class Lyrics {
	/*
	!LYRICS DATA (only if the major file version is >= 4):
	4 bytes:	Associated track for the lyrics
		LYRICS DATA REPEATED 5 TIMES: (In order for lyric lines 1 through 5)
			[4 bytes]:	Start from bar #
			[4 bytes]:	Lyric string length
			[varies]:	Lyric string
		...(repeated data)... 
	*/
	int associatedTrack;
	int startFrom;
	int lyricLength;
	String string;
}

class VolumeEQ {
	/*
	!VOLUME/EQUALIZATION SETTINGS (only if the major file version is > 5)
	[4 bytes]:	Master volume (value from 0 - 200, default is 100)
	[4 bytes]:	Unknown data/padding
	[1 byte]:	Number of increments of .1dB the volume for the 32Hz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 60Hz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 125Hz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 250Hz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 500Hz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 1KHz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 2KHz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 4KHz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 8KHz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the 16KHz band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the overall volume is lowered (gain)
	*/
	int masterVolume;
	int unknownData;
	int _32Hz, _60Hz, _125Hz, _250Hz, _500Hz, _1KHz, _2KHz, _4KHz, _8KHz, _16KHz, overallGain;
}
class PageSetup {
	/*
	!PAGE SETUP (only if the major file version is >= 5):
	[4 bytes]:	Page format length (mm)
	[4 bytes]:	Page format width (mm)
	[4 bytes]: 	Left margin (mm)
	[4 bytes]: 	Right margin (mm)
	[4 bytes]: 	Top margin (mm)
	[4 bytes]: 	Bottom margin (mm)
	[4 bytes]:	Score size (proportion in percent)
	[2 bytes]:	Enabled header/footer fields bitmask (defined in notes section)
	[4 bytes]:	Title header/footer field length (including string length byte that follows this value)
	[1 byte]:	Title header/footer string length
	[varies]:	Title header/footer string
	[4 bytes]:	Subtitle header/footer field length (including string length byte that follows this value)
	[1 byte]:	Subtitle header/footer string length
	[varies]:	Subtitle header/footer string
	[4 bytes]:	Artist header/footer field length (including string length byte that follows this value)
	[1 byte]:	Artist header/footer string length
	[varies]:	Artist header/footer string
	[4 bytes]:	Album header/footer field length (including string length byte that follows this value)
	[1 byte]:	Album header/footer string length
	[varies]:	Album header/footer string
	[4 bytes]:	Words (lyricist) header/footer field length (including string length byte that follows this value)
	[1 byte]:	Words (lyricist) header/footer string length
	[varies]:	Words (lyricist) header/footer string
	[4 bytes]:	Music (composer) header/footer field length (including string length byte that follows this value)
	[1 byte]:	Music (composer) header/footer string length
	[varies]:	Music (composer) header/footer string
	[4 bytes]:	Words & Music header/footer field length (including string length byte that follows this value)
	[1 byte]:	Words & Music header/footer string length
	[varies]:	Words & Music header/footer string
	[4 bytes]:	Copyright header/footer (line 1) field length (including string length byte that follows this value)
	[1 byte]:	Copyright header/footer (line 1) string length
	[varies]:	Copyright header/footer (line 1) string
	[4 bytes]:	Copyright header/footer (line 2) field length (including string length byte that follows this value)
	[1 byte]:	Copyright header/footer (line 2) string length
	[varies]:	Copyright header/footer (line 2) string
	[4 bytes]:	Page Number header/footer field length (including string length byte that follows this value)
	[1 byte]:	Page Number header/footer string length
	[varies]:	Page Number header/footer string
	*/
	int pageFormatLength;
	int pageFormatWidth;
	int leftMargin;
	int rightMargin;
	int topMargin;
	int bottomMargin;
	int scoreSize;
	int enabledHeaderFooterFieldsBitmask;
	Field titleHeaderFooter;
	Field subtitleHeaderFooter;
	Field artistHeaderFooter;
	Field albumHeaderFooter;
	Field wordsHeaderFooter;
	Field musicHeaderFooter;
	Field wordsNmusicHeaderFooter;
	Field[] copyrightHeaderFooter;
	Field pageNumberHeaderFooter;
}
class KeyOctaveInfo {
	/*
	!KEY/OCTAVE INFORMATION (if the major file version is >= 4)
	[1 byte]:	The song's key
	[3 bytes]:	Unknown data/padding
	[1 byte]:	The song's used octave?
	*/
	int key;
	int unknownData;
	int octave;
}
class KeyInfo {
	/*
	!KEY INFORMATION (if the major file version is < 4)
	[4 bytes]:	The song's key 	
	*/ 
	int key;
}

class TrackData {
	/*
	4 bytes:	Instrument patch number
	1 byte:		Volume (value from 0 to 16, default is 13)
	1 byte:		Pan (value from 0 to 16, default is 8)
	1 byte:		Chorus
	1 byte:		Reverb
	1 byte:		Phaser
	1 byte:		Tremolo
	2 bytes:	Unused/Padding bytes (set to 0x0) 
	*/ 
	int instrumentPatchNumber;
	int volume;
	int pan;
	int chorus;
	int reverb;
	int phaser;
	int tremolo;
}

class MusicalDirectionsDefinitions {
	/*
	[2 bytes]:	The beat number at which the "Coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Double coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Segno" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Segno segno" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Fine" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da capo" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da capo al coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da capo al double coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da capo al fine" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno al coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno al double coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno al fine" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno segno" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno segno al coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno segno al double coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da segno segno al fine" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da coda" symbol is placed (0xFF = unused)
	[2 bytes]:	The beat number at which the "Da double coda" symbol is placed (0xFF = unused)
	 */ 
	int coda;
	int doubleCoda;
	int segno;
	int segnoSegno;
	int fine;
	int daCapo;
	int daCapoAlCoda;
	int daCapoAlDoubleCoda;
	int daCapoAlFine;
	int daSegno;
	int daSegnoAlCoda;
	int daSegnoAlDoubleCoda;
	int daSegnoAlFine;
	int daSegnoSegno;
	int daSegnoSegnoAlCoda;
	int daSegnoSegnoAlDoubleCoda;
	int daSegnoSegnoAlFine;
	int daCoda;
	int daDoubleCoda;
} 

class BarChunk {
	/*
	1 byte:		Bar (measure) bitmask (defined in notes section)
	!TS NUMERATOR (if the bitmask declares this)
		[1 byte]:	TS numerator
	!TS DENOMINATOR (if the bitmask declares this)
		[1 byte]:	TS denominator
	!NEW SECTION (if the bitmask declares this)
	{
		[4 bytes]:	Section name field length (including string length byte that follows this value)
		[1 byte]:	Length of section name string
		[varies]:	Section name string
		[4 bytes]:	Color to render the section name with (RGB intensities, with the most significant byte set padded with a value of 0)
	}
	!KEY SIGNATURE CHANGE (if the bitmask declares this)
	{
		[1 byte]:	Key (see notes section)
		[1 byte]:	Major/Minor (value is 0 if it is major, or 1 if it is minor)
	}
?	!BEAM EIGHT NOTES BY VALUES (if the bitmask declares either a new TS num/den and the major file version is >= 4?): (note:  These 4 bytes are verified to not be in 3.0/4.0 version GP files)
		[4 bytes]:	Beam eight notes by values
	!END OF REPEAT (if the bitmask declares this)
		[1 byte]:	Number of repeats?
	!NUMBER OF ALTERNATE ENDING (if the bitmask declares this)
		[1 byte]:	Alternate ending number?
	!PADDING (if the major file version is >= 5):
		[1 byte]:	Unknown data/padding (set to 0x0)
	!TRIPLET FEEL (if the major file version is >= 5):
		[1 byte]:	(0 = no triplet feel, 1 = Triplet 8th, 2 = Triplet 16th)
	!PADDING (if the major file version is >= 5):
		[1 byte]:	Unknown data/padding (set to 0x0)
	*/ 
	int barBitmask;
	int tsNumerator;
	int tsDenominator;
	Field sectionName;
	int sectionNameWith;
	int key;
	int isMinor;
	int beamEightNotesByValues;
	int endOfRepeat;
	int numberOfAlternateEnding;
	int tripletFeel;
}

class TrackChunk {
	/*
	TRACK CHUNK, for each track:
		1 byte:		Track bitmask (defined in notes section)
		TRACK NAME (41 bytes):
			1 byte:		Length of track name
			[varies]:	Track name string
			[varies]:	Padding (pad track name string to 40 bytes with bytes set to 0x0)
	 
		4 bytes:	Number of strings used in this track

		STRING TUNING CHUNK, definitions for 7 strings (starting with the lowest numbered string, ie. high e), or padded if there are less than 7 strings (28 bytes):
			4 bytes:	The MIDI note this string plays open (Values between 0 through 127, where a value of 0 refers to note C0)
			[varies]:	Optional padding (pad tuning chunk to 28 bytes, values set to 0xFF)
	 
		4 bytes:	MIDI port used
		4 bytes:	MIDI channel used (must be 10 if this is a drum track)
		4 bytes:	MIDI channel used for effects
		4 bytes:	Number of frets used for this instrument
		4 bytes:	The fret number at which a capo is placed (0 for no capo)
		4 bytes:	Track color (RGB intensities, with the most significant byte set padded with a value of 0)
	 
		!UNKNOWN DATA/PADDING (if the file version is 5.0)
			[41 bytes]:	Unknown
	 */

	int barBitmask;
	int trackNameLength;
	String trackName;
	int numberOfStrings;
	int stringTuningChunk;
	int midiPortUsed;
	int midiChannelUsed;
	int midiChannelUsedForEffects;
	int numberOfFrets;
	int trackColor;
	TrackSettings trackSettings;
	int unknownPadding;
}
class TrackSettings {
	/*
	!TRACK SETTINGS (if the file version is > 5.0)
	[1 byte]:	Track properties 1 bitmask (defined in notes section)
	[1 byte]:	Track properties 2 bitmask (defined in notes section)
	[1 byte]:	Unknown
	[1 byte]:	MIDI bank (if "Use MIDI" is enabled in track instrument options)
	[1 byte]:	Human playing (Track instrument options)
	[1 byte]:	Auto-Accentuation on the Beat (Track instrument options)
	[31 bytes]:	Unknown
	[1 byte]:	Selected sound bank option (Track instrument options)
	[7 bytes]:	Unknown
	[1 byte]:	Number of increments of .1dB the volume for the low frequency band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the mid frequency band is lowered
	[1 byte]:	Number of increments of .1dB the volume for the high frequency band is lowered
	[1 byte]:	Number of increments of .1dB the volume for all frequencies is lowered (gain)
	[4 bytes]:	Track instrument effect 1 field length (including string length byte that follows this value)
	[1 byte]:	Track instrument effect 1 string length
	[varies]:	Track instrument effect 1 string
	[4 bytes]:	Track instrument effect 2 field length (including string length byte that follows this value)
	[1 byte]:	Track instrument effect 2 string length
	[varies]:	Track instrument effect 2 string
	*/
	int bitmask1;
	int bitmask2;
	int unknown;
	int midiBank;
	int humanPlaying;
	int autoAccentuationOnTheBeat;
	int unknown2;
	int selectedSoundBankOption;
	int unknown3;
	int lowFrequency;
	int midFrequency;
	int highFrequency;
	int allFrequencies;
	Field instrumentEffect1;
	Field instrumentEffect2;
}

public class GpFile {
	float version;

	Field title;
	Field subtitle;
	Field artist;
	Field album;
	Field lyricist;
	Field music;
	Field copyright;
	Field tab;
	Field instructions;
	Field[] notices;
	Lyrics[] lyrics;
	VolumeEQ volumeEQ;
	PageSetup pageSetup;
	Field songData;
	int bpm;
	int uninterestingData;
	KeyOctaveInfo keyOctaveInfo;
	KeyInfo keyInfo;
	TrackData[] trackData;
	MusicalDirectionsDefinitions musicalDirectionsDefinitions;
	int masterReverb;
	int bars;
	int tracks;
	BarChunk[] barChunk;
	TrackChunk[] trackChunk;
	
	BufferedReader br;
	FileInputStream fis;
	InputStreamReader isr;
	
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
	
	public Field getContent41x() {
		Field tmpField = new Field();
		StringBuilder sb = new StringBuilder();
		
		byte[] byteTmp = readBytes(4);
		
		
		readCount+=4;

		tmpField.fieldLength = byteArrayToInt(byteTmp);
		tmpField.stringLength = readChar();
		readCount++;
		
		byteTmp = readBytes(tmpField.stringLength);
		readCount+=tmpField.stringLength;
		
		try {
			tmpField.string = new String(byteTmp, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return tmpField;
	}
	
	public GpFile(File f) {
		try {
			int tmpInt = 0, rythmFeel = 0;
			String tmpStr = null;
			fis = new FileInputStream(f);
			/*
			FILE VERSION HEADER (31 bytes):
			1 byte:		Version string length
			[varies]:	Version string
			[varies]:	Padding (pad version string to 30 bytes)
			 
			SONG ATTRIBUTES HEADER:
			4 bytes:	Title field length (including string length byte that follows this value)
			1 byte:		Title string length
			[varies]:	Title string
			4 bytes:	Subtitle field length (including string length byte that follows this value)
			1 byte:		Subtitle string length 
			[varies]:	Subtitle string
			4 bytes:	Artist field length (including string length byte that follows this value)
			1 byte:		Artist string length
			[varies]:	Artist string
			4 bytes:	Album field length (including string length byte that follows this value)
			1 byte:		Album string length
			[varies]:	Album string
			 
			!WORDS (LYRICIST) STRING (only if the major file version is >= 5):
			[4 bytes]:	Words field length (including string length byte that follows this value)
			[1 byte]:	Words string length
			[varies]:	Words string
			4 bytes:	Music (composer) field length (including string length byte that follows this value)
			1 byte:		Music string length
			[varies]:	Music string
			4 bytes:	Copyright field length (including string length byte that follows this value)
			1 byte:		Copyright string length
			[varies]:	Copyright string
			4 bytes:	Tab (transcriber) field length (including string length byte that follows this value)
			1 byte:		Tab string length
			[varies]:	Tab string
			4 bytes:	Instructions field length (including string length byte that follows this value)
			1 byte:		Instructions string length
			[varies]:	Instructions string
			4 bytes:	Number of notice entries
			 
			NOTICE CHUNK, for each notice entry:
			4 bytes:	Notice field length
			1 byte:		Notice string length
			[varies]:	Notice string
			...(variably repeated data)...
			 
			!Shuffle rhythm feel (only if the major file version is  <= 4):
			[1 byte]:		Shuffle rhythm feel 
			*/
			
			tmpInt = fis.read();
			tmpStr = new String(readBytes(tmpInt), encoding);
			
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
			tmpInt = byteArrayToInt(readBytes(4));
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
					lyrics[i].associatedTrack = byteArrayToInt(readBytes(4));
					lyrics[i].startFrom = byteArrayToInt(readBytes(4));
					if(lyrics[i].startFrom != 0) {
						lyrics[i].lyricLength = byteArrayToInt(readBytes(4));
						lyrics[i].string = new String(readBytes(lyrics[i].lyricLength), encoding);
					}
					else {
					}
				}
				
			}
			
			// !VOLUME/EQUALIZATION SETTINGS (only if the major file version is > 5)
			if(version > 5) {
				volumeEQ = new VolumeEQ();
				volumeEQ.masterVolume = byteArrayToInt(readBytes(4));
				volumeEQ.unknownData = byteArrayToInt(readBytes(4));
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
				pageSetup.pageFormatLength = byteArrayToInt(readBytes(4));
				pageSetup.pageFormatWidth = byteArrayToInt(readBytes(4));
				pageSetup.leftMargin = byteArrayToInt(readBytes(4));
				pageSetup.rightMargin = byteArrayToInt(readBytes(4));
				pageSetup.topMargin = byteArrayToInt(readBytes(4));
				pageSetup.bottomMargin = byteArrayToInt(readBytes(4));
				pageSetup.scoreSize = byteArrayToInt(readBytes(4));
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
			
			bpm = byteArrayToInt(readBytes(4));
			
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
				keyInfo.key = byteArrayToInt(readBytes(4));
			}
			// TrackData
			trackData = new TrackData[64];
			for(int i=0;i<64;i++) {
				trackData[i] = new TrackData();
				trackData[i].instrumentPatchNumber = byteArrayToInt(readBytes(4));
				trackData[i].volume = readChar();
				trackData[i].pan = readChar();
				trackData[i].chorus = readChar();
				trackData[i].reverb = readChar();
				trackData[i].phaser = readChar();
				trackData[i].tremolo = readChar();
				readBytes(2);
			}
			
			// 으아아아아아아아아아아아아앙ㅇ아아아아아아아아아아ㅏㅏ아ㅓ미나어ㅣㅏㅓ지ㅏㅓ
			
			// !MUSICAL DIRECTIONS DEFINITIONS (if the major file version is >= 5) (38 bytes):
			if(version >= 5) {
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
			
			// !MASTER REVERB SETTING (if the major file version is >= 5):
			if(version >= 5) {
				masterReverb = byteArrayToInt(readBytes(4));
			}
			
			bars = byteArrayToInt(readBytes(4));
			tracks = byteArrayToInt(readBytes(4));
			
			barChunk = new BarChunk[bars];
			
			trackChunk = new TrackChunk[bars];
			
			for(int i=0;i<bars;i++) {
				/*
				The bar (measure) bitmask declares which parameters are defined for the measure:
				Bit 0 (LSB):	Time signature change (numerator)
				Bit 1:		Time signature change (denominator)
				Bit 2:		Beginning of repeat
				Bit 3:		End of repeat
				Bit 4:		Number of alternate ending
				Bit 5:		New section
				Bit 6:		Key signature change
				Bit 7 (MSB):	Double bar 
				*/
				barChunk[i] = new BarChunk();
				barChunk[i].barBitmask = readChar();
				System.out.println(barChunk[i].barBitmask+" "+toBinary(barChunk[i].barBitmask,8));
				//System.out.println(barChunk[i].barBitmask+" "+Integer.toBinaryString(barChunk[i].barBitmask));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		//File f = new File("d:\\ehehflrkd.gp5");
		//File f = new File("d:\\Jason Mraz - I'm Yours.gp5");
		File f = new File("F:\\Bandscores(GP)\\이적_-_하늘을달리다-1212zxc.gp5");
		
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

	
