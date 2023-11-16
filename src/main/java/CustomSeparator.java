package main.java;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class CustomSeparator extends JPanel {
    public CustomSeparator(int widthDiv, int height) {
        super();
        int width = MainWindow.windowWidth - MainWindow.windowWidth / widthDiv;

        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
    }
}
