import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.Semaphore;

public class Announcer implements Runnable {
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
	
	public Announcer(Semaphore mutex1, Semaphore mutex2, 
					
					Semaphore roomInLine, Semaphore[] agentAvailable, 
					
					Semaphore customerEntered, Semaphore numbered, Semaphore waiting, Semaphore called,
					Semaphore waitLine, Semaphore inLine, Semaphore serveCustomer, 
					Semaphore beingServed, Semaphore agentAsksExam, Semaphore customerTakesExam, 
					Semaphore giveLicense, Semaphore getsLicense, 
					
					Semaphore[] finished, 
					
					Queue<Customer> entry, Queue<Customer> waitingRoom, Queue<Customer> line, 
					Queue<Customer> served) {
		
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
				SemFunctions.wait(roomInLine); // Wait until there's room in line
				
				SemFunctions.wait(waiting); // Wait until a customer is waiting
				// Critical section
				SemFunctions.wait(mutex2);
				call();
				SemFunctions.signal(mutex2);	
				// 
				SemFunctions.signal(called); // Signal to customer that its # has been called
				
				SemFunctions.wait(waitLine); // Wait until the customer's in the line
				// Critical section
				SemFunctions.wait(mutex1);
				putInLine();
				SemFunctions.signal(mutex1);
				// 
				SemFunctions.signal(inLine); // Signal to agent that customer's waiting in line
			}
			catch (Exception e) {
			}
		}
	}
	
	public void call() {	
		// Call customer in waiting room
		Customer nextInLine = waitingRoom.peek();
		System.out.println("Announcer calls number " + nextInLine.number);
	}
	
	public void putInLine() {
		// Take out of waiting room and put in line
		Customer nextInLine = waitingRoom.poll();
		line.add(nextInLine);
		System.out.println("Customer " + nextInLine.customerID + " moves to agent line");
	}
}