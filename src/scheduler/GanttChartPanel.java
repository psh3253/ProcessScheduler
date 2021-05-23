package scheduler;

import javax.swing.*;
import java.awt.*;

public class GanttChartPanel extends JPanel {

    private int[][] result = null;

    private int x = 0;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.RED);
        if (result == null)
            return;
        for (int i = 0; i < result.length; i++) {
            g.drawString("p" + result[i][0], x * 10, 10);
            g.drawString(String.valueOf(x), x * 10, 25);
            g.drawRect(x * 10, 35, result[i][1] * 10, 30);
            x += result[i][1];
        }
    }

    public void setResult(int[][] result) {
        this.result = result;
    }
}
