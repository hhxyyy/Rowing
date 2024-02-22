package nl.tudelft.sem.template.scheduler.models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.h2.engine.User;
import org.springframework.data.util.Pair;

/**
 * Model representing a user request for a match.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String userId;
    // Start and end time of the availability
    private List<CustomPair<Date, Date>> availability;
    // The 5 preferred positions in this order: cox, coach, port side rower, starboard side rower, sculling rower
    private boolean[] positions;
    //Possible requirements: "Certificate", "Gender", "Organization", "Professional"
    Map<String, String> requirements;

    /** Constructor that creates an userRequest with only one filled position that is in the parameter filled.
     *
     * @param userId the id of the user
     * @param availability the list of pairs with the availabilty
     * @param filled the position that will be filled
     * @param requirements the requirements of the user
     */
    public UserRequest(String userId, List<CustomPair<Date, Date>> availability,
                       int filled, Map<String, String> requirements) {
        this.userId = userId;
        this.availability = availability;
        this.positions = new boolean[]{false, false, false, false, false};
        this.positions[filled] = true;
        this.requirements = requirements;

    }
}
