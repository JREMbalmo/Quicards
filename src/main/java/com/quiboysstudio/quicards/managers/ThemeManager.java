package com.quiboysstudio.quicards.managers;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;

    private boolean darkMode = true;
    private final List<ThemeChangeListener> listeners = new ArrayList<>();

    private ThemeManager() { }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void toggleTheme() {
        darkMode = !darkMode;
        notifyListeners();
    }

    public void addListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (ThemeChangeListener l : listeners) {
            l.onThemeChanged(darkMode);
        }
    }
}