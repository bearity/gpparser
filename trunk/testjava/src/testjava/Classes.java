package testjava;

class Version {
	final static String GUITAR_PRO_510 = "FICHIER GUITAR PRO v5.10";
	final static String GUITAR_PRO_5 = "FICHIER GUITAR PRO v5.00";
	final static String GUITAR_PRO_4_06 = "FICHIER GUITAR PRO v4.06";
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
	VoiceChunk[] voiceChunk;
}

class VoiceChunk {
	int numberOfBeats;
	BeatSubChunk[] beatSubChunk;
}

class BeatSubChunk {
	int beatBitmask;
	int restType;
	int beatDuration;
	byte[] nTuplet;
	//!CHORD DIAGRAM (if the beat bitmask declares that a chord diagram is present):
	ChordDiagram0 chordDiagram0;
	ChordDiagram1 chordDiagram1;
	int chordDiagramFormat;
	Field beatText;
	EffectsPresent effectsPresent;
	MixTableChange mixTableChange;
	int usedStringMask;
	StringChunk[] stringChunk;
	byte[] noteTransposeBitmask;
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
	int unknown;
	int noteEffect1Bitmask;
	int noteEffect2Bitmask;
	BendChunk bendChunk;
	int graceNoteFret;
	int grateNoteDynamicity;
	int graceNoteTransitionType;
	int graceNoteDuration;
	int graceNotePosition;
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
	int unknown;
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
	int unknown2;
	Field rseEffect2;
	Field rseEffect1;
}

class EffectsPresent {
	int beatEffectsBitmask;
	int extendedBeatEffectsBitmask;
	int stringEffect;
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
	int includeThirteenthInterval;
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