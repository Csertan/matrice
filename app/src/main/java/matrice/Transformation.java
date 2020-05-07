package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Enum for signaling applicable transformation upon each Move
 */
public enum Transformation {
    INVERT(0), ROTATE(1);

    private int id;

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
    @Contract(pure = true)
    public int getId() {
        return this.id;
    }
}
