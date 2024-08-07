package oracle.com.c1apiautomation.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.Closeable;

public abstract class SelectableBase implements Cloneable {

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

    @Override
    public SelectableBase clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (SelectableBase) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

