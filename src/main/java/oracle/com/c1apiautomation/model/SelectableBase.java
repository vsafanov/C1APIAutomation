package oracle.com.c1apiautomation.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.Closeable;

public abstract class SelectableBase implements Cloneable {

    private  BooleanProperty selected = new SimpleBooleanProperty();

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
            SelectableBase copy = (SelectableBase) super.clone();
            // Deep copy the BooleanProperty
            copy.selected = new SimpleBooleanProperty(this.selected.get());//.set(this.isSelected());
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

