package by.babanin.todo.view.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private final Image image;

    public ImagePanel(Image image) {
        this.image = image;
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
