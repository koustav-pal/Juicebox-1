/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2015 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.state;

import juicebox.HiCGlobals;
import juicebox.MainWindow;
import juicebox.gui.SuperAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Zulkifl on 7/31/2015.
 */


public class Slideshow extends JDialog {

    private final static JPanel nextPanel = new JPanel(new BorderLayout());
    private final static JPanel prevPanel = new JPanel(new BorderLayout());
    private final static JPanel labelPanel = new JPanel(new BorderLayout());
    private final static JButton nextButton = new JButton("\u25BA");
    private final static JButton prevButton = new JButton("\u25C4");
    private static final String statesForSlideshow = HiCGlobals.xmlSavedStatesFileName;
    private static final long serialVersionUID = -1443095232042271867L;
    private final JLabel slideLabel;

    public Slideshow(MainWindow mainWindow, final SuperAdapter superAdapter) {
        super(mainWindow);
        // TODO zgire - if panel needs to be independent of juicebox, then remove the line above (i.e. pass in mainwindow as parent)

        setLayout(new FlowLayout());
        setResizable(true);
        setVisible(true);
        setSize(400, 100);
        add(prevPanel);
        add(labelPanel);
        add(nextPanel);

        prevPanel.add(prevButton, BorderLayout.EAST);
        prevPanel.setVisible(true);

        slideLabel = new JLabel();
        labelPanel.add(slideLabel, BorderLayout.CENTER);
        labelPanel.setVisible(true);

        nextPanel.add(nextButton, BorderLayout.WEST);
        nextPanel.setVisible(true);

        try {
            final ArrayList<String> savedStatePaths = new ArrayList<String>();
            Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            dom = db.parse(statesForSlideshow);
            NodeList nodeList = dom.getElementsByTagName("STATE");
            for (int i = 0; i < nodeList.getLength(); i++) {
                savedStatePaths.add(nodeList.item(i).getAttributes().getNamedItem("SelectedPath").getNodeValue());
            }
            System.out.println(savedStatePaths);
            final int numSlides = savedStatePaths.size();

            slideLabel.setText(savedStatePaths.get(0));

            prevButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int counter = savedStatePaths.indexOf(slideLabel.getText());
                    if (counter >= 0) {
                        counter = ((counter - 1) + numSlides) % numSlides;
                        slideLabel.setText(savedStatePaths.get(counter));
                        LoadStateFromXMLFile.reloadSelectedState(superAdapter, savedStatePaths.get(counter));
                    }
                }
            });


            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int counter = savedStatePaths.indexOf(slideLabel.getText());
                    if (counter < numSlides) {
                        counter = (counter + 1) % numSlides;
                        slideLabel.setText(savedStatePaths.get(counter));
                        LoadStateFromXMLFile.reloadSelectedState(superAdapter, savedStatePaths.get(counter));
                    }
                }
            });


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        setLocationRelativeTo(getOwner());
    }
}