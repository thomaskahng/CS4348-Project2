import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.Semaphore;

public class InformationDesk implements Runnable {
	// Numbering of customers
	private int num;
	
	// Mutex semaphores
	private Semaphore mutex1;
	private Semaphore mutex2;
	
	// Line and agent capacity
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
	
	public InformationDesk(int num, 
						
					Semaphore mutex1, Semaphore mutex2, 
					
					Semaphore roomInLine, Semaphore[] agentAvailable, 
					
					Semaphore customerEntered, Semaphore numbered, Semaphore waiting, Semaphore called,
					Semaphore waitLine, Semaphore inLine, Semaphore serveCustomer, 
					Semaphore beingServed, Semaphore agentAsksExam, Semaphore customerTakesExam, 
					Semaphore giveLicense, Semaphore getsLicense, 
					
					Semaphore[] finished, 
					
					Queue<Customer> entry, Queue<Customer> waitingRoom, Queue<Customer> line, 
					Queue<Customer> served) {		
					
		// Numbering of customers
		this.num = num;
		
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
				SemFunctions.wait(customerEntered); // Wait for customer to enter
				// Critical section
				
				// Assign a number and increment number
				SemFunctions.wait(mutex1);
				giveNumber();
				++num;
				SemFunctions.signal(mutex1);
				//	
				SemFunctions.signal(numbered); // Signal to customer to go to waiting room
			}
			
			catch (Exception e) {
			}
		}
	}
	
	public void giveNumber() {
		// Get the new entered customer and assign a number
		Customer custo = entry.poll();
		custo.setNumber(num);
		
		// Add to waiting room and print
		waitingRoom.add(custo);
		System.out.println("Customer " + custo.customerID 
							+ " gets number " + this.num + ", enters waiting room");
	}
}