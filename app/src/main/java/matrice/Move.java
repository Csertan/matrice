package matrice;

import androidx.annotation.Nullable;

/**
 * Enum for signaling type of chosen Move to Game handler
 */
public enum Move {
    HORIZONTAL(0), VERTICAL(1), LEFTABRIGHTBE(2), LEFTBERIGHTAB(3);

    private final int id;

    /**
     * Default constructor for enum type object
     * @param id desired id of the new object
     */
    Move(int id) {
        this.id = id;
    }

    /**
     * Function for getting enum object when passing Integer id
     * @param id of the enum type
     * @return Returns the type matching the given id or null if missing value
     */
    @Nullable
    public static Move fromId(int id) {
        for(Move type : values()) {
            if(type.getId() == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Function for getting Move type given an angle.
     * Directions are defined as follows:
     *
     * Horizontal move: [330°, 0°] & [150°, 210°]
     * Vertical move: [60°, 120°] & [240°, 300°]
     * From left top to right bottom: [120°, 150°] & [300°, 330°]
     * From left bottom to right top: [210°, 240°] & [30°, 60°]
     *
     * @param angle Angle of the move from 0° to 360°
     * @return Move corresponding to the direction of the angle
     */
    public static Move fromAngle(double angle) {
        if(inRange(angle, 30.0, 60.0) || inRange(angle, 210.0, 240.0))
            return Move.LEFTBERIGHTAB;
        else if(inRange(angle, 60.0, 120.0) || inRange(angle, 240.0, 300.0))
            return Move.VERTICAL;
        else if(inRange(angle, 120.0 ,150.0) || inRange(angle, 300.0, 330.0))
            return  Move.LEFTABRIGHTBE;
        else
            return Move.HORIZONTAL;
    }

    /**
     * Helper function for fromAngle() method
     * @param angle from 0° to 360°
     * @param lower lower bound of the interval
     * @param upper upper bound of the interval
     * @return whether the given angle is in the specified interval
     */
    private static boolean inRange(double angle, double lower, double upper) {
        return (angle >= lower) && (angle < upper);
    }
}
