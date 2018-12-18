package io.pivotal.pal.tracker;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long,TimeEntry> entryMap = new HashMap<>();
    long lastId = 0;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        long id = allocateNewId();
        TimeEntry entryWithId = new TimeEntry(id, timeEntry);
        entryMap.put(id, entryWithId);
        return entryWithId;
    }

    private long allocateNewId() {
        lastId++;
        return lastId;
    }

    @Override
    public TimeEntry find(long id) {
        return entryMap.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(entryMap.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (find(id) != null) {
            entryMap.put(id, new TimeEntry(id, timeEntry));
        }
        return find(id);
    }

    @Override
    public void delete(long id) {
        entryMap.remove(id);
    }
}
