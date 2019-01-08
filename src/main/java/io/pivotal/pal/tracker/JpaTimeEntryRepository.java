package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JpaTimeEntryRepository implements TimeEntryRepository {
    private TimeEntryRecordRepository recordRepository;

    public JpaTimeEntryRepository(TimeEntryRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        return recordRepository.save(new TimeEntryRecord(timeEntry)).toTimeEntry();
    }

    @Override
    public TimeEntry find(long id) {
        Optional<TimeEntryRecord> result = recordRepository.findById(id);
        return result.isPresent() ? result.get().toTimeEntry() : null;
    }

    @Override
    public List<TimeEntry> list() {
        return StreamSupport
                .stream(recordRepository.findAll().spliterator(), false)
                .map(TimeEntryRecord::toTimeEntry)
                .collect(Collectors.toList());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntryRecord record = new TimeEntryRecord(timeEntry);
        record.setId(id);
        return recordRepository.save(record).toTimeEntry();
    }

    @Override
    public void delete(long id) {
        recordRepository.deleteById(id);
    }
}
