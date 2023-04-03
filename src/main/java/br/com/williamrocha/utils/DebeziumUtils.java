package br.com.williamrocha.utils;

import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class DebeziumUtils {
    public static Map<String, Object> getPayload(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent, String operation){
        var sourceRecord = sourceRecordRecordChangeEvent.record();
        log.debug("Key = {}, Value = {}", sourceRecord.key(), sourceRecord.value());
        var sourceRecordChangeValue= (Struct) sourceRecord.value();
        Struct struct = (Struct) sourceRecordChangeValue.get(operation);
        if(struct==null){
            return null;
        }
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

}
