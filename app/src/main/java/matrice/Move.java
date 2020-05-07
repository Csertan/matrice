package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Enum for signaling type of chosen Move to Game handler
 */
public enum Move {
    HORIZONTAL(0), VERTICAL(1), LEFTABRIGHTBE(2), LEFTBERIGHTAB(3);

    private int id;

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
    @Contract(pure = true)
    public int getId() {
        return this.id;
    }
}
