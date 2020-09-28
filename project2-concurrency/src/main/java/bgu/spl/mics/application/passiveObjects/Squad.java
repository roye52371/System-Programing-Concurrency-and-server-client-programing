package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.application.subscribers.Moneypenny;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private static Squad squadInstance = new Squad();

	private Map<String, Agent> squad;

	private Squad() {
		squad = new HashMap<String, Agent>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return squadInstance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for (int i = 0; i < agents.length; i++) {
			if (agents[i] != null)
				this.squad.put(agents[i].getSerialNumber(), agents[i]);
		}
	}

	/**
	 * Releases agents.
	 */
	public synchronized void releaseAgents(List<String> serials){
		for (int i = 0; i < serials.size(); i++) {
			if(this.squad.containsKey(serials.get(i)))
				this.squad.get(serials.get(i)).release();
		}
		notifyAll();
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time * 100);
		} catch (Exception e) { }
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ג€˜falseג€™ if an agent of serialNumber ג€˜serialג€™ is missing, and ג€˜trueג€™ otherwise
	 */
	public synchronized boolean getAgents(List<String> serials) {
		List<String> mySerials = new LinkedList<>();
		for (int i = 0; i < serials.size(); i++) {
			String s = serials.get(i);
			mySerials.add(s);
		}
		for (int i = 0; i < mySerials.size(); i++)
			if(!squad.containsKey(mySerials.get(i)))
				return false;
		int size = mySerials.size();
		for (int i = 0; i < size; i++) {
			Agent agent = findSmallestAgent(mySerials);
			mySerials.remove(agent.getSerialNumber());
			if (agent.isAvailable())
				agent.acquire();
			else {
				while (!agent.isAvailable() & !Moneypenny.isOneMoneypennyTerminated()) {
					try {
						this.wait();
					} catch (InterruptedException ignored) { }
				}
				agent.acquire();
			}
		}
		return true;
	}

	private Agent findSmallestAgent(List<String> serials) {
		Agent agent = squad.get(serials.get(0));
		for (int i = 1; i < serials.size(); i++)
			if(serials.get(i).compareTo(agent.getSerialNumber()) < 0)
				agent = squad.get(serials.get(i));
		return agent;
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> agentsNames = new LinkedList<>();
		for (int i = 0; i < serials.size(); i++)
			if(this.squad.containsKey(serials.get(i)))
				agentsNames.add(this.squad.get(serials.get(i)).getName());
		return agentsNames;
	}

}
