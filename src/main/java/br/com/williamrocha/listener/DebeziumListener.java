package br.com.williamrocha.listener;

import br.com.williamrocha.utils.DebeziumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation;

@Slf4j
@Component
public class DebeziumListener {

    private final Executor executor = Executors.newSingleThreadExecutor();    
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (Objects.nonNull(this.debeziumEngine)) {
            this.debeziumEngine.close();
        }
    }

    public DebeziumListener(Configuration customerConnectorConfiguration) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(customerConnectorConfiguration.asProperties())
            .notifying(this::handleChangeEvent)
            .build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        var sourceRecord = sourceRecordRecordChangeEvent.record();
        log.debug("Key = {}, Value = {}", sourceRecord.key(), sourceRecord.value());
        var sourceRecordChangeValue= (Struct) sourceRecord.value();
        log.debug("SourceRecordChangeValue = '{}'", sourceRecordChangeValue);
         if (sourceRecordChangeValue != null) {
             try {
                 Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

                 if(operation != Operation.READ) {

                     Map<String, Object> payloadBefore = DebeziumUtils.getPayload(sourceRecordRecordChangeEvent,BEFORE);
                     Map<String, Object> payloadAfter = DebeziumUtils.getPayload(sourceRecordRecordChangeEvent,AFTER);

                     log.info("Updated Data Operation:"+operation+" Before: "+payloadBefore
                             +" After: "+payloadAfter);

                     try {
                         log.info("Updated Data JSON Operation:"+operation+" Before: "+new ObjectMapper().writeValueAsString(payloadBefore)
                                 +" After: "+new ObjectMapper().writeValueAsString(payloadAfter));
                     } catch (JsonProcessingException e) {
                         throw new RuntimeException(e);
                     }
                 }
             } catch (DataException e){
                log.warn(e.getMessage());
             }
         }
    }

}