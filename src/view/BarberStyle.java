
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

public class BarberStyle {

    public static final Color COLOR_PRIMARY   = new Color(0, 102, 204);
    public static final Color COLOR_SECONDARY = new Color(245, 245, 245);
    public static final Color COLOR_ACCENT    = new Color(100, 100, 100);
    public static final Color COLOR_WHITE     = Color.WHITE;
    public static final Color COLOR_TEXT      = new Color(50, 50, 50);
    public static final Color COLOR_DANGER    = new Color(220, 53, 69);
    public static final Color COLOR_SUCCESS   = new Color(40, 167, 69);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);

    /**
     * Deve ser chamado UMA VEZ antes de criar qualquer janela (ex: no Main).
     * Remove todos os efeitos de hover do UIManager globalmente.
     */
    public static void applyGlobalStyle() {
        UIManager.put("Button.rollover", false);
        UIManager.put("Button.rolloverEnabled", false);
        UIManager.put("Button.select", new ColorUIResource(COLOR_PRIMARY));

        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("Table.focusCellBackground", new ColorUIResource(Color.WHITE));
        UIManager.put("Table.focusCellForeground", new ColorUIResource(COLOR_TEXT));
        UIManager.put("TableHeader.cellBorder", BorderFactory.createEmptyBorder());

        UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(180, 180, 180)));

        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(new Color(180, 180, 180)));
    }

    public static void applyButtonStyle(JButton button) {
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getDefaultCursor());
    }

    public static void applyBackButtonStyle(JButton button) {
        button.setBackground(COLOR_ACCENT);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getDefaultCursor());
    }

    public static void applyDangerButtonStyle(JButton button) {
        button.setBackground(COLOR_DANGER);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getDefaultCursor());
    }

    public static void applySuccessButtonStyle(JButton button) {
        button.setBackground(COLOR_SUCCESS);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Aplica estilo padrão a uma JTable, sem efeitos de hover.
     */
    public static void applyTableStyle(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(173, 214, 255));
        table.setSelectionForeground(COLOR_TEXT);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setFocusable(false);

        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setCursor(Cursor.getDefaultCursor());
    }

    public static JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(0, 60));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(COLOR_WHITE);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);

        return header;
    }
}
