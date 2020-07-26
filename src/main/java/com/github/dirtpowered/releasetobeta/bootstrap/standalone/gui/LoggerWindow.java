/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.bootstrap.standalone.gui;

import javax.swing.*;
import java.awt.*;

//TODO: make it better
public class LoggerWindow {
    private JTextArea logArea;
    private boolean headless;

    public LoggerWindow(boolean headless) {
        this.headless = headless;
    }

    public void show() {
        if (headless) return;

        JFrame frame = new JFrame("Release2Beta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 300);

        frame.setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setBackground(Color.DARK_GRAY);
        logArea.setMargin(new Insets(5,5,5,5));

        logArea.setDisabledTextColor(Color.LIGHT_GRAY);
        logArea.setEnabled(false);
        logArea.setFont(new Font("monospaced", Font.PLAIN, 12));

        frame.getContentPane().add(BorderLayout.CENTER, logArea);

        JScrollPane scroll = new JScrollPane (logArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scroll);

        frame.setVisible(true);
    }

    void append(String text) {
        if (headless) return;

        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}