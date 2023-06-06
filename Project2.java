import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.Semaphore;

public class Project2 {
	// Mutex semaphores
	private static Semaphore mutex1 = new Semaphore(1);
	private static Semaphore mutex2 = new Semaphore(1);
	
	// Line and agent capacity
	private static Semaphore roomInLine = new Semaphore(4);
	private static Semaphore[] agentAvailable = new Semaphore[2];
	
	// Other communication semaphores
	private static Semaphore customerEntered = new Semaphore(0);
	private static Semaphore numbered = new Semaphore(0);
	private static Semaphore waiting = new Semaphore(0);
	private static Semaphore called = new Semaphore(0);
	private static Semaphore waitLine = new Semaphore(0);
	private static Semaphore inLine = new Semaphore(0);
	private static Semaphore serveCustomer = new Semaphore(0);
	private static Semaphore beingServed = new Semaphore(0);
	private static Semaphore agentAsksExam = new Semaphore(0);
	private static Semaphore customerTakesExam = new Semaphore(0);
	private static Semaphore giveLicense = new Semaphore(0);
	private static Semaphore getsLicense = new Semaphore(0);
	
	// Semaphore array for finished customers
	private static Semaphore[] finished = new Semaphore[20];
	
	private static Queue<Customer> entry = new LinkedList<>();
	private static Queue<Customer> waitingRoom = new LinkedList<>();
	private static Queue<Customer> line = new LinkedList<>();
	private static Queue<Customer> served = new LinkedList<>();

	public static void main(String args[]) {
		final int numCustomers = 20;
		final int numAgents = 2;
		
		// Nothing is finished to begin with
		for (int i = 0; i < numCustomers; i++) {
			finished[i] = new Semaphore(0);
		}
		
		// All agents is available
		for (int i = 0; i < numAgents; i++) {
			agentAvailable[i] = new Semaphore(0);
		}

		// Information desk object and thread
		InformationDesk infoDesk = new InformationDesk(0, 
								
								mutex1, mutex2, 
							
								roomInLine, agentAvailable,
								
								customerEntered, numbered, waiting, called, waitLine,
								inLine, serveCustomer, beingServed, agentAsksExam, customerTakesExam, 
								giveLicense, getsLicense, 
								
								finished, 
								
								entry, waitingRoom, line, served);
		
		Thread infoDeskThread = new Thread(infoDesk);
		infoDeskThread.start();
		System.out.println("Information desk created");
		
		// Announcer object and thread
		Announcer announcer = new Announcer(mutex1, mutex2, 
							
							roomInLine, agentAvailable,
							
							customerEntered, numbered, waiting, called, waitLine,
							inLine, serveCustomer, beingServed, agentAsksExam, customerTakesExam, 
							giveLicense, getsLicense,  
							
							finished, 
							
							entry, waitingRoom, line, served);
		
		Thread announcerThread = new Thread(announcer);
		announcerThread.start();
		System.out.println("Announcer created");
		
		// Agent objects and threads 
		Agent agents[] = new Agent[numAgents];
		Thread agentThread[] = new Thread[numAgents];

		// Create custumer threads
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new Agent(i, -1,
						
						mutex1, mutex2, 
							
						roomInLine, agentAvailable,
						
						customerEntered, numbered, waiting, called, waitLine,
						inLine, serveCustomer, beingServed, agentAsksExam, customerTakesExam, 
						giveLicense, getsLicense, 
						
						finished, 
						
						entry, waitingRoom, line, served);
						
			agentThread[i] = new Thread(agents[i]);
			System.out.println("Agent " + i + " created");
			
			agentThread[i].setDaemon(true);
			agentThread[i].start();
		}
		
		// Customer objects and threads 
		Customer customers[] = new Customer[numCustomers];
		Thread customerThread[] = new Thread[numCustomers];

		// Create custumer threads
		for (int i = 0; i < numCustomers; ++i) {
			customers[i] = new Customer(i, -1, -1,
							
							mutex1, mutex2, 
							
							roomInLine, agentAvailable,
							
							customerEntered, numbered, waiting, called, waitLine, 
							inLine, serveCustomer, beingServed, agentAsksExam, customerTakesExam, 
							giveLicense, getsLicense,  
							
							finished, 
							
							entry, waitingRoom, line, served);
							
			customerThread[i] = new Thread(customers[i]);
			
			customerThread[i].setDaemon(true);
			customerThread[i].start();
		}

		// Release customer threads
		for (int i = 0; i < numCustomers; ++i) {
			try {
				customerThread[i].join();
			}
			catch (InterruptedException e) {
			}
		}
		
		// Program ends
		System.out.println("Done");
		System.exit(0);
	}
}