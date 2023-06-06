import java.util.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class SemFunctions {
	public static void wait(Semaphore sem) {
		try {
			sem.acquire();
		}
		catch (InterruptedException e) {
		}
	}
	
	public static void signal(Semaphore sem) {
		sem.release();
	}
}