package matrice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum FigureSet {
    OPLUS(0), OEX(1), PLUSMINUS(2);

    private int id;

    FigureSet(int id) {
        this.id = id;
    }
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
