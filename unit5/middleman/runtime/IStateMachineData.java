package runtime;

public interface IStateMachineData {
	
	public static final int 
		EXECUTE_TRANSITION = 0, 
		DISCARD_EVENT = 1,
		TERMINATE_SYSTEM = 2;
	
	public int fire(String event, Object object, Scheduler scheduler);
	
}
