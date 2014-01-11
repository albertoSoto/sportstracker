package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.Date310Utils;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.GuiCreateUtils;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXDatePicker;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * This class is the implementation of the dialog for editing / adding
 * Note entries.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class NoteDialog extends JDialog {

    private final STContext context;
    private final STDocument document;

    private JXDatePicker dpDate;
    private JSpinner spHour, spMinute;
    private JTextArea taText;
    private JButton btOK, btCancel;

    /**
     * This is the exercises object edited in this dialog.
     */
    private Note note;

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_OK = "st.dlg.note.ok";
    private static final String ACTION_CANCEL = "st.dlg.note.cancel";


    /**
     * Standard c'tor. The method setNote() needs to be called before
     * showing the dialog.
     *
     * @param context the SportsTracker context
     * @param document the application document component
     */
    @Inject
    public NoteDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        initGUI();

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        btOK.setAction(actionMap.get(ACTION_OK));

        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);
    }

    private void initGUI() {
        setName("st.dlg.note");

        // create all controls
        JLabel laDate = GuiCreateUtils.createLabel("st.dlg.note.date", false);
        JLabel laTime = GuiCreateUtils.createLabel("st.dlg.note.time", false);
        JLabel laTimeSeparator = new JLabel(":");
        JLabel laText = GuiCreateUtils.createLabel("st.dlg.note.text", false);

        dpDate = GuiCreateUtils.createDatePicker();
        spHour = GuiCreateUtils.createSpinner(new SpinnerNumberModel(12, 0, 23, 1));
        spMinute = GuiCreateUtils.createSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        taText = new JTextArea();
        taText.setLineWrap(true);
        taText.setWrapStyleWord(true);
        JScrollPane spText = new JScrollPane(taText);
        // use same font in textarea as in textfield (not default on Win32)
        taText.setFont(new JTextField().getFont());

        btOK = new JButton();
        btCancel = new JButton();
        JPanel pButtons = GuiCreateUtils.createDialogButtonPanel(btCancel, btOK);

        // use MigLayout as layout manager
        Container pane = getContentPane();
        pane.setLayout(new MigLayout(
                "insets 12, gap 12", // Layout Constraints
                "[][]25[][]5[]5[]",  // Column constraints
                "[]16[]8[grow][]")); // Row constraints

        pane.add(laDate);
        pane.add(dpDate);
        pane.add(laTime);
        pane.add(spHour);
        pane.add(laTimeSeparator);
        pane.add(spMinute, "wrap");
        pane.add(laText, "wrap");
        pane.add(spText, "spanx, width 400, height 160, grow, wrap");
        pane.add(pButtons, "spanx, growx");

        getRootPane().setDefaultButton(btOK);
        setResizable(false);
        setLocationRelativeTo(getParent());
        pack();
    }

    /**
     * Initializes the dialog with the specified note.
     *
     * @param note the Note object to be edited
     */
    public void setNote(Note note) {
        this.note = note;
        setInitialValues();
    }

    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {
        // display "Add New ..." title when it's a new Note
        if (document.getNoteList().getByID(note.getId()) == null) {
            super.setTitle(context.getResReader().getString("st.dlg.note.title.add"));
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // must be done before displaying, otherwise it's not placed centered
            setLocationRelativeTo(getParent());
        }
        super.setVisible(visible);
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues() {

        // set date (formatting is done by the textfield) and time
        dpDate.setDate(Date310Utils.localDateTimeToDate(note.getDateTime()));
        spHour.setValue(note.getDateTime().getHour());
        spMinute.setValue(note.getDateTime().getMinute());

        taText.setText(note.getText());
        taText.setCaretPosition(0);
    }

    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name = ACTION_OK)
    public void ok() {

        // create a new Note, because user can cancel after validation errors
        // => so we don't modify the original Note
        Note newNote = new Note(note.getId());

        // check date input
        if (dpDate.getDate() == null) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.note.error.date");
            dpDate.requestFocus();
            return;
        }

        // store date and time of the note
        LocalDateTime newDateTime = Date310Utils.dateToLocalDateTime(dpDate.getDate());
        newDateTime = newDateTime.withHour((Integer) spHour.getValue());
        newDateTime = newDateTime.withMinute((Integer) spMinute.getValue());
        newDateTime = newDateTime.withSecond(0);
        note.setDateTime(newDateTime);

        // get note text
        String strText = taText.getText().trim();
        if (strText.length() == 0) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.note.error.no_text");
            taText.requestFocus();
            return;
        }
        newNote.setText(strText);

        // finally store the new Note and close dialog
        document.getNoteList().set(newNote);
        this.dispose();
    }

    /**
     * Action for closing the dialog with the Cancel button.
     */
    @Action(name = ACTION_CANCEL)
    public void cancel() {
        this.dispose();
    }
}