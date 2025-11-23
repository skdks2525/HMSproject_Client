package client.view;

import client.net.NetworkService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MyReservationPanel extends JPanel {

    private JPanel listPanel;
    private JTextField txtSearchName;
    private String loggedInName;
    
    public MyReservationPanel(String userName) {
        this.loggedInName = userName;
        initComponents();
        
        // ID 자동 조회
        if (loggedInName != null && !loggedInName.isEmpty()) {
            txtSearchName.setText(loggedInName);
            loadMyReservations();
        }
    }
    
    public MyReservationPanel() {
        this("");
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 검색 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("내 예약 확인");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        
        txtSearchName = new JTextField(10);
        JButton btnSearch = new JButton("조회");
        btnSearch.setBackground(new Color(0, 0, 0)); // 검정색으로 했음
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> loadMyReservations());
        
        topPanel.add(lblTitle);
        topPanel.add(new JLabel("   예약자명(ID):"));
        topPanel.add(txtSearchName);
        topPanel.add(btnSearch);
        
        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 패널
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(245, 245, 245));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // 예약 목록 기능
    private void loadMyReservations() {
        String name = txtSearchName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약자 이름을 입력해주세요.");
            return;
        }

        // 기존 목록 지우기
        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();

        // 서버에 리스트 요청
        String response = NetworkService.getInstance().sendRequest("GET_RES_BY_NAME:" + name);

        if (response != null && response.startsWith("RES_LIST:")) {
            String data = response.substring("RES_LIST:".length());
            
            if (!data.isEmpty()) {
                String[] list = data.split("/");
                for (String item : list) {
 
                    String[] info = item.split(","); 
                    
                    if (info.length >= 8) { 
                        JPanel card = createReservationCard(
                            info[0].trim(), info[1].trim(), info[2].trim(), info[3].trim(),
                            info[4].trim(), info[5].trim(), info[6].trim(), info[7].trim());
                        listPanel.add(card);
                        listPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 간격 추가
                    }
                }
                // 화면 갱신
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

    // 예약 카드 UI 생성
    private JPanel createReservationCard(String resId, String roomNum, String name, String inDate, String outDate, String guests, String phone, String payment) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1000, 130)); 
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblIcon = new JLabel(roomNum + "호", SwingConstants.CENTER);
        lblIcon.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        lblIcon.setPreferredSize(new Dimension(120, 80));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(230, 240, 255));
        lblIcon.setForeground(new Color(0, 51, 102));
        card.add(lblIcon, BorderLayout.WEST);
        
        // 예약 상세 정보
        JPanel infoP = new JPanel(new GridLayout(3, 1));
        infoP.setOpaque(false);
        
        // 결제 상태에 따라 색이 변함

        JLabel lblName = new JLabel();
        lblName.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        if ("Unpaid".equals(payment)) {
            // 미결제 상태 -> 빨간색 경고
            lblName.setText(name + "님 예약 (미결제 - 18시 마감)");
            lblName.setForeground(Color.RED);
        } else {
            // 결제 완료 상태 -> 초록색 확정 문구
            lblName.setText(name + "님 예약 (예약 확정)");
            lblName.setForeground(new Color(0, 153, 51)); // 진한 초록
        }
        
        JLabel lblDate = new JLabel(inDate + " ~ " + outDate);
        lblDate.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblDate.setForeground(new Color(0, 102, 0)); 

        JLabel lblPhone = new JLabel(phone + " (" + guests + "명) | ID: " + resId);
        lblPhone.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        lblPhone.setForeground(Color.GRAY);

        infoP.add(lblName);
        infoP.add(lblDate);
        infoP.add(lblPhone);
        card.add(infoP, BorderLayout.CENTER);

        // 결제 & 취소 패널
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        btnPanel.setOpaque(false);

        // 결제 버튼
        if ("Unpaid".equals(payment)) {
            JButton btnPay = new JButton("결제수단 등록");
            btnPay.setBackground(new Color(100, 150, 255)); // 파란색
            btnPay.setForeground(Color.WHITE);
            btnPay.setFocusPainted(false);
            btnPay.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            btnPay.addActionListener(e -> requestPayment(resId));
            btnPanel.add(btnPay);
        } else {
            // 결제 완료 시 '완료' 표시
            JLabel lblDone = new JLabel("결제 완료", SwingConstants.CENTER);
            lblDone.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lblDone.setForeground(new Color(0, 153, 51));
            btnPanel.add(lblDone);
        }

        // 취소 버튼
        JButton btnCancel = new JButton("예약 취소");
        btnCancel.setBackground(new Color(255, 235, 235)); // 연한 빨강
        btnCancel.setForeground(Color.RED);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnCancel.addActionListener(e -> requestCancel(resId));
        btnPanel.add(btnCancel);

        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    // 결제 요청 기능
    private void requestPayment(String resId) {
        String cardInfo = JOptionPane.showInputDialog(this, 
                "신용카드 번호를 입력해주세요 (예: 0000-0000-0000-0000)", 
                "결제수단 등록", JOptionPane.QUESTION_MESSAGE);
        
        if (cardInfo != null && !cardInfo.trim().isEmpty()) {
            // 서버 프로토콜: UPDATE_PAYMENT:예약ID:결제정보
            String request = "UPDATE_PAYMENT:" + resId + ":" + cardInfo;
            String response = NetworkService.getInstance().sendRequest(request);
            
            if ("PAYMENT_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "결제 정보가 등록되었습니다!");
                loadMyReservations(); // 화면 새로고침 (상태 변경 확인)
            } else {
                JOptionPane.showMessageDialog(this, "등록 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 예약 취소 기능
    private void requestCancel(String resId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "예약(ID: " + resId + ")을 정말 취소하시겠습니까?", "취소 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String response = NetworkService.getInstance().sendRequest("DELETE_RESERVATION:" + resId);
            if (response != null && response.startsWith("DELETE_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "취소되었습니다.");
                loadMyReservations(); // 화면 새로고침
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}