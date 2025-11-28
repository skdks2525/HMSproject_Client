package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

public class PastOccupancyPanel extends JPanel {
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JButton searchButton;
    private final JTable table;
    private final JLabel infoLabel;
    private final JLabel avgLabel = new JLabel();
    private final DefaultTableModel model;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 과거 점유율 패널
     * - 상단: 기간 입력, 조회 버튼, 안내 메시지
     * - 상단 카드: 기간 평균 점유율 강조
     * - 표: 날짜별, 타입별, 평균 점유율 표시
     * - 서버와 통신: GET_PAST_OCCUPANCY 요청/응답 파싱
     */
    public PastOccupancyPanel() {

        setLayout(new BorderLayout());
        // 상단 입력 패널: 기간 입력, 조회 버튼, 안내 메시지
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.add(new JLabel("시작일 (yyyy-MM-dd): "));
        startDateField = new JTextField(10); // 시작일 입력
        topPanel.add(startDateField);
        topPanel.add(new JLabel("~ 종료일 (yyyy-MM-dd): "));
        endDateField = new JTextField(10);   // 종료일 입력
        topPanel.add(endDateField);
        searchButton = new JButton("조회");  // 조회 버튼
        topPanel.add(searchButton);
        infoLabel = new JLabel();            // 안내 메시지
        topPanel.add(infoLabel);

        // 상단 요약 카드: 기간 평균 점유율 강조
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        avgLabel.setText("기간 평균 점유율: 0%");
        avgLabel.setFont(avgLabel.getFont().deriveFont(14f));
        avgLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        cards.add(avgLabel);

        // 상단 전체 래핑
        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.add(topPanel, BorderLayout.NORTH);
        northWrap.add(cards, BorderLayout.CENTER);
        add(northWrap, BorderLayout.NORTH);

        // 표: 날짜, 타입별, 평균 점유율
        model = new DefaultTableModel(new String[]{"날짜", "스탠다드 점유율", "디럭스 점유율", "스위트 점유율", "평균 점유율"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(tableScroll, BorderLayout.CENTER);

        setDefaultDates(); // 기본 시작일/종료일 세팅
        searchButton.addActionListener(this::handleSearch); // 조회 버튼 이벤트
    }

    private void setDefaultDates() {
        // 기본 시작일: 2025-10-01, 종료일: 오늘
        startDateField.setText("2025-10-01");
        endDateField.setText(LocalDate.now().format(DATE_FORMAT));
    }

    private void handleSearch(ActionEvent ignored) {
        // 날짜 입력 검증 및 서버 요청
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();
        try {
            LocalDate startDate = LocalDate.parse(start, DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(end, DATE_FORMAT);
            if (endDate.isBefore(startDate)) {
                infoLabel.setText("종료일이 시작일보다 빠를 수 없습니다.");
                return;
            }
            infoLabel.setText("");
            fetchAndFillTable(start, end); // 서버에서 데이터 받아 표 채움
        } catch (Exception ex) {
            infoLabel.setText("날짜 형식 오류");
        }
    }

    private void fetchAndFillTable(String start, String end) {
        // 표 초기화 및 서버 데이터 요청/파싱
        model.setRowCount(0);
        avgLabel.setText("");
        String req = "GET_PAST_OCCUPANCY:" + start + ":" + end;
        String resp = NetworkService.getInstance().sendRequest(req);
        if (resp == null || !resp.startsWith("PAST_OCCUPANCY:")) {
            infoLabel.setText("서버 오류 또는 데이터 없음");
            return;
        }
        String data = resp.substring("PAST_OCCUPANCY:".length());
        if (data.trim().isEmpty()) return;
        String[] parts = data.split("\\|");
        if (parts.length >= 2) {
            avgLabel.setText("기간 평균 점유율: " + parts[0] + "%"); // 상단 카드에 평균 표시
            for (String entry : parts[1].split(";")) {
                String[] cols = entry.split(",");
                if (cols.length == 5) {
                    model.addRow(cols); // 표에 한 줄씩 추가
                }
            }
        }
    }
}
