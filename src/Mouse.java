import lombok.Getter;
import lombok.Setter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
@Setter
public class Mouse extends MouseAdapter {
    public int x, y;
    public boolean pressed;
    public boolean released;

    public Mouse() {
        x = 0;
        y = 0;
        pressed = false;
        released = false;
    }

    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        pressed = true;
    }

    public void mouseReleased(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        released = true;
    }
}
