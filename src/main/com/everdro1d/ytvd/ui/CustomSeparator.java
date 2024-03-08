package main.com.everdro1d.ytvd.ui;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class CustomSeparator extends JPanel {
    public CustomSeparator(boolean subtract, int widthDiv, int height) {
        super();
        int width = MainWindow.windowWidth / widthDiv;
        if (subtract) width = MainWindow.windowWidth - (MainWindow.windowWidth / widthDiv);

        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
    }
}
