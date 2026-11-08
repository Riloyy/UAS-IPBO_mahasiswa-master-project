package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.dao.StudentDAO;
import com.ubl.datamahasiswa.model.Student;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JTextField dengan saran (autocomplete) NIM: menampilkan popup daftar NIM yang cocok
 * saat mengetik. Pilih dengan klik, Enter, atau Tab.
 */
public class NimSuggestField extends JTextField {

    private final List<String> allNims;
    private final JPopupMenu popup = new JPopupMenu();
    private final JList<String> list = new JList<>();
    private boolean updating = false;

    public NimSuggestField(List<String> nims) {
        super(14);
        this.allNims = new ArrayList<>(nims);

        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setPreferredSize(new Dimension(190, 40));
        setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new Color(0xAFCFC2), 1, true),
                new javax.swing.border.EmptyBorder(6, 10, 6, 10)));

        list.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        popup.add(sp);
        popup.setFocusable(false);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                acceptSelection();
            }
        });

        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!popup.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        refresh();
                        return;
                    }
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        moveSelection(1);
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        moveSelection(-1);
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        acceptSelection();
                        e.consume();
                        break;
                    case KeyEvent.VK_TAB:
                        acceptSelection();
                        // biarkan fokus pindah
                        break;
                    case KeyEvent.VK_ESCAPE:
                        popup.setVisible(false);
                        e.consume();
                        break;
                    default:
                        break;
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Component opp = e.getOppositeComponent();
                if (opp != null && (opp == list || SwingUtilities.isDescendingFrom(opp, popup))) {
                    return;
                }
                SwingUtilities.invokeLater(() -> popup.setVisible(false));
            }
        });
    }

    /** Bangun dari DAO agar daftar NIM dimuat otomatis. */
    public static NimSuggestField fromDao(StudentDAO dao) {
        List<String> nims = new ArrayList<>();
        for (Student s : dao.findAll()) {
            if (s.getNim() != null) nims.add(s.getNim());
        }
        return new NimSuggestField(nims);
    }

    private void refresh() {
        if (updating) return;
        String text = getText().trim().toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String n : allNims) {
            if (text.isEmpty() || n.toLowerCase().startsWith(text)) {
                filtered.add(n);
            }
        }
        if (filtered.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        list.setListData(filtered.toArray(new String[0]));
        if (list.getSelectedIndex() < 0) list.setSelectedIndex(0);

        int h = Math.min(filtered.size() * 22 + 4, 160);
        popup.setPreferredSize(new Dimension(getWidth(), h));
        if (!popup.isVisible()) {
            popup.show(this, 0, getHeight());
        } else {
            popup.pack();
        }
    }

    private void moveSelection(int delta) {
        int idx = list.getSelectedIndex() + delta;
        int max = list.getModel().getSize() - 1;
        if (idx < 0) idx = 0;
        if (idx > max) idx = max;
        list.setSelectedIndex(idx);
        list.ensureIndexIsVisible(idx);
    }

    private void acceptSelection() {
        String sel = list.getSelectedValue();
        if (sel == null && list.getModel().getSize() > 0) {
            sel = list.getModel().getElementAt(0);
        }
        if (sel != null) {
            updating = true;
            try {
                setText(sel);
                // pindah caret ke akhir
                setCaretPosition(getText().length());
            } finally {
                updating = false;
            }
        }
        popup.setVisible(false);
    }
}
