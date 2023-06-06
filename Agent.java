import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.Semaphore;

public class Agent implements Runnable {
	// Agent ID and customer being served
	private int agentID;
	private int customer;
	
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
	
	public Agent(int agentID, int customer,
				
				Semaphore mutex1, Semaphore mutex2, 
					
				Semaphore roomInLine, Semaphore[] agentAvailable, 
				
				Semaphore customerEntered, Semaphore numbered, Semaphore waiting, Semaphore called,
				Semaphore waitLine, Semaphore inLine, Semaphore serveCustomer, 
				Semaphore beingServed, Semaphore agentAsksExam, Semaphore customerTakesExam, 
				Semaphore giveLicense, Semaphore getsLicense, 
				
				Semaphore[] finished, 
				
				Queue<Customer> entry, Queue<Customer> waitingRoom, Queue<Customer> line, 
				Queue<Customer> served) {
				
		// Agent ID and customer being served
		this.agentID = agentID;
		this.customer = customer;
		
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
		while (true) {
			try {
				SemFunctions.wait(inLine); // Wait till customer is in agent line
				// Critical section
				SemFunctions.wait(mutex1);
				serve();
				SemFunctions.signal(mutex1);
				// 				
				SemFunctions.signal(serveCustomer); // Signal to customer for service
				
				SemFunctions.wait(beingServed); // Wait until customer receives service
				// Critical section
				SemFunctions.wait(mutex1);
				askExam();
				SemFunctions.signal(mutex1); 
				// 				
				SemFunctions.signal(agentAsksExam); // Signal to customer to take exams
				
				
				SemFunctions.wait(customerTakesExam); // Wait until customer finishes exams
				// Critical section
				SemFunctions.wait(mutex1);
				giveLicense();
				SemFunctions.signal(mutex1);
				// 				
				SemFunctions.signal(giveLicense); // Signal to customer to give license
				
				SemFunctions.wait(getsLicense);			
				SemFunctions.signal(finished[this.customer]);
				
				SemFunctions.wait(agentAvailable[this.agentID]);
				SemFunctions.signal(roomInLine); // Wait until customer receives license
			}
			catch (Exception e) {
			}
		}
	}
	
	public void serve() {
		// Take out of agent line and assign agent
		Customer custo = line.poll();
		custo.setAgent(this.agentID);
		
		// Served by agent
		served.add(custo);
		System.out.println("Agent " + this.agentID + " is serving customer " + custo.customerID);
	}
	
	public void askExam() {
		// Customer is being directly served
		Customer custo = served.poll();
		this.customer = custo.customerID;
		
		// Print statement
		System.out.println("Agent " + this.agentID + " asks customer " 
							+ this.customer + " to take photo and eye exam");
	}
	
	public void giveLicense() {
		// Give license to customer
		System.out.println("Agent " + this.agentID + " gives license to customer " 
							+ this.customer);
	}
}