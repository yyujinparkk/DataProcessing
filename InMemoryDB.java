import java.util.HashMap;
import java.util.Map;

public class InMemoryDB implements IInMemoryDB {
    private Map<String, Integer> mainData = new HashMap<>(); // Stores the main state of the database
    private Map<String, Integer> transactionData; // Temporarily stores data during a transaction
    private boolean isInTransaction = false; // Tracks if a transaction is currently active

    @Override
    public Integer get(String key) {
        // Returns value from transaction data if in transaction and key exists there
        if (isInTransaction && transactionData.containsKey(key)) {
            return transactionData.get(key);
        }
        return mainData.getOrDefault(key, null); // Returns null if key does not exist
    }

    @Override
    public void put(String key, int value) {
        // Throws exception if try to put a key-value pair without a transaction
        if (!isInTransaction) {
            throw new IllegalStateException("No transaction is in progress."); 
        }
        transactionData.put(key, value); // Stores the key-value pair in the transaction data
    }

    @Override
    public void begin_transaction() {
        // Throws exception if trying to start a new transaction while one is already active
        if (isInTransaction) {
            throw new IllegalStateException("A transaction is already in progress.");
        }
        isInTransaction = true;
        transactionData = new HashMap<>(mainData); // Starts a new transaction, copying current state to transaction data
    }

    @Override
    public void commit() {
        // Throws exception if there is no transaction to commit
        if (!isInTransaction) {
            throw new IllegalStateException("No transaction is in progress to commit.");
        }
        mainData = new HashMap<>(transactionData);
        transactionData = null;
        isInTransaction = false;
    }

    @Override
    public void rollback() {
        if (!isInTransaction) {
            throw new IllegalStateException("No transaction is in progress to rollback.");
        }
        transactionData = null;
        isInTransaction = false;
    }

    public static void main(String[] args) {
        IInMemoryDB inmemoryDB = new InMemoryDB();

        try {
            // returns null
            System.out.println(inmemoryDB.get("A"));
            inmemoryDB.put("A", 5); // throws an error
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        inmemoryDB.begin_transaction();
        inmemoryDB.put("A", 5);
        // returns null
        System.out.println(inmemoryDB.get("A"));

        inmemoryDB.put("A", 6);
        inmemoryDB.commit();
        // returns 6
        System.out.println(inmemoryDB.get("A"));

        try {
            inmemoryDB.commit(); // throws an error
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // returns null
        System.out.println(inmemoryDB.get("B"));

        inmemoryDB.begin_transaction();
        inmemoryDB.put("B", 10);
        inmemoryDB.rollback();
        // returns null
        System.out.println(inmemoryDB.get("B"));
    }
}
