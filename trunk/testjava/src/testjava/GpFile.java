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
import java.util.ArrayList;
import java.util.Arrays;

class Version {
	final static String GUITAR_PRO_510 = "FICHIER GUITAR PRO v5.10";
	final static String GUITAR_PRO_5 = "FICHIER GUITAR PRO v5.00";
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
	String barBitmaskStr;
	int tsNumerator;
	int tsDenominator;
	Field sectionName;
	byte[] sectionNameWith;
	int key;
	int isMinor;
	byte[] beamEightNotesByValues;
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

	int trackBitmask;
	String trackBitmaskStr;
	int trackNameLength;
	String trackName;
	int numberOfStrings;
	int[] stringTuningChunk;
	int midiPortUsed;
	int midiChannelUsed;
	int midiChannelUsedForEffects;
	int numberOfFrets;
	byte[] trackColor;
	int capoFret;
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
	byte[] unknown2;
	int selectedSoundBankOption;
	byte[] unknown3;
	int lowFrequency;
	int midFrequency;
	int highFrequency;
	int allFrequencies;
	Field instrumentEffect1;
	Field instrumentEffect2;
}

class BeatChunk {
	//!BEAT CHUNK, for each measure:
	//for each track (if the file major version is >= 5, this data is given twice, once for each "voice"):
	BeatChunkTrack[] beatChunkTrack;
}
class BeatChunkTrack {
	VoiceChunk[] voicChunk;
}

class VoiceChunk {
	int numberOfBeats;
	BeatSubChunk[] beatSubChunk;
}

class BeatSubChunk {
	int beatBitmask;
	int restType;
	int beatDuration;
	int nTuplet;
	//!CHORD DIAGRAM (if the beat bitmask declares that a chord diagram is present):
	ChordDiagram0 chordDiagram0;
	ChordDiagram1 chordDiagram1;
	int chordDiagramFormat;
	Field beatText;
	EffectsPresent effectsPresent;
	MixTableChange mixTableChange;
	int usedStringMask;
	StringChunk[] stringChunk;
	int noteTransposeBitmask;
}

class StringChunk {
	/*
	!STRING CHUNK, for each populated string (starting with the lowest numbered string, ie. high e):
		[1 byte]:	Note bitmask (defined in notes section)
		!NOTE TYPE (if the note bitmask declares this)
			[1 byte]:	Note type (1 = normal, 2 = tie, 3 = dead)
		!TIME INDEPENDENT DURATION (if the note bitmask declares this)
			[1 byte]:	Duration (-2 = whole note, -1 = half note, 0 = quarter note, 1 = eighth note, 2 = sixteenth note, 3 = thirty-second note, etc.)
			[1 byte]:	N-tuplet (ie. 3 for a triplet)
		!NOTE DYNAMIC (if the note bitmask declares this)
			[1 byte]:	How soft/strong the note is played ranging from pianissimo possibile (ppp, value of 1) to fortissimo possibile (fff, value of 8): ppp, pp, p, mp, mf, f, ff, fff
					(If the note dynamic is not defined, a value of 6, forte, is assumed)
		[1 byte]:	Fret number
		!RIGHT/LEFT HAND FINGERING (if the note bitmask declares this)
			[1 byte]:	Left hand fingering associated with this note (-1 = nothing, 0 = thumb, 1 = index, 2 = middle, 3 = ring, 4 = pinky)
			[1 byte]:	Right hand fingering associated with this note (-1 = nothing, 0 = thumb, 1 = index, 2 = middle, 3 = ring, 4 = pinky)
?					!UNKNOWN DATA/PADDING (if the file version is >= 5.0) (note:  This byte is verified to not be in 3.x/4.x version GP files)
			[1 byte]:	Unknown/padding	
		!NOTE EFFECT (if the note bitmask declares this)
			[1 byte]:	Note effect 1 bitmask (defined in notes section)
			!NOTE EFFECT 2 BITMASK (if the file version is >= 4.0)
				[1 byte]:	Note effect 2 bitmask (defined in notes section)
			!BEND (if the note effect 1 bitmask defines this)
				[varies]:	BEND CHUNK
			!GRACE NOTE (if the note effect 1 bitmask defines this)
				[1 byte]:	The grace note's fret number
				[1 byte]:	The dynamic of the grace note ranging from pianissimo possibile (ppp, value of 1) to fortissimo possibile (fff, value of 8): ppp, pp, p, mp, mf, f, ff, fff
						(If the dynamic is not defined, a value of 6, forte, is assumed)
				[1 byte]:	The grace note's transition type (0 = none, 1 = slide, 2 = bend, 3 = hammer)
				[1 byte]:	Duration of the grace note (3 = 16th note, 2 = 24th note, 1 = 32nd note)
			!TREMOLO PICKING (if the note effect 2 bitmask defines this)
				[1 byte]:	Picking speed (3 = 32nd note, 2 = 16th note, 1 = 8th note)
			!SLIDE (if the note effect 2 bitmask defines this)
				[1 byte]:	Slide type (-2 = slide into from above, -1 = slide into from below, 0 = no slide, 1 = shift slide, 2 = legato slide, 3 = slide out of downwards, 4 = slide out of upwards)
			!HARMONIC (if the note effect 2 bitmask defines this)
				[1 byte]:	Harmonic type (0 = none, 1 = Natural, 15 = Artificial+5, 17 = Artificial+7, 22 = Artificial+12, 3 = Tapped, 4 = Pitch, 5 = Semi)
			!TRILL (if the note effect 2 bitmask defines this)
				[1 byte]:	The fret being trilled with
				[1 byte]:	The period between each of the two trilled notes (0 = 4th note, 1 = 8th note, 2 = 16th note)

	*/
	int noteBitmask;
	int noteType;
	int duration;
	int nTuplet;
	int dynamicity;
	int fretNumber;
	int leftHandFingering;
	int rightHandFingering;
	byte[] unknown;
	int noteEffect1Bitmask;
	int noteEffect2Bitmask;
	BendChunk bendChunk;
	int graceNoteFret;
	int grateNoteDynamicity;
	int graceNoteTransitionType;
	int graceNoteDuration;
	int tremoloPickingSpeed;
	int slideType;
	int harmonicType;
	int trillFret;
	int trillPeriod;
}

class MixTableChange {
	int numberOfNewInstrument;
	int rseNumber1;
	int rseNumber2;
	int rseNumber3;
	byte[] unknown;
	int newVolume;
	int newPanValue;
	int newChorusValue;
	int newReverbValue;
	int newPhaserValue;
	int newTremoloValue;
	Field tempoStringData;
	int newTempo;
	int volumeChange;
	int panChange;
	int reverbChange;
	int chorusChange;
	int phaserChange;
	int tremoloChange;
	int tempoChange;
	int isTempoTextStringHidden;
	int mixTableAppliedTracksBitmask;
	byte[] unknown2;
	Field rseEffect2;
	Field rseEffect1;
}

class EffectsPresent {
	int beatEffectsBitmask;
	int extendedBeatEffectsBitmask;
	int tappingPoppingSlappingStatus;
	int tremoloStatus;
	int strokeEffect;
	int pickStrokeEffect;
	BendChunk bendChunk;
	int downstrokeSpeed;
	int upstrokeSpeed;
	int pickstrokeSpeed;
}

class ChordDiagram0 {
	Field chordNameString;
	
	int beginFret;
	/*
	FRET CHUNK, definitions for each of the track's defined strings (not padded to 7 strings), starting with the lowest numbered string, ie. high e (-1 = not played, 0 = played open):
		For each of the track's strings
		[4 bytes]:	The fret number at which this string is being pressed
	...(repeated data)... 
	*/
	int[] pressedFret;
}
class ChordDiagram1 {
	int displaySharpInsteadFlat;
	int chordRoot;
	byte[] unkonwn;
	int chordType;
	byte[] unknown2;
	int option;
	byte[] unknown3;
	int lowestNotePlayedInTheChord;
	int plusminusOption;
	byte[] unknown4;
	int chordNameStringLength;
	String chordName;
	int tonalityOfTheFifth;
	byte[] unknown5;
	int tonalityOfTheNinth;
	byte[] unknown6;
	int tonalityOfTheEleventh;
	byte[] unknown7;
	int chordDiagramBaseFretPosition;
	int[] fretChunk;
	int numberOfBarresInTheChord;
	int[] barrPosition;
	int[] barrFirstString;
	int[] barrLastString;
	int includeFirstInterval;
	int includeThirdInterval;
	int includeFifthInterval;
	int includeSeventhInterval;
	int includeNinthInterval;
	int includeEleventhInterval;
	int includeThirteenInterval;
	int[] fingeringChunk;
	int isChordFingeringDisplayed;
}
class BendChunk {
	int bendType;
	int bendHeight;
	int numberOfBendPoints;
	BendPointChunk[] bendPointChunk;
}
class BendPointChunk {
	int absoluteTimePosition;
	int verticalPosition;
	int vibratoType;
}

class ChordType {
	static final int _M = 0;
	static final int _7 = 1;
	static final int _7M = 2;
	static final int _6 = 3;
	static final int _m = 4;
	static final int _m7 = 5;
	static final int _m7M = 6;
	static final int _m6 = 7;
	static final int _sus2 = 8;
	static final int _sus4 = 9;
	static final int _7sus2 = 10;
	static final int _7sus4 = 11;
	static final int _dim = 12;
	static final int _aug = 13;
	static final int _5 = 14;
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
	ArrayList<BeatChunk> beatChunkList;
	
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
	public int readInt(int characters) {
		return byteArrayToInt(readBytes(characters));
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
						lyrics[i].string = new String(readBytes(lyrics[i].lyricLength), encoding);
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
			// TrackData
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
				masterReverb = readInt(4);
			}
			
			bars = readInt(4);
			tracks = readInt(4);
			
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
				barChunk[i].barBitmaskStr = toBinary(barChunk[i].barBitmask,8);
				if(barChunk[i].barBitmaskStr.charAt(7) == '1') {
					barChunk[i].tsNumerator = readChar();
				}
				if(barChunk[i].barBitmaskStr.charAt(6) == '1') {
					barChunk[i].tsDenominator = readChar();
				}
				if(barChunk[i].barBitmaskStr.charAt(2) == '1') {
					barChunk[i].sectionName = getContent41x();
					barChunk[i].sectionNameWith = readBytes(4);
				}
				if(barChunk[i].barBitmaskStr.charAt(1) == '1') {
					barChunk[i].key = readChar();
					barChunk[i].isMinor = readChar();
				}
				if(barChunk[i].barBitmaskStr.charAt(7) == '1' && barChunk[i].barBitmaskStr.charAt(6) == '1') {
					barChunk[i].beamEightNotesByValues = readBytes(4);
				}
				if(barChunk[i].barBitmaskStr.charAt(4) == '1') {
					barChunk[i].endOfRepeat = readChar();
				}
				if(barChunk[i].barBitmaskStr.charAt(3) == '1') {
					barChunk[i].numberOfAlternateEnding = readChar();
				}
				if(version >= 5) {
					readChar();
					barChunk[i].tripletFeel = readChar();
					readChar();
				}
			}
			for(int i=0;i<tracks;i++) {
				/*
				Bit 0 (LSB):	Drums track
				Bit 1:		12 stringed guitar track
				Bit 2:		Banjo track
				Bit 3:		Unknown
				Bit 4:		Marked for solo playback
				Bit 5:		Marked for muted playback
				Bit 6:		Use RSE playback (track instrument option)
				Bit 7:		Indicate tuning on the score (track properties)
				*/ 
				trackChunk[i] = new TrackChunk();
				trackChunk[i].trackBitmask = readChar();
				trackChunk[i].trackBitmaskStr = toBinary(trackChunk[i].trackBitmask,8);
				trackChunk[i].trackNameLength = readChar();
				trackChunk[i].trackName = new String(readBytes(trackChunk[i].trackNameLength), encoding);
				readBytes(40-trackChunk[i].trackNameLength);
				trackChunk[i].numberOfStrings = readInt(4);
				trackChunk[i].stringTuningChunk = new int[7];
				for(int j=0;j<7;j++) {
					trackChunk[i].stringTuningChunk[j] = readInt(4);
				}
				trackChunk[i].midiPortUsed = readInt(4);
				trackChunk[i].midiChannelUsed = readInt(4);
				trackChunk[i].midiChannelUsedForEffects = readInt(4);
				trackChunk[i].numberOfFrets = readInt(4);
				trackChunk[i].capoFret = readInt(4);
				trackChunk[i].trackColor = readBytes(4);
				if(version > 5) {
					trackChunk[i].trackSettings = new TrackSettings();
					trackChunk[i].trackSettings.bitmask1 = readChar();
					trackChunk[i].trackSettings.bitmask2 = readChar();
					trackChunk[i].trackSettings.unknown = readChar();
					trackChunk[i].trackSettings.midiBank = readChar();
					trackChunk[i].trackSettings.humanPlaying = readChar();
					trackChunk[i].trackSettings.autoAccentuationOnTheBeat = readChar();
					trackChunk[i].trackSettings.unknown2 = readBytes(31);
					trackChunk[i].trackSettings.selectedSoundBankOption = readChar();
					trackChunk[i].trackSettings.unknown3 = readBytes(7);
					trackChunk[i].trackSettings.lowFrequency = readChar();
					trackChunk[i].trackSettings.midFrequency = readChar();
					trackChunk[i].trackSettings.highFrequency = readChar();
					trackChunk[i].trackSettings.allFrequencies = readChar();
					trackChunk[i].trackSettings.instrumentEffect1 = getContent41x();
					System.out.println(trackChunk[i].trackSettings.instrumentEffect1.string);
					trackChunk[i].trackSettings.instrumentEffect2 = getContent41x();
				}
				if(version == 5) {
					readBytes(41);
				}
			}
			if(version >= 5) {
				readChar();
			}
			// BEAT CHUNK
			BeatChunk beatChunk = new BeatChunk();
			beatChunk.beatChunkTrack = new BeatChunkTrack[tracks];
			
			int voices = (version >= 5) ? 2: 1;
			
			for(int i=0;i<tracks;i++) {
				beatChunk.beatChunkTrack[i].voicChunk = new VoiceChunk[voices];
				for(int j=0;j<voices;j++) {
					beatChunk.beatChunkTrack[i].voicChunk[j] = new VoiceChunk();
					beatChunk.beatChunkTrack[i].voicChunk[j].numberOfBeats = readInt(4);
					for(int k=0;k<beatChunk.beatChunkTrack[i].voicChunk[j].numberOfBeats;k++) {
						beatChunk.beatChunkTrack[i].voicChunk[j].beatSubChunk[k] = new BeatSubChunk();
						BeatSubChunk b = beatChunk.beatChunkTrack[i].voicChunk[j].beatSubChunk[k];
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
						b.beatBitmask = readChar();
						String beatBitmaskString = toBinary(b.beatBitmask, 8);
						if(beatBitmaskString.charAt(1) == '1') {
							b.restType = readChar();
						}
						b.beatDuration = readChar();
						if(beatBitmaskString.charAt(2) == '1') {
							b.nTuplet = readInt(4);
						}
						if(beatBitmaskString.charAt(6) == '1') {
							b.chordDiagramFormat = readChar();
							// !CHORD DIAGRAM FORMAT 0 (if the format identifier was 0, ie. GP3 format):
							if(b.chordDiagramFormat == 0) {
								b.chordDiagram0 = new ChordDiagram0();
								b.chordDiagram0.chordNameString = getContent41x();
								b.chordDiagram0.beginFret = readInt(4);
								b.chordDiagram0.pressedFret = new int[trackChunk[i].numberOfStrings];
								for(int l=0;l<trackChunk[i].numberOfStrings;l++) {
									b.chordDiagram0.pressedFret[l] = readInt(4);
								}
							}
							// !CHORD DIAGRAM FORMAT 1 (if the format identifier was 1, ie. GP4 format) (105 bytes):
							if(b.chordDiagramFormat == 1) {
								b.chordDiagram1 = new ChordDiagram1();
								b.chordDiagram1.displaySharpInsteadFlat = readChar();
								readBytes(3);
								b.chordDiagram1.chordRoot = readChar();
								b.chordDiagram1.unkonwn = readBytes(3);
								b.chordDiagram1.chordType = readChar();
								b.chordDiagram1.unknown2 = readBytes(3);
								b.chordDiagram1.option = readChar();
								b.chordDiagram1.unknown2 = readBytes(3);
								b.chordDiagram1.lowestNotePlayedInTheChord = readInt(4);
								b.chordDiagram1.plusminusOption = readChar();
								b.chordDiagram1.unknown3 = readBytes(4);
								b.chordDiagram1.chordNameStringLength = readChar();
								b.chordDiagram1.chordName = new String(readBytes(b.chordDiagram1.chordNameStringLength),encoding);
								readBytes(20-b.chordDiagram1.chordNameStringLength);
								readBytes(2);
								b.chordDiagram1.tonalityOfTheFifth = readChar();
								readBytes(3);
								b.chordDiagram1.tonalityOfTheNinth = readChar();
								readBytes(3);
								b.chordDiagram1.tonalityOfTheEleventh = readChar();
								readBytes(3);
								b.chordDiagram1.chordDiagramBaseFretPosition = readInt(4);
								b.chordDiagram1.fretChunk = new int[7];
								for(int l=0;l<7;l++) {
									b.chordDiagram1.fretChunk[l] = readInt(4);
								}
								b.chordDiagram1.numberOfBarresInTheChord = readChar();
								b.chordDiagram1.barrPosition = new int[5];
								for(int l=0;l<5;l++) {
									b.chordDiagram1.barrPosition[l] = readChar();
								}
								b.chordDiagram1.barrFirstString = new int[5];
								for(int l=0;l<5;l++) {
									b.chordDiagram1.barrFirstString[l] = readChar();
								}
								b.chordDiagram1.barrLastString = new int[5];
								for(int l=0;l<5;l++) {
									b.chordDiagram1.barrLastString[l] = readChar();
								}
								b.chordDiagram1.includeFirstInterval = readChar();
								b.chordDiagram1.includeThirdInterval = readChar();
								b.chordDiagram1.includeFifthInterval = readChar();
								b.chordDiagram1.includeSeventhInterval = readChar();
								b.chordDiagram1.includeNinthInterval = readChar();
								b.chordDiagram1.includeEleventhInterval = readChar();
								readChar();
								
								b.chordDiagram1.fingeringChunk = new int[7];
								for(int l=0;l<7;l++) {
									b.chordDiagram1.fingeringChunk[l] = readChar();
								}
								b.chordDiagram1.isChordFingeringDisplayed = readChar();
							}
						}
						if(beatBitmaskString.charAt(5) == '1') {
							b.beatText = getContent41x();
						}
						if(beatBitmaskString.charAt(4) == '1') {
							/*
							Byte 1
							 Bits 0 (LSB) - 4:	Unknown
							 Bit 5:			Tapping, popping or slapping effect
							 Bit 6:			Stroke effect
							 Bit 7 (MSB):		Unused (set to 0)
							Byte 2 (extended beat effects, only if the major file version is >= 4):
							 Bit 0 (LSB):		Rasguedo
							 Bit 1:			Pickstroke
							 Bit 2:			Tremolo bar
							 Bits 3 - 7:		Unused (set to 0)
							*/
							
							b.effectsPresent = new EffectsPresent();
							b.effectsPresent.beatEffectsBitmask = readChar();
							String beatEffectsBitmaskString = toBinary(b.effectsPresent.beatEffectsBitmask,8);
							String extendedEffectsBitmaskString = null;
							if(version >= 4) {
								b.effectsPresent.extendedBeatEffectsBitmask = readChar();
								extendedEffectsBitmaskString = toBinary(b.effectsPresent.extendedBeatEffectsBitmask,8);
							}
							if(beatEffectsBitmaskString.charAt(2) == '1') {
								b.effectsPresent.tappingPoppingSlappingStatus = readChar();
							}
							if(version >= 4) {
								if(extendedEffectsBitmaskString.charAt(5) == '1') {
									/*
									[1 byte]:	The type of bend (0 = none, 1 = bend, 2 = bend and release, 3 = bend->release->bend, 4 = prebend, 5 = prebend and release,
										6 = tremolo dip, 7 = tremolo dive, 8 = tremolo release [up], 9 = tremolo inverted dip, 10 = tremolo return, 11 = tremolo release [down])
									[4 bytes]:	Bend height, measured in how much the pitch changes in cents (where the distance between two semi tones is 100 cents)
									[4 bytes]:	Number of points to display the bend with
									BEND POINT CHUNK, for each bend point
										[4 bytes]:	Absolute time position relative to previous bend point (Valued 0 through 60, in sixtieths of the note's duration)
										[4 bytes]:	Vertical position (Pitch alteration from normal note, in intervals of 25 cents)
										[1 byte]:	Vibrato type (0 = none, 1 = fast, 2 = average, 3 = slow)
									 */
									b.effectsPresent.bendChunk = new BendChunk();
									b.effectsPresent.bendChunk.bendType = readChar();
									b.effectsPresent.bendChunk.bendHeight = readInt(4);
									b.effectsPresent.bendChunk.numberOfBendPoints = readInt(4);
									for(int l=0;l<b.effectsPresent.bendChunk.numberOfBendPoints;l++) {
										b.effectsPresent.bendChunk.bendPointChunk[l] = new BendPointChunk();
										b.effectsPresent.bendChunk.bendPointChunk[l].absoluteTimePosition = readInt(4);
										b.effectsPresent.bendChunk.bendPointChunk[l].verticalPosition = readInt(4);
										b.effectsPresent.bendChunk.bendPointChunk[l].vibratoType = readChar();
									}
								}
							}
							if(beatEffectsBitmaskString.charAt(1) == '1') {
								b.effectsPresent.downstrokeSpeed = readChar();
								b.effectsPresent.upstrokeSpeed = readChar();
								b.effectsPresent.pickstrokeSpeed = readChar();
							}
						}
						if(beatBitmaskString.charAt(3) == '1') {
							b.mixTableChange = new MixTableChange();
							b.mixTableChange.numberOfNewInstrument = readChar();
							b.mixTableChange.rseNumber1 = readInt(4);
							b.mixTableChange.rseNumber2 = readInt(4);
							b.mixTableChange.rseNumber3 = readInt(4);
							b.mixTableChange.unknown = readBytes(4);
							b.mixTableChange.newVolume = readChar();
							b.mixTableChange.newPanValue = readChar();
							b.mixTableChange.newChorusValue = readChar();
							b.mixTableChange.newReverbValue = readChar();
							b.mixTableChange.newPhaserValue = readChar();
							b.mixTableChange.newTremoloValue = readChar();
							b.mixTableChange.tempoStringData = getContent41x();
							b.mixTableChange.newTempo = readInt(4);
							b.mixTableChange.volumeChange = readChar();
							b.mixTableChange.panChange = readChar();
							b.mixTableChange.chorusChange = readChar();
							b.mixTableChange.reverbChange = readChar();
							b.mixTableChange.phaserChange = readChar();
							b.mixTableChange.tremoloChange = readChar();
							b.mixTableChange.tempoChange = readChar();
							if(b.mixTableChange.tempoChange != 0) {
								b.mixTableChange.isTempoTextStringHidden = readChar();
							}
							if(version >= 4) {
								b.mixTableChange.mixTableAppliedTracksBitmask = readChar();
							}
							if(version >= 5) {
								b.mixTableChange.unknown2 = readBytes(1);
							}
							if(version >= 5) {
								b.mixTableChange.rseEffect2 = getContent41x();
								b.mixTableChange.rseEffect1 = getContent41x();
							}
						}
						b.usedStringMask = readChar();
						String usedString = toBinary(b.usedStringMask,8);
						
						b.stringChunk = new StringChunk[trackChunk[i].numberOfStrings];
						for(int l=0;i<trackChunk[i].numberOfStrings;i++) {
							b.stringChunk[l] = new StringChunk();
							b.stringChunk[l].noteBitmask = readChar();
							/*
							Bit 0 (LSB):	Time-independent duration
							Bit 1:		Dotted note
							Bit 2:		Ghost note
							Bit 3:		Note effects present
							Bit 4:		Note dynamic
							Bit 5:		Note type
							Bit 6:		Accentuated note
							Bit 7:		Right/Left hand fingering 
							 */
							String noteBitMaskString = toBinary(b.stringChunk[l].noteBitmask,8);
							if(noteBitMaskString.charAt(2) == 1) {
								b.stringChunk[l].noteType = readChar();
							}
							if(noteBitMaskString.charAt(7) == 1) {
								b.stringChunk[l].duration = readChar();
								b.stringChunk[l].nTuplet = readChar();
							}
							if(noteBitMaskString.charAt(3) == 1) {
								b.stringChunk[l].dynamicity = readChar();
							}
							if(noteBitMaskString.charAt(1) == 1) {
								b.stringChunk[l].leftHandFingering = readChar();
								b.stringChunk[l].rightHandFingering = readChar();
							}
							if(version >= 5) {
								b.stringChunk[l].unknown = readBytes(1);
							}
							if(noteBitMaskString.charAt(4) == 1) {
								b.stringChunk[l].noteEffect1Bitmask = readChar();
								if(version >= 4) {
									b.stringChunk[l].noteEffect2Bitmask = readChar();
								}
								b.stringChunk[l].bendChunk = new BendChunk();
								b.stringChunk[l].bendChunk.bendType = readChar();
								b.stringChunk[l].bendChunk.bendHeight = readInt(4);
								b.stringChunk[l].bendChunk.numberOfBendPoints = readInt(4);
//								for(int m=0;m<b.stringChunk[l].bendChunk.numberOfBendPoints;l++) {
//									b.stringChunk[l].bendChunk.bendPointChunk[l] = new BendPointChunk();
//									b.stringChunk[l].bendChunk.bendPointChunk[l].absoluteTimePosition = readInt(4);
//									b.stringChunk[l].bendChunk.bendPointChunk[l].verticalPosition = readInt(4);
//									b.stringChunk[l].bendChunk.bendPointChunk[l].vibratoType = readChar();
//								}
							}
						}
						
					}
				}
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

	
