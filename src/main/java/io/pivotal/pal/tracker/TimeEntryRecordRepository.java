package io.pivotal.pal.tracker;

import org.springframework.data.repository.CrudRepository;

public interface TimeEntryRecordRepository extends CrudRepository<TimeEntryRecord,Long> {
}
