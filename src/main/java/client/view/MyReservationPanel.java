package client.view;

import client.net.NetworkService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MyReservationPanel extends JPanel {

    private JPanel listPanel;
    private JTextField txtSearchName;
    private String loggedInName;
    
    public MyReservationPanel(String userName) {
        this.loggedInName = userName;
        initComponents();
        
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

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("내 예약 확인");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        
        txtSearchName = new JTextField(10);
        JButton btnSearch = new JButton("조회");
        btnSearch.setBackground(new Color(0, 0, 0));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> loadMyReservations());
        
        topPanel.add(lblTitle);
        topPanel.add(new JLabel("   예약자명(ID):"));
        topPanel.add(txtSearchName);
        topPanel.add(btnSearch);
        
        add(topPanel, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(245, 245, 245));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // 예약 목록 조회
    private void loadMyReservations() {
        String name = txtSearchName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약자 이름을 입력해주세요.");
            return;
        }

        listPanel.removeAll();
        listPanel.revalidate();
        listPanel.repaint();

        String response = NetworkService.getInstance().sendRequest("GET_RES_BY_NAME:" + name);

        if (response != null && response.startsWith("RES_LIST:")) {
            String data = response.substring("RES_LIST:".length());
            
            if (!data.isEmpty()) {
                String[] list = data.split("\\|");
                for (String item : list) {
                    if (item.trim().isEmpty()) continue;
                    String[] info = item.split(","); 
                    
                    if (info.length >= 10) { 
                        String statusVal = info[7].trim();
                        
                        int price = 0;
                        int capacity = 2; // 기본 정원수를 2로 지정
                        
                        if (info.length >= 13) {
                            try { 
                                price = Integer.parseInt(info[11].trim()); 
                                capacity = Integer.parseInt(info[12].trim()); 
                            } catch(Exception e) {}
                        }

                        JPanel card = createReservationCard(
                            info[0].trim(), // resId
                            info[1].trim(), // roomNum
                            info[2].trim(), // name
                            info[3].trim(), // in
                            info[4].trim(), // out
                            info[5].trim(), // guests
                            info[6].trim(), // phone
                            statusVal,      
                            price,          // 가격
                            capacity        // 정원수
                        );
                        
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

    private JPanel createReservationCard(String resId, String roomNum, String name, String inDate, String outDate, String guests, String phone, String status, int pricePerNight, int capacity) {
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
        
        JPanel infoP = new JPanel(new GridLayout(3, 1));
        infoP.setOpaque(false);
        
        JLabel lblName = new JLabel();
        lblName.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        
        if ("Unpaid".equalsIgnoreCase(status)) {
            lblName.setText(name + "님 예약 (미결제 - 결제 필요)");
            lblName.setForeground(Color.RED);
        } else if ("Confirmed".equalsIgnoreCase(status)) {
            lblName.setText(name + "님 예약 (예약 확정)");
            lblName.setForeground(new Color(0, 153, 51));
        } else if ("CheckedIn".equalsIgnoreCase(status)) {
            lblName.setText(name + "님 (투숙 중)");
            lblName.setForeground(Color.BLUE);
        } else if ("CheckedOut".equalsIgnoreCase(status)) {
            lblName.setText(name + "님 (체크아웃)");
            lblName.setForeground(Color.GRAY);
        } else {
            lblName.setText(name + "님 예약 (" + status + ")");
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

        // 결제 & 취소 버튼 패널
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        btnPanel.setOpaque(false);

        // Unpaid일 때만 결제 버튼 보임
        if ("Unpaid".equalsIgnoreCase(status)) {
            JButton btnPay = new JButton("결제수단 등록");
            btnPay.setBackground(new Color(100, 150, 255));
            btnPay.setForeground(Color.WHITE);
            btnPay.setFocusPainted(false);
            btnPay.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            
            // 클릭 시 requestPayment 호출 (가격 정보 전달)
            btnPay.addActionListener(e -> requestPayment(resId, inDate, outDate, guests, pricePerNight, capacity));
            btnPanel.add(btnPay);
        } else {
            JLabel lblDone = new JLabel("등록 완료", SwingConstants.CENTER);
            lblDone.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lblDone.setForeground(new Color(0, 153, 51));
            btnPanel.add(lblDone);
        }

        JButton btnCancel = new JButton("예약 취소");
        btnCancel.setBackground(new Color(255, 235, 235));
        btnCancel.setForeground(Color.RED);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnCancel.addActionListener(e -> requestCancel(resId));
        btnPanel.add(btnCancel);

        card.add(btnPanel, BorderLayout.EAST);
        return card;
    }

    // 결제 요청: 카드 정보 입력 -> 금액 포함하여 서버 전송
    private void requestPayment(String resId, String inDate, String outDate, String guestsStr, int pricePerNight, int capacity) {
        
        // 결제 금액 계산 (박수 * 1박요금)
        long totalAmount = 0;
        try {
            // 날짜 차이(연박) 계산
            LocalDate d1 = LocalDate.parse(inDate);
            LocalDate d2 = LocalDate.parse(outDate);
            long nights = ChronoUnit.DAYS.between(d1, d2);
            if (nights < 1) nights = 1;

            // 인원 수 파싱
            int currentGuests = Integer.parseInt(guestsStr);
            
            // 초과 인원 비용 계산
            int extraCost = 0;
            if (currentGuests > capacity) {
                // 1인당 20,000원 * 초과인원
                extraCost = (currentGuests - capacity) * 20000;
            }

            // 총액 계산: (기본박비 + 초과비용) * 연박일수
            totalAmount = (pricePerNight + extraCost) * nights;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "비용 계산 중 오류 발생: " + e.getMessage());
            return;
        }

        // 결제 수단 선택 팝업
        Object[] options = {"신용카드", "계좌이체", "무통장입금"};
        int choice = JOptionPane.showOptionDialog(this,
                "결제 수단을 선택해주세요.\n결제 금액: " + String.format("%,d원", totalAmount), // 금액 안내
                "결제 수단 선택",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == -1) return;
        String method = (String) options[choice];
        
        String cardNum = "0", cvc = "0", expiry = "0", pw = "0";

        if ("신용카드".equals(method)) {
            // 신용카드 입력UI
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField txtNum = new JTextField();
            JTextField txtCVC = new JTextField();
            JTextField txtExpiry = new JTextField("MM/YY");
            JPasswordField txtPw = new JPasswordField();

            panel.add(new JLabel("카드 번호:")); panel.add(txtNum);
            panel.add(new JLabel("CVC (3자리):")); panel.add(txtCVC);
            panel.add(new JLabel("유효기간:")); panel.add(txtExpiry);
            panel.add(new JLabel("비번 (앞2자리):")); panel.add(txtPw);

            int result = JOptionPane.showConfirmDialog(this, panel, "신용카드 정보 입력", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            cardNum = txtNum.getText().trim();
            cvc = txtCVC.getText().trim();
            expiry = txtExpiry.getText().trim();
            pw = new String(txtPw.getPassword()).trim();
            
            if (cardNum.isEmpty() || cvc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "필수 정보를 입력해주세요.");
                return;
            }
        } else {
            // 계좌이체 입력UI
            String sender = JOptionPane.showInputDialog(this, "입금자명을 입력해주세요:", method, JOptionPane.QUESTION_MESSAGE);
            if (sender == null || sender.trim().isEmpty()) return;
            cardNum = sender; 
        }

        // 예약자에게 입력받은 결제정보를 서버에 전송함(전체금액도 같이 보냄)
        String request = String.format("UPDATE_PAYMENT:%s:%s:%s:%s:%s:%s:%d", 
                resId, method, cardNum, cvc, expiry, pw, totalAmount);
        
        System.out.println("[Client] 결제 요청 전송: " + request);
        String response = NetworkService.getInstance().sendRequest(request);
        
        if (response != null && response.startsWith("PAYMENT_SUCCESS")) { // 서버 응답 메시지에 맞춤
            JOptionPane.showMessageDialog(this, "결제가 완료되었습니다!\n(" + method + ": " + totalAmount + "원)");
            loadMyReservations();
        } else {
            JOptionPane.showMessageDialog(this, "결제 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
        }
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