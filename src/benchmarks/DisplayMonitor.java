package benchmarks;

import java.util.concurrent.ArrayBlockingQueue;

import android.R.menu;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import tools.TaskMessage;

public class DisplayMonitor implements Runnable  {
	
	private ArrayBlockingQueue<TaskMessage> queue;
	private TextView view;
	private Handler handler;
	
	public DisplayMonitor(ArrayBlockingQueue<TaskMessage> queue, TextView view ) {
		this.queue = queue;
		this.view = view;
		this.handler= new Handler();
	}
	
	@Override
	public void run() {
		
		this.handler.post(new Runnable() {
			public void run() {
				view.setText("");
			}
		});
		
		TaskMessage m = null;
		do {
			try {
				m = this.queue.take();
				final String s = m.toString()+"\n";
				//We have to use a handler because only the main thread can update a view.
				this.handler.post(new Runnable() {
					public void run() {
						view.append(s);
						view.invalidate();
					}
				});
				//Log.i("DISPLAY","Got message: "+m);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}while(!m.task.equals(TestHarness.DONE));
		Log.i("DISPLAY","DONE");
	}

}
