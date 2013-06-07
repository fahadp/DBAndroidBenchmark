package tools;

public class TaskMessage {

	public final String task;
	public final long time;
	public final String message;
	
	public TaskMessage(String task, long time, String message) {
		this.task = task;
		this.time = time;
		this.message = message;
	}
	
	public String toString() {
		return String.format("Task: %s -- %s (%s)\n  %s",this.task,this.time,this.message,this.time/60.0);
	}
}