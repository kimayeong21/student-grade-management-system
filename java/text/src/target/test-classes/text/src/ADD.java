import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UniversityGradePlanner extends JFrame {
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> semesterComboBox;
    private int currentPage;

    public UniversityGradePlanner() {
        currentPage = 0;

        // 로그인 화면 설정
        showLoginScreen();
    }

    private void showLoginScreen() {
        setTitle("개인 성적 플래너 - 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("개인 성적 플래너", SwingConstants.CENTER);
        titleLabel.setFont(new Font("돋움", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JLabel idLabel = new JLabel("아이디:", SwingConstants.RIGHT);
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("비밀번호:", SwingConstants.RIGHT);
        JPasswordField passwordField = new JPasswordField();

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(id, password)) {
                    showMainScreen();
                } else {
                    JOptionPane.showMessageDialog(UniversityGradePlanner.this, "아이디 또는 비밀번호가 올바르지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 회원가입 버튼
        JButton signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSignUpDialog();
            }
        });

        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(signUpButton);
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);
    }

    private boolean authenticate(String id, String password) {
        // 여기에서 아이디와 비밀번호를 확인하는 로직을 추가합니다.
        // 예시로 "admin"과 "password"로 인증합니다.
        return "admin".equals(id) && "password".equals(password);
    }

    private void showMainScreen() {
        getContentPane().removeAll();
        setTitle("개인 성적 플래너");
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("개인 성적 플래너", SwingConstants.CENTER);
        titleLabel.setFont(new Font("돋움", Font.PLAIN, 22));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 왼쪽 패널 - 버튼
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        Font buttonFont = new Font("돋움", Font.PLAIN, 16);
        LineBorder buttonBorder = new LineBorder(Color.BLACK, 1, true);

        JButton addButton = new JButton("추가");
        addButton.setFont(buttonFont);
        addButton.setBorder(buttonBorder);
        addButton.setPreferredSize(new Dimension(100, 50));
        addButton.addActionListener(new AddButtonListener());
        panel.add(addButton);

        JButton updateButton = new JButton("수정");
        updateButton.setFont(buttonFont);
        updateButton.setBorder(buttonBorder);
        updateButton.setPreferredSize(new Dimension(100, 50));
        updateButton.addActionListener(new UpdateButtonListener());
        panel.add(updateButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.setFont(buttonFont);
        deleteButton.setBorder(buttonBorder);
        deleteButton.setPreferredSize(new Dimension(100, 50));
        deleteButton.addActionListener(new DeleteButtonListener());
        panel.add(deleteButton);
        add(panel, BorderLayout.WEST);

        // 성적 테이블
        String[] columnNames = {"교과목명", "년도/학기", "이수구분", "등급", "학점"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(tableModel);

        Font tableFont = new Font("돋움", Font.PLAIN, 18);
        gradeTable.setFont(tableFont);
        gradeTable.setRowHeight(30);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < gradeTable.getColumnCount(); i++) {
            gradeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader tableHeader = gradeTable.getTableHeader();
        tableHeader.setFont(new Font("돋움", Font.PLAIN, 18));
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 패널 - 페이지 네비게이션 및 검색
        JPanel bottomPanel = new JPanel(new BorderLayout());
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
                if (currentPage < semesterComboBox.getItemCount() - 1) {
                    currentPage++;
                    updateTable();
                }
            }
        });
        pagePanel.add(nextButton);

        bottomPanel.add(pagePanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("검색:", SwingConstants.CENTER), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("돋움", Font.PLAIN, 16));
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("검색");
        searchButton.setFont(buttonFont);
        searchButton.addActionListener(new SearchButtonListener(searchField));
        searchPanel.add(searchButton, BorderLayout.EAST);
        bottomPanel.add(searchPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // 프로그램 시작 시 데이터베이스에서 성적 불러오기
        loadGradesFromDatabase();
        revalidate();
        repaint();
    }

    private void showSignUpDialog() {
        JDialog signUpDialog = new JDialog(this, "회원가입", true);
        signUpDialog.setSize(300, 200);
        signUpDialog.setLayout(new GridLayout(4, 2, 10, 10));
        signUpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel idLabel = new JLabel("아이디:");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton registerButton = new JButton("가입");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.equals(confirmPassword)) {
                    if (registerUser(id, password)) {
                        JOptionPane.showMessageDialog(signUpDialog, "회원가입이 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                        signUpDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(signUpDialog, "회원가입에 실패했습니다.", "실패", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(signUpDialog, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signUpDialog.add(idLabel);
        signUpDialog.add(idField);
        signUpDialog.add(passwordLabel);
        signUpDialog.add(passwordField);
        signUpDialog.add(confirmPasswordLabel);
        signUpDialog.add(confirmPasswordField);
        signUpDialog.add(new JLabel());
        signUpDialog.add(registerButton);

        signUpDialog.setVisible(true);
    }

    private boolean registerUser(String id, String password) {
        // 여기에서 사용자 등록 로직을 추가합니다.
        // 예시로 등록에 성공한 것으로 가정합니다.
        // 실제로는 데이터베이스에 저장하는 코드가 필요합니다.
        return true;
    }

    private void loadGradesFromDatabase() {
        tableModel.setRowCount(0);
        try (Connection connection = DatabaseConnector.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM grades");
            while (resultSet.next()) {
                String subject = resultSet.getString("subject");
                String semester = resultSet.getString("semester");
                String classSection = resultSet.getString("class_section");
                String grade = resultSet.getString("grade");
                String credits = resultSet.getString("credits");
                tableModel.addRow(new Object[]{subject, semester, classSection, grade, credits});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedGradeId(int selectedRow) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT id FROM grades LIMIT ?, 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedRow);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void updateTable() {
        loadGradesFromDatabase();
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            GradeInputDialog dialog = new GradeInputDialog(UniversityGradePlanner.this, "성적 추가", true, "추가");
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                DatabaseConnector.addGrade(
                        dialog.getSubject(),
                        dialog.getSemester(),
                        dialog.getClassSection(),
                        dialog.getGrade(),
                        dialog.getCredits()
                );
                loadGradesFromDatabase();
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                String subject = tableModel.getValueAt(selectedRow, 0).toString();
                String semester = tableModel.getValueAt(selectedRow, 1).toString();
                String classSection = tableModel.getValueAt(selectedRow, 2).toString();
                String grade = tableModel.getValueAt(selectedRow, 3).toString();
                String credits = tableModel.getValueAt(selectedRow, 4).toString();

                GradeInputDialog dialog = new GradeInputDialog(UniversityGradePlanner.this, "성적 수정", true, "수정", subject, semester, classSection, grade, credits);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    int confirm = JOptionPane.showConfirmDialog(UniversityGradePlanner.this,
                            "정말로 수정하시겠습니까?",
                            "수정 확인", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        int gradeId = getSelectedGradeId(selectedRow);
                        DatabaseConnector.updateGrade(gradeId, dialog.getSubject(), dialog.getSemester(), dialog.getClassSection(), dialog.getGrade(), dialog.getCredits());
                        loadGradesFromDatabase();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(UniversityGradePlanner.this, "수정할 성적을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(UniversityGradePlanner.this,
                        "정말로 삭제하시겠습니까?",
                        "삭제 확인", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int gradeId = getSelectedGradeId(selectedRow);
                    DatabaseConnector.deleteGrade(gradeId);
                    loadGradesFromDatabase();
                }
            } else {
                JOptionPane.showMessageDialog(UniversityGradePlanner.this, "삭제할 성적을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        private JTextField searchField;

        public SearchButtonListener(JTextField searchField) {
            this.searchField = searchField;
        }

        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText().toLowerCase();
            tableModel.setRowCount(0);
            try (Connection connection = DatabaseConnector.getConnection()) {
                String query = "SELECT * FROM grades WHERE LOWER(subject) LIKE ? OR LOWER(semester) LIKE ? OR LOWER(class_section) LIKE ? OR LOWER(grade) LIKE ? OR LOWER(credits) LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                String searchPattern = "%" + searchText + "%";
                preparedStatement.setString(1, searchPattern);
                preparedStatement.setString(2, searchPattern);
                preparedStatement.setString(3, searchPattern);
                preparedStatement.setString(4, searchPattern);
                preparedStatement.setString(5, searchPattern);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String subject = resultSet.getString("subject");
                    String semester = resultSet.getString("semester");
                    String classSection = resultSet.getString("class_section");
                    String grade = resultSet.getString("grade");
                    String credits = resultSet.getString("credits");
                    tableModel.addRow(new Object[]{subject, semester, classSection, grade, credits});
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UniversityGradePlanner().setVisible(true));
    }
}

class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/university_grades";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addGrade(String subject, String semester, String classSection, String grade, String credits) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO grades (subject, semester, class_section, grade, credits) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, subject);
            preparedStatement.setString(2, semester);
            preparedStatement.setString(3, classSection);
            preparedStatement.setString(4, grade);
            preparedStatement.setString(5, credits);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateGrade(int id, String subject, String semester, String classSection, String grade, String credits) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE grades SET subject = ?, semester = ?, class_section = ?, grade = ?, credits = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, subject);
            preparedStatement.setString(2, semester);
            preparedStatement.setString(3, classSection);
            preparedStatement.setString(4, grade);
            preparedStatement.setString(5, credits);
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGrade(int id) {
        try (Connection connection = getConnection()) {
            String query = "DELETE FROM grades WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class GradeInputDialog extends JDialog {
    private JTextField subjectField, classSectionField, gradeField, creditsField, semesterField;
    private boolean confirmed = false;

    public GradeInputDialog(JFrame parent, String title, boolean modal, String buttonText) {
        super(parent, title, modal);
        setupUI(buttonText);
    }

    public GradeInputDialog(JFrame parent, String title, boolean modal, String buttonText, String subject, String semester, String classSection, String grade, String credits) {
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

        JButton confirmButton = new JButton(buttonText);
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
