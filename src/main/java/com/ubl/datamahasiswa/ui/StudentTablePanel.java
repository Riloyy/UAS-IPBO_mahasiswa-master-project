package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.dao.StudentDAO;
import com.ubl.datamahasiswa.model.Student;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class StudentTablePanel extends WhiteCardPanel {

    private static final Color NAVY = new Color(0x0D2038);
    private static final Color STEEL = new Color(0x3B6EA5);
    private static final Color RED = new Color(0xC0392B);
    private static final Color GREEN = new Color(0x2E7D32);
    private static final Color YELLOW = new Color(0xC9A227);
    private static final Color BLUE = new Color(0x1565C0);
    private static final Color AMBER = new Color(0xE8940F);
    private static final Color AMBER_TEXT = new Color(0x272100);
    private static final Color TEXT = new Color(0x22333B);
    private static final Color GRAY = new Color(0x6E7B8A);
    private static final Color ROW_ODD = new Color(0xEEF7F2);
    private static final Color ROW_HOVER = new Color(0xC5EAD9);
    private static final Color ROW_SELECTED = new Color(0xF6A821);
    private static final Color FIELD_BORDER = new Color(0xAFCFC2);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final StudentDAO dao = new StudentDAO();
    private StudentTableModel model;
    private JTable table;
    private JTextField searchField;
    private JLabel statusLabel;
    private int hoveredRow = -1;
    private final List<StudentSelectionListener> selectionListeners = new ArrayList<>();

    public interface StudentSelectionListener {
        void onSelected(Student s);
    }

    public void addSelectionListener(StudentSelectionListener l) {
        selectionListeners.add(l);
    }

    public void selectFirstRow() {
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            fireSelection();
        }
    }

    public StudentTablePanel() {
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(24, 28, 22, 28));

        add(buildHeadingArea(), BorderLayout.NORTH);

        model = new StudentTableModel();
        table = buildTable(model);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fireSelection();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xDCE9E2)));
        add(scroll, BorderLayout.CENTER);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(GRAY);
        add(statusLabel, BorderLayout.SOUTH);

        refreshData();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshData();
            }
        });
    }

    private JComponent buildHeadingArea() {
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Data Mahasiswa");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        t1.setForeground(NAVY);
        JLabel t2 = new JLabel("Kelola data mahasiswa, lalu cetak laporan PDF.");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        t2.setForeground(GRAY);
        titleStack.add(t1);
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(t2);

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        searchWrap.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel lblSearch = new JLabel("Cari  ");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSearch.setForeground(TEXT);
        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(FIELD_BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        searchWrap.add(lblSearch, BorderLayout.WEST);
        searchWrap.add(searchField, BorderLayout.CENTER);
        RoundedButton btnClearSearch = new RoundedButton("Clear", new Color(0xD8E4DE), NAVY);
        btnClearSearch.addActionListener(e -> searchField.setText(""));
        searchWrap.add(btnClearSearch, BorderLayout.EAST);
        row1.add(titleStack, BorderLayout.WEST);
        row1.add(searchWrap, BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);

        JPanel leftButtons = new JPanel();
        leftButtons.setOpaque(false);
        leftButtons.setLayout(new BoxLayout(leftButtons, BoxLayout.X_AXIS));
        RoundedButton btnAdd = new RoundedButton("Tambah", NAVY, Color.WHITE);
        RoundedButton btnEdit = new RoundedButton("Edit", STEEL, Color.WHITE);
        RoundedButton btnDelete = new RoundedButton("Hapus", RED, Color.WHITE);
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        leftButtons.add(btnAdd);
        leftButtons.add(Box.createHorizontalStrut(10));
        leftButtons.add(btnEdit);
        leftButtons.add(Box.createHorizontalStrut(10));
        leftButtons.add(btnDelete);

        RoundedButton btnReport = new RoundedButton("Cetak Laporan (PDF)", AMBER, AMBER_TEXT);
        btnReport.addActionListener(e -> onOpenReport());

        row2.add(leftButtons, BorderLayout.WEST);
        row2.add(btnReport, BorderLayout.EAST);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(row1);
        stack.add(Box.createVerticalStrut(14));
        stack.add(row2);
        return stack;
    }

    private JTable buildTable(StudentTableModel tableModel) {
        JTable t = new JTable(tableModel);
        t.setOpaque(false);
        t.setRowHeight(40);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        t.setRowSelectionAllowed(true);
        t.setColumnSelectionAllowed(false);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setReorderingAllowed(false);
        t.getColumnModel().getColumn(0).setPreferredWidth(150);
        t.getColumnModel().getColumn(1).setPreferredWidth(230);
        t.getColumnModel().getColumn(2).setPreferredWidth(170);
        t.getColumnModel().getColumn(3).setPreferredWidth(90);
        t.getColumnModel().getColumn(4).setPreferredWidth(90);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jt, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(jt, value, false, false, row, column);
                boolean rowSelected = jt.isRowSelected(row);
                if (rowSelected) {
                    c.setBackground(ROW_SELECTED);
                    c.setForeground(AMBER_TEXT);
                    c.setFont(FONT_BOLD);
                } else if (row == hoveredRow) {
                    c.setBackground(ROW_HOVER);
                    c.setForeground(Color.BLACK);
                    c.setFont(FONT_PLAIN);
                 } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ODD);
                    c.setForeground(TEXT);
                    c.setFont(FONT_PLAIN);
                }
                if (column == 4) {
                    if (!rowSelected) {
                        Color sc = statusColor(String.valueOf(value));
                        if (sc != null) c.setForeground(sc);
                    }
                    c.setFont(FONT_BOLD);
                }
                if (c instanceof JLabel jl) {
                    jl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                }
                ((JComponent) c).setBorder(new EmptyBorder(0, 12, 0, 8));
                return c;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jt, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(jt, value, false, false, row, column);
                c.setBackground(NAVY);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                ((JComponent) c).setBorder(new EmptyBorder(6, 12, 6, 8));
                return c;
            }
        };
        JTableHeader hdr = t.getTableHeader();
        hdr.setDefaultRenderer(headerRenderer);
        hdr.setOpaque(true);
        hdr.setBackground(NAVY);
        hdr.setPreferredSize(new Dimension(0, 34));

        t.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    t.repaint();
                }
            }
        });
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredRow != -1) {
                    hoveredRow = -1;
                    t.repaint();
                }
            }
        });
        return t;
    }

    private static Color statusColor(String status) {
        if (status == null) return null;
        String s = status.trim().toLowerCase();
        if (s.equals("aktif")) return GREEN;
        if (s.equals("cuti")) return YELLOW;
        if (s.equals("nonaktif") || s.equals("non-aktif") || s.equals("non aktif")) return RED;
        if (s.equals("lulus")) return BLUE;
        return null;
    }

    private void fireSelection() {
        int viewRow = table.getSelectedRow();
        Student s = null;
        if (viewRow != -1) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            s = model.getStudentAt(modelRow);
        }
        for (StudentSelectionListener l : selectionListeners) {
            l.onSelected(s);
        }
    }

    private void refreshData() {
        String kw = searchField == null ? "" : searchField.getText().trim();
        List<Student> rows = kw.isEmpty() ? dao.findAll() : dao.search(kw);
        model.setRows(rows);
        if (statusLabel != null) {
            statusLabel.setText(rows.size() + " mahasiswa ditampilkan");
        }
    }

    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private void onAdd() {
        StudentFormDialog dlg = new StudentFormDialog(owner(), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            refreshData();
        }
    }

    private void onEdit() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(owner(), "Pilih mahasiswa yang ingin diedit.", "Pilih Data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Student s = model.getStudentAt(modelRow);
        StudentFormDialog dlg = new StudentFormDialog(owner(), s);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            refreshData();
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(owner(), "Pilih mahasiswa yang ingin dihapus.", "Pilih Data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Student s = model.getStudentAt(modelRow);
        int opt = JOptionPane.showConfirmDialog(owner(),
                "Yakin ingin menghapus mahasiswa ini?\nNIM  : " + s.getNim() + "\nNama : " + s.getNama(),
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            dao.delete(s.getNim());
            refreshData();
            JOptionPane.showMessageDialog(owner(),
                    "Data mahasiswa berhasil dihapus:\nNIM  : " + s.getNim() + "\nNama : " + s.getNama(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onOpenReport() {
        ReportDialog dlg = new ReportDialog(owner());
        dlg.setVisible(true);
    }
}
