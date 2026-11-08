package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class StudentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"NIM", "Nama Mahasiswa", "Jurusan", "Angkatan", "Status"};

    private List<Student> rows = new ArrayList<>();

    public void setRows(List<Student> rows) {
        this.rows = new ArrayList<>(rows);
        fireTableDataChanged();
    }

    public Student getStudentAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.getNim();
            case 1: return s.getNama();
            case 2: return s.getJurusan();
            case 3: return s.getAngkatan();
            case 4: return s.getStatus();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
