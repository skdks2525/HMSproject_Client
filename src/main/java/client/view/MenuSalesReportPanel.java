package client.view;

import client.net.NetworkService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 식음료 매출 리포트 패널
 * - 상단: 시작/종료일 입력, 조회 버튼, 상태 메시지
 * - 요약 카드: 평균 매출
 * - 중앙: 날짜별 매출/최다판매메뉴 표
 *
 * 서버와 통신: GET_MENU_SALES:yyyy-MM-dd:yyyy-MM-dd
 * 응답: MENU_SALES:평균매출|날짜,매출,최다판매메뉴;날짜,매출,최다판매메뉴;...
 */
/**
 * 식음료 매출 리포트 패널
 * - 기간별 평균 매출, 날짜별 매출/최다판매메뉴 표를 조회/표시
 * - 서버와 GET_MENU_SALES 프로토콜로 통신
 * - JTable로 표 형태로 결과 표시
 */
public class MenuSalesReportPanel extends JPanel {
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JButton searchButton;
    private final JLabel avgSalesLabel; // 일평균 매출
    private final JLabel totalSalesLabel; // 총 매출
    private final JLabel infoLabel;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 패널 생성자: UI 구성 및 이벤트 연결
     * - 상단: 기간 입력, 조회 버튼, 안내 메시지
     * - 중앙: JTable(날짜, 매출, 최다판매메뉴)
     * - 하단: 평균 매출 표시
     */
    public MenuSalesReportPanel() {
        setLayout(new BorderLayout());
        // 상단 입력 패널: 기간 입력, 조회 버튼, 안내 메시지
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.add(new JLabel("시작일 (yyyy-MM-dd): "));
        startDateField = new JTextField(10);
        topPanel.add(startDateField);
        topPanel.add(new JLabel("~ 종료일 (yyyy-MM-dd): "));
        endDateField = new JTextField(10);
        topPanel.add(endDateField);
        searchButton = new JButton("조회");
        topPanel.add(searchButton);
        infoLabel = new JLabel();
        topPanel.add(infoLabel);

        // 상단 카드: 총 매출, 일평균 매출
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        totalSalesLabel = new JLabel("총 매출: ₩0");
        totalSalesLabel.setFont(totalSalesLabel.getFont().deriveFont(14f));
        totalSalesLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        avgSalesLabel = new JLabel("일 평균 매출: ₩0");
        avgSalesLabel.setFont(avgSalesLabel.getFont().deriveFont(14f));
        avgSalesLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        cards.add(totalSalesLabel);
        cards.add(avgSalesLabel);

        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.add(topPanel, BorderLayout.NORTH);
        northWrap.add(cards, BorderLayout.CENTER);
        add(northWrap, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"날짜", "매출", "최다판매메뉴"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(this::handleSearch);
        setDefaultDates();
    }

    /**
     * 시작/종료일 기본값 세팅 (시작: 2025-10-01, 종료: 어제)
     */
    private void setDefaultDates() {
        startDateField.setText("2025-10-01");
        endDateField.setText(LocalDate.now().minusDays(1).format(DATE_FORMAT));
    }

    /**
     * 조회 버튼 클릭 시 호출: 입력 검증 후 fetchAndFillTable 실행
     */
    private void handleSearch(ActionEvent ignored) {
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();
        LocalDate today = LocalDate.now();
        try {
            LocalDate startDate = LocalDate.parse(start, DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(end, DATE_FORMAT);
            if (endDate.isAfter(today.minusDays(1))) {
                infoLabel.setText("종료일은 어제까지 선택 가능합니다.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                infoLabel.setText("종료일이 시작일보다 빠를 수 없습니다.");
                return;
            }
            infoLabel.setText("");
            fetchAndFillTable(start, end);
        } catch (Exception ex) {
            infoLabel.setText("날짜 형식 오류");
        }
    }

    /**
     * 서버에 GET_MENU_SALES 요청 후 응답 파싱, JTable에 데이터 채움
     * - 응답 예시: MENU_SALES:평균매출|날짜,매출,최다판매메뉴;...
     * - 평균 매출은 하단 라벨에, 표 데이터는 JTable에 표시
     */
    private void fetchAndFillTable(String start, String end) {
        String req = "GET_MENU_SALES:" + start + ":" + end;
        String resp = NetworkService.getInstance().sendRequest(req);
        if (resp == null || !resp.startsWith("MENU_SALES:")) {
            infoLabel.setText("서버 오류 또는 데이터 없음");
            tableModel.setRowCount(0);
            totalSalesLabel.setText("총 매출: ₩0");
            avgSalesLabel.setText("일 평균 매출: ₩0");
            return;
        }
        String data = resp.substring("MENU_SALES:".length());
        String[] parts = data.split("\\|", 2);
        if (parts.length != 2) {
            infoLabel.setText("데이터 파싱 오류");
            tableModel.setRowCount(0);
            totalSalesLabel.setText("총 매출: ₩0");
            avgSalesLabel.setText("일 평균 매출: ₩0");
            return;
        }
        String avgStr = parts[0];
        String tableStr = parts[1];
        // 표 데이터 파싱 및 총합 계산
        tableModel.setRowCount(0);
        int total = 0;
        int count = 0;
        if (!tableStr.trim().isEmpty()) {
            for (String row : tableStr.split(";")) {
                String[] cols = row.split(",");
                if (cols.length == 3) {
                    tableModel.addRow(new Object[]{cols[0], cols[1], cols[2]});
                    try {
                        total += Integer.parseInt(cols[1]);
                        count++;
                    } catch (Exception ignore) {}
                }
            }
        }
        totalSalesLabel.setText("총 매출: ₩" + total);
        avgSalesLabel.setText("일 평균 매출: ₩" + avgStr);
    }
}
