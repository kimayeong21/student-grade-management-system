import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class UniversityGradeManager extends JFrame {
    private JTable gradeTable; // 성적을 표시할 테이블
    private DefaultTableModel tableModel; // 테이블 모델
    private HashMap<String, ArrayList<String[]>> semesterGrades; // 년도/학기별 성적을 저장하는 해시맵
    private JComboBox<String> semesterComboBox; // 년도/학기 선택 콤보박스
    private List<String> semesters; // 학기 목록
    private int currentPage; // 현재 페이지 번호

    public UniversityGradeManager() {
        semesterGrades = new HashMap<>();
        semesters = new ArrayList<>();
        currentPage = 0;

        // 기본 설정
        setTitle("개인 성적 관리 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // 상단 패널에 제목과 열람 버튼 추가
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("개인 성적 관리 프로그램", SwingConstants.CENTER);
        titleLabel.setFont(new Font("돋움", Font.PLAIN, 22));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton openFileButton = new JButton("열람");
        openFileButton.setFont(new Font("돋움", Font.PLAIN, 18));
        openFileButton.addActionListener(new OpenFileButtonListener());
        topPanel.add(openFileButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 왼쪽 패널에 버튼 추가
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 5, 5));

        // 버튼 스타일 설정
        Font buttonFont = new Font("돋움", Font.PLAIN, 16);
        LineBorder buttonBorder = new LineBorder(Color.BLACK, 1, true);

        // 추가 버튼
        JButton addButton = new JButton("추가");
        addButton.setFont(buttonFont);
        addButton.setBorder(buttonBorder);
        addButton.setPreferredSize(new Dimension(100, 50)); // 크기 줄이기
        addButton.addActionListener(new AddButtonListener());
        panel.add(addButton);

        // 수정 버튼
        JButton updateButton = new JButton("수정");
        updateButton.setFont(buttonFont);
        updateButton.setBorder(buttonBorder);
        updateButton.setPreferredSize(new Dimension(100, 50)); // 크기 줄이기
        updateButton.addActionListener(new UpdateButtonListener());
        panel.add(updateButton);

        // 삭제 버튼
        JButton deleteButton = new JButton("삭제");
        deleteButton.setFont(buttonFont);
        deleteButton.setBorder(buttonBorder);
        deleteButton.setPreferredSize(new Dimension(100, 50)); // 크기 줄이기
        deleteButton.addActionListener(new DeleteButtonListener());
        panel.add(deleteButton);

        // 패널을 왼쪽에 추가
        add(panel, BorderLayout.WEST);

        // 성적을 표시할 테이블
        String[] columnNames = { "교과목명", "년도/학기", "이수구분", "등급", "학점" };
        tableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(tableModel);

        // 테이블 폰트 크기 설정
        Font tableFont = new Font("돋움", Font.PLAIN, 18);
        gradeTable.setFont(tableFont);
        gradeTable.setRowHeight(30);

        // 테이블 내용 가운데 정렬 설정
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < gradeTable.getColumnCount(); i++) {
            gradeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 테이블 헤더 폰트 크기 설정
        JTableHeader tableHeader = gradeTable.getTableHeader();
        tableHeader.setFont(new Font("돋움", Font.PLAIN, 18));
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);

        // 검색 및 페이지 패널
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // 페이지 네비게이션 패널
        JPanel pagePanel = new JPanel();
        JButton prevButton = new JButton("◀");
        prevButton.setFont(buttonFont);
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 0) {
                    currentPage--;
                    updateTable();
                }
            }
        });
        pagePanel.add(prevButton);

        semesterComboBox = new JComboBox<>();
        semesterComboBox.setFont(buttonFont);
        semesterComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentPage = semesterComboBox.getSelectedIndex();
                updateTable();
            }
        });
        pagePanel.add(semesterComboBox);

        JButton nextButton = new JButton("▶");
        nextButton.setFont(buttonFont);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage < semesters.size() - 1) {
                    currentPage++;
                    updateTable();
                }
            }
        });
        pagePanel.add(nextButton);

        bottomPanel.add(pagePanel, BorderLayout.NORTH);

        // 검색 패널
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(new JLabel("검색:", SwingConstants.CENTER), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("돋움", Font.PLAIN, 16));
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("검색");
        searchButton.setFont(buttonFont);
        searchButton.addActionListener(new SearchButtonListener(searchField));
        searchPanel.add(searchButton, BorderLayout.EAST);
        bottomPanel.add(searchPanel, BorderLayout.SOUTH);

        // 검색 및 페이지 패널을 아래에 추가
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 테이블 업데이트 메소드
    private void updateTable() {
        String selectedSemester = semesters.get(currentPage);
        semesterComboBox.setSelectedIndex(currentPage);

        if (selectedSemester == null)
            return;

        ArrayList<String[]> grades = semesterGrades.get(selectedSemester);
        if (grades == null)
            return;

        tableModel.setRowCount(0); // 테이블 초기화

        for (String[] grade : grades) {
            tableModel.addRow(grade);
        }
    }

    // 성적 추가 버튼 리스너
    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            GradeInputDialog dialog = new GradeInputDialog(UniversityGradeManager.this, "성적 추가", true, "추가");
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                String subject = dialog.getSubject();
                String semester = dialog.getSemester();
                String classSection = dialog.getClassSection();
                String grade = dialog.getGrade();
                String credits = dialog.getCredits();

                if (!semesterGrades.containsKey(semester)) {
                    semesterGrades.put(semester, new ArrayList<>());
                    semesters.add(semester);
                    semesterComboBox.addItem(semester);
                }

                semesterGrades.get(semester).add(new String[] { subject, semester, classSection, grade, credits });
                updateTable();
            }
        }
    }

    // 성적 수정 버튼 리스너
    private class UpdateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                String selectedSemester = semesters.get(currentPage);
                ArrayList<String[]> grades = semesterGrades.get(selectedSemester);
                if (grades == null)
                    return;

                String subject = grades.get(selectedRow)[0];
                String semester = grades.get(selectedRow)[1];
                String classSection = grades.get(selectedRow)[2];
                String grade = grades.get(selectedRow)[3];
                String credits = grades.get(selectedRow)[4];

                GradeInputDialog dialog = new GradeInputDialog(UniversityGradeManager.this, "성적 수정", true, "수정",
                        subject, semester, classSection, grade, credits);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    int confirm = JOptionPane.showConfirmDialog(UniversityGradeManager.this,
                            "정말로 수정하시겠습니까?",
                            "수정 확인", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        subject = dialog.getSubject();
                        semester = dialog.getSemester();
                        classSection = dialog.getClassSection();
                        grade = dialog.getGrade();
                        credits = dialog.getCredits();

                        grades.set(selectedRow, new String[] { subject, semester, classSection, grade, credits });
                        updateTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(UniversityGradeManager.this, "수정할 성적을 선택하세요.", "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 성적 삭제 버튼 리스너
    private class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                String selectedSemester = semesters.get(currentPage);
                ArrayList<String[]> grades = semesterGrades.get(selectedSemester);
                if (grades == null)
                    return;

                int confirm = JOptionPane.showConfirmDialog(UniversityGradeManager.this,
                        "정말로 삭제하시겠습니까?",
                        "삭제 확인", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    grades.remove(selectedRow);
                    updateTable();
                }
            } else {
                JOptionPane.showMessageDialog(UniversityGradeManager.this, "삭제할 성적을 선택하세요.", "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 파일 열기 버튼 리스너
    private class OpenFileButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(UniversityGradeManager.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getName().toLowerCase();
                if (fileName.endsWith(".txt")) {
                    loadGradesFromTextFile(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(UniversityGradeManager.this, "지원하지 않는 파일 형식입니다.", "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // 텍스트 파일에서 성적을 불러오는 메소드
    private void loadGradesFromTextFile(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            semesterGrades.clear();
            semesters.clear();
            tableModel.setRowCount(0);
            semesterComboBox.removeAllItems();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+"); // 공백 기준으로 분리
                if (parts.length >= 5) {
                    String subject = parts[0];
                    String semester = parts[1];
                    String classSection = parts[2];
                    String grade = parts[3];
                    String credits = parts[4];

                    if (!semesterGrades.containsKey(semester)) {
                        semesterGrades.put(semester, new ArrayList<>());
                        semesters.add(semester);
                        semesterComboBox.addItem(semester);
                    }

                    semesterGrades.get(semester).add(new String[] { subject, semester, classSection, grade, credits });
                }
            }
            updateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 검색 버튼 리스너
    private class SearchButtonListener implements ActionListener {
        private JTextField searchField;

        public SearchButtonListener(JTextField searchField) {
            this.searchField = searchField;
        }

        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText().toLowerCase();
            DefaultTableModel searchModel = new DefaultTableModel(new String[] { "교과목명", "년도/학기", "이수구분", "등급", "학점" },
                    0);
            for (String semester : semesters) {
                ArrayList<String[]> grades = semesterGrades.get(semester);
                for (int i = 0; i < grades.size(); i++) {
                    String subject = grades.get(i)[0].toLowerCase();
                    String sem = grades.get(i)[1].toLowerCase();
                    String classSection = grades.get(i)[2].toLowerCase();
                    String grade = grades.get(i)[3].toLowerCase();
                    String credits = grades.get(i)[4].toLowerCase();
                    if (subject.contains(searchText) || sem.contains(searchText) || classSection.contains(searchText)
                            || grade.contains(searchText) || credits.contains(searchText)) {
                        searchModel.addRow(grades.get(i));
                    }
                }
            }
            JTable searchTable = new JTable(searchModel);
            searchTable.setFont(new Font("돋움", Font.PLAIN, 18));
            searchTable.setRowHeight(30);
            JTableHeader searchTableHeader = searchTable.getTableHeader();
            searchTableHeader.setFont(new Font("돋움", Font.PLAIN, 18));
            ((DefaultTableCellRenderer) searchTableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
            JOptionPane.showMessageDialog(null, new JScrollPane(searchTable), "검색 결과", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UniversityGradeManager().setVisible(true);
            }
        });
    }
}

class GradeInputDialog extends JDialog {
    private JTextField subjectField, classSectionField, gradeField, creditsField, semesterField;
    private boolean confirmed = false;
    private JButton confirmButton;

    public GradeInputDialog(JFrame parent, String title, boolean modal, String buttonText) {
        super(parent, title, modal);
        setupUI(buttonText);
    }

    public GradeInputDialog(JFrame parent, String title, boolean modal, String buttonText, String subject,
            String semester, String classSection, String grade, String credits) {
        super(parent, title, modal);
        setupUI(buttonText);
        subjectField.setText(subject);
        semesterField.setText(semester);
        classSectionField.setText(classSection);
        gradeField.setText(grade);
        creditsField.setText(credits);
    }

    private void setupUI(String buttonText) {
        setLayout(new GridLayout(8, 2, 5, 5));

        JLabel subjectLabel = new JLabel("교과목명:");
        subjectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subjectLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        add(subjectLabel);
        subjectField = new JTextField();
        subjectField.setFont(new Font("돋움", Font.PLAIN, 14));
        add(subjectField);

        JLabel semesterLabel = new JLabel("년도/학기:");
        semesterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        semesterLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        add(semesterLabel);
        semesterField = new JTextField();
        semesterField.setFont(new Font("돋움", Font.PLAIN, 14));
        add(semesterField);

        JLabel classSectionLabel = new JLabel("이수구분:");
        classSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        classSectionLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        add(classSectionLabel);
        classSectionField = new JTextField();
        classSectionField.setFont(new Font("돋움", Font.PLAIN, 14));
        add(classSectionField);

        JLabel gradeLabel = new JLabel("등급:");
        gradeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gradeLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        add(gradeLabel);
        gradeField = new JTextField();
        gradeField.setFont(new Font("돋움", Font.PLAIN, 14));
        add(gradeField);

        JLabel creditsLabel = new JLabel("학점:");
        creditsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        creditsLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        add(creditsLabel);
        creditsField = new JTextField();
        creditsField.setFont(new Font("돋움", Font.PLAIN, 14));
        add(creditsField);

        confirmButton = new JButton(buttonText);
        confirmButton.setFont(new Font("돋움", Font.PLAIN, 14));
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                setVisible(false);
            }
        });
        add(confirmButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.setFont(new Font("돋움", Font.PLAIN, 14));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                setVisible(false);
            }
        });
        add(cancelButton);

        pack();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSubject() {
        return subjectField.getText();
    }

    public String getSemester() {
        return semesterField.getText();
    }

    public String getClassSection() {
        return classSectionField.getText();
    }

    public String getGrade() {
        return gradeField.getText();
    }

    public String getCredits() {
        return creditsField.getText();
    }
}
