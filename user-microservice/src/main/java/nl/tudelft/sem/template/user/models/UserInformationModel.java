package nl.tudelft.sem.template.user.models;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.user.domain.CustomPair;


public class UserInformationModel {
    private transient List<CustomPair<Date, Date>> availability;
    private transient boolean[] positions;

    public UserInformationModel(List<CustomPair<Date, Date>> availability, boolean[] positions) {
        this.availability = availability;
        this.positions = positions;
    }

    public List<CustomPair<Date, Date>> getAvailability() {
        return availability;
    }

    public boolean[] getPositions() {
        return positions;
    }

    /**
     * Validity check for the user request.
     *
     * @return whether the information that the app user provided
     *     is valid or not through a boolean value.
     */
    public boolean checkIfValid() {
        return positions != null
                && positions.length == 5
                && containsTrue(positions)
                && availability != null
                && availability
                .stream() // timeFrames start before they end
                .allMatch(timeFrame -> timeFrame.getFirst().compareTo(timeFrame.getSecond()) < 0);
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
