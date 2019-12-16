package Core;

import javafx.scene.layout.Pane;

public interface GUIController
{
    void update(Long currentNanoTime);
    void render(Long currentNanoTime);
    Pane load();
}
