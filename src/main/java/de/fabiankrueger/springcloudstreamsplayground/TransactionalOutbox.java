package de.fabiankrueger.springcloudstreamsplayground;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TransactionalOutbox {

    private final TransactionalOutboxMessageRepository repository;
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 10L)
    public void process() {
        // FIXME: running this on multiple instances can result in race conditions
        // TODO: store id/information about the application instance that attempted to send the message
        //       so it can be found and send by the tx-ob of the exact same instance
        //       verify if this allows tx/idempotent kafka producer
        // TODO: partition selection must be exact same as if the producer would have sent the message directly.
        //       see producer setting 'PartitionSelectorStrategy'/'partitionSelectorExpression'
        //       (https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_configuration_options)
        // TODO: If producer settings differ for different outbox topics, it needs to be possible to define a tx-ob per topic.
        //       E.g. serializer settings
        List<TransactionalOutboxMessage> outboxMessages = repository.findByOffsetIsNull();
        outboxMessages.stream().parallel().forEach(this::processMessage);
        repository.deleteByOffsetIsNotNull();
    }

    @Transactional
    void processMessage(TransactionalOutboxMessage transactionalOutboxMessage) {
        try {
            Long offset = sendMessage(transactionalOutboxMessage);
            transactionalOutboxMessage.setOffset(offset);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while sending message [" + transactionalOutboxMessage.getId() + "] to '" + transactionalOutboxMessage.getDestination() + "'", e);
        }
    }

    private Long sendMessage(TransactionalOutboxMessage transactionalOutboxMessage) throws ExecutionException, InterruptedException {
        Message<String> message = MessageBuilder.withPayload(transactionalOutboxMessage.getPayload())
                .copyHeaders(transactionalOutboxMessage.getHeadersMap())
                .setHeader(KafkaHeaders.TOPIC, transactionalOutboxMessage.getDestination())
                .build();
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(message);
        return send.get().getRecordMetadata().offset();
    }
}
