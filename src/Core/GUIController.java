package Core;

import javafx.scene.layout.Pane;

public interface GUIController
{
    void update(Double elapsedTime);
    void render(Double elapsedTime);
    Pane load();
}
