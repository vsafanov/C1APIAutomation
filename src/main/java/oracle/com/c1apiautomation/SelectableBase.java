package oracle.com.c1apiautomation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class SelectableBase {
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty getSelected() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}

