package main.java;

import javax.swing.*;
import java.awt.*;

public class DoNotAskAgainConfirmDialog extends JPanel {
    private JCheckBox doNotAskAgainCheckBox;

    public DoNotAskAgainConfirmDialog(Object message) {
        setLayout(new BorderLayout());

        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            add(new JLabel(message.toString()));
        }

        doNotAskAgainCheckBox = new JCheckBox("Don't ask me again");
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(doNotAskAgainCheckBox);
        add(checkBoxPanel, BorderLayout.SOUTH);
    }

    public boolean isDoNotAskAgainSelected() {
        return doNotAskAgainCheckBox.isSelected();
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, String prefsKey) {
        int result;

        if (MainWorker.prefs.getBoolean(prefsKey, false)) {
            return JOptionPane.YES_OPTION;
        } else {
            DoNotAskAgainConfirmDialog confirmDialog = new DoNotAskAgainConfirmDialog(message);
            result = JOptionPane.showOptionDialog(parentComponent, confirmDialog, title, optionType, messageType, null, null, null);
            if (confirmDialog.isDoNotAskAgainSelected()) {
                MainWorker.prefs.putBoolean(prefsKey, true);
            }
        }
        return result;
    }
}
