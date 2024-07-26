package application.tools;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FormValidation {
    /**
     * Vérifie si un champ de texte est vide et affiche un message d'erreur si c'est le cas.
     *
     * @param textFields    Champs de texte
     * @param labels        Étiquettes d'erreur
     * @param errorMessages Message d'erreur
     * @return true si le champ de texte est vide, false sinon
     * @author SHARIFI Daner
     */
    public static boolean handleIsTextFieldEmpty(TextField[] textFields, Label[] labels, String[] errorMessages) {
        boolean isTextFieldEmpty = false;
        int focusIndex = -1;
        for (int i = 0; i < textFields.length; i++) {
            if (textFields[i].getText().trim().isEmpty()) {
                setFieldError(textFields[i], labels[i], errorMessages[i] + " vide");
                isTextFieldEmpty = true;
                if (focusIndex == -1) focusIndex = i;
            } else {
                setFieldValid(textFields[i], labels[i]);
            }
        }
        if (focusIndex != -1) textFields[focusIndex].requestFocus();
        return !isTextFieldEmpty;
    }

    /**
     * Mettre en évidence un champ de texte et son étiquette d'erreur.
     *
     * @param textField    Champ de texte
     * @param label        Étiquette d'erreur
     * @param errorMessage Message d'erreur
     * @author SHARIFI Daner
     */
    public static void setFieldError(TextField textField, Label label, String errorMessage) {
        setFieldNeutral(textField, label);
        label.setText(errorMessage);
        textField.getStyleClass().remove("text-field-valid");
        textField.getStyleClass().add("text-field-error");
    }

    /**
     * Mettre en évidence un champ de texte et son étiquette de succès.
     *
     * @param textField Champ de texte
     * @param label     Étiquette d'erreur
     * @author SHARIFI Daner
     */
    public static void setFieldValid(TextField textField, Label label) {
        setFieldNeutral(textField, label);
        label.setText("");
        textField.getStyleClass().remove("text-field-error");
        textField.getStyleClass().add("text-field-valid");
    }

    /**
     * Réinitialise un champ de texte et son étiquette d'erreur.
     *
     * @param textField Champ de texte
     * @param label     Étiquette d'erreur
     * @author SHARIFI Daner
     */
    public static void setFieldNeutral(TextField textField, Label label) {
        label.setText("");
        textField.getStyleClass().remove("text-field-error");
        textField.getStyleClass().remove("text-field-valid");
    }

    /**
     * Vérifie si la longueur d'un champ de texte est invalide et affiche un message d'erreur si c'est le cas.
     *
     * @param textFields    Champs de texte
     * @param labels        Étiquettes d'erreur
     * @param min           Longueur minimale
     * @param max           Longueur maximale
     * @param errorMessages Message d'erreur
     * @return true si la longueur du champ de texte est invalide, false sinon
     * @author SHARIFI Daner
     */
    public static boolean handleIsTextFieldLengthInvalid(TextField[] textFields, Label[] labels, int[] min, int[] max, String[] errorMessages) {
        boolean isTextFieldLengthInvalid = false;
        int focusIndex = -1;
        for (int i = 0; i < textFields.length; i++) {
            if (textFields[i].getText().length() < min[i] || textFields[i].getText().length() > max[i]) {
                errorMessages[i] = errorMessages[i] + " doit contenir entre " + min[i] + " et " + max[i] + " caractères.";
                setFieldError(textFields[i], labels[i], errorMessages[i]);
                isTextFieldLengthInvalid = true;
                if (focusIndex == -1) focusIndex = i;
            } else {
                setFieldValid(textFields[i], labels[i]);
            }
        }
        if (focusIndex != -1) textFields[focusIndex].requestFocus();
        return !isTextFieldLengthInvalid;
    }

    /**
     * Vérifie si un champ de texte est un entier et affiche un message d'erreur si ce n'est pas le cas.
     *
     * @param textFields    Champs de texte
     * @param labels        Étiquettes d'erreur
     * @param errorMessages Message d'erreur
     * @return true si le champ de texte est un entier, false sinon
     * @author SHARIFI Daner
     */
    public static boolean handleIsIntegerMinMax(TextField[] textFields, Label[] labels, int[] min, int[] max, String[] errorMessages) {
        boolean isTextFieldLengthInvalid = false;
        int focusIndex = -1;

        boolean hasMin = min.length == textFields.length;
        boolean hasMax = max.length == textFields.length;
        String errorMessage = "";

        for (int i = 0; i < textFields.length; i++) {
            try {
                float valueF = Float.parseFloat(textFields[i].getText());
                int value = (int) valueF;
                boolean isValid = true;

                if (hasMax && hasMin) {
                    if (value < min[i] || value > max[i]) {
                        errorMessage = " doit être compris entre " + min[i] + " et " + max[i] + ".";
                        isValid = false;
                    }
                } else if (hasMax) {
                    if (value > max[i]) {
                        errorMessage = " doit être inférieur à " + max[i] + ".";
                        isValid = false;
                    }
                } else if (hasMin) {
                    if (value < min[i]) {
                        errorMessage = " doit être supérieur à " + min[i] + ".";
                        isValid = false;
                    }
                } else {
                    errorMessage = "Erreur de configuration.";
                    isValid = false;
                }
                if (isValid) {
                    setFieldValid(textFields[i], labels[i]);
                } else {
                    errorMessage = !errorMessages[i].isEmpty() ? errorMessages[i] : errorMessage;
                    setFieldError(textFields[i], labels[i], errorMessage);
                    isTextFieldLengthInvalid = true;
                    if (focusIndex == -1) focusIndex = i;
                }
            } catch (NumberFormatException e) {
                errorMessage = !errorMessages[i].isEmpty() ? errorMessages[i] : "Ce champ doit être un entier.";
                setFieldError(textFields[i], labels[i], errorMessage);
                isTextFieldLengthInvalid = true;
                if (focusIndex == -1) focusIndex = i;
            }
        }
        return !isTextFieldLengthInvalid;
    }

}
