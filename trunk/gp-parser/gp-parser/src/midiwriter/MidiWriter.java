package midiwriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MidiWriter {
	private StringBuilder sb;
	private File target;
	private int mTracks;
	private int mFormat;
	
	public MidiWriter(File f) {
		target = f;
		FileOutputStream fos;
		sb = new StringBuilder();
		
	}
	public void setTracks(int tracks) {
		mTracks = tracks;
	}
	public void setFormat(int format) {
		mFormat = format;
	}
	public void write() {
		if(target.exists()) {
			if(target.exists()) {
				System.out.println("Target file already exists.");
			}
			else {
				try {
					target.createNewFile();
					sb.append(new MidiHeader(mTracks, mFormat).getHeader());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		File f = new File("d:\\test.midi");
		MidiWriter mr = new MidiWriter(f);
		
	}
}

class MidiHeader {
	StringBuilder sb;
	public MidiHeader(int mTracks, int mFormat) {
		sb = new StringBuilder();
	}
	public String getHeader() {
		
		sb.append("MThd");
		sb.append(0x00);
		sb.append(0x00);
		sb.append(0x00);
		sb.append(0x06);
		
		return sb.toString();
	}
}