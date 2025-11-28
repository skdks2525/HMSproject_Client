package client.view;

import javax.swing.*;
import java.awt.*;

public class OccupancyReportPanel extends JPanel {
    public OccupancyReportPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("과거 점유율", new PastOccupancyPanel());
        tabbedPane.addTab("현재 점유율", new CurrentOccupancyPanel());
        tabbedPane.addTab("미래 점유율 예측", new FutureOccupancyPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }
}
