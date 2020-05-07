package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Enum for signaling type of chosen figure set to Game handler
 */
public enum FigureSet {
    OPLUS(0), OEX(1), PLUSMINUS(2);

    private int id;

    /**
     * Default constructor for enum type object
     * @param id desired id of the new object
     */
    FigureSet(int id) {
        this.id = id;
    }

    /**
     * Function for getting enum object when passing Integer id
     * @param id of the enum type
     * @return Returns the type matching the given id or null if missing value
     */
    @Nullable
    public static FigureSet fromId(int id) {
        for(FigureSet type : values()) {
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
