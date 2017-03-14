package scaledmarkets.dabl.exec;

public class PretendTaskContainer extends TaskContainer {
	
	public void executeTask(Task task) throws Exception {
		System.out.println("Task " + task.getName() + " would be executed");
	}
}
