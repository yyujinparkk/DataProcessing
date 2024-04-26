public interface IInMemoryDB {
    Integer get(String key);
    void put(String key, int value) throws IllegalStateException;
    void begin_transaction() throws IllegalStateException;
    void commit() throws IllegalStateException;
    void rollback() throws IllegalStateException;
}
