package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum Move {
    HORIZONTAL(0), VERTICAL(1), LEFTABRIGHTBE(2), LEFTBERIGHTAB(3);

    private int id;

    Move(int id) {
        this.id = id;
    }
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
