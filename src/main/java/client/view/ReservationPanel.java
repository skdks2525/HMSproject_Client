package client.view;
import client.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 *
 * @author user
 */
public class ReservationPanel extends JPanel{
    private JTextField txtCheckIn, txtCheckOut, txtName, txtPhone, txtRequest;
    private JSpinner spinGuests;
    private JComboBox<String> comboPayment;
    private ButtonGroup roomGroup;
    
    public ReservationPanel(){
        initComponents();
    }
    
    private void initComponents(){
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);     
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조절
        scrollPane.setBorder(null); // 테두리 제거
        add(scrollPane, BorderLayout.CENTER);
        
        //타이틀
        JLabel lblTitle = new JLabel("객실 조회");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 정보 입력 패널
        mainPanel.add(createInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 객실 선택
        mainPanel.add(createRoomSelectionPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 예약 버튼
        JButton btnReserve = new JButton("객실 예약");
        btnReserve.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnReserve.setBackground(new Color(0, 102, 51));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReserve.setMaximumSize(new Dimension(200, 50));
        btnReserve.addActionListener(this::handleReserveAction);
        
        mainPanel.add(btnReserve);
    }
    
    private JPanel createRoomSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("객실 선택"));

        roomGroup = new ButtonGroup(); 

        // 카드 1
        JPanel card1 = createRoomCard("일반 객실", "220,000원", 
                "무료 Wi-Fi, 에어컨, TV", "STD");
        panel.add(card1);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 카드 2
        JPanel card2 = createRoomCard("고급 객실", "440,000원", 
                "무료 Wi-Fi, 미니바, 조식 포함", "DLX");
        panel.add(card2);
        
         //카드3
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        JPanel card3 = createRoomCard("초고급 객실", "1,000,000원",
                "아무튼 다 무료", "STE");
        panel.add(card3);

        return panel;
    }

    private JPanel createRoomCard(String title, String price, String options, String actionCommand) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1000, 120)); // 카드 높이 고정

        // 왼쪽: 아이콘/이미지 대신 간단한 텍스트 박스
        JLabel lblIcon = new JLabel("ROOM", SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(100, 80));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(Color.GRAY);
        lblIcon.setForeground(Color.WHITE);
        card.add(lblIcon, BorderLayout.WEST);

        // 중앙: 정보
        JPanel infoP = new JPanel(new GridLayout(3, 1));
        infoP.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        
        JLabel lblPrice = new JLabel(price + " / 1박");
        lblPrice.setForeground(new Color(255, 102, 0));

        JLabel lblOpt = new JLabel(options);
        lblOpt.setForeground(Color.DARK_GRAY);

        infoP.add(lblTitle);
        infoP.add(lblPrice);
        infoP.add(lblOpt);
        
        card.add(infoP, BorderLayout.CENTER);

        // 오른쪽: 선택 버튼
        JRadioButton rbtn = new JRadioButton("선택");
        rbtn.setOpaque(false);
        rbtn.setActionCommand(actionCommand);
        roomGroup.add(rbtn);
        
        JPanel btnP = new JPanel(new GridBagLayout());
        btnP.setOpaque(false);
        btnP.add(rbtn);
        
        card.add(btnP, BorderLayout.EAST);

        return card;
    }

    // --- [예약 로직] ---
    private void handleReserveAction(ActionEvent e) {
        ButtonModel selectedModel = roomGroup.getSelection();
        if (selectedModel == null) {
            JOptionPane.showMessageDialog(this, "객실을 선택해주세요.");
            return;
        }
        String roomType = selectedModel.getActionCommand(); // STD or DLX or STE

        String in = txtCheckIn.getText();
        String out = txtCheckOut.getText();
        String name = txtName.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약자 성함을 입력해주세요.");
            return;
        }

        // 임시 방번호 배정 로직 (서버가 처리하는 게 좋지만 일단 클라이언트에서)
        String tempRoomNum;
        if(roomType.equals("STD")){
            tempRoomNum = "101";
        }
        else if(roomType.equals("DLX")){
            tempRoomNum = "201";
        }
        else{
            tempRoomNum = "301";
        }    

        // 프로토콜: ADD_RESERVATION:방번호:이름:입실:퇴실
        String request = String.format("ADD_RESERVATION:%s:%s:%s:%s", 
                tempRoomNum, name, in, out);
        
        String response = NetworkService.getInstance().sendRequest(request);

        if ("RESERVE_SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "예약이 완료되었습니다!");
            // 입력창 초기화
            txtName.setText("");
            roomGroup.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "예약 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("예약 정보 입력"));
        panel.setMaximumSize(new Dimension(1000, 200)); 

        // 1행
        panel.add(new JLabel("Check-In (YYYY-MM-DD)"));
        panel.add(new JLabel("Check-Out (YYYY-MM-DD)"));
        panel.add(new JLabel("인원수"));
        panel.add(new JLabel("예약자 성함"));

        txtCheckIn = new JTextField("2025-01-01");
        txtCheckOut = new JTextField("2025-01-02");
        spinGuests = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        txtName = new JTextField();

        panel.add(txtCheckIn);
        panel.add(txtCheckOut);
        panel.add(spinGuests);
        panel.add(txtName);

        // 2행
        panel.add(new JLabel("전화번호"));
        panel.add(new JLabel("결제 수단"));
        panel.add(new JLabel("요청사항"));
        panel.add(new JLabel("")); 

        txtPhone = new JTextField("010-0000-0000");
        String[] payments = {"Credit Card", "Cash", "Samsung Pay"};
        comboPayment = new JComboBox<>(payments);
        txtRequest = new JTextField();

        panel.add(txtPhone);
        panel.add(comboPayment);
        panel.add(txtRequest);
        panel.add(new JLabel("")); 

        return panel;
    }
}
