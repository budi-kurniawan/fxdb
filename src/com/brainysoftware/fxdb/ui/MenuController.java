package com.brainysoftware.fxdb.ui;

import java.util.ResourceBundle;
import com.brainysoftware.fxdb.util.GUIUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */

public class MenuController implements Initializable {

    @FXML private MenuBar menuBar;

    @FXML private void handleAboutAction(final ActionEvent event) {
        GUIUtil.showMessageDialog("FxDb 0.9", "Author: Budi Kurniawan (http://brainysoftware.com)");
    }

    @FXML private void handleExit(final ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);
        System.out.println("Initialize MenuController...");
    }
}