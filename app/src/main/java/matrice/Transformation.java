package matrice;

import androidx.annotation.Nullable;

/**
 * Enum for signaling applicable transformation upon each Move
 */
public enum Transformation {
    INVERT(0), ROTATE(1);

    private final int id;

    /**
     * Default constructor for enum type object
     * @param id desired id of the new object
     */
    Transformation(int id) {
        this.id = id;
    }

    /**
     * Function for getting enum object when passing Integer id
     * @param id of the enum type
     * @return Returns the type matching the given id or null if missing value
     */
    @Nullable
    public static Transformation fromId(int id) {
        for(Transformation type : values()) {
            if(type.getId() == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Function for getting Transformation type given an angle
     * Transformation are defined as follows:
     *
     * If the user swipes from left or from top the move is handled with an inversion,
     * else when the user swipes from right or from bottom the move is handled with a rotation.
     *
     * @param angle Angle of the move from 0° to 360°
     * @return Type corresponding the direction of the move
     */
    public static Transformation fromAngle(double angle) {
        if(inRange(angle, 60.0, 120.0) || inRange(angle, 150, 210))
            return Transformation.ROTATE;
        else
            return Transformation.INVERT;
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
