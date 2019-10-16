package datastorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import runtime.SchedulerData;

public class WriteFile {
	
	public java.io.File file;
	BufferedWriter bufferedWriter;
	FileWriter fileWriter;
	SchedulerData scheduler;

	public WriteFile(File f, SchedulerData sched) {
		scheduler = sched;
		file = f;
		try {
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			System.err.println("WriteFile IO exception: " + e);
			scheduler.addToQueueLast("WriteFileError");
		}
	}
	
	public void writeStringToFile(String s) {
		try {
			bufferedWriter.write(s);
		} catch (IOException e) {
			scheduler.addToQueueLast("WriteFileError");
		}
	}

	public void closeFile() {
		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			scheduler.addToQueueLast("WriteFileError");
		}
	}


}
