package com.danyazero.submissionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danyazero.submissionservice.entity.Event;
import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.IllegalRequstArgumentException;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.*;
import com.danyazero.submissionservice.repository.EventRepository;
import com.danyazero.submissionservice.repository.SubmissionRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private KafkaTemplate<String, SubmissionCreatedEvent> submissionKafkaTemplate;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private LanguageService languageService;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SubmissionService submissionService;

    private UUID userId;
    private SubmissionDto submissionDto;
    private Language language;
    private Submission submission;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        submissionDto = new SubmissionDto(1, 1, "print('hello')");
        language = new Language(1, "python");
        submission = Submission.builder()
                .id(100)
                .problemId(submissionDto.problemId())
                .solution(submissionDto.solution())
                .status(SubmissionStatus.CREATED)
                .createdAt(Instant.now())
                .language(language)
                .userId(userId)
                .build();
    }

    @Nested
    @DisplayName("Create Submission Tests")
    class CreateSubmissionTests {
        @Test
        @DisplayName("Should successfully create submission when all valid inputs are provided")
        void createSubmission_Success() {
            when(languageService.getLanguage(submissionDto.languageId())).thenReturn(language);
            when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
            when(eventRepository.save(any(Event.class))).thenAnswer(i -> {
                Event e = i.getArgument(0);
                e.setId(101);
                return e;
            });
            CompletableFuture<SendResult<String, SubmissionCreatedEvent>> future = CompletableFuture.completedFuture(
                    new SendResult<>(null, new RecordMetadata(new TopicPartition("topic", 0), 0, 0, 0, 0, 0))
            );
            when(submissionKafkaTemplate.sendDefault(any(SubmissionCreatedEvent.class))).thenReturn(future);

            Submission result = submissionService.createSubmission(userId, submissionDto);

            assertNotNull(result);
            assertEquals(100, result.getId());
            assertNotNull(result.getEvents());
            assertFalse(result.getEvents().isEmpty());
            verify(submissionRepository).save(any(Submission.class));
            verify(eventRepository).save(any(Event.class));
            verify(submissionKafkaTemplate).sendDefault(any(SubmissionCreatedEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when User ID is null")
        void createSubmission_NullUserId() {
            assertThrows(IllegalRequstArgumentException.class, () -> submissionService.createSubmission(null, submissionDto));
            verifyNoInteractions(submissionRepository, eventRepository, submissionKafkaTemplate);
        }

        @Test
        @DisplayName("Should throw exception when Submission Payload is null")
        void createSubmission_NullPayload() {
            assertThrows(IllegalRequstArgumentException.class, () -> submissionService.createSubmission(userId, null));
            verifyNoInteractions(submissionRepository, eventRepository, submissionKafkaTemplate);
        }
    }

    @Nested
    @DisplayName("Get Problem Status Tests")
    class GetProblemStatusTests {
        @Test
        @DisplayName("Should return solved true when an accepted submission exists")
        void getProblemStatus_Solved() {
            when(submissionRepository.findFirstByUserIdIsAndProblemIdAndStatus(userId, 1, SubmissionStatus.ACCEPTED))
                    .thenReturn(Optional.of(submission));

            ProblemStatus status = submissionService.getProblemStatus(userId, 1);

            assertTrue(status.isSolved());
        }

        @Test
        @DisplayName("Should return solved false when no accepted submissions exist")
        void getProblemStatus_NotSolved() {
            when(submissionRepository.findFirstByUserIdIsAndProblemIdAndStatus(userId, 1, SubmissionStatus.ACCEPTED))
                    .thenReturn(Optional.empty());

            ProblemStatus status = submissionService.getProblemStatus(userId, 1);

            assertFalse(status.isSolved());
        }
    }

    @Nested
    @DisplayName("Get Submission Tests")
    class GetSubmissionTests {
        @Test
        @DisplayName("Should return submission successfully when valid ID is provided")
        void getSubmission_Success() {
            when(submissionRepository.findById(100)).thenReturn(Optional.of(submission));

            Submission result = submissionService.getSubmission(100);

            assertNotNull(result);
            assertEquals(100, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when submission ID is null")
        void getSubmission_NullId() {
            assertThrows(IllegalRequstArgumentException.class, () -> submissionService.getSubmission(null));
        }

        @Test
        @DisplayName("Should throw exception when submission is not found")
        void getSubmission_NotFound() {
            when(submissionRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(RequestException.class, () -> submissionService.getSubmission(999));
        }
    }

    @Nested
    @DisplayName("Get Submissions Page Tests")
    class GetSubmissionsTests {
        @Test
        @DisplayName("Should retrieve and map submissions correctly given valid filters")
        void getSubmissions_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Submission> page = new PageImpl<>(List.of(submission), pageable, 1);
            when(submissionRepository.findAllByProblemIdAndUserIdOrderByIdDesc(1, userId, pageable))
                    .thenReturn(page);

            PageDto<SubmissionResponseDto> result = submissionService.getSubmissions(1, userId, pageable);

            assertNotNull(result);
            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("Should throw exception when sorting User ID is null")
        void getSubmissions_NullUserId() {
            Pageable pageable = PageRequest.of(0, 10);
            assertThrows(IllegalRequstArgumentException.class, () -> submissionService.getSubmissions(1, null, pageable));
        }

        @Test
        @DisplayName("Should throw exception when Pageable is null")
        void getSubmissions_NullPageable() {
            assertThrows(IllegalRequstArgumentException.class, () -> submissionService.getSubmissions(1, userId, null));
        }
    }

    @Nested
    @DisplayName("Update Submission Status Tests")
    class UpdateSubmissionStatusTests {
        @Test
        @DisplayName("Should correctly update status and create Event when valid")
        void updateSubmissionStatus_Success() {
            when(submissionRepository.findById(100)).thenReturn(Optional.of(submission));
            
            submissionService.updateSubmissionStatus(100, SubmissionStatus.ACCEPTED);

            verify(submissionRepository).save(submission);
            verify(eventRepository).save(any(Event.class));
            assertEquals(SubmissionStatus.ACCEPTED, submission.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when status format is null")
        void updateSubmissionStatus_NullStatus() {
            assertThrows(RuntimeException.class, () -> submissionService.updateSubmissionStatus(100, null));
            verifyNoInteractions(submissionRepository, eventRepository);
        }

        @Test
        @DisplayName("Should throw exception when trying to update non-existing submission")
        void updateSubmissionStatus_SubmissionNotFound() {
            when(submissionRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(RequestException.class, () -> submissionService.updateSubmissionStatus(999, SubmissionStatus.ACCEPTED));
            verify(submissionRepository, never()).save(any());
            verify(eventRepository, never()).save(any());
        }
    }
}
