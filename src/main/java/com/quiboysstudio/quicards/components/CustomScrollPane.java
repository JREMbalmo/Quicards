package com.quiboysstudio.quicards.components;

import javax.swing.*;
import java.awt.*;

public class CustomScrollPane extends JScrollPane {

    public CustomScrollPane(Component view) {
        super(view);

        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(null);
        getVerticalScrollBar().setUnitIncrement(16);

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}