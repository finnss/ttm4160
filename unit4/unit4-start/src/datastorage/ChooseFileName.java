package datastorage;

import java.io.*;
import javax.swing.*;
import runtime.SchedulerData;

public class ChooseFileName {
	
	Runnable r;
	boolean saveDialog;
	SchedulerData scheduler;
	
	public ChooseFileName(boolean save, SchedulerData sched) {
		saveDialog = save;
		scheduler = sched;
		r = new Runnable() {
			@Override
			public void run() {
				JFileChooser fc = new JFileChooser();
				int returnVal;
				if (saveDialog) {
					returnVal = fc.showSaveDialog(null);
				} else {
					returnVal = fc.showOpenDialog(null);
				}
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					scheduler.addToQueueLast("ChooseFilenameSave", file);
				} else {
					scheduler.addToQueueLast("ChooseFilenameCancel");
				}
			}
		};
		SwingUtilities.invokeLater(r);
	}


}
