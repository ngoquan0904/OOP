package com.example.Game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Mouse {
    public  int x;
    public  int y;
    boolean Pressed;

    public void setPressed(boolean isPressed) {
        this.Pressed = isPressed;
    }

    public boolean isPressed() {
        return Pressed;
    }
}

