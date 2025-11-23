package client.view;

import client.net.NetworkService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MyReservationPanel extends JPanel {

    private JPanel listPanel;
    private JTextField txtSearchName;
    private String loggedInName; //로그인한 사용자 이름

    public MyReservationPanel(String userName) {
        this.loggedInName = userName;
        initComponents();
        
        // 패널이 만들어지면 사용자를 조회
        loadMyReservations(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 검색 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("내 예약 확인");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        
        // 검색창에 로그인한 이름을 미리 채워둠
        txtSearchName = new JTextField(loggedInName, 10);
        
        // 다른사람 예약내역 조회 못하게 함
        txtSearchName.setEditable(false); 

        JButton btnSearch = new JButton("조회");
        btnSearch.setBackground(new Color(0, 0, 0));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> loadMyReservations());
        
        topPanel.add(lblTitle);
        topPanel.add(new JLabel("   예약자명:"));
        topPanel.add(txtSearchName);
        topPanel.add(btnSearch);
        
        add(topPanel, BorderLayout.NORTH);

        // 리스트 패널
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(245, 245, 245));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- 기능 구현 ---

    private void loadMyReservations() {
        // 검색창 이름으로 조회
        String name = txtSearchName.getText().trim();
        if (name.isEmpty()) return;

        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();

        // 서버 요청
        String response = NetworkService.getInstance().sendRequest("GET_RES_BY_NAME:" + name);

        if (response != null && response.startsWith("RES_LIST:")) {
            String data = response.substring("RES_LIST:".length());
            
            if (!data.isEmpty()) {
                String[] list = data.split("/");
                for (String item : list) {
                    String[] info = item.split(","); 
                    if (info.length >= 5) {
                        // 카드 추가
                        JPanel card = createReservationCard(info[0], info[1], info[2], info[3], info[4]);
                        listPanel.add(card);
                        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
                listPanel.revalidate();
                listPanel.repaint();
            } else {
                showEmptyMessage("예약 내역이 없습니다.");
            }
        } else {
            showEmptyMessage("조회 실패: " + response);
        }
    }
    
    private void showEmptyMessage(String msg) {
        JLabel lblMsg = new JLabel(msg, SwingConstants.CENTER);
        lblMsg.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        lblMsg.setForeground(Color.GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        listPanel.add(Box.createVerticalGlue()); 
        listPanel.add(lblMsg);
        listPanel.add(Box.createVerticalGlue()); 
        listPanel.revalidate();
        listPanel.repaint();
    }

private JPanel createReservationCard(String resId, String roomNum, String name, String inDate, String outDate) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1000, 100)); 
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblIcon = new JLabel(roomNum + "호", SwingConstants.CENTER);
        lblIcon.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        lblIcon.setPreferredSize(new Dimension(100, 60));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(230, 240, 255)); 
        lblIcon.setForeground(new Color(0, 51, 102));    
        card.add(lblIcon, BorderLayout.WEST);

        JPanel infoP = new JPanel(new GridLayout(2, 1));
        infoP.setOpaque(false);
        
        JLabel lblName = new JLabel(name + "님 예약 (ID: " + resId + ")");
        lblName.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        
        JLabel lblDate = new JLabel("📅 일정: " + inDate + " ~ " + outDate);
        lblDate.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblDate.setForeground(Color.DARK_GRAY);

        infoP.add(lblName);
        infoP.add(lblDate);
        card.add(infoP, BorderLayout.CENTER);

        JButton btnCancel = new JButton("예약 취소");
        btnCancel.setBackground(new Color(255, 235, 235)); 
        btnCancel.setForeground(Color.RED);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        
        btnCancel.addActionListener(e -> requestCancel(resId));
        
        card.add(btnCancel, BorderLayout.EAST);

        return card;
    }

    private void requestCancel(String resId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "예약(ID: " + resId + ")을 정말 취소하시겠습니까?", "취소 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String response = NetworkService.getInstance().sendRequest("DELETE_RESERVATION:" + resId);
            if (response != null && response.startsWith("DELETE_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "취소되었습니다.");
                loadMyReservations(); 
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}