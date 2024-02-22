package nl.tudelft.sem.template.scheduler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventModel {
    public enum Type {
        COMPETITION,
        TRAINING
    }

    @JsonProperty("id")
    private long id;
    @JsonProperty("ownerId")
    private String ownerId;
    @JsonProperty("timeFrame")
    private CustomPair<Date, Date> time;
    @JsonProperty("type")
    Type isCompetition;
    @JsonProperty("requirements")
    Map<String, String> requirements;
    @JsonProperty("location")
    private String location;
}
