package test.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryController;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TimeEntryControllerTest {
    private TimeEntryRepository mockTimeEntryRepository;
    private TimeEntryController controller;
    private MeterRegistry mockMeterRegistry;
    private Counter mockCounter;
    private DistributionSummary mockSummary;

    @Before
    public void setUp() throws Exception {
        mockTimeEntryRepository = mock(TimeEntryRepository.class);
        mockMeterRegistry = mock(MeterRegistry.class);
        mockCounter = mock(Counter.class);
        mockSummary = mock(DistributionSummary.class);

        doReturn(mockCounter).when(mockMeterRegistry).counter("timeEntry.actionCounter");
        doReturn(mockSummary).when(mockMeterRegistry).summary("timeEntry.summary");
        controller = new TimeEntryController(mockTimeEntryRepository, mockMeterRegistry);
    }

    @Test
    public void testCreate() throws Exception {
        long projectId = 123L;
        long userId = 456L;
        TimeEntry timeEntryToCreate = new TimeEntry(projectId, userId, LocalDate.parse("2017-01-08"), 8);

        long timeEntryId = 1L;
        TimeEntry expectedResult = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-08"), 8);
        doReturn(expectedResult)
            .when(mockTimeEntryRepository)
            .create(any(TimeEntry.class));


        ResponseEntity response = controller.create(timeEntryToCreate);


        verify(mockTimeEntryRepository).create(timeEntryToCreate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testRead() throws Exception {
        long timeEntryId = 1L;
        long projectId = 123L;
        long userId = 456L;
        TimeEntry expected = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-08"), 8);
        doReturn(expected)
            .when(mockTimeEntryRepository)
            .find(timeEntryId);

        ResponseEntity<TimeEntry> response = controller.read(timeEntryId);

        verify(mockTimeEntryRepository).find(timeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testRead_NotFound() throws Exception {
        long nonExistentTimeEntryId = 1L;
        doReturn(null)
            .when(mockTimeEntryRepository)
            .find(nonExistentTimeEntryId);

        ResponseEntity<TimeEntry> response = controller.read(nonExistentTimeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testList() throws Exception {
        List<TimeEntry> expected = asList(
            new TimeEntry(1L, 123L, 456L, LocalDate.parse("2017-01-08"), 8),
            new TimeEntry(2L, 789L, 321L, LocalDate.parse("2017-01-07"), 4)
        );
        doReturn(expected).when(mockTimeEntryRepository).list();

        ResponseEntity<List<TimeEntry>> response = controller.list();

        verify(mockTimeEntryRepository).list();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate() throws Exception {
        long timeEntryId = 1L;
        long projectId = 987L;
        long userId = 654L;
        TimeEntry expected = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-07"), 4);
        doReturn(expected)
            .when(mockTimeEntryRepository)
            .update(eq(timeEntryId), any(TimeEntry.class));

        ResponseEntity response = controller.update(timeEntryId, expected);

        verify(mockTimeEntryRepository).update(timeEntryId, expected);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        long nonExistentTimeEntryId = 1L;
        doReturn(null)
            .when(mockTimeEntryRepository)
            .update(eq(nonExistentTimeEntryId), any(TimeEntry.class));

        ResponseEntity response = controller.update(nonExistentTimeEntryId, new TimeEntry());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDelete() throws Exception {
        long timeEntryId = 1L;
        ResponseEntity<TimeEntry> response = controller.delete(timeEntryId);
        verify(mockTimeEntryRepository).delete(timeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
