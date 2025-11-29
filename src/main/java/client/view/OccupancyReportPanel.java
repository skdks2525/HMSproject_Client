package client.view;

import javax.swing.*;
import java.awt.*;

public class OccupancyReportPanel extends JPanel {
    public OccupancyReportPanel() {
        setLayout(new BorderLayout());

        // 상단 제목/뒤로가기 패널
        // 상단 제목/뒤로가기 패널 제거

        // 기존 탭 패널 아래로 내림
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("과거 점유율", new PastOccupancyPanel());
        tabbedPane.addTab("현재 점유율", new CurrentOccupancyPanel());
        tabbedPane.addTab("미래 점유율 예측", new FutureOccupancyPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }
}
