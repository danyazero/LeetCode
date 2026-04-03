package ua.danyazero.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ua.danyazero.notificationservice.model.SubmissionUpdatedEvent;
import ua.danyazero.notificationservice.model.UpdateMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "submissions-execution", groupId = "notification-service", containerFactory = "submissionExecutionListenerContainerFactory")
    public void listen(SubmissionUpdatedEvent message) {
        log.info("Received submission execution event with id -> {}, data -> {}", message.getEventId(), message.getData());
        try {
            messagingTemplate.convertAndSendToUser(
                    message.getData().userId().toString(),
                    "/queue/submission-updates",
                    new UpdateMessage(message.getData().submissionId(), message.getData().submissionStatus())
            );
        } catch (MessagingException e) {
            log.warn("Cannot send submission -> {} status {} update, an WebSocket error occurred.", message.getData().submissionId(), message.getData().submissionStatus().getValue());
        }
    }
}
