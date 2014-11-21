package de.saring.exerciseviewer.gui;

import de.saring.util.AppResources;
import de.saring.util.ResourceReader;
import de.saring.util.unitcalc.FormatUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * This is the GUI context of the ExerciseViewer sub-application. It contains the
 * ApplicationContext of the Swing Application Framework and some helper methods.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface EVContext {

    /**
     * Returns the ApplicationContext of the Swing Application Framework.
     *
     * @return the ApplicationContext
     */
    ApplicationContext getSAFContext();

    /**
     * Returns the main frame of the application (e.g. needed for dialog creation).
     *
     * @return the main frame of the application
     */
    JFrame getMainFrame();

    /**
     * Returns the primary stage (main window) of the JavaFX application.
     *
     * @return Stage
     */
    Stage getPrimaryStage();

    /**
     * Displays the specified dialog. This will be delegated to SingleFrameApplication.show(),
     * so the benefits of the Swing Application Framework can be used.
     *
     * @param dlg the dialog to display
     */
    void showDialog(JDialog dlg);

    /**
     * Displays a modal message dialog with the specified message text, title and
     * message type for the specified parent component.
     *
     * @param parent the parent component of the message dialog
     * @param msgType message type of dialog (use constants of JOptionPane)
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     * @param arguments list of objects which needs to be inserted in the message text (optional)
     */
    void showMessageDialog(Component parent, int msgType, String titleKey, String messageKey, Object... arguments);

    /**
     * Displays a modal confirmation dialog with the specified message text and title
     * for the specified parent component.
     *
     * @param parent the parent component of the message dialog
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     * @return an int indicating the option selected by the user
     */
    int showConfirmDialog(Component parent, String titleKey, String messageKey);

    /**
     * Displays a JavaFX modal message dialog of the passed type with the specified message title and message
     * for the specified parent window.
     *
     * @param parent the parent component of the message dialog
     * @param alertType the type of the message dialog
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     * @param arguments list of objects which needs to be inserted in the message text (optional)
     * @return An Optional that contains the result of the displayed dialog.
     */
    Optional<ButtonType> showFxMessageDialog(javafx.stage.Window parent, Alert.AlertType alertType, String titleKey,
                             String messageKey, Object... arguments);

    /**
     * Displays a JavaFX modal text input dialog for the specified parameters.
     *
     * @param parent parent window of the input dialog
     * @param titleKey resource key for the dialog title
     * @param messageKey resource key for the dialog message
     * @param initialValue initial text value to be displayed
     * @return Optional containing the entered String (can be empty text) or Optional.empty() when the user has cancelled the dialog
     */
    Optional<String> showFxTextInputDialog(javafx.stage.Window parent, String titleKey, String messageKey,
            String initialValue);

    /**
     * Returns the helper class for reading resources from the applications properties
     * files for the current locale.
     *
     * @return the helper class for reading resources
     */
    ResourceReader getResReader();

    /**
     * Returns the provider of application text resources for the JavaFX based UI.
     *
     * @return AppResources
     */
    AppResources getFxResources();

    /**
     * Returns the format utils class for the current unit system.
     *
     * @return the current FormatUtils instance
     */
    FormatUtils getFormatUtils();
}
