package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum Transformation {
    INVERT(0), ROTATE(1);

    private int id;

    Transformation(int id) {
        this.id = id;
    }
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
