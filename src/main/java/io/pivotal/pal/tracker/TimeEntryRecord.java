package io.pivotal.pal.tracker;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "time_entries"
)
public class TimeEntryRecord {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "hours")
    private Integer hours;

    protected TimeEntryRecord() {
        // Needed for JPA
    }

    public TimeEntryRecord(TimeEntry timeEntry) {
        id = timeEntry.getId();
        projectId = timeEntry.getProjectId();
        userId = timeEntry.getUserId();
        date = java.sql.Date.valueOf(timeEntry.getDate());
        hours = timeEntry.getHours();
    }

    public TimeEntry toTimeEntry() {
        return new TimeEntry(id,
                projectId,
                userId,
                new java.sql.Date(date.getTime()).toLocalDate(),
                hours);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }


}
