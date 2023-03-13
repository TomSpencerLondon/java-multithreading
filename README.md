# Java Concurrency and Multithreading
https://www.youtube.com/watch?v=WldMTtUWqTg


### What is multitasking
Multitasking allows several activities to occur concurrently on the computer.
Humans do tasks in parallel - brushing teeth and listening to music.
Lets consider:
- process-based multitasking
- thread-based multitasking

### Process based multitasking
Allows processes (i.e. programs) to run concurrently on the computer. For instance, running Ms Paint while also working with the word processor.

### Thread based multitasking
Allows parts of the same program to run concurrently on the computer. MS Word is printing and formatting text at the same time.
Multitasking in a particular program involves thread based multitasking.

### Threads vs Process
- Two threads share the same address space
- context switching between threads is usually less expensive than between processes
- The cost of communication between threads is relatively low

### Why multithreading?
Humans are multitaskers - developers building a large code base. Building can take 30 minutes to an hour. Developers read a book or documentation - we don't wait for the task to complete.
Next work is not dependent on earlier task. We can put the code on build and write documentation in parallel.
- In a single-threaded environment, only one task at a time can be performed
- CPU cycles are wasted, for example, when waiting for user input
- Multitasking allows idle CPU time to be put to good use


### What is a thread?
- A thread is an independent sequential path of execution in a program
- Java has main thread - when the main thread stops the program stops
- Many threads can run concurrently within a program
- At runtime, threads in a program exist in a common memory space and can, therefore, share both data and code (i.e. they are lightweight compared to processes)


### Threads: 3 important concepts relating to multithreading in Java
1. Creating threads and providing the code that gets executed by a thread
2. Accessing common data and code through synchronization
3. Transitioning between thread states

### Main thread
- When a standalone application is run, a user thread is automatically created to execute the main() method of the application. This thread is called the main thread.
- If no other user threads are spawned the program terminates when the main() method finishes executing
- All other threads, called child threads, are spawned from the main thread
- The runtime environment distinguishes between user threads and daemon threads
- If the main thread stops executed there are two other threads the child thread and the daemon thread
- Calling the setDaemon method in Thread class starts the status of the thread as either daemon or user, but this must be done before the thread is started
- As long as a user thread is alive, the JVM does not terminate
- A daemon thread is at the mercy of the runtime system: it is stopped if there are no more user threads running, thus terminating the program

### Thread creation
- A thread in Java is represented by an object of the Thread class
- Creating threads is achieved in one of two ways:
1. Implementing the java.lang.Runnable interface
2. Extending the java.lang.Thread class

```java
public class Thread1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("inside thread1 " + i);
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("main is starting");
        Thread1 thread1 = new Thread1("thread1");
//        thread1.setDaemon(true);
        thread1.start();


        System.out.println("main is exiting");

    }
}
```
The order is random:
```bash
main is starting
main is exiting
inside thread1 thread10
inside thread1 thread11
inside thread1 thread12
inside thread1 thread13
inside thread1 thread14
```


Add Runnable thread:

```java
public class Thread2 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread() + ", " + i);
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("main is starting");
        Thread thread1 = new Thread1("thread1");
//        thread1.setDaemon(true);
        thread1.start();

        Thread thread2 = new Thread(new Thread2(), "thread2");

        thread2.start();

        System.out.println("main is exiting");

    }
}
```

```bash
main is starting
main is exiting
Thread[#22,thread2,5,main], 0
Thread[#22,thread2,5,main], 1
Thread[#22,thread2,5,main], 2
Thread[#22,thread2,5,main], 3
Thread[#22,thread2,5,main], 4
inside thread1 thread10
inside thread1 thread11
inside thread1 thread12
inside thread1 thread13
inside thread1 thread14
```
JVM decides when to execute the threads. The run() method in the Thread class does nothing:
```java
public class Thread implements Runnable {
    @Override
    public void run() {
        Runnable task = holder.task;
        if (task != null) {
            task.run();
        }
    }
}
```
We pass a Thread class to the Thread class to ensure that it runs the run() method.
