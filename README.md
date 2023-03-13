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

    public void start() {
        synchronized (this) {
            // zero status corresponds to state "NEW".
            if (holder.threadStatus != 0)
                throw new IllegalThreadStateException();
            start0();
        }
    }

}
```
We pass a Thread class to the Thread class to ensure that it runs the run() method.
Extending the Thread class means you can't extend further classes. If you use the Runnable interface you have the option of extending
other classes. Most developers implement Runnable - because this allows more freedom to our design.
We can also use a lambda to implement the Runnable interface:
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("main is starting");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread() + ", " + i);
            }
        }, "thread2");

        thread2.start();

        System.out.println("main is exiting");

        new Thread().run();

    }
}
```
Runnable:
![image](https://user-images.githubusercontent.com/27693622/224640494-a21c94a4-ce9e-492d-ae42-f2c09dd3bb36.png)
Thread:
![image](https://user-images.githubusercontent.com/27693622/224640733-99457a60-859f-4cd4-a431-3ee9babeb8e9.png)


### Synchronization
- Threads share the same memory space, i.e. they can share resources (objects)
- However, there are critical situations where it is desirable that only one thread at a time has access to a shared resource

![image](https://user-images.githubusercontent.com/27693622/224641072-7a4ec888-dc16-463f-a509-10a56120d682.png)

This is how we can have a race condition.

We can look at this with a stack class:
```java
public class Stack {
    private int[] array;
    private int stackTop;

    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    public boolean push(int element) {
        if (isFull()) {
            return false;
        }
        ++stackTop;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        array[stackTop] = element;

        return true;
    }

    public int pop() {
        if (isEmpty()) {
            return Integer.MIN_VALUE;
        }

        int result = array[stackTop];
        array[stackTop] = Integer.MIN_VALUE;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        stackTop--;
        return result;
    }
}
```
The code is used and can break:
```java
public class ThreadTester2 {
    public static void main(String[] args) {
        Stack stack = new Stack(5);

        new Thread(() -> {
            int counter = 0;
            while (counter++ < 10) {
                System.out.println("Pushed: " + stack.push(100));
            }
        }, "Pusher").start();

        new Thread(() -> {
            int counter = 0;
            while (counter++ < 10) {
                System.out.println("Popped: " + stack.pop());
            }
        }, "Popper").start();

        System.out.println("main is exiting");
    }
}

```
The state of the class runs inconsistently:
```bash
main is exiting
Popped: 0
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Popped: -2147483648
Pushed: true
Pushed: true
Pushed: true
Pushed: true
Pushed: true
Pushed: true
Pushed: false
Pushed: false
Pushed: false
Pushed: false

```
When two threads are running, the stack class causes problems. We need to ensure that the push() and pop() methods which change
the state of the objects are not changing the state inconsistently. We have to devise methodology so that the action is accessed by only
one thread at a time. We should only allow one thread at a time. To do this we can add locks so that the JVM scheduling algorithm can 
decide which thread to run.

Now both methods in Stack are bound by the same object:
```java
public class Stack {
    private int[] array;
    private int stackTop;

    Object lock;
    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;
        
        lock = new Object();
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    // t1, t2, t3
    public boolean push(int element) {
        synchronized(lock) {
            if (isFull()) {
                return false;
            }
            ++stackTop;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            array[stackTop] = element;

            return true;
        }

    }

    // t1, t4, t5
    public int pop() {
        synchronized(lock) {
            if (isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int result = array[stackTop];
            array[stackTop] = Integer.MIN_VALUE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stackTop--;
            return result;
        }
        
    }
}

```
Binding by two objects one for each method allows the other thread to access the functions in parallel:
```java
public class Stack {
    private int[] array;
    private int stackTop;

    Object lock1;
    Object lock2;
    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;
        
        lock1 = new Object();
        lock2 = new Object();
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    // t1, t2, t3
    public boolean push(int element) {
        synchronized(lock1) {
            if (isFull()) {
                return false;
            }
            ++stackTop;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            array[stackTop] = element;

            return true;
        }

    }

    // t1, t4, t5
    public int pop() {
        synchronized(lock2) {
            if (isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int result = array[stackTop];
            array[stackTop] = Integer.MIN_VALUE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stackTop--;
            return result;
        }
        
    }
}
```
This is not good because it would allow the push and pop to be executed at the same time. 
We therefore want the same lock for push and pop:

```java
public class Stack {
    private int[] array;
    private int stackTop;

    Object lock;
    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;
        
        lock = new Object();
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    // t1, t2, t3
    public boolean push(int element) {
        synchronized(lock) {
            if (isFull()) {
                return false;
            }
            ++stackTop;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            array[stackTop] = element;

            return true;
        }

    }

    // t1, t4, t5
    public int pop() {
        synchronized(lock) {
            if (isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int result = array[stackTop];
            array[stackTop] = Integer.MIN_VALUE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stackTop--;
            return result;
        }
        
    }
}
```

We can also add synchronized to the function:
```java
public class Stack {
    private int[] array;
    private int stackTop;

    Object lock;
    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;
        
        lock = new Object();
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    // t1, t2, t3
    public synchronized boolean push(int element) {
            if (isFull()) {
                return false;
            }
            ++stackTop;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            array[stackTop] = element;

            return true;
    }

    // t1, t4, t5
    public synchronized int pop() {
            if (isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int result = array[stackTop];
            array[stackTop] = Integer.MIN_VALUE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stackTop--;
            return result;
    }
}
```
This is the same as wrapping the entire function with ```synchronized(this)```. For all the synchronized methods the lock is the same for every method
in the class. Only one thread will have access to all syncrhonized methods. 

```bash
main is exiting
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Pushed: true
Popped: 100
Popped: 100
```

### Synchronized Methods:
- While a thread is inside a synchronized method of an object all other threads that wish to execute this synchronized method or any other syncrhonized method
of the object will have to waite
- This restriction does not apply to the method that already has the lock and is executing a syncrhonized method of the object
- Such a method can invoke other syncrhonized methods of the object without being blocked
- The non-syncrhonized methods of the object can always be called at any time by any thread

### Rules of synchronization
- A thread must acquire the object lock associated with a shared resource before it can enter the shared resource
- The runtime system ensures that no other thread can enter a shard resource if another thread already holds the object lock associated with it
- If a thread cannot immediately acquire the object lock, it is blocked, i.e., it must wait for the lock to become available
- When a thread exits a shared resource, the runtime system ensures that the object lock is also relinquished. If another thread is waiting for this object
lock it can try to acquire the lock in order to gain access to the shared resource.
- It should be made clear that programs should not make any assumptions about the order in which threads are granted ownership of the lock

The sleep method ```Thread.sleep(1000);``` in the code above increases the chance of corrupted threads.

### Static Synchronized Methods
- A thread acquiring the lock of a class to execute a static synchronized method has no effect on any thread acquiring the lock on any object of the class
to execute a syncrhonized instance method.
- In other words, synchronization of static methods in a class is independent of the synchronization of instance methods on objects of the class.
- A subclass decides whether the new definition of an inherited synchronized method will remain synchronized in the subclass

### What is a race condition?
It occurs when two more threads simultaneously update the same value (stackTopIndex) and, as a consequence, leave the value in an undefined or
inconsistent state

### Synchronized blocks
- Whereas execution of syncrhonized methods of an object is syncrhonized on the lock of the object, the synchronized blck allows execution
of the arbitrary code to be synchronized on the lock of an arbitrary object
- The general form of the syncrhonized statement is as follows:
```bash
synchronized (object ref expression) {
    // code block
        }
```
- The object ref expression must evaluate to a non-null reference value, otherwise a NullPointerException is thrown
- This object ref is analogous to a synchronized method

### Summary
A thread can hold a lock on an object:
- By executing a synchronized instance method of the object (this)
- By executing the body of a syncrhonized block that syncrhonizes on the object (this)
- By executing a syncrhonized static method of a class or a block inside a static method (in which case, the object is the Class object representing
the class in the JVM)

### Thread safety
It's the term used to describe the design of classes that ensure that the state of their objects is always consistent, even when the objects are used concurrently
by multiple threads. E.g. StringBuffer

### Volatile
The volatile keyword:

![image](https://user-images.githubusercontent.com/27693622/224656072-558ef083-6644-443a-9ba8-5525a44c2362.png)

![image](https://user-images.githubusercontent.com/27693622/224656251-e28156cc-14c5-4f75-a60f-1267244e6c28.png)

If a value is updated the status of the flag is read from the RAM as opposed to the cache.

```java
public class TVSet {
    private static volatile TVSet tvSet = null;
    
    private TVSet() {
        System.out.println("TV set instantiated");
    }
    
    public static TVSet getTvSet() {
        if (tvSet == null) {
            synchronized (TVSet.class) {
                if (tvSet == null) {
                    tvSet = new TVSet();
                }
            }
        }
        
        return tvSet;
    }
}

```
The TVSet is synchronized and the instance status refers to the RAM through the keyword volatile. This avoids an instance being created which would
violate the singleton design principle.

![image](https://user-images.githubusercontent.com/27693622/224661841-1ebddd6a-98a3-4974-8b39-624971e6b4f4.png)

```java
public class BlockingQueue {
    private Queue<Integer> queue;

    int capacity;

    private BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public boolean add(int item) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            queue.add(item);
            queue.notifyAll();

            return true;
        }
    }

    public int remove() {
        synchronized (queue) {
            while (queue.size() == 0) {
                // do something
                try {
                    queue.wait();



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int element = queue.poll();
            queue.notifyAll();
            return element;
        }
    }
}
```

We use a while loop to ensure that adder1 and adder2 can continue to check the condition. This allows the capacity variable to
control the capacity of the queue.

### Thread States

![image](https://user-images.githubusercontent.com/27693622/224662735-7663ee42-6566-45dc-a39a-bd4f7c2dfb87.png)

When thread is running you can call Thread.sleep(); this ensures the thread goes into non-runnable state. It still does not
release the lock.

### Thread States
Thread states Thread.State

| enum type | state | Description of thread   |
|:---------:|:----:|:--:|
|  NEW  | New  | Created but not yet started |
|  BLOCKED  | Runnable  | Blocked while waiting for a lock |
|  WAITING  | Waiting for notify, Blocked for join completion  | Waiting indefinitely for another thread to perform a particular action |
|  TIMED_WAITING  | Sleeping, waiting for notify, Blocked for join completion  | Waiting for another thread to perform an action for up to a specified time. |
|  TERMINATED  | Dead | Completed execution |

```java
public class ThreadTester3 {
    public static void main(String[] args) {
        Thread states = new Thread(() -> {
            try {
                Thread.sleep(1);
                for (int i = 1000; i > 0; i--) {

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "States");

        states.start();

        while (true) {
            Thread.State state = states.getState();
            System.out.println(state);
            if (state == Thread.State.TERMINATED) {
                break;
            }
        }
    }
}

```

This prints the following states:
```bash
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
RUNNABLE
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TIMED_WAITING
TERMINATED
```

### Running and Yielding

![image](https://user-images.githubusercontent.com/27693622/224672941-e9e825e9-fc2d-44d8-923f-8f642cc5714f.png)

Yield method puts the thread that was running into the regular run state to give the Thread chance to run.
It is just an advisory method to the JVM.

![image](https://user-images.githubusercontent.com/27693622/224673835-ed6c746c-5239-4cfc-9e5d-dad1286717e6.png)

Waiting and notifying:
![image](https://user-images.githubusercontent.com/27693622/224675223-bac1988e-a000-4c40-b536-d516815487ee.png)

This diagram shows the process of threads waiting for locks:
![image](https://user-images.githubusercontent.com/27693622/224680051-e325d203-63b9-4af4-9811-4fa29c301aac.png)

### Waiting and Notifying
A thread in the Waiting-for-notification state can be awakened by the occurrence of any one of these three incidents:
- Another thread invokes the notify() method on the object of the waiting thread,
and the waiting thread is selected as the thread to be awakened.
- The waiting thread times out
- Another thread interrupts the waiting thread

### Notify
- Invoking the notify() method on an object wakes up a single thread that is waiting for the lock of this object
- The selection of a thread to awaken is dependent on the thread policies implemented by the JVM
- On being notified, a waiting thread first transits to the Blocked-for-lock acquisition state to acquire the
lock on the object, and not directly to the Ready-to-Run state
- The thread is removed from the wait set of the object

### Important methods:
- final void wait(long timeout) throws InterruptedException
- final void wait(long timeout, int nanos) throws InterruptedException
- final void wait() throws InterruptedException
- final void notify()
- final void notifyAll()

#### Timed Out
- The wait() call specified the time the thread should wait before being timed out,
if it was not awakened by being notified
- The awakened thread competes in the usual manner to execute again
- The awakened thread has no way of knowing whether it was timed out
or woken up by one of the notification methods

### Interrupted
- Another thread invoked the interrupt() method on the waiting thread
- The awakened thread is enabled, but the return from the wait() call will result in an InterruptedException
if and when the awakened thread finally gets a chance to run.
- The code invoking the wait() method must be prepared to handle this checked exception

### Thread running with main (join())

```java
public class ThreadTester4 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread());
        }, "Our thread");
        
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("main is existing");
    }
}
```

This blocks the main method to wait for child threads before running rest of main.

![image](https://user-images.githubusercontent.com/27693622/224685752-34b12a3f-2f36-4d01-984d-ba0d4760499b.png)

### Thread Priority
- Threads are assigned priorities that the thread scheduler can use to determine how the threads will be scheduled
- The thread scheduler uses priorities to decide which thread to run
- Priorities are integer values from 1 (lowest priority given by the constant Thread.MIN_PRIORITY) to 10
  (highest priority given by the constant Thread.MAX_PRIORITY).
The default priority is 5 (Thread.NORM_PRIORITY)
- The thread inherits the priority of its parent thread
- The priority of a thread can set using the setPriority() method and read using the getPriority() method,
both of which are defined in the Thread class.
- The setPriority() method is an advisory method, meaning that it provides a hint from the
program to the JVM, which the JVM is in no way obliged to honor.

### Thread Scheduler:
- Schedulers in JVM implementations usually employ one of 2 strategies
1. Preemptive scheduling
2. Time spaced round-robin scheduling

### Deadlocks
- A deadlock is a situation where a thread is waiting for an object lock that another thread holds,
and this second thread is waiting for an object lock that the first thread holds
- Since each thread is waiting for the other thread to relinquish a lock, they both remain waiting forever in the Blocked-for-lock-acquisition state
- The threads are said to be deadlocked

![image](https://user-images.githubusercontent.com/27693622/224690295-79fd861b-060b-4608-928b-aa0b012093b2.png)

### Deadlock situation:
```java

public class ThreadTester5 {

    public static void main(String[] args) {
        String lock1 = "lock1";
        String lock2 = "lock2";

        Thread thread1 = new Thread(() -> {
            synchronized (lock2) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread1");

        Thread thread2 = new Thread(() -> {
            synchronized (lock1) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread2");

        thread1.start();
        thread2.start();
    }
}

```

This is a fixed version:
```java
public class ThreadTester5 {

    public static void main(String[] args) {
        String lock1 = "lock1";
        String lock2 = "lock2";

        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread1");

        Thread thread2 = new Thread(() -> {
            synchronized (lock1) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread2");

        thread1.start();
        thread2.start();
    }
}

```