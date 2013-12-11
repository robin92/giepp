package pl.pisz.airlog.giepp.desktop.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import pl.pisz.airlog.giepp.desktop.menus.MainMenuBar;
import pl.pisz.airlog.giepp.desktop.util.HelperTools;

/**
 * @author Rafal
 */
public class MainFrame
        extends JFrame {
    
    private static final String FRAME_CAPTION   = "giepp-desktop";
    
    private JTabbedPane mTabbedPane;
    
    public MainFrame(JPanel[] tabs, String[] titles) {
        super(FRAME_CAPTION);        
        if (tabs.length != titles.length) throw new IllegalArgumentException("need the same amount of tabs and titles");
        
        this.initTabs(tabs, titles);
        this.initComponent();
    }
    
    private void initTabs(JPanel[] tabs, String[] titles) {
        mTabbedPane = new JTabbedPane();
        
        for (int i = 0; i < tabs.length; i++) {
            JPanel panel = tabs[i];
            String title = titles[i];
            
            mTabbedPane.addTab(title, panel);
        }
    }
    
    private void initComponent() {
        this.setLayout(new BorderLayout());
        this.add(mTabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               String[] titles = new String[] {"Moje konto", "Notowania", "Obserwowane", "Statystyki"};
               JPanel[] panels = new JPanel[titles.length];
               for (int i = 0; i < titles.length; i++) panels[i] = HelperTools.newTextPanel(titles[i]);
               
               MainMenuBar mmb = new MainMenuBar();
               mmb.setMenuListener(new MainMenuBar.MainMenuListener() {
                  @Override
                  public void onFileQuit(java.awt.event.ActionEvent ae) {
                      System.exit(0);
                  }
               });
               
               JFrame frame = new MainFrame(panels, titles);
               
               frame.setJMenuBar(mmb);
               frame.setMinimumSize(new Dimension(800, 600));
               
               frame.setVisible(true);
           }
        });
    }

}
