import javax.sound.midi.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class BeatBox {
	JFrame frame;
	JPanel mainPanel;
	ArrayList<JCheckBox> checkBoxList;
	
	Sequencer sequencer;
	Sequence seq;
	Track track;
	
	public String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
			"Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
			"High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
			"Cowbell", "Vibraslap", "Low-mid Tom", "High Agro", "Open High Conga"};
	public int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
	
	public static void main(String[] args) {
		new BeatBox().BuildGUI();
	}
	public void BuildGUI() {
		frame = new JFrame("Cyber BeatBox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		checkBoxList = new ArrayList<JCheckBox>();
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		
		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		JButton upTempo = new JButton("Up Tempo");
		upTempo.addActionListener(new UpTempoListener());
		buttonBox.add(upTempo);
		
		JButton downTempo = new JButton("Down Tempo");
		downTempo.addActionListener(new DownTempoListener());
		buttonBox.add(downTempo);
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ClearListener());
		buttonBox.add(clear);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for (int i = 0; i < 16; i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}
		background.add(BorderLayout.EAST, buttonBox);
		background.add(BorderLayout.WEST, nameBox);
		
		frame.getContentPane().add(background);
		
		GridLayout grid = new GridLayout(16, 16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainPanel);
		
		for (int i = 0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkBoxList.add(c);
			mainPanel.add(c);
		}
		
		setUpMidi();
		
		frame.setBounds(50,50,300,300);
		frame.pack();
		frame.setVisible(true);
	}

	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			seq = new Sequence(Sequence.PPQ, 4);
			track = seq.createTrack();
			sequencer.setTempoInBPM(120);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildTrackAndStart() {
		int[] trackList = null;
		
		seq.deleteTrack(track);
		track = seq.createTrack();
		
		createTrackList();
		track.add(makeEvent(192, 9, 1, 0, 15));
		
		try {
			sequencer.setSequence(seq);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}
	private void createTrackList() {
		int[] trackList;
		for (int i = 0; i < 16; i++) {
			trackList = new int[16];
			
			int key = instruments[i];
			
			for (int j = 0; j < 16; j++) {
				JCheckBox jc = checkBoxList.get(j + 16*i);
				if (jc.isSelected()) {
					trackList[j] = key;
				} else {
					trackList[j] = 0;
				}
			}
			
			makeTrack(trackList);
			
			track.add(makeEvent(176, 1, 127, 0, 16));
		}
	}
	
	private void makeTrack(int[] trackList) {
		for (int i = 0; i < 16; i++) {
			int key = trackList[i];
			if (key != 0) {
				track.add(makeEvent(144, 9, key, 100, i));
				track.add(makeEvent(128, 9, key, 100, i + 1));
			}
		}
	}

	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return event;
	}
	
	public class MyStartListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}
	public class MyStopListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}
	public class UpTempoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor * 1.03));
		}
	}
	public class DownTempoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor * .97));
		}
	}
	public class ClearListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent a) {
			for (JCheckBox box : checkBoxList) {
				box.setSelected(false);
			}
		}
	}
}


