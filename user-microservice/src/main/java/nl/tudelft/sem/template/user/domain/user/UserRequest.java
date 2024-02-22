package nl.tudelft.sem.template.user.domain.user;

import lombok.*;
import nl.tudelft.sem.template.user.domain.CustomPair;

import java.util.*;

@Getter
public class UserRequest {
    private String userId;
    // Start and end time of the availability
    private List<CustomPair<Date, Date>> availability;
    // The 5 preferred positions in this order: cox, coach, port side rower, starboard side rower, sculling rower
    private boolean[] positions;
    //Possible requirements: "Certificate", "Gender", "Organization"
    private Map<String, String> requirements;

    /**
     * Creates a new user request.
     *
     * @param userId the user's ID
     * @param availability the availability of the user
     * @param positions the positions that the user wants to fill
     * @param requirements the requirements that the user has.
     */
    public UserRequest(String userId, List<CustomPair<Date,
            Date>> availability, boolean[] positions, Map<String,
            String> requirements) {
        this.userId = userId;
        this.availability = availability;
        this.positions = positions;
        this.requirements = requirements;
    }

    /**
     * Validity check for the UserRequest.
     *
     * @return true iff the model meets all conditions
     */
    public boolean checkIfValid() {
        return userId != null && positions != null && availability != null
                && availability.stream()
                    .allMatch(timeFrame -> timeFrame.getFirst().compareTo(timeFrame.getSecond()) < 0)
                && positions.length == 5 && containsTrue(positions) && requirements != null
                && requirements
                    .entrySet()
                    .stream()
                    .noneMatch(e -> e.getKey().isBlank() || e.getValue().isBlank());
    }

    /**
     * Helper method that checks if positions has at least a true value.
     */
    public boolean containsTrue(boolean[] positions) {
        for (boolean position : positions) {
            if (position) {
                return true;
            }
        }
        return false;
    }

}
