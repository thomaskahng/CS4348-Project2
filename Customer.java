import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.Semaphore;

// acquire = wait
// release = signal

public class Customer implements Runnable {
	// Customer ID, number of order, and agent
	int customerID;
	int number;
	int agent;
	
	// Mutex semaphores
	private Semaphore mutex1;
	private Semaphore mutex2;
	
	private Semaphore roomInLine;
	private Semaphore[] agentAvailable;
	
	// Semaphores for all cooperations
	private Semaphore customerEntered;
	private Semaphore numbered;
	private Semaphore waiting;
	private Semaphore called;
	private Semaphore waitLine;
	private Semaphore inLine;
	private Semaphore serveCustomer;
	private Semaphore beingServed;
	private Semaphore agentAsksExam;
	private Semaphore customerTakesExam;
	private Semaphore giveLicense;
	private Semaphore getsLicense;
	
	// Semaphore array for finished customers
	private Semaphore[] finished;
	
	// Queues
	private Queue<Customer> entry;
	private Queue<Customer> waitingRoom;
	private Queue<Customer> line;
	private Queue<Customer> served;

	public Customer(int customerID, int number, int agent,
	
					Semaphore mutex1, Semaphore mutex2, 
					
					Semaphore roomInLine, Semaphore[] agentAvailable, 
					
					Semaphore customerEntered, Semaphore numbered, Semaphore waiting, Semaphore called,
					Semaphore waitLine, Semaphore inLine, Semaphore serveCustomer, 
					Semaphore beingServed, Semaphore agentAsksExam, Semaphore customerTakesExam, 
					Semaphore giveLicense, Semaphore getsLicense, 
					
					Semaphore[] finished, 
					
					Queue<Customer> entry, Queue<Customer> waitingRoom, Queue<Customer> line, 
					Queue<Customer> served) {
					
		// Customer ID, number of order, and agent
		this.customerID = customerID;
		this.number = number;
		this.agent = agent;
		
		// Mutex semaphores
		this.mutex1 = mutex1;
		this.mutex2 = mutex2;
		
		// Line and agent capacity
		this.roomInLine = roomInLine;
		this.agentAvailable = agentAvailable;
		
		// Semaphores for all cooperations
		this.customerEntered = customerEntered;
		this.numbered = numbered;
		this.waiting = waiting;
		this.called = called;
		this.waitLine = waitLine;
		this.inLine = inLine;
		this.serveCustomer = serveCustomer;
		this.beingServed = beingServed;
		this.agentAsksExam = agentAsksExam;
		this.customerTakesExam = customerTakesExam;
		this.giveLicense = giveLicense;
		this.getsLicense = getsLicense;
		
		// Semaphore array for finished customers
		this.finished = finished;
		
		// Queues
		this.entry = entry;
		this.waitingRoom = waitingRoom;
		this.line = line;
		this.served = served;
	}
	
	@Override
	public void run() {
		try {
			SemFunctions.wait(mutex1); 
			enter();
			SemFunctions.signal(mutex1); 
			
			SemFunctions.signal(customerEntered); // Signal to info desk a customer entered DMV
			
			SemFunctions.wait(numbered); // Wait until info desk gives number
			// Critical section
			SemFunctions.signal(waiting); // Signal to announcer that customer's in waiting room
			
			SemFunctions.wait(called);	// Wait until announcer calls name
			// Critical section
			SemFunctions.signal(waitLine); // Signal to announcer that customer's coming to agent line
			
			SemFunctions.wait(serveCustomer); // Wait until agent serves customer
			// Critical section
			SemFunctions.wait(mutex1);
			beingServed();
			SemFunctions.signal(mutex1);
			// 
			SemFunctions.signal(beingServed); // Signal to agent that customer is being served
			
			SemFunctions.wait(agentAsksExam); // Wait until agent asks to take exams
			// Critical section
			SemFunctions.wait(mutex1);
			takeExam();
			SemFunctions.signal(mutex1);
			// 
			SemFunctions.signal(customerTakesExam); // Signal to agent the customer took the exams
		
			SemFunctions.wait(giveLicense); // Wait until agent gives license
			// Critical section
			SemFunctions.wait(mutex1);
			getLicense();
			SemFunctions.signal(mutex1);
			// 		
			SemFunctions.signal(getsLicense); // Signal to agent that customer received license
			
			SemFunctions.wait(finished[this.customerID]); // Wait until agent is done with the customer
			// Critical section
			SemFunctions.wait(mutex1);
			finish();
			SemFunctions.signal(mutex1);
			// 					
			SemFunctions.signal(agentAvailable[this.agent]); // Signal to agent that the agent is now available
		}
		
		catch (Exception e) {
		}
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public void setAgent(int agent) {
		this.agent = agent;
	}
	
	public void enter() {
		// Enters DMV
		entry.add(this);
		System.out.println("Custumer " + customerID + " created, enters DMV"); 
	}

	public void beingServed() {
		// Being served by agent
		System.out.println("Customer " + this.customerID + " is being served by agent " + this.agent);
	}
	
	public void takeExam() {
		// Took exam from agent
		System.out.println("Customer " + this.customerID + " completes photo and eye exam for agent " 
							+ this.agent);
	}
	
	public void getLicense() {
		// Got license from agent
		System.out.println("Customer " + this.customerID + " gets license and departs");
	}
	
	public void finish() {
		// Finished getting license 
		System.out.println("Customer " + this.customerID + " was joined");
	}
}